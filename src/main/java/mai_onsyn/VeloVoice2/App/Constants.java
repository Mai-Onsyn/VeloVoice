package mai_onsyn.VeloVoice2.App;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {

    private static String URL;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.74 Safari/537.36 Edg/99.0.1150.55";
    public static final String ORIGIN = "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold";
    public static final String VOICES_LIST_URL = "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?trustedclienttoken=6A5AA1D4EAFF4E9FB37E23D68491D6F4";

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

}
