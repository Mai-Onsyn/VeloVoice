package mai_onsyn.VeloVoice.NetWork;

import javafx.application.Platform;
import javafx.scene.control.Label;
import mai_onsyn.AnimeFX.Frame.Module.SmoothProgressBar;
import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.App.Runtime;
import mai_onsyn.VeloVoice.NetWork.TTS.EdgeTTSClient;
import mai_onsyn.VeloVoice.NetWork.TTS.TTSClient;
import mai_onsyn.VeloVoice.Utils.AudioPlayer;
import mai_onsyn.VeloVoice.Utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        if (logger != null) logger.prompt("开始建立TTS连接");
        else System.out.println("开始建立TTS连接");
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
                            if (logger != null) logger.warn(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                            else System.out.println(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                            client.connect();
                            clients.set(I, client);
                            continue;
                        }

                        break;
                    }

                    threadMonitor[I].complete(null);
                    if (!consoleMode) Platform.runLater(() -> {
                        ((SmoothProgressBar) progressPane.getChildren().get(0)).setProgress(progress);
                        ((Label) progressPane.getChildren().get(2)).setText(String.format("连接TTS - %.1f%% - [%d/%d]", progress * 100, I + 1, THREAD_COUNT));
                        progressPane.flushWidth(progressPane.getLayoutBounds().getWidth());
                    });
                });
            }
            CompletableFuture<?> all = CompletableFuture.allOf(threadMonitor);
            all.get();
            if (logger != null) logger.prompt(clients.size() + "个TTS连接已建立");
            else System.out.println(clients.size() + "个TTS连接已建立");
            return true;
        } catch (Exception e) {
            if (logger != null) logger.error("连接中止，因为 " + e);
            else System.out.println("连接中止，因为 " + e);
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
                if (logger != null) logger.prompt("TTS连接已关闭");
                else System.out.println("TTS连接已关闭");
                return true;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (logger != null) logger.warn("等待关闭过程被中断");
                else System.out.println("等待关闭过程被中断");
                return false;
            }
        }
        if (logger != null) logger.error("超时 - 部分TTS连接未能成功关闭！");
        else System.out.println("超时 - 部分TTS连接未能成功关闭！");
        return false;
    }

    public List<String> tasks;
    public void execute2(List<String> input, File outputFolder, String fileName) throws InterruptedException {
        final int taskCount = input.size();
        currentCount = taskCount;

        currentProgress.setValue(0);

        List<Thread> virtualThreads = new ArrayList<>(THREAD_COUNT);
        try {
            tasks = new ArrayList<>(input);
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
                                tasks.clear();
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
                                tasks.clear();
                                break rootLoop;
                            }

                            if (data == null) {
                                client = new EdgeTTSClient();
                                if (logger != null) logger.warn(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                                else System.out.println(String.format("线程%s连接错误，尝试第%d次重连", Thread.currentThread().getName(), tryCount + 1));
                                client.connect();
                                clients.set(I, client);
                            }
                        }

                        if (data == null) {
                            data = new ArrayList<>();
                            if (logger != null) logger.error(String.format("线程%s已达最大重试次数，语句\"%s\"将被忽略！", Thread.currentThread().getName(), message));
                            else System.out.println(String.format("线程%s已达最大重试次数，语句\"%s\"将被忽略！", Thread.currentThread().getName(), message));
                        }

                        synchronized (this) {
                            resultPool.put(messageID, data);
                            currentProgress.setValue(currentProgress.getValue() + 1);
                            if (consoleMode) System.out.printf("\r当前 - %s - %.2f%% - [%d/%d]", currentFile, (float) currentProgress.get() / currentCount * 100, currentProgress.get(), currentCount);
                        }
                    }
                    monitor.complete(null);
                }));
            }

            CompletableFuture<?> threadsMonitor = CompletableFuture.allOf(monitorList);
            threadsMonitor.get();
            System.out.println();

            if (AppConfig.splitChapter) {
                int totalBytes = countAudio(resultPool);
                int fileByteSize = (int) (360_000 * AppConfig.maxAudioDuration);
                int fileCount = totalBytes / fileByteSize;
                //System.out.println("fileCount: " + fileCount + " totalBytes: " + totalBytes);
                FileOutputStream fos;
                if (fileCount == 0) fos = new FileOutputStream(new File(outputFolder, fileName + ".mp3"));
                else fos = new FileOutputStream(new File(outputFolder, String.format("%s_p%s.mp3", fileName, Util.padZero(0, fileCount))));
                try {
                    int byteCount = 0;
                    int piece = 0;
                    for (int i = 0; i < taskCount; i++) {

                        for (byte[] bytes : resultPool.get(i)) {
                            fos.write(bytes);
                            byteCount += bytes.length;
                        }

                        if (byteCount > 360_000 * AppConfig.maxAudioDuration) {
                            byteCount = 0;
                            fos.close();
                            fos = new FileOutputStream(new File(outputFolder, String.format("%s_p%s.mp3", fileName, Util.padZero(++piece, fileCount))));
                            if (AppConfig.isAppendNameForSplitChapter) {
                                for (byte[] bytes : resultPool.get(0)) {
                                    fos.write(bytes);
                                    byteCount += bytes.length;
                                }
                                //暂时没加重连检测 祈祷不要断线吧...............................................................
                                EdgeTTSClient client = new EdgeTTSClient();
                                client.connect();
                                List<byte[]> counterVoice = client.sendText(UUID.randomUUID(), "p" + piece);
                                client.close();

                                for (byte[] bytes : counterVoice) {
                                    fos.write(bytes);
                                    byteCount += bytes.length;
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    if (logger != null) logger.error("写入文件时发生错误: " + e);
                    else System.out.println("写入文件时发生错误: " + e);
                } finally {
                    fos.close();
                }

            }
            else {
                int byteCount = 0;
                try (FileOutputStream fos = new FileOutputStream(new File(outputFolder, fileName + ".mp3"))) {
                    for (int i = 0; i < taskCount; i++) {
                        for (byte[] bytes : resultPool.get(i)) {
                            fos.write(bytes);
                            byteCount += bytes.length;
                        }
                    }
                } catch (IOException e) {
                    if (logger != null) logger.error("写入文件时发生错误: " + e);
                    else System.out.println("写入文件时发生错误: " + e);
                }
                //System.out.println(byteCount);
            }

        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                virtualThreads.forEach(Thread::interrupt);
                //shutdown();
                throw new InterruptedException();
            }
            else {
                if (logger != null) logger.error(String.format("章节\"%s\"转换时出错，因为 - %s", currentFile, e));
                else System.out.printf("章节\"%s\"转换时出错，因为 - %s%n", currentFile, e);
            }
        }
    }

    private int countAudio(Map<Integer, List<byte[]>> lines) {
        int count = 0;
        int i = 0;
        while (lines.get(i) != null) {
            for (byte[] bytes : lines.get(i++)) {
                count += bytes.length;
            }
        }
        return count;
    }
}
