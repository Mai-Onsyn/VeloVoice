package mai_onsyn.VeloVoice.Text;

import mai_onsyn.AnimeFX.I18N;
import mai_onsyn.AnimeFX.Utls.AXTreeItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
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

import static mai_onsyn.VeloVoice.App.Runtime.*;
import static mai_onsyn.VeloVoice.App.Constants.*;

public class TextUtil {

    private static final Logger log = LogManager.getLogger(TextUtil.class);

    public static List<String> splitText(String text) {

        List<String> lines = new ArrayList<>();
        char[] forceSplitChars = unescape(textConfig.getString("ForceSplitChars")).toCharArray();
        int lastIndex = 0;
        for (int i = 0; i < text.length(); i++) {
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

    public static List<String> splitWrap(String text) {
        String[] split = text.split("\n");
        List<String> lines = new ArrayList<>();
        for (String line : split) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                lines.add(trimmed);
            }
        }
        return lines;
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
                log.warn(I18N.getCurrentValue("log.text_util.warn.charset_not_found"), file.getAbsolutePath());
                result = new String(fileContent, StandardCharsets.UTF_8);
            }
            else result = new String(fileContent, Charset.forName(detectedCharset));
            if (sources.get("LocalTXT").getConfig().getBoolean("ParseHtmlCharacters")) return decodeHtmlEntities(result);
            else return result;
        } catch (Exception e) {
            log.error(I18N.getCurrentValue("log.text_util.error.load_error"), file.getAbsolutePath(), e);
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
                    log.error(I18N.getCurrentValue("log.text_util.error.create_file_failed"), file.getAbsolutePath(), e);
                    throw new RuntimeException(e);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {

                fos.write(target.text().getBytes(Charset.forName(textConfig.getString("TXTSaveEncoding"))));
                log.debug("Saved File: {}", file);

            } catch (IOException e) {
                log.error(I18N.getCurrentValue("log.text_util.error.save_failed"), file.getAbsolutePath(), e);
                throw new RuntimeException(e);
            }
        }
        log.info(I18N.getCurrentValue("log.text_util.info.save_success"));
    }

    public static String unescape(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {
                if (i + 1 < str.length()) {
                    char next = str.charAt(i + 1);
                    switch (next) {
                        case '\\': c = '\\'; i++; break;
                        case '\"': c = '\"'; i++; break;
                        case '\'': c = '\''; i++; break;
                        case 't': c = '\t'; i++; break;
                        case 'b': c = '\b'; i++; break;
                        case 'n': c = '\n'; i++; break;
                        case 'r': c = '\r'; i++; break;
                        case 'f': c = '\f'; i++; break;
                        case 's': c = ' '; i++; break;
                        case 'u':
                            if (i + 5 < str.length()) {
                                String hex = str.substring(i + 2, i + 6);
                                c = (char) Integer.parseInt(hex, 16);
                                i += 5;
                            }
                            break;
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String formatMillisToTime(long milliseconds) {
        long totalSeconds = milliseconds / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String createHtmlChapter(String title, List<String> lines) {
        // 使用XML解析器，更适合epub格式
        Document doc = Jsoup.parse("", "", Parser.xmlParser());
        doc.outputSettings().charset("UTF-8");
        doc.outputSettings().prettyPrint(true);

        // 添加XML声明和DOCTYPE
        doc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        doc.append("<!DOCTYPE html>");

        // 创建HTML元素
        Element html = doc.appendElement("html");
        html.attr("xmlns", "http://www.w3.org/1999/xhtml");
//        html.attr("xmlns:epub", "http://www.idpf.org/2007/ops");
//        html.attr("lang", "zh-CN");
//        html.attr("xml:lang", "zh-CN");

        // Head部分
        Element head = html.appendElement("head");
        head.appendElement("title").text(title);
        head.appendElement("meta").attr("charset", "UTF-8");

        // Body部分
        Element body = html.appendElement("body");
        body.appendElement("h1").text(title);

        // 处理内容行
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty()) {
                body.appendElement("p").text(trimmedLine);
            }
        }

        return doc.html();
    }


    //部分小说txt里有html转义符 需要解析
    private static String decodeHtmlEntities(String s) {
        return Parser.unescapeEntities(s, false);
    }

    private static void collect(List<String> parent, String s) {
        if (s.isEmpty()) return;
        if (s.length() < textConfig.getInteger("SplitThresholds")) {
            parent.add(s);
            return;
        }

        String[] ab = dichotomy(s);
        collect(parent, ab[0]);
        collect(parent, ab[1]);
    }

    private static String[] dichotomy(String s) {
        int halfLength = s.length() / 2;

        for (char c : unescape(textConfig.getString("SplitChars")).toCharArray()) {
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
