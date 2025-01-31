import mai_onsyn.VeloVoice2.Text.TextUtil;

import java.util.List;

public class Test01 {

    public static void main(String[] args) {
        String text = "这是一个测试句子。它包含多个标点符号？和！";
        List<String> parts = TextUtil.splitText(text);

        for (String part : parts) {
            System.out.println(part);
        }
    }
}