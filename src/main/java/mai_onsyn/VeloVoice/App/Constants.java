package mai_onsyn.VeloVoice.App;

import mai_onsyn.VeloVoice.NetWork.LoadTarget.Epub;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.LocalTXT;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Source;
import mai_onsyn.VeloVoice.NetWork.LoadTarget.Wenku8;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Constants {

    private static String URL;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.55";
    public static final String ORIGIN = "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold";
    public static final String VOICES_LIST_URL = "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?trustedclienttoken=6A5AA1D4EAFF4E9FB37E23D68491D6F4";

    public static final double UI_SPACING = 12.0;
    public static final double UI_HEIGHT = 27.0;

    public static final Map<String, Source> sources = new LinkedHashMap<>();
    static {    //在这里添加自定义的加载源
        sources.put("LocalTXT", new LocalTXT());
        sources.put("Epub", new Epub());
        sources.put(("Wenku8"), new Wenku8());
    }

    public static final List<String> availableTTSNames = new ArrayList<>();
    static {    //在这里添加TTS引擎
        availableTTSNames.addAll(List.of("Edge TTS", "Natural TTS", "Multi TTS", "GPT-SoVITS TTS"));
    }


    public static final Map<String, String> LANG_HEADCODE_TO_NAME_MAPPING;
    public static final Map<String, String> LANG_NAME_TO_HEADCODE_MAPPING;

    static {
        LANG_HEADCODE_TO_NAME_MAPPING = Map.<String, String>ofEntries(
                Map.entry("af", "Afrikaans"), // 南非荷兰语
                Map.entry("am", "Amharic"), // 阿姆哈拉语(አማርኛ)
                Map.entry("ar", "العربية"), // 阿拉伯语
                Map.entry("az", "Azərbaycan dili"), // 阿塞拜疆语
                Map.entry("bg", "Български"), // 保加利亚语
                Map.entry("bn", "বাংলা"), // 孟加拉语
                Map.entry("bs", "Bosanski"), // 波斯尼亚语
                Map.entry("ca", "Català"), // 加泰罗尼亚语
                Map.entry("cs", "Čeština"), // 捷克语
                Map.entry("cy", "Cymraeg"), // 威尔士语
                Map.entry("da", "Dansk"), // 丹麦语
                Map.entry("de", "Deutsch"), // 德语
                Map.entry("el", "Ελληνικά"), // 希腊语
                Map.entry("en", "English"), // 英语
                Map.entry("es", "Español"), // 西班牙语
                Map.entry("et", "Eesti"), // 爱沙尼亚语
                Map.entry("fa", "فارسی"), // 波斯语
                Map.entry("fi", "Suomi"), // 芬兰语
                Map.entry("fr", "Français"), // 法语
                Map.entry("ga", "Gaeilge"), // 爱尔兰语
                Map.entry("gl", "Galego"), // 加利西亚语
                Map.entry("gu", "ગુજરાતી"), // 古吉拉特语
                Map.entry("he", "עברית"), // 希伯来语
                Map.entry("hi", "हिन्दी"), // 印地语
                Map.entry("hr", "Hrvatski"), // 克罗地亚语
                Map.entry("hu", "Magyar"), // 匈牙利语
                Map.entry("id", "Bahasa Indonesia"), // 印度尼西亚语
                Map.entry("is", "Íslenska"), // 冰岛语
                Map.entry("it", "Italiano"), // 意大利语
                Map.entry("iu", "Inuktitut"), // 因纽特语(ᐃᓄᒃᑎᑐᑦ)
                Map.entry("ja", "日本語"), // 日语
                Map.entry("jv", "Basa Jawa"), // 爪哇语
                Map.entry("ka", "ქართული"), // 格鲁吉亚语
                Map.entry("kk", "Қазақ тілі"), // 哈萨克语
                Map.entry("km", "ភាសាខ្មែរ"), // 高棉语
                Map.entry("kn", "ಕನ್ನಡ"), // 卡纳达语
                Map.entry("ko", "한국어"), // 韩语
                Map.entry("lo", "ລາວ"), // 老挝语
                Map.entry("lt", "Lietuvių"), // 立陶宛语
                Map.entry("lv", "Latviešu"), // 拉脱维亚语
                Map.entry("mk", "Македонски"), // 马其顿语
                Map.entry("ml", "മലയാളം"), // 马拉雅拉姆语
                Map.entry("mn", "Монгол"), // 蒙古语
                Map.entry("mr", "मराठी"), // 马拉地语
                Map.entry("ms", "Bahasa Melayu"), // 马来语
                Map.entry("mt", "Malti"), // 马耳他语
                Map.entry("my", "Burmese"), // 缅甸语(မြန်မာဘာသာ)
                Map.entry("nb", "Norsk Bokmål"), // 挪威语（书面）
                Map.entry("ne", "नेपाली"), // 尼泊尔语
                Map.entry("nl", "Nederlands"), // 荷兰语
                Map.entry("pl", "Polski"), // 波兰语
                Map.entry("ps", "پښتو"), // 普什图语
                Map.entry("pt", "Português"), // 葡萄牙语
                Map.entry("ro", "Română"), // 罗马尼亚语
                Map.entry("ru", "Русский"), // 俄语
                Map.entry("si", "සිංහල"), // 僧伽罗语
                Map.entry("sk", "Slovenčina"), // 斯洛伐克语
                Map.entry("sl", "Slovenščina"), // 斯洛文尼亚语
                Map.entry("so", "Soomaali"), // 索马里语
                Map.entry("sq", "Shqip"), // 阿尔巴尼亚语
                Map.entry("sr", "Српски"), // 塞尔维亚语
                Map.entry("su", "Basa Sunda"), // 巽他语
                Map.entry("sv", "Svenska"), // 瑞典语
                Map.entry("sw", "Kiswahili"), // 斯瓦希里语
                Map.entry("ta", "தமிழ்"), // 泰米尔语
                Map.entry("te", "తెలుగు"), // 泰卢固语
                Map.entry("th", "ไทย"), // 泰语
                Map.entry("tr", "Türkçe"), // 土耳其语
                Map.entry("uk", "Українська"), // 乌克兰语
                Map.entry("ur", "اردو"), // 乌尔都语
                Map.entry("uz", "Oʻzbekcha"), // 乌兹别克语
                Map.entry("vi", "Tiếng Việt"), // 越南语
                Map.entry("zh", "中文"), // 中文
                Map.entry("zu", "isiZulu") // 祖鲁语
        );
        LANG_NAME_TO_HEADCODE_MAPPING = new HashMap<>();
        for (Map.Entry<String, String> entry : LANG_HEADCODE_TO_NAME_MAPPING.entrySet()) {
            LANG_NAME_TO_HEADCODE_MAPPING.put(entry.getValue(), entry.getKey());
        }
    }


    private static String genSecMsGec() {
        long currentTime = System.currentTimeMillis();
        long ticks = (long) ((currentTime / 1000.0) + 11644473600L) * 10000000L;
        long roundedTicks = ticks - (ticks % 3000000000L);
        String strToHash = roundedTicks + "6A5AA1D4EAFF4E9FB37E23D68491D6F4";

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(strToHash.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateSec_MS_GEC() {

        String SEC_MS_GEC = genSecMsGec();
        String SEC_MS_GEC_Version = "1-131.0.2903.112";

        URL = String.format("wss://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4&Sec-MS-GEC=%s&Sec-MS-GEC-Version=%s", SEC_MS_GEC, SEC_MS_GEC_Version);
    }

    public static String getEdgeTTSURL() {
        updateSec_MS_GEC();
        return URL;
    }


    public static class I18nKeyMaps {
        public static final Map<String, String> CONTEXT = new HashMap<>();
        public static final Map<String, String> SOURCES = new HashMap<>();

        static {
            CONTEXT.put("new file", "context.menu.new_file");
            CONTEXT.put("new folder", "context.menu.new_folder");
            CONTEXT.put("copy", "context.menu.copy");
            CONTEXT.put("cut", "context.menu.cut");
            CONTEXT.put("paste", "context.menu.paste");
            CONTEXT.put("paste append", "context.menu.paste_append");
            CONTEXT.put("rename", "context.menu.rename");
            CONTEXT.put("move up", "context.menu.move_up");
            CONTEXT.put("move down", "context.menu.move_down");
            CONTEXT.put("expand all", "context.menu.expand_all");
            CONTEXT.put("clear", "context.menu.clear");
            CONTEXT.put("delete", "context.menu.delete");
            CONTEXT.put("undo", "context.menu.undo");
            CONTEXT.put("select all", "context.menu.select_all");

            CONTEXT.put("file popup", "context.menu.new_file");
            CONTEXT.put("folder popup", "context.menu.new_folder");
            CONTEXT.put("rename popup", "context.menu.rename");


            sources.forEach((key, source) -> SOURCES.put(key, source.getNameSpace()));
        }
    }
}