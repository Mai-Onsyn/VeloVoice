package mai_onsyn.AnimeFX2;

import javafx.scene.image.WritableImage;
import mai_onsyn.AnimeFX2.Utls.Toolkit;

import java.io.IOException;

public class ResourceManager {
    public static final WritableImage copy;
    public static final WritableImage paste;
    public static final WritableImage pasteAppend;
    public static final WritableImage cut;
    public static final WritableImage selectAll;
    public static final WritableImage clear;
    public static final WritableImage undo;
    public static final WritableImage expand;
    public static final WritableImage rename;
    public static final WritableImage up;
    public static final WritableImage down;
    public static final WritableImage delete;

    public static final WritableImage triangle;
    public static final WritableImage fork;
    public static final WritableImage file;
    public static final WritableImage fileNew;
    public static final WritableImage folder;
    public static final WritableImage folderNew;

    static {
        try {
            copy = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/copy.png").getPixelReader(), 512, 512);
            paste = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/paste.png").getPixelReader(), 512, 512);
            pasteAppend = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/paste_append.png").getPixelReader(), 512, 512);
            cut = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/cut.png").getPixelReader(), 512, 512);
            selectAll = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/select_all.png").getPixelReader(), 512, 512);
            clear = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/clear.png").getPixelReader(), 512, 512);
            delete = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/delete.png").getPixelReader(), 512, 512);
            undo = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/undo.png").getPixelReader(), 512, 512);
            expand = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/expand.png").getPixelReader(), 512, 512);
            folderNew = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/folder_new.png").getPixelReader(), 512, 512);
            fileNew = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/file_new.png").getPixelReader(), 512, 512);
            rename = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/rename.png").getPixelReader(), 512, 512);
            up = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/up.png").getPixelReader(), 512, 512);
            down = new WritableImage(Toolkit.loadImage("textures/icons/context_menu/down.png").getPixelReader(), 512, 512);

            triangle = new WritableImage(Toolkit.loadImage("textures/icons/triangle.png").getPixelReader(), 384, 384);
            folder = new WritableImage(Toolkit.loadImage("textures/icons/folder.png").getPixelReader(), 512, 512);
            file = new WritableImage(Toolkit.loadImage("textures/icons/file.png").getPixelReader(), 512, 512);
            fork = new WritableImage(Toolkit.loadImage("textures/icons/fork.png").getPixelReader(), 512, 512);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
