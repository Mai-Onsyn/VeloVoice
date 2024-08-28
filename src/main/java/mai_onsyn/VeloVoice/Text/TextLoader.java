package mai_onsyn.VeloVoice.Text;

import org.jsoup.parser.Parser;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class TextLoader {

    public static String load(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());

            UniversalDetector detector = new UniversalDetector(null);
            detector.handleData(fileContent, 0, fileContent.length);
            detector.dataEnd();

            return decodeHtmlEntities(new String(fileContent, Charset.forName(detector.getDetectedCharset())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //部分小说txt里有html转义符 需要解析
    private static String decodeHtmlEntities(String s) {
        return Parser.unescapeEntities(s, false);
    }

}
