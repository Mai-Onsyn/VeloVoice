import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import mai_onsyn.VeloVoice2.NetWork.Item.LocalTXT;

import java.util.Arrays;
import java.util.List;

public class Test01 {

    public static void main(String[] args) {

        JSONObject o1 = JSONObject.parseObject("{\"a\": \"\\n\"}");
        System.out.println(Arrays.toString(o1.getString("a").toCharArray()));

        String jsonString = JSONObject.toJSONString(o1, true);

        System.out.println(jsonString);

        JSONObject o2 = JSONObject.parseObject(jsonString);
        System.out.println(Arrays.toString(o2.getString("a").toCharArray()));
    }
}