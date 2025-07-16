package mai_onsyn.AnimeFX;

import java.util.Map;

@Deprecated
public interface LanguageSwitchable {

    void switchLanguage(String str);

    Map<LanguageSwitchable, String> getLanguageElements();
}
