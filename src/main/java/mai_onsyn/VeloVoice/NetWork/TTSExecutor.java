package mai_onsyn.VeloVoice.NetWork;

import javafx.application.Platform;
import javafx.scene.control.Label;
import mai_onsyn.AnimeFX.Frame.Module.SmoothProgressBar;
import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.TTSClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static mai_onsyn.VeloVoice.App.Runtime.*;

public class TTSExecutor {

    private final List<TTSClient> clients;
    private final int THREAD_COUNT;

    public TTSExecutor(int threadCount) {
        THREAD_COUNT = threadCount;
        clients = new ArrayList<>(threadCount);
        for (int i = 0; i < threadCount; i++) {
            clients.add(new EdgeTTSClient());
        }
    }

    public boolean connect() {
        logger.prompt("开始建立TTS连接");
        try {
            CompletableFuture<?>[] threadMonitor = new CompletableFuture[THREAD_COUNT];
            for (int i = 0; i < clients.size(); i++) {
                threadMonitor[i] = new CompletableFuture<>();
                double progress = (i + 1.0) / THREAD_COUNT;
                int I = i;
                Thread.ofVirtual().name("Connect-Thread-" + i).start(() -> {
                    TTSClient client = clients.get(I);

                    int tryCount = -1;
                    while (tryCount < AppConfig.retryCount) {
                        tryCount++;

                        client.connect();

                        if (!client.isOpen()) {
                            client = new EdgeTTSClient();
                            logger.warn(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                            client.connect();
                            clients.set(I, client);
                            continue;
                        }

                        break;
                    }

                    threadMonitor[I].complete(null);
                    Platform.runLater(() -> {
                        ((SmoothProgressBar) progressPane.getChildren().get(0)).setProgress(progress);
                        ((Label) progressPane.getChildren().get(2)).setText(String.format("连接TTS - %.1f%% - [%d/%d]", progress * 100, I + 1, THREAD_COUNT));
                        progressPane.flushWidth(progressPane.getLayoutBounds().getWidth());
                    });
                });
            }
            CompletableFuture<?> all = CompletableFuture.allOf(threadMonitor);
            all.get();
            logger.prompt(clients.size() + "个TTS连接已建立");
            return true;
        } catch (Exception e) {
            logger.error("连接中止，因为 " + e);
            return false;
        }
//        long endTime = System.currentTimeMillis() + AppConfig.timeoutSeconds * 1000L;
//
//        while (System.currentTimeMillis() < endTime) {
//            boolean allConnected = true;
//
//            for (TTSClient client : clients) {
//                if (!client.isOpen()) {
//                    allConnected = false;
//                    break;
//                }
//            }
//            if (allConnected) {
//                //System.out.println(clients.size() + "个WebSocket连接已建立");
//                logger.log(clients.size() + "个TTS连接已建立");
//                return true;
//            }
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                logger.log("TTS连接过程意外中止");
//                //System.out.println("连接过程意外中止");
//                return false;
//            }
//        }
//        logger.log("TTS连接超时");
//        //System.out.println("连接超时");
//        return false;
    }

    public boolean shutdown() {
        for (TTSClient client : clients) {
            client.close();
        }

        long endTime = System.currentTimeMillis() + AppConfig.timeoutSeconds * 1000L;

        while (System.currentTimeMillis() < endTime) {
            boolean allClosed = true;

            for (TTSClient client : clients) {
                if (client.isOpen()) {
                    allClosed = false;
                    break;
                }
            }
            if (allClosed) {
                logger.prompt("TTS连接已关闭");
                return true;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("等待关闭过程被中断");
                return false;
            }
        }
        logger.error("超时 - 部分TTS连接未能成功关闭！");
        return false;
    }

    public void execute2(List<String> input, File output) throws InterruptedException {
        final int taskCount = input.size();
        currentCount = taskCount;

        currentProgress.setValue(0);

        List<Thread> virtualThreads = new ArrayList<>(THREAD_COUNT);
        try (FileOutputStream fos = new FileOutputStream(output)) {
            List<String> tasks = new ArrayList<>(input);
            List<Integer> taskIDs = new ArrayList<>(taskCount);
            for (int i = 0; i < taskCount; i++) taskIDs.add(i);

            Map<Integer, List<byte[]>> resultPool = new HashMap<>();
            CompletableFuture<?>[] monitorList = new CompletableFuture[THREAD_COUNT];

            for (int i = 0; i < THREAD_COUNT; i++) {
                CompletableFuture<?> monitor = new CompletableFuture<>();
                monitorList[i] = monitor;
                final int I = i;
                virtualThreads.add(Thread.ofVirtual().name("Task-Thread-" + i).start(() -> {
                    TTSClient client = clients.get(I);
                    rootLoop:
                    while (!Thread.currentThread().isInterrupted()) {
                        if (AppConfig.pausing) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                            continue;
                        }
                        String message;
                        int messageID;
                        synchronized (this) {
                            if (tasks.isEmpty()) {
                                break;
                            }
                            message = tasks.getFirst();
                            messageID = taskIDs.getFirst();
                            tasks.removeFirst();
                            taskIDs.removeFirst();
                        }

                        int tryCount = -1;
                        List<byte[]> data = null;
                        while (data == null && tryCount < AppConfig.retryCount) {
                            tryCount++;
                            try {
                                data = client.sendText(UUID.randomUUID(), message);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break rootLoop;
                            }

                            if (data == null) {
                                client = new EdgeTTSClient();
                                logger.warn(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                                client.connect();
                                clients.set(I, client);
                            }
                        }

                        if (data == null) {
                            data = new ArrayList<>();
                            logger.error(String.format("线程%s已达最大重试次数，语句\"%s\"将被忽略！", Thread.currentThread().getName(), message));
                        }

                        synchronized (this) {
                            resultPool.put(messageID, data);
                            currentProgress.setValue(currentProgress.getValue() + 1);
                        }
                    }
                    monitor.complete(null);
                }));
            }

            CompletableFuture<?> threadsMonitor = CompletableFuture.allOf(monitorList);
            threadsMonitor.get();

            for (int i = 0; i < taskCount; i++) {
                for (byte[] bytes : resultPool.get(i)) {
                    fos.write(bytes);
                }
            }
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                virtualThreads.forEach(Thread::interrupt);
                //shutdown();
                throw new InterruptedException();
            }
            else logger.error(String.format("章节\"%s\"转换时出错，因为 - %s", currentFile, e));
        }
    }

//    @Deprecated
//    public void execute(List<String> tasks, File output) {
//        final int taskCount = tasks.size();
//        currentProgressCount = taskCount;
//
//        int currentCount = -1;
//        List<String> resultPool = new ArrayList<>();
//
//        tryLoop:
//        while (currentCount < AppConfig.retryCount) {
//            if (Thread.currentThread().isInterrupted()) {
//                logger.debug("Executor exit in loop head");
//                return;
//            }
//            if (currentCount == -1) {
//                TTS.temptTotalCount = TTS.overallProgress.getValue();
//            } else TTS.overallProgress.setValue(TTS.temptTotalCount);
//            resultPool.clear();
//            currentCount++;
//
//            Platform.runLater(() -> completeCountProperty.set(0));
//
//            Thread[] threads = null;
//            try (FileOutputStream fos = new FileOutputStream(output)) {
//
//                threads = new Thread[THREAD_COUNT];
//
//                List<String>[] splitTasks = splitTask(tasks);
//                List<List<byte[]>> resultList = new ArrayList<>(THREAD_COUNT);
//                for (int i = 0; i < splitTasks.length; i++) {
//                    int I = i;
//                    resultList.add(new ArrayList<>());
//                    threads[i] = Thread.ofVirtual().name("TTS-Task-Thread-" + i).start(() -> {
//                        try {
//                            TTSClient client = clients.get(I);
//                            for (String message : splitTasks[I]) {
//                                Timer timer = new Timer();
//                                timer.schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        System.out.println(splitTasks[I].indexOf(message) + " / " + (splitTasks[I].size() - 1) + ": " + message);
//                                    }
//                                }, AppConfig.timeoutSeconds * 1000L);
//                                List<byte[]> bytes = client.sendText(UUID.randomUUID(), message);
//                                timer.cancel();
//                                synchronized (resultList.get(I)) {
//                                    resultPool.add(message);
//                                    resultList.get(I).addAll(bytes);
//                                    Platform.runLater(() -> completeCountProperty.set(completeCountProperty.get() + 1));
//                                }
//                            }
//                        } catch (Exception e) {
//                            Thread.currentThread().interrupt();
//                        }
//                    });
//                }
//
//                AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
//
//                completeCountProperty.addListener((o, ov, nv) -> {
//                    startTime.set(System.currentTimeMillis()); // 每次更新重置计时
//                });
//
//                while (completeCountProperty.get() != taskCount) {
//                    if (Thread.currentThread().isInterrupted()) return; //终止
//                    if (AppConfig.pausing) startTime.set(System.currentTimeMillis());   //暂停不计时
//                    long elapsedTime = System.currentTimeMillis() - startTime.get();
//                    if (elapsedTime > AppConfig.timeoutSeconds * 4000L) {
//                        logger.warn("超时！重试次数：" + (currentCount + 1));
//                        //logger.log("超时字符串：" + findDifferent(tasks, resultPool));
//                        for (Thread thread : threads) {
//                            thread.interrupt(); // 中断线程
//                        }
//                        if (!Thread.currentThread().isInterrupted()) continue tryLoop; // 重新执行任务
//                        else {
//                            logger.debug("Executor exit in loop inner body");
//                            return;    //终止信号
//                        }
//                    }
//                    //logger.log(String.valueOf(Thread.currentThread().isInterrupted()));
//                    Thread.sleep(500);
//                }
//
//                for (List<byte[]> data : resultList) {
//                    for (byte[] bytes : data) {
//                        fos.write(bytes);
//                    }
//                }
//
//                return;
//
//            } catch (Exception e) {
//                if (threads != null) {
//                    for (Thread thread : threads) {
//                        thread.interrupt(); // 中断线程
//                    }
//                }
//                Thread.currentThread().interrupt();
//                logger.debug("Executor exit in Exception");
//            }
//        }
//    }

    private List<String>[] splitTask(List<String> tasks) {
        int minChildrenSize = tasks.size() / THREAD_COUNT;
        int remainder = tasks.size() % THREAD_COUNT;

        List<String>[] result = new List[THREAD_COUNT];

        int index = 0;
        for (int i = 0; i < THREAD_COUNT; i++) {
            result[i] = new ArrayList<>();
            for (int j = 0; j < minChildrenSize; j++) {
                result[i].add(tasks.get(index));
                index++;
            }

            if (i < remainder) {
                result[i].add(tasks.get(index));
                index++;
            }
        }

        return result;
    }
}
