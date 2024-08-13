package mai_onsyn.AnimeFX.Frame.Utils;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX.Frame.Module.DiffusionButton;

import java.util.ArrayList;
import java.util.List;

public class DiffusionButtonGroup {

    private final List<DiffusionButton> group = new ArrayList<>();

    private DiffusionButton switchedNow;
    private DiffusionButton switchedLast;

    private Color switchedColor = new Color(0.1, 0.2, 1, 0.5);
    private Color switchedFocusColor = new Color(0.8, 0.4, 0.8, 0.5);
    private Color switchedTextColor = new Color(0.5, 1, 0.5, 0.5);
    private Color switchedTextFocusColor = new Color(1, 0.9, 0.9, 0.5);
    private Color freeColor;
    private Color freeFocusColor;
    private Color freeTextColor;
    private Color freeTextFocusColor;

    public DiffusionButtonGroup(DiffusionButton... buttons) {
        group.addAll(List.of(buttons));
        if (buttons.length != 0) {
            freeColor = buttons[0].getBgColor();
            freeFocusColor = buttons[0].getBgFocusColor();
            freeTextColor = buttons[0].getTextColor();
            freeTextFocusColor = buttons[0].getTextFocusColor();
        }

        for (DiffusionButton button : buttons) {
            addEventListener(button);
        }
    }

    public void setSwitchedColor(Color switchedColor) {
        this.switchedColor = switchedColor;
    }
    public void setSwitchedFocusColor(Color switchedFocusColor) {
        this.switchedFocusColor = switchedFocusColor;
    }
    public void setSwitchedTextColor(Color switchedTextColor) {
        this.switchedTextColor = switchedTextColor;
    }
    public void setSwitchedTextFocusColor(Color switchedTextFocusColor) {
        this.switchedTextFocusColor = switchedTextFocusColor;
    }

    public DiffusionButton getSwitching() {
        return switchedNow;
    }

    public DiffusionButton getLastSwitching() {
        return switchedLast;
    }

    public List<DiffusionButton> getAllButtons() {
        return group;
    }

    public void addAll(DiffusionButton... buttons) {
        if (buttons.length != 0) {
            freeColor = buttons[0].getBgColor();
            freeFocusColor = buttons[0].getBgFocusColor();
            freeTextColor = buttons[0].getTextColor();
            freeTextFocusColor = buttons[0].getTextFocusColor();
        }

        for (DiffusionButton button : buttons) {
            addEventListener(button);
        }
        group.addAll(List.of(buttons));
    }

    public void add(DiffusionButton button) {
        freeColor = button.getBgColor();
        freeFocusColor = button.getBgFocusColor();
        freeTextColor = button.getTextColor();
        freeTextFocusColor = button.getTextFocusColor();

        addEventListener(button);

        group.add(button);
    }

    public void remove(DiffusionButton button) {
        group.remove(button);
    }

    private void addEventListener(DiffusionButton button) {
        button.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (switchedNow == button) return;

            switchedLast = switchedNow;
            switchedNow = button;

            if (switchedLast != null) {
                switchedLast.setBgColor(freeColor);
                switchedLast.setBgFocusColor(freeFocusColor, false);
                switchedLast.setTextColor(freeTextColor, true);
                switchedLast.setTextFocusColor(freeTextFocusColor, false);
            }

            switchedNow.setBgColor(switchedColor);
            switchedNow.setBgFocusColor(switchedFocusColor, true);
            switchedNow.setTextColor(switchedTextColor, false);
            switchedNow.setTextFocusColor(switchedTextFocusColor, true);
        });
    }
}
