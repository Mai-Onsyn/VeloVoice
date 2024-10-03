package mai_onsyn.VeloVoice;

import javafx.application.Application;
import mai_onsyn.VeloVoice.App.ConfigListener;
import mai_onsyn.VeloVoice.App.Runtime;
import mai_onsyn.VeloVoice.App.Theme;

import java.util.Objects;

import static mai_onsyn.VeloVoice.App.AppConfig.isWindowSupport;

public class Main {

    static {
        Thread configSaveThread = ConfigListener.CONFIG_SAVE_THREAD;    //加载ConfigListener类的静态代码块
        configSaveThread.start();
        try {
            if (isWindowSupport) System.loadLibrary("javafxblur"); //Blur.loadBlurLibrary();
        } catch (UnsatisfiedLinkError e) {
            Runtime.systemSupportButLibraryNotExist = true;
            isWindowSupport = false;
            Theme.enableWinUI = false;
        }
    }

    public static void main(String[] args) {

        if (args.length != 0) {
            if (Objects.equals(args[0], "no-gui")) {
                Runtime.consoleMode = true;
                ConsoleApp.main(args);
                return;
            }
        }
        //System.out.println(Arrays.toString(args));

        Application.launch(FrameApp.class);

    }

}