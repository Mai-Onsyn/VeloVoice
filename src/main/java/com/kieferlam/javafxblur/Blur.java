package com.kieferlam.javafxblur;

import javafx.stage.Stage;

/**
 * Singleton handler enum class Blur.
 * This class provides global methods to load and apply blur effects to a JavaFX stage.
 */
public enum Blur {
    NONE(0), BLUR_BEHIND(3), ACRYLIC(4);
    private final int accentState;

    Blur(int accentState) {
        this.accentState = accentState;
    }

    private static final String BLUR_TARGET_PREFIX = "_JFX";
    private static final NativeBlur _extBlur = new NativeBlur();

    /**
     * Loads the required blur library.
     * This should be called at the very start of your main function.
     * The "javafxblur" library file should be added to your library path.
     */
    public static void loadBlurLibrary(){
        System.loadLibrary("javafxblur");
    }

    private static void _extApplyBlur(String target, int accentState){
        _extBlur._extApplyBlur(target, accentState);
    }

    /**
     * Calls the external (native) function to apply the blur effect to a JavaFX stage.
     * The JavaFX stage must be visible before this function is called.
     * If the stage is ever hidden (destroyed, not minimised), this function must be called again once visible.
     * @param stage
     */
    public static void applyBlur(Stage stage, Blur blur){
        if (!stage.isShowing()) System.err.println("Warning: blur effect was called on a hidden stage!");
        String stageTitle = stage.getTitle();
        String targetTitle = BLUR_TARGET_PREFIX + (System.currentTimeMillis() % 1000);
        stage.setTitle(targetTitle);
        _extApplyBlur(targetTitle, blur.accentState);
        stage.setTitle(stageTitle);
    }

}
