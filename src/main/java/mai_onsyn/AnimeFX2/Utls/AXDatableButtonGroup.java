package mai_onsyn.AnimeFX2.Utls;

import mai_onsyn.AnimeFX2.Module.AXButton;

import java.util.HashMap;
import java.util.Map;

public class AXDatableButtonGroup<T> extends AXButtonGroup{

    private final Map<AXButton, T> buttonMap = new HashMap<>();

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

}
