package mai_onsyn.VeloVoice.Text;

import com.ibm.icu.text.CharsetDetector;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLoader {

    public static final Map<String, String> ENTITIES = new HashMap<>();

    static {
        ENTITIES.put("9834", "\u266A"); // ♪
        ENTITIES.put("65532", "\uFFFD"); // 替换字符（通常用于表示无法识别的字符）
        ENTITIES.put("10084", "\u2744"); // ❄
        ENTITIES.put("10045", "\u2212"); // ➖
        ENTITIES.put("8711", "\u2204"); // ∄
        ENTITIES.put("9839", "\u266B"); // ♩
        ENTITIES.put("9829", "\u2665"); // ♥
        ENTITIES.put("65279", "\uFEFF"); // 零宽不间断空格
        ENTITIES.put("12316", "\u2248"); // ≈
        ENTITIES.put("18487", "\u300A"); // 《
        ENTITIES.put("9327", "\u25AA"); // ▪
        ENTITIES.put("9825", "\u2764"); // ♡
    }

    public static String load(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            CharsetDetector detector = new CharsetDetector();
            detector.setText(fileContent);

            //return Files.readString(file.toPath(), Charset.forName(detector.detect().getName()));
            return decodeHtmlEntities(new String(fileContent, Charset.forName(detector.detect().getName())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String decodeHtmlEntities(String input) {
        Pattern pattern = Pattern.compile("&#(\\d+);");
        Matcher matcher = pattern.matcher(input);

        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String number = matcher.group(1);
            if (ENTITIES.containsKey(number)) {
                String replacement = ENTITIES.get(number);
                matcher.appendReplacement(result, replacement);
            } else {
                // 如果映射中不存在该实体，可以选择保留原样或进行其他处理
                matcher.appendReplacement(result, matcher.group());
            }
        }

        matcher.appendTail(result);

        return result.toString();
    }

}
