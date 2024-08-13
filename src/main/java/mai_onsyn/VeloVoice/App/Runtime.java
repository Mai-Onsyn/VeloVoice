package mai_onsyn.VeloVoice.App;

import javafx.beans.property.SimpleIntegerProperty;
import mai_onsyn.AnimeFX.Frame.Layout.AutoPane;
import mai_onsyn.AnimeFX.Frame.Module.FXLogger;

public class Runtime {

    public static boolean runningState = false;

    public static Thread TaskMainThread;

    public static FXLogger logger;

    public static final SimpleIntegerProperty totalProgress = new SimpleIntegerProperty(0);
    public static volatile SimpleIntegerProperty currentProgress = new SimpleIntegerProperty(0);
    public static AutoPane progressPane = new AutoPane();
    public static String currentFile;
    public static int totalCount;
    public static int currentCount;

}
