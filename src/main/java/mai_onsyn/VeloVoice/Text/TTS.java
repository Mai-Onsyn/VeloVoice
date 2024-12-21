package mai_onsyn.VeloVoice.Text;

import mai_onsyn.VeloVoice.App.AppConfig;
import mai_onsyn.VeloVoice.NetWork.TTSExecutor;
import mai_onsyn.VeloVoice.Utils.Structure;
import mai_onsyn.VeloVoice.Utils.Util;

import java.io.File;
import java.util.List;

import static mai_onsyn.VeloVoice.App.AppConfig.*;
import static mai_onsyn.VeloVoice.App.Runtime.*;

public class TTS {

    public static TTSExecutor executor;

    public static void startNewTask(Structure<List<String>> tree, File structureRoot) {
        currentFile = "";
        totalCount = countStructure(tree, 0);
        totalProgress.setValue(0);
        executor = new TTSExecutor(AppConfig.connectThread);
        if (!executor.connect()) {
            return;
        }
        try {
            if (tree.getChildren().size() == 1) runTask(tree.getChildren().getFirst(), structureRoot, false, 0, 1);
            else for (int i = 0; i < tree.getChildren().size(); i++) {
                runTask(tree.getChildren().get(i), structureRoot, isAppendOrdinal, i + 1, tree.getChildren().size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdown();
        currentFile = "";
    }

    private static void runTask(Structure<List<String>> tree, File structureRoot, boolean ordinal, int counter, int rootSize) throws InterruptedException {
        //System.out.println(Thread.currentThread().isInterrupted());
        if (Thread.currentThread().isInterrupted()) {
            //logger.debug("TTS exit");
            return; //终止信号
        }
        if (tree.getData() == null) {
            structureRoot = new File(structureRoot, ordinal ? String.format("%s. %s", Util.padZero(counter, rootSize), tree.getName()) : tree.getName());
            if (!structureRoot.exists()) if (!structureRoot.mkdirs()) {
                if (logger != null) logger.error("无法创建音频输出文件夹 - " + structureRoot.getAbsolutePath());
                else System.out.println("无法创建音频输出文件夹 - " + structureRoot.getAbsolutePath());
                return;
            }

            if (tree.getChildren().size() == 1) runTask(tree.getChildren().getFirst(), structureRoot, false, 0, 1);
            else for (int i = 0; i < tree.getChildren().size(); i++) {
                runTask(tree.getChildren().get(i), structureRoot, isAppendOrdinal, i + 1, tree.getChildren().size());
            }
        }
        else {
            currentFile = tree.getName();
            if (logger != null) logger.info("当前 - \"" + currentFile + "\"");
            executor.execute2(tree.getData(), structureRoot, ordinal ? String.format("%s. %s", Util.padZero(counter, rootSize), tree.getName()) : tree.getName());
        }
    }

    private static int countStructure(Structure<List<String>> structure, int count) {
        // 检查当前Structure是否包含数据（即List<String>）
        if (structure.getData() != null) {
            // 将当前Structure的数据（List<String>）的大小加到总数上
            count += structure.getData().size();
        }

        // 检查当前Structure是否有子节点
        if (structure.getChildren() != null) {
            // 遍历所有子节点，并递归调用countStructure方法
            for (Structure<List<String>> child : structure.getChildren()) {
                count = countStructure(child, count);
            }
        }

        // 返回更新后的总数
        return count;
    }
}
