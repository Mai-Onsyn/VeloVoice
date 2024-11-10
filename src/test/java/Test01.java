public class Test01 {


    public static void main(String[] args) {
        System.out.println(checkFloat(""));
        System.out.println(checkFloat("1.0"));
        System.out.println(checkFloat("1"));
        System.out.println(checkFloat("."));
        System.out.println(checkFloat(".2"));
        System.out.println(checkFloat("1."));
        System.out.println(checkFloat("-"));
        System.out.println(checkFloat("-1.0"));
        System.out.println(checkFloat("-1"));
        System.out.println(checkFloat("-."));
        System.out.println(checkFloat("-.2"));
        System.out.println(checkFloat("-1."));
        System.out.println(checkFloat("-1.2"));

        System.out.println(checkFloat("-1..2"));
        System.out.println(checkFloat("1..2"));
        System.out.println(checkFloat("1.."));

    }

    private static boolean testIsDividable(double a, double b) {
        long aa = (long) (a * 0x100000000L);
        long bb = (long) (b * 0x100000000L);

        return aa % bb < 0x100000000L;
    }

    /**
     * 0 - readable number;
     * 1 - empty;
     * 2 - not a number;
     * 3 - only a minus sign;
     */
    private static int checkInteger(String s) {
        System.out.print(s + " : ");
        if (s == null || s.isEmpty()) {
            return 1;
        }

        // 判断是否只包含负号
        if (s.equals("-")) {
            return 3;
        }

        // 判断是否是负数且后面跟着数字
        if (s.matches("-\\d+")) {
            return 0;
        }

        // 判断是否是纯数字
        if (s.matches("\\d+")) {
            return 0;
        }

        // 如果不符合以上任何条件，则为无效输入
        return 2;
    }


    /**
     * 0 - readable number;
     * 1 - empty;
     * 2 - not a number;
     * 3 - only a minus sign;
     * 4 - start with number and end with dot;
     * 5 - a number is sandwiched between a minus sign and a dot;
     */
    private static int checkFloat(String s) {
        System.out.print(s + " : ");
        if (s == null || s.isEmpty()) {
            return 1;
        }

        // 判断是否只有负号
        if (s.equals("-")) {
            return 3;
        }

        // 判断是否是负数的浮动数（例如 -12.34）
        if (s.matches("-?\\d+\\.\\d+$")) {
            return 0;
        }

        // 判断是否是正数的浮动数（例如 12.34）
        if (s.matches("\\d+\\.\\d+$")) {
            return 0;
        }

        // 判断是否是整数（正整数或负整数）
        if (s.matches("-?\\d+$")) {
            return 0;
        }

        // 判断是否是负号后接数字和点
        if (s.matches("-\\d+\\.$")) {
            return 5;
        }

        // 判断是否是正号后接数字和点
        if (s.matches("\\d+\\.$")) {
            return 4;
        }

        // 判断无效输入
        return 2;
    }



}