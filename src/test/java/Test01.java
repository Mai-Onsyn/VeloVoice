import mai_onsyn.VeloVoice2.Text.TextUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test01 {

    public static void main(String[] args) {
        String input = "This is a test string with some test patterns.";
        String regex = "test"; // 你的正则表达式
        int t = 10; // 从第 t 位之后开始查找

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);
        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(input);

        // 从第 t 位之后开始查找
        if (matcher.find(t)) {
            int start = matcher.start(); // 匹配项的起始索引
            int end = matcher.end();     // 匹配项的终止索引
            System.out.println("匹配项起始索引: " + start);
            System.out.println("匹配项终止索引: " + end);
            System.out.println("匹配项内容: " + input.substring(start, end));
        } else {
            System.out.println("未找到匹配项");
        }
    }
}