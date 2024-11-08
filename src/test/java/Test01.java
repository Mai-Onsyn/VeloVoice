public class Test01 {


    public static void main(String[] args) {
        System.out.println(testIsDividable(1.0, 0.1));
        System.out.println(testIsDividable(3.3, 0.3));
        System.out.println(testIsDividable(5.0, 0.5));
    }

    private static boolean testIsDividable(double a, double b) {
        long aa = (long) (a * 0x100000000L);
        long bb = (long) (b * 0x100000000L);

        return aa % bb < 0x100000000L;
    }
}