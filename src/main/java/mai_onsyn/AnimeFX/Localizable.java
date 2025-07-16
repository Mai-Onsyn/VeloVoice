package mai_onsyn.AnimeFX;

import java.util.List;
import java.util.Map;

public interface Localizable {

    String getI18NKey();

    List<Localizable> getChildrenLocalizable();

    void setI18NKey(String key);

    void setChildrenI18NKeys(Map<String, String> keyMap);

    void localize(String text);

}
