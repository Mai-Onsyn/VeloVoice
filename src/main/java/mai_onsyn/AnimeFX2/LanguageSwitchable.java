package mai_onsyn.AnimeFX2;

import java.util.Map;

@Deprecated
public interface LanguageSwitchable {

    void switchLanguage(String str);

    Map<LanguageSwitchable, String> getLanguageElements();
}
