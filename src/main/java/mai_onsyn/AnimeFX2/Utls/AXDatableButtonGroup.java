package mai_onsyn.AnimeFX2.Utls;

import javafx.beans.value.ChangeListener;
import javafx.util.Pair;
import mai_onsyn.AnimeFX2.Module.AXButton;

import java.util.HashMap;
import java.util.Map;

public class AXDatableButtonGroup<T> extends AXButtonGroup{

    private final Map<AXButton, T> buttonMap = new HashMap<>();
    private ChangeListener<Pair<AXButton, T>> onSelectedChanged;

    public AXDatableButtonGroup(AXButton button, T data) {
        super(button);

        buttonMap.put(button, data);
    }

    public void register(AXButton button, T data) {
        super.register(button);

        buttonMap.put(button, data);
    }

    public T getData(AXButton button) {
        return buttonMap.get(button);
    }

    @Override
    public void remove(AXButton button) {
        super.remove(button);

        buttonMap.remove(button);
    }

    @Override
    public void selectButton(AXButton button) {
        if (selectedButton != button) {
            lastSelectedButton = selectedButton;
            selectedButton = button;

            if (super.onSelectedChanged != null) {
                super.onSelectedChanged.changed(null, lastSelectedButton, selectedButton);
            }
            if (onSelectedChanged != null) {
                onSelectedChanged.changed(null, new Pair<>(lastSelectedButton, buttonMap.get(lastSelectedButton)), new Pair<>(selectedButton, buttonMap.get(selectedButton)));
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

    public void setOnSelectedChangedDatable(ChangeListener<Pair<AXButton, T>> onSelectedChanged) {
        this.onSelectedChanged = onSelectedChanged;
    }

}
