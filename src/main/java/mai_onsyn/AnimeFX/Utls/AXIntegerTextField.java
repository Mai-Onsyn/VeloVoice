package mai_onsyn.AnimeFX.Utls;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import mai_onsyn.AnimeFX.Module.AXTextField;

public class AXIntegerTextField extends AXTextField {

    private final SimpleIntegerProperty valueProperty;

    private int textState = 1;
    private boolean textChangeLock = false;

    public AXIntegerTextField(int min, int max, int value) {
        super();

        if (value < min || value > max) throw new IllegalArgumentException(String.format("Value %d must be between min(%d) and max(%d)", value, min, max));
        super.setText(String.valueOf(value));

        valueProperty = new SimpleIntegerProperty(value);
        TextField textField = super.textField();
        valueProperty.addListener((e, ov, nv) -> {
            textChangeLock = true;

            textField.setText(String.valueOf(nv.intValue()));

            textChangeLock = false;
        });

        textField.textProperty().addListener((e, ov, nv) -> {
            if (!textChangeLock) {
                textChangeLock = true;

                textState = checkInteger(nv);
                switch (textState) {
                    case 0 -> {
                        int newValue = Math.min(Math.max(Integer.parseInt(nv), min), max);
                        if (valueProperty.get() != newValue) {
                            valueProperty.set(newValue);
                        }
                    }
                    case 1, 3 -> {}
                    case 2 -> {
                        textField.setText(ov);
                        Toolkit.defaultToolkit.beep();
                    }
                }

                textChangeLock = false;
            }
        });
        textField.focusedProperty().addListener((e, ov, nv) -> {
            if (!nv) {
                if (
                        textState == 1 ||
                        textState == 3 ||
                        Integer.parseInt(textField.getText()) > max ||
                        Integer.parseInt(textField.getText()) < min
                ) textField.setText(String.valueOf(valueProperty.get()));
            }
        });

    }

    public SimpleIntegerProperty valueProperty() {
        return valueProperty;
    }



    /**
     * 0 - readable number;
     * 1 - empty;
     * 2 - not a number;
     * 3 - only a minus sign;
     */
    private static int checkInteger(String s) {
        if (s == null || s.isEmpty()) {
            return 1;
        }

        // 判断是否只包含负号
        if (s.equals("-")) {
            return 3;
        }

        // 判断是否是负数且后面跟着数字 或是纯数字
        if (s.matches("-\\d+") || s.matches("\\d+")) {
            return 0;
        }

        // 如果不符合以上任何条件，则为无效输入
        return 2;
    }

}
