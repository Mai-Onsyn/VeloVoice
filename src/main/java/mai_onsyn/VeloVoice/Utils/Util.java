package mai_onsyn.VeloVoice.Utils;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Util {
    /**
     * Windows 10 April 2018 Update (1803, build 17134) required to run
     */
    public static boolean isWindowSupport() {
        if(!System.getProperty("os.name").equals("Windows 10")) {
            return false;
        }
        Kernel32 kernel = Kernel32.INSTANCE;
        WinNT.OSVERSIONINFOEX vex = new WinNT.OSVERSIONINFOEX();
        if (kernel.GetVersionEx(vex)) {
            try {
                int build = Integer.parseInt(vex.dwBuildNumber.toString());
                return build >= 17134;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return false;
    }

    public static class DrawUtil {
        //窗体拉伸属性
        private static boolean isRight;// 是否处于右边界调整窗口状态
        private static boolean isBottomRight;// 是否处于右下角调整窗口状态
        private static boolean isBottom;// 是否处于下边界调整窗口状态
        private final static int RESIZE_WIDTH = 10;// 判定是否为调整窗口状态的范围与边界距离
        //private final static double MIN_WIDTH = 300;// 窗口最小宽度
        //private final static double MIN_HEIGHT = 250;// 窗口最小高度

        public static void addDrawFunc(Stage stage, Pane root) {
            root.setOnMouseMoved((MouseEvent event) -> {
                event.consume();
                double x = event.getSceneX();
                double y = event.getSceneY();
                double width = stage.getWidth();
                double height = stage.getHeight();
                Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
                // 先将所有调整窗口状态重置
                isRight = isBottomRight = isBottom = false;
                if (y >= height - RESIZE_WIDTH) {
                    if (x <= RESIZE_WIDTH) {// 左下角调整窗口状态

                    } else if (x >= width - RESIZE_WIDTH) {// 右下角调整窗口状态
                        isBottomRight = true;
                        cursorType = Cursor.SE_RESIZE;
                    } else {// 下边界调整窗口状态
                        isBottom = true;
                        cursorType = Cursor.S_RESIZE;
                    }
                } else if (x >= width - RESIZE_WIDTH) {// 右边界调整窗口状态
                    isRight = true;
                    cursorType = Cursor.E_RESIZE;
                }
                // 最后改变鼠标光标
                root.setCursor(cursorType);
            });

            root.setOnMouseDragged((MouseEvent event) -> {
                double x = event.getSceneX();
                double y = event.getSceneY();
                // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
                double nextX = stage.getX();
                double nextY = stage.getY();
                double nextWidth = stage.getWidth();
                double nextHeight = stage.getHeight();
                if (isRight || isBottomRight) {// 所有右边调整窗口状态
                    nextWidth = x;
                }
                if (isBottomRight || isBottom) {// 所有下边调整窗口状态
                    nextHeight = y;
                }
//                if (nextWidth <= stage.getMinWidth()) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
//                    nextWidth = stage.getMinWidth();
//                }
//                if (nextHeight <= stage.getMinHeight()) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
//                    nextHeight = stage.getMinHeight();
//                }
                nextWidth = Math.max(nextWidth, stage.getMinWidth());
                nextHeight = Math.max(nextHeight, stage.getMinHeight());
                // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
                stage.setX(nextX);
                stage.setY(nextY);
                stage.setWidth(nextWidth);
                stage.setHeight(nextHeight);
            });
        }
    }
}
