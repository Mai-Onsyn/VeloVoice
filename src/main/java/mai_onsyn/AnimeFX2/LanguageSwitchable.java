package mai_onsyn.AnimeFX2;

import java.util.Map;

public interface LanguageSwitchable {

    void switchLanguage(String str);

    Map<String, LanguageSwitchable> getLanguageElements();
}
