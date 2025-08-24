package mai_onsyn.VeloVoice.FrameFactory;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mai_onsyn.AnimeFX.ResourceManager;
import mai_onsyn.AnimeFX.Styles.*;
import mai_onsyn.AnimeFX.Utls.Toolkit;
import mai_onsyn.VeloVoice.App.Resource;

import static mai_onsyn.VeloVoice.App.Constants.UI_HEIGHT;
import static mai_onsyn.VeloVoice.App.Runtime.themeManager;

public class FrameThemes {

    public static Font TITLE_FONT = Font.font(16);
    public static Font STANDARD_FONT = Font.font(13);
    public static Font SMALLER_FONT = Font.font(11);

    public static Color MAIN_TEXT_COLOR = Color.BLACK;
    public static Color MAIN_TEXT_HOVER_COLOR = Color.PINK;

    public static Color MODULE_BASE_COLOR;
    public static Color MODULE_BASE_COLOR_OPAQUE;
    public static Color BUTTON_BASE_COLOR;
    public static Color BUTTON_HOVER_COLOR;
    public static Color BUTTON_PRESSED_COLOR;

    public static Color THEME_COLOR = Color.LIGHTCORAL;

    public static Color BUTTON_FILL_COLOR = Color.web("8080ff80");


    public static final Color TEXT_INPUT_SELECTED_BACKGROUND = Color.web("#0051b2");

    public static final double LIGHT_GRAY = 0.9647;
    public static final double DARK_GRAY = 0.2313;

    public static final double ANIME_RATE = 1.0;
    public static final double BORDER_RADIUS = 1.0;
    public static final double BORDER_ARC_SIZE = 10.0;

    public static final double TREEVIEW_RETRACTION = 22;

    public static void setDarkMode(boolean darkMode) {
        MAIN_TEXT_COLOR = darkMode ? Color.WHITE : Color.BLACK;

        Platform.runLater(() -> {
            ResourceManager.grayResources.forEach((r) -> Toolkit.adjustImageColor(r, MAIN_TEXT_COLOR));
            Resource.grayResources.forEach((r) -> Toolkit.adjustImageColor(r, MAIN_TEXT_COLOR));
        });

        MODULE_BASE_COLOR = darkMode ? Color.gray(0.12, 0.5) : Color.gray(1, 0.6);
        MODULE_BASE_COLOR_OPAQUE = darkMode ? Color.gray(0.172, 0.8) : Color.gray(1, 0.8);
        BUTTON_BASE_COLOR = darkMode ? Color.gray(0.314, 0.72) : Color.gray(0.745, 0.65);
        BUTTON_HOVER_COLOR = darkMode ? Color.gray(0.431, 0.7) : Color.gray(0.627, 0.72);
        BUTTON_PRESSED_COLOR = darkMode ? Color.gray(0.549, 0.8) : Color.gray(0.51, 0.8);

        themeManager.flushAll();
    }

    public static void setThemeColor(Color color) {
        THEME_COLOR = color;
        MAIN_TEXT_HOVER_COLOR = THEME_COLOR;
        themeManager.flushAll();
    }

