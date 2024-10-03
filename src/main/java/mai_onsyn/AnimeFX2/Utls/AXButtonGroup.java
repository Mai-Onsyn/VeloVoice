package mai_onsyn.AnimeFX2.Utls;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import mai_onsyn.AnimeFX2.AutoUpdatable;
import mai_onsyn.AnimeFX2.Module.AXButton;
import mai_onsyn.AnimeFX2.Styles.AXButtonStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXButtonGroupStyle;
import mai_onsyn.AnimeFX2.Styles.DefaultAXButtonStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AXButtonGroup implements AutoUpdatable {

    private AXButtonStyle style = new DefaultAXButtonStyle();
    private AXButtonStyle selectedStyle = new DefaultAXButtonGroupStyle();

    private final List<AXButton> buttons = new ArrayList<>();
    private final Map<AXButton, EventHandler<MouseEvent>> buttonHandlers = new HashMap<>();

    private AXButton selectedButton;
    private AXButton lastSelectedButton;

    public AXButtonGroup(AXButton... buttons) {
        register(buttons);
    }

    public void register(AXButton... e) {
        for (AXButton button : e) {

            buttons.add(button);

            EventHandler<MouseEvent> eventEventHandler = event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    selectButton(button);
                }
            };
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, eventEventHandler);

            buttonHandlers.put(button, eventEventHandler);

        }

    }

    public void remove(AXButton button) {
        if (buttons.contains(button)) {
            buttons.remove(button);
            button.removeEventHandler(MouseEvent.MOUSE_CLICKED, buttonHandlers.get(button));
        }
        if (button == selectedButton) selectedButton = null;
        if (button == lastSelectedButton) lastSelectedButton = null;
    }

    public void selectButton(AXButton button) {
        if (selectedButton != button) {
            lastSelectedButton = selectedButton;
            selectedButton = button;

            if (lastSelectedButton != null) {
                lastSelectedButton.setTheme(style);
                lastSelectedButton.update();
            }
            if (selectedButton != null) {
                selectedButton.setTheme(selectedStyle);
                selectedButton.update();
            }
        }
    }

    public void setFreeStyle(AXButtonStyle style) {
        this.style = style;
    }

    public void setSelectedStyle(AXButtonStyle style) {
        this.selectedStyle = style;
    }


    @Override
    public void update() {
        for (AXButton button : buttons) {
            button.update();
        }
    }
}
