package mai_onsyn.AnimeFX2.Utls;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import mai_onsyn.AnimeFX2.Module.AXTextField;

public class AXFloatTextField extends AXTextField {
    private final SimpleDoubleProperty valueProperty;

    private int textState = 1;
    private boolean textChangeLock = false;

    public AXFloatTextField(double min, double max, double value) {
        super();

        if (value < min || value > max) throw new IllegalArgumentException("value must be between min and max");

        valueProperty = new SimpleDoubleProperty(value);
        TextField textField = super.textField();
        valueProperty.addListener((e, ov, nv) -> System.out.println(nv.doubleValue()));

        textField.textProperty().addListener((e, ov, nv) -> {
            if (!textChangeLock) {
                textChangeLock = true;

                textState = checkFloat(nv);
                switch (textState) {
                    case 0 -> {
                        double newValue = Math.min(Math.max(Double.parseDouble(nv), min), max);
                        if (valueProperty.get() != newValue) {
                            valueProperty.set(newValue);
                        }
                    }
                    case 1, 3, 4, 5 -> {}
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
                        textState == 4 ||
                        textState == 5 ||
                        Double.parseDouble(textField.getText()) > max ||
                        Double.parseDouble(textField.getText()) < min
                ) textField.setText(String.valueOf(valueProperty.get()));
            }
        });

    }

    public SimpleDoubleProperty valueProperty() {
        return valueProperty;
    }


    /**
     * 0 - readable number;
     * 1 - empty;
     * 2 - not a number;
     * 3 - only a minus sign;
     * 4 - start with number and end with dot;
     * 5 - a number is sandwiched between a minus sign and a dot;
     * 6 - a number is sandwiched between a minus and a dot sign, and end with a number;
     */
    private static int checkFloat(String s) {
        if (s == null || s.isEmpty()) {
            return 1;
        }

        // 判断是否只有负号
        if (s.equals("-")) {
            return 3;
        }

        // 判断是否是负数的浮动数（例如 -12.34）
        if (s.matches("-?\\d+\\.\\d+$")) {
            return 0;
        }

        // 判断是否是正数的浮动数（例如 12.34）
        if (s.matches("\\d+\\.\\d+$")) {
            return 0;
        }

        // 判断是否是整数（正整数或负整数）
        if (s.matches("-?\\d+$")) {
            return 0;
        }

        // 判断是否是负号后接数字和点
        if (s.matches("-\\d+\\.$")) {
            return 5;
        }

        // 判断是否是正号后接数字和点
        if (s.matches("\\d+\\.$")) {
            return 4;
        }

        // 判断无效输入
        return 2;
    }

}
