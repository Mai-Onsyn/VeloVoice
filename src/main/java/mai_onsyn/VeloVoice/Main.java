package mai_onsyn.VeloVoice;

import com.kieferlam.javafxblur.Blur;
import javafx.application.Application;
import mai_onsyn.VeloVoice.App.ConfigListener;
import mai_onsyn.VeloVoice.Utils.Util;

public class Main {

    static {
        Thread configSaveThread = ConfigListener.CONFIG_SAVE_THREAD;    //加载ConfigListener类的静态代码块
        configSaveThread.start();
        if (Util.isWindowSupport()) Blur.loadBlurLibrary();
    }

    public static void main(String[] args) {

//        if (args.length != 0) {
//            if (Objects.equals(args[0], "no-gui")) {
//                ConsoleApp.main(args);
//                return;
//            }
//        }

        //System.out.println("launch");
        Application.launch(FrameApp.class);
    }

}