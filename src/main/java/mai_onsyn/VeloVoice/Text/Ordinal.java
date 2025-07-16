package mai_onsyn.VeloVoice.Text;

public class Ordinal {

    private int value;

    public Ordinal(boolean startByZero) {
        if (startByZero) value = 0;
        else value = 1;
    }

    public int next() {
        return value++;
    }
}
