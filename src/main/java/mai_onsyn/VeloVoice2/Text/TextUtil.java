package mai_onsyn.VeloVoice2.Text;

import mai_onsyn.AnimeFX2.Utls.AXTreeItem;
import mai_onsyn.VeloVoice2.App.Runtime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.parser.Parser;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static mai_onsyn.VeloVoice2.App.Runtime.*;

public class TextUtil {

    private static final Logger log = LogManager.getLogger(TextUtil.class);

    public static List<String> splitText(String text) {

        List<String> lines = new ArrayList<>();
        char[] forceSplitChars = textConfig.getString("ForceSplitChars").toCharArray();
        int lastIndex = 0;
        for (int i = 0; i < text.toCharArray().length; i++) {
            for (char forceSplitChar : forceSplitChars) {
                if (text.charAt(i) == forceSplitChar) {
                    String line = text.substring(lastIndex, i + 1).trim();
                    if (!line.isEmpty()) {
                        lines.add(line);
                    }
                    lastIndex = i + 1;
                }
            }
        }
        lines.add(text.substring(lastIndex).trim());

        List<String> splitText = new ArrayList<>();

        for (String line : lines) {
            collect(splitText, line);
        }

        return splitText;
    }

    public static String load(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            UniversalDetector detector = new UniversalDetector(null);
            detector.handleData(fileContent, 0, fileContent.length);
            detector.dataEnd();

            String detectedCharset = detector.getDetectedCharset();
            String result;
            if (detectedCharset == null) {
                log.warn(String.format("The encoding format of the file \"%s\" cannot be parsed and will be parsed using the default UTF-8", file.getAbsolutePath()));
                result = new String(fileContent, StandardCharsets.UTF_8);
            }
            else result = new String(fileContent, Charset.forName(detectedCharset));
            if (sources.get("LocalTXT").getConfig().getBoolean("ParseHtmlCharacters")) return decodeHtmlEntities(result);
            else return result;
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    public static void save(AXTreeItem root, File folder) {
        List<ExecuteItem> files = ExecuteItem.parseStructure(root, folder);

        for (ExecuteItem target : files) {
            File file = new File(target.folder(), target.name() + ".txt");


            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    if (!file.createNewFile()) {
                        throw new IOException("Could not create file: " + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {

                fos.write(target.text().getBytes(Charset.forName(textConfig.getString("TXTSaveEncoding"))));

                log.debug("Saved File: " + file);

            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        log.info("Save task has been finished");
    }


    //部分小说txt里有html转义符 需要解析
    private static String decodeHtmlEntities(String s) {
        return Parser.unescapeEntities(s, false);
    }

    private static void collect(List<String> parent, String s) {
        if (s.isEmpty()) return;
        if (s.length() < textConfig.getInteger("SplitLength")) {
            parent.add(s);
            return;
        }

        String[] ab = dichotomy(s);
        collect(parent, ab[0]);
        collect(parent, ab[1]);
    }

    private static String[] dichotomy(String s) {
        int halfLength = s.length() / 2;

        for (char c : textConfig.getString("SplitChars").toCharArray()) {
            for (int i = 0; i < halfLength - 2; i++) {
                if (s.charAt(halfLength + i) == c)
                    return new String[] {
                            s.substring(0, halfLength + 1 + i),
                            s.substring(halfLength + 1 + i)
                    };
                if (s.charAt(halfLength - i) == c)
                    return new String[] {
                            s.substring(0, halfLength + 1 - i),
                            s.substring(halfLength + 1 - i)
                    };
            }
        }
        return new String[] {
                s.substring(0, halfLength + 1),
                s.substring(halfLength + 1)
        };
    }

}
