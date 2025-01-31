package mai_onsyn.AnimeFX2.Utls;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
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

    protected AXButtonStyle style = new DefaultAXButtonStyle();
    protected AXButtonStyle selectedStyle = new DefaultAXButtonGroupStyle();

    protected final List<AXButton> buttons = new ArrayList<>();
    protected final Map<AXButton, EventHandler<MouseEvent>> buttonHandlers = new HashMap<>();

    protected AXButton selectedButton;
    protected AXButton lastSelectedButton;
    protected ChangeListener<AXButton> onSelectedChanged;

    public AXButtonGroup(AXButton... buttons) {
        register(buttons);

    }

    public void register(AXButton... e) {
        for (AXButton button : e) {

            buttons.add(button);

            EventHandler<MouseEvent> eventEventHandler = _ -> selectButton(button);
            button.addEventHandler(MouseEvent.MOUSE_CLICKED, eventEventHandler);

            buttonHandlers.put(button, eventEventHandler);
        }
    }

    public void remove(AXButton button) {
        if (buttons.contains(button)) {
            buttons.remove(button);
            button.removeEventHandler(MouseEvent.MOUSE_CLICKED, buttonHandlers.get(button));
        }
        if (button == selectedButton) {
            selectedButton = null;
            onSelectedChanged.changed(null, lastSelectedButton, null);
        }
        if (button == lastSelectedButton) {
            lastSelectedButton = null;
            onSelectedChanged.changed(null, null, selectedButton);
        }
    }

    public void selectButton(AXButton button) {
        if (selectedButton != button) {
            lastSelectedButton = selectedButton;
            selectedButton = button;

            if (onSelectedChanged != null) {
                onSelectedChanged.changed(null, lastSelectedButton, selectedButton);
            }

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

    public void setOnSelectedChanged(ChangeListener<AXButton> onSelectedChanged) {
        this.onSelectedChanged = onSelectedChanged;
    }

    public AXButton getSelectedButton() {
        return selectedButton;
    }

    public List<AXButton> getButtonList() {
        return buttons;
    }

    @Override
    public void update() {
        for (AXButton button : buttons) {
            button.update();
        }
    }
}