    public static final AXContextPaneStyle CONTEXT_PANE = new AXContextPaneStyle() {
        @Override
        public double getItemHeight() {
            return UI_HEIGHT;
        }

        @Override
        public double getBGInsets() {
            return 2.5;
        }

        @Override
        public double getPaneWidth() {
            return 200;
        }

        @Override
        public double getMaxHeight() {
            return 512;
        }

        @Override
        public AXButtonStyle getItemStyle() {
            return TRANSPARENT_BUTTON;
        }

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR_OPAQUE;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXTextInputPopupStyle INPUT_POPUP = new AXTextInputPopupStyle() {
        @Override
        public AXTextFieldStyle getTextFieldStyle() {
            return TEXT_FIELD;
        }

        @Override
        public Font getTextFont() {
            return TITLE_FONT;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public double getWidth() {
            return 400;
        }

        @Override
        public double getHeight() {
            return 200;
        }

        @Override
        public double getTextFieldHeight() {
            return UI_HEIGHT;
        }

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR_OPAQUE;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXButtonStyle TRANSPARENT_BUTTON = new AXButtonStyle() {

        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }
    };

    public static final AXButtonStyle TITLE_BUTTON = new AXButtonStyle() {

        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return TITLE_FONT;
        }
    };

    public static final AXButtonStyle BUTTON = new AXButtonStyle() {

        @Override
        public Color getBGColor() {
            return BUTTON_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }
    };

    public static final AXButtonStyle TRANSPARENT_SELECTED_BUTTON = new AXButtonStyle() {
        @Override
        public Color getTextColor() {
            return Toolkit.getInverseColor(THEME_COLOR);
        }

        @Override
        public Color getTextHoverColor() {
            return Toolkit.getInverseColor(MAIN_TEXT_HOVER_COLOR);
        }

        @Override
        public Color getFillColor() {
            return Toolkit.getInverseColor(BUTTON_FILL_COLOR);
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public Color getBGColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXButtonStyle SELECTED_BUTTON = new AXButtonStyle() {
        @Override
        public Color getTextColor() {
            return Toolkit.getInverseColor(THEME_COLOR);
        }

        @Override
        public Color getTextHoverColor() {
            return Toolkit.getInverseColor(MAIN_TEXT_HOVER_COLOR);
        }

        @Override
        public Color getFillColor() {
            return Toolkit.getInverseColor(BUTTON_FILL_COLOR);
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public Color getBGColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXTextFieldStyle TRANSPARENT_TEXT_FIELD = new AXTextFieldStyle() {
        @Override
        public AXContextPaneStyle getContextMenuStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public Color getLineColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextSelectedColor() {
            return Toolkit.getInverseColor(MAIN_TEXT_COLOR);
        }

        @Override
        public Color getTextSelectedBGColor() {
            return TEXT_INPUT_SELECTED_BACKGROUND;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public double getAreaInsets() {
            return 5.0;
        }

        @Override
        public double getLineWeight() {
            return 0.7;
        }

        @Override
        public double getLineInsets() {
            return 2.5;
        }

        @Override
        public Color getBGColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return 1.0;
        }

        @Override
        public double getBorderArcSize() {
            return 10.0;
        }
    };

    public static final AXTextFieldStyle TEXT_FIELD = new AXTextFieldStyle() {

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public AXContextPaneStyle getContextMenuStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public Color getLineColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextSelectedColor() {
            return Toolkit.getInverseColor(MAIN_TEXT_COLOR);
        }

        @Override
        public Color getTextSelectedBGColor() {
            return TEXT_INPUT_SELECTED_BACKGROUND;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public double getAreaInsets() {
            return 5;
        }

        @Override
        public double getLineWeight() {
            return 0.7;
        }

        @Override
        public double getLineInsets() {
            return 2.5;
        }
    };

    public static final AXInlineTextAreaStyle CSS_TEXT_AREA = new AXInlineTextAreaStyle() {
        @Override
        public AXContextPaneStyle getContextMenuStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public Color getDefaultTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextSelectedBGColor() {
            return TEXT_INPUT_SELECTED_BACKGROUND;
        }

        @Override
        public double getAreaInsets() {
            return 5;
        }

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXTextAreaStyle TEXT_AREA = new AXTextAreaStyle() {

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public AXContextPaneStyle getContextMenuStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextSelectedColor() {
            return Toolkit.getInverseColor(MAIN_TEXT_COLOR);
        }

        @Override
        public Color getTextSelectedBGColor() {
            return TEXT_INPUT_SELECTED_BACKGROUND;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public double getAreaInsets() {
            return 5;
        }
    };

    public static final AXTreeViewStyle TREE_VIEW = new AXTreeViewStyle() {
        @Override
        public AXTreeItemStyle getRootItemStyle() {
            return new AXTreeItemStyle() {
                @Override
                public Image getIcon() {
                    return ResourceManager.fork;
                }

                @Override
                public double getIconInsets() {
                    return 3;
                }

                @Override
                public double getTextLeftInsets() {
                    return 0;
                }

                @Override
                public double getTextRightInsets() {
                    return 10;
                }

                @Override
                public double getChildrenInsets() {
                    return TREEVIEW_RETRACTION;
                }

                @Override
                public double getItemHeight() {
                    return 25;
                }

                @Override
                public Color getTextColor() {
                    return MAIN_TEXT_COLOR;
                }

                @Override
                public Color getTextHoverColor() {
                    return MAIN_TEXT_HOVER_COLOR;
                }

                @Override
                public Color getFillColor() {
                    return BUTTON_FILL_COLOR;
                }

                @Override
                public Font getTextFont() {
                    return SMALLER_FONT;
                }

                @Override
                public Color getBGColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }
            };
        }

        @Override
        public AXTreeItemStyle getFolderItemStyle() {
            return new AXTreeItemStyle() {
                @Override
                public Image getIcon() {
                    return ResourceManager.folder;
                }

                @Override
                public double getIconInsets() {
                    return 3;
                }

                @Override
                public double getTextLeftInsets() {
                    return 0;
                }

                @Override
                public double getTextRightInsets() {
                    return 10;
                }

                @Override
                public double getChildrenInsets() {
                    return TREEVIEW_RETRACTION;
                }

                @Override
                public double getItemHeight() {
                    return 25;
                }

                @Override
                public Color getTextColor() {
                    return MAIN_TEXT_COLOR;
                }

                @Override
                public Color getTextHoverColor() {
                    return MAIN_TEXT_HOVER_COLOR;
                }

                @Override
                public Color getFillColor() {
                    return BUTTON_FILL_COLOR;
                }

                @Override
                public Font getTextFont() {
                    return SMALLER_FONT;
                }

                @Override
                public Color getBGColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }
            };
        }

        @Override
        public AXTreeItemStyle getFileItemStyle() {
            return new AXTreeItemStyle() {
                @Override
                public Image getIcon() {
                    return ResourceManager.file;
                }

                @Override
                public double getIconInsets() {
                    return 3;
                }

                @Override
                public double getTextLeftInsets() {
                    return 0;
                }

                @Override
                public double getTextRightInsets() {
                    return 10;
                }

                @Override
                public double getChildrenInsets() {
                    return TREEVIEW_RETRACTION;
                }

                @Override
                public double getItemHeight() {
                    return 25;
                }

                @Override
                public Color getTextColor() {
                    return MAIN_TEXT_COLOR;
                }

                @Override
                public Color getTextHoverColor() {
                    return MAIN_TEXT_HOVER_COLOR;
                }

                @Override
                public Color getFillColor() {
                    return BUTTON_FILL_COLOR;
                }

                @Override
                public Font getTextFont() {
                    return SMALLER_FONT;
                }

                @Override
                public Color getBGColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }
            };
        }

        @Override
        public AXButtonStyle getSelectedButtonStyle() {
            return new AXButtonStyle() {
                @Override
                public Color getTextColor() {
                    return Toolkit.getInverseColor(THEME_COLOR);
                }

                @Override
                public Color getTextHoverColor() {
                    return Toolkit.getInverseColor(MAIN_TEXT_HOVER_COLOR);
                }

                @Override
                public Color getFillColor() {
                    return Toolkit.getInverseColor(BUTTON_FILL_COLOR);
                }

                @Override
                public Font getTextFont() {
                    return SMALLER_FONT;
                }

                @Override
                public Color getBGColor() {
                    return THEME_COLOR;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }
            };
        }

        @Override
        public AXContextPaneStyle getContextMenuStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public AXTextInputPopupStyle getTextInputPopupStyle() {
            return INPUT_POPUP;
        }

        @Override
        public double getContentInsets() {
            return 10;
        }

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXProgressBarStyle SMALL_PROGRESS_BAR = new AXProgressBarStyle() {

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return 0.2;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public Color getInnerBGColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getInnerBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getInnerBorderRadius() {
            return 0;
        }

        @Override
        public double getInnerBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public double getBorderInsets() {
            return 0;
        }
    };

    public static final AXProgressBarStyle WIDE_PROGRESS_BAR = new AXProgressBarStyle() {

        @Override
        public Color getBGColor() {
            return MODULE_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getPressedShadow() {
            return Color.TRANSPARENT;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return 1;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public Color getInnerBGColor() {
            return THEME_COLOR;
        }

        @Override
        public Color getInnerBorderColor() {
            return Color.TRANSPARENT;
        }

        @Override
        public double getInnerBorderRadius() {
            return 8;
        }

        @Override
        public double getInnerBorderArcSize() {
            return BORDER_ARC_SIZE;
        }

        @Override
        public double getBorderInsets() {
            return 1.5;
        }
    };

    public static final AXChoiceBoxStyle CHOICE_BOX = new AXChoiceBoxStyle() {
        @Override
        public Color getTextColor() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public Color getTextHoverColor() {
            return MAIN_TEXT_HOVER_COLOR;
        }

        @Override
        public Color getFillColor() {
            return BUTTON_FILL_COLOR;
        }

        @Override
        public Font getTextFont() {
            return STANDARD_FONT;
        }

        @Override
        public double getSignalRelateX() {
            return 5;
        }

        @Override
        public double getSignalScale() {
            return 20;
        }

        @Override
        public RelativePosition getSignalRelate() {
            return RelativePosition.RIGHT;
        }

        @Override
        public Image getSignalImage() {
            return ResourceManager.triangle;
        }

        @Override
        public AXContextPaneStyle getContextPaneStyle() {
            return CONTEXT_PANE;
        }

        @Override
        public AXButtonStyle getFreeButtonStyle() {
            return TRANSPARENT_BUTTON;
        }

        @Override
        public AXButtonStyle getSelectedButtonStyle() {
            return TRANSPARENT_SELECTED_BUTTON;
        }

        @Override
        public Color getBGColor() {
            return BUTTON_BASE_COLOR;
        }

        @Override
        public Color getHoverShadow() {
            return BUTTON_HOVER_COLOR;
        }

        @Override
        public Color getPressedShadow() {
            return BUTTON_PRESSED_COLOR;
        }

        @Override
        public Color getBorderColor() {
            return THEME_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }

        @Override
        public double getBorderRadius() {
            return BORDER_RADIUS;
        }

        @Override
        public double getBorderArcSize() {
            return BORDER_ARC_SIZE;
        }
    };

    public static final AXSliderStyle SLIDER = new AXSliderStyle() {

        @Override
        public AXProgressBarStyle getTrackStyle() {
            return new AXProgressBarStyle() {

                @Override
                public Color getBGColor() {
                    return BUTTON_BASE_COLOR;
                }

                @Override
                public Color getHoverShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getPressedShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getBorderColor() {
                    return THEME_COLOR;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return 0;
                }

                @Override
                public double getBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }

                @Override
                public Color getInnerBGColor() {
                    return THEME_COLOR;
                }

                @Override
                public Color getInnerBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getInnerBorderRadius() {
                    return 0;
                }

                @Override
                public double getInnerBorderArcSize() {
                    return BORDER_ARC_SIZE;
                }

                @Override
                public double getBorderInsets() {
                    return 0;
                }
            };
        }

        @Override
        public AXBaseStyle getThumbStyle() {
            return new AXBaseStyle() {
                @Override
                public Color getBGColor() {
                    return Color.WHITE;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return THEME_COLOR;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return 20;
                }
            };
        }

        @Override
        public double getThumbWidth() {
            return 20;
        }

        @Override
        public double getThumbHeight() {
            return 20;
        }

        @Override
        public double getTrackHeight() {
            return 6;
        }

        @Override
        public double getHoveredScale() {
            return 1.2;
        }

        @Override
        public double getClickedScale() {
            return 0.9;
        }

        @Override
        public double getPressedScale() {
            return 1.1;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }
    };

    public static final AXSwitchStyle SWITCH = new AXSwitchStyle() {
        @Override
        public AXBaseStyle getThumbStyle() {
            return new AXBaseStyle() {
                @Override
                public Color getBGColor() {
                    return THEME_COLOR;
                }

                @Override
                public Color getHoverShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getPressedShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return UI_HEIGHT;
                }
            };
        }

        @Override
        public AXBaseStyle getSwitchedThumbStyle() {
            return new AXBaseStyle() {
                @Override
                public Color getBGColor() {
                    return Color.WHITE;
                }

                @Override
                public Color getHoverShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getPressedShadow() {
                    return Color.TRANSPARENT;
                }

                @Override
                public Color getBorderColor() {
                    return Color.TRANSPARENT;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return UI_HEIGHT;
                }
            };
        }

        @Override
        public AXBaseStyle getTrackStyle() {
            return new AXBaseStyle() {
                @Override
                public Color getBGColor() {
                    return BUTTON_BASE_COLOR;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return THEME_COLOR;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return UI_HEIGHT;
                }
            };
        }

        @Override
        public AXBaseStyle getSwitchedTrackStyle() {
            return new AXBaseStyle() {
                @Override
                public Color getBGColor() {
                    return THEME_COLOR;
                }

                @Override
                public Color getHoverShadow() {
                    return BUTTON_HOVER_COLOR;
                }

                @Override
                public Color getPressedShadow() {
                    return BUTTON_PRESSED_COLOR;
                }

                @Override
                public Color getBorderColor() {
                    return THEME_COLOR;
                }

                @Override
                public double getAnimeRate() {
                    return ANIME_RATE;
                }

                @Override
                public double getBorderRadius() {
                    return BORDER_RADIUS;
                }

                @Override
                public double getBorderArcSize() {
                    return UI_HEIGHT;
                }
            };
        }

        @Override
        public double getThumbInsetsScale() {
            return 0.1;
        }

        @Override
        public double getHoveredScale() {
            return 1.1;
        }

        @Override
        public double getClickedScale() {
            return 0.8;
        }

        @Override
        public double getPressedScale() {
            return 0.9;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }
    };

    public static final AXLangLabelStyle STANDARD_LABEL = new AXLangLabelStyle() {
        @Override
        public Font getFont() {
            return STANDARD_FONT;
        }

        @Override
        public Color getFill() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }
    };

    public static final AXLangLabelStyle TITLE_LABEL = new AXLangLabelStyle() {
        @Override
        public Font getFont() {
            return TITLE_FONT;
        }

        @Override
        public Color getFill() {
            return MAIN_TEXT_COLOR;
        }

        @Override
        public double getAnimeRate() {
            return ANIME_RATE;
        }
    };

}
