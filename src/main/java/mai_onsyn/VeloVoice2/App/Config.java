package mai_onsyn.VeloVoice2.App;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import mai_onsyn.AnimeFX2.I18N;
import mai_onsyn.AnimeFX2.Localizable;
import mai_onsyn.AnimeFX2.Module.*;
import mai_onsyn.AnimeFX2.Styles.DefaultAXBaseStyle;
import mai_onsyn.AnimeFX2.Utls.*;
import mai_onsyn.AnimeFX2.layout.AutoPane;

import java.util.*;

public class Config extends JSONObject {

    private final Map<String, ConfigType> types = new HashMap<>();
    private final Map<String, Config> subConfig = new LinkedHashMap<>();

    public enum ConfigType {
        STRING, INTEGER, DOUBLE, BOOLEAN, COLOR, MAP, CONFIG
    }

    public Config() {
        super(new LinkedHashMap<>());
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this, true);
    }

    public ConfigType getType(String key) {
        return types.get(key);
    }

    public void registerString(String key, String value) {
        types.put(key, ConfigType.STRING);
        put(key, value);
    }
    public void registerInteger(String key, int value) {
        types.put(key, ConfigType.INTEGER);
        put(key, value);
    }
    public void registerDouble(String key, double value) {
        types.put(key, ConfigType.DOUBLE);
        put(key, value);
    }
    public void registerBoolean(String key, boolean value) {
        types.put(key, ConfigType.BOOLEAN);
        put(key, value);
    }
    public void registerColor(String key, Color value) {
        types.put(key, ConfigType.COLOR);
        put(key, Toolkit.colorToString(value));
    }
    public void registerMap(String key, Map<String, String> value) {
        types.put(key, ConfigType.MAP);
        put(key, value);
    }
    public void registerConfig(String key, Config value) {
        types.put(key, ConfigType.CONFIG);
        subConfig.put(key, value);
        put(key, value);
    }

    public void setString(String key, String value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setInteger(String key, int value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setDouble(String key, double value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setBoolean(String key, boolean value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }
    public void setColor(String key, Color value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, Toolkit.colorToString(value));
    }
    public void setMap(String key, Map<String, String> value) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        put(key, value);
    }

    public String getString(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getString(key);
    }
    public Double getDouble(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getDouble(key);
    }
    public Integer getInteger(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getInteger(key);
    }
    public Boolean getBoolean(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return super.getBoolean(key);
    }
    public Color getColor(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return Color.web(super.getString(key));
    }
    public Map<String, String> getMap(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return parseMap(super.getString(key));
    }
    public Config getConfig(String key) {
        if (!types.containsKey(key)) throw new IllegalArgumentException("Key " + key + " is not registered");
        return subConfig.get(key);
    }

    public static LinkedHashMap<String, String> parseMap(String s) {
        Map<String, Object> innerMap = JSONObject.parseObject(s, Feature.OrderedField).getInnerMap();

        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        for (Entry<String, Object> entry : innerMap.entrySet()) {
            linkedHashMap.put(entry.getKey(), (String) entry.getValue());
        }
        return linkedHashMap;
    }


    public ConfigItem genInputStringItem(String key, String promptNameSpace) {
        AXTextField textField = new AXTextField();
        textField.setText(getString(key));
        textField.textField().textProperty().addListener((o, ov, nv) -> {
            setString(key, nv);
        });
        textField.setI18NKey(promptNameSpace);
        I18N.registerComponent(textField);
        return new ConfigItem(key, textField, 0.4);
    }

    public ConfigItem genChooseStringItem(String key, List<String> options) {
        AXChoiceBox choiceBox = new AXChoiceBox();
        choiceBox.getTextLabel().setText(getString(key));
        for (String option : options) {
            AXButton item = choiceBox.createItem();
            item.setUserData(option);
            item.setText(option);
            item.setOnMouseClicked(event -> {
                setString(key, option);
                choiceBox.getTextLabel().setText(option);
            });
        }

        //choose default
        AXButtonGroup group = choiceBox.getButtonGroup();
        group.getButtonList().forEach(button -> {
            if (Objects.equals(button.getUserData(), getString(key))) {
                group.selectButton(button);
            }
        });
        group.addOnSelectChangedListener((o, ov, nv) -> {
            if (nv == null) {
                this.setString(key, "null");
            } else {
                this.setString(key, nv.getUserData().toString());
            }
        });


        return new ConfigItem(key, choiceBox, 0.4);
    }

    public ConfigItem genFloatSlidItem(String key, double min, double max, double step) {
        AutoPane box = new AutoPane();
        AXSlider slider = new AXSlider(min, max, step, getDouble(key));
        AXFloatTextField textField = new AXFloatTextField(min, max, getDouble(key));
        textField.setText(String.format("%.2f", getDouble(key)));

        textField.valueProperty().bindBidirectional(slider.valueProperty());
        textField.valueProperty().addListener((o, ov, nv) -> setDouble(key, nv.doubleValue()));

        box.getChildren().addAll(slider, textField);
        box.setPosition(slider, false, 0, Constants.UI_HEIGHT * 2, 0, 0);
        box.setPosition(textField, false, Constants.UI_HEIGHT * 1.4, 0, 0, 0);
        box.flipRelativeMode(textField, AutoPane.Motion.LEFT);

        return new ConfigItem(key, box, 0.4);
    }

    public ConfigItem genIntegerSlidItem(String key, int min, int max, int step) {
        AutoPane box = new AutoPane();
        AXSlider slider = new AXSlider(min, max, step, getDouble(key));
        AXIntegerTextField textField = new AXIntegerTextField(min, max, getInteger(key));
        textField.setText(String.valueOf(getInteger(key)));

        textField.valueProperty().bindBidirectional(slider.valueProperty());
        textField.valueProperty().addListener((o, ov, nv) -> setInteger(key, nv.intValue()));

        box.getChildren().addAll(slider, textField);
        box.setPosition(slider, false, 0, Constants.UI_HEIGHT * 2, 0, 0);
        box.setPosition(textField, false, Constants.UI_HEIGHT * 1.4, 0, 0, 0);
        box.flipRelativeMode(textField, AutoPane.Motion.LEFT);

        return new ConfigItem(key, box, 0.4);
    }

    public ConfigItem genSwitchItem(String key) {
        AutoPane box = new AutoPane();
        AXSwitch switcher = new AXSwitch(getBoolean(key));
        switcher.stateProperty().addListener((o, ov, nv) -> setBoolean(key, nv));

        box.getChildren().add(switcher);
        box.setPosition(switcher, false, 0, Constants.UI_HEIGHT * 2, 0, 0);
        box.flipRelativeMode(switcher, AutoPane.Motion.RIGHT);
        return new ConfigItem(key, box, 0.4);
    }

//    public ConfigItem genChooseItem(String key, Map<String, String> options) {
//        AXChoiceBox choiceBox = new AXChoiceBox();
//
//        for (Entry<String, String> entry : options.entrySet()) {
//            AXButton item = choiceBox.createItem();
//            item.getTextLabel().setText(entry.getValue());
//        }
//
//    }

    public static class ConfigItem extends AXBase implements Localizable {

        private final Label label;
        private final AutoPane innerItem;
        private static final DefaultAXBaseStyle style = new DefaultAXBaseStyle();
        static {
            style.setBGColor(Color.TRANSPARENT);
            style.setBorderColor(Color.TRANSPARENT);
            Color shadow = Color.rgb(128, 128, 128, 0.2);
            style.setHoverShadow(shadow);
            style.setPressedShadow(shadow);
            style.setAnimeRate(0.5);
        }

        public ConfigItem(String initName, AutoPane content, double rv) {
            this(initName, content, true, rv);
        }

        public ConfigItem(String initName, AutoPane content, boolean mode, double rv) {
            super();
            super.setTheme(style);
            super.update();
            super.setClip(null);

            label = new Label(initName);
            innerItem = content;
            super.getChildren().addAll(label, innerItem);
            super.setPosition(label, mode, 0, rv, 0, 0);
            super.setPosition(innerItem, mode, rv, 0, 0, 0);
        }

        public AutoPane getContent() {
            return innerItem;
        }

        public Label getLabel() {
            return label;
        }

        private String langKey = "";

        @Override
        public String getI18NKey() {
            return langKey;
        }

        @Override
        public List<Localizable> getChildrenLocalizable() {
            if (innerItem instanceof Localizable localizable) {
                return localizable.getChildrenLocalizable();
            } else return List.of();
        }

        @Override
        public void setI18NKey(String key) {
            langKey = key;
        }

        @Override
        public void setChildrenI18NKeys(Map<String, String> keyMap) {
            if (innerItem instanceof Localizable localizable) {
                localizable.setChildrenI18NKeys(keyMap);
            }
        }

        @Override
        public void localize(String text) {
            label.setText(text);
        }
    }

    public static class ConfigBox extends VBox {

        private final double itemHeight;

        public ConfigBox(double spacing, double height) {
            this.itemHeight = height;
            super.setSpacing(spacing);
        }

        public void addConfigItem(AutoPane... items) {
            addConfigItem(getChildren().size(), items);
        }

        public void addConfigItem(int index, AutoPane... items) {
            int i = index;
            for (AutoPane item : items) {
                item.setMaxHeight(itemHeight);
                item.setMinHeight(itemHeight);
                getChildren().add(i++, item);
            }
        }

        public AXLangLabel addTitle(String title) {
            AutoPane titlePane = new AutoPane();
            titlePane.setMaxHeight(itemHeight);
            titlePane.setMinHeight(itemHeight);

            AXLangLabel label = new AXLangLabel(title);
            label.setAlignment(Pos.CENTER);
            titlePane.getChildren().add(label);
            titlePane.setPosition(label, false, 0, 0, 0, 0);

            getChildren().add(titlePane);
            return label;
        }

        public void lineBreak() {
            Region region = new Region();
            region.setMaxHeight(itemHeight);
            region.setMinHeight(itemHeight);
            getChildren().add(region);
        }

    }


    private String formatMap(Map<String, String> map) {
        return JSONObject.toJSONString(map, true);
    }
}
