package mai_onsyn.gecProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Getter {

    private final Thread mitmproxyThread;
    private final Thread edgeSwitch;

    private String Sec_MS_GEC = "";
    private String Sec_MS_GEC_Version = "";

    public Getter() {
        mitmproxyThread = new Thread(this::launchMitmproxy, "MITM-Thread");
        edgeSwitch = new Thread(() -> {
            try {
                launchEdge();
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(Constants.flushPeriod);
                    closeEdge();
                    Thread.sleep(500);
                    launchEdge();
                }
                closeEdge();
            } catch (InterruptedException ignored) {}
        }, "EdgeSwitch-Thread");
    }

    public String get() {
        return String.format("""
                {
                    "Sec-MS-GEC": "%s",
                    "Sec-MS-GEC-Version": "%s"
                }
                """, Sec_MS_GEC, Sec_MS_GEC_Version);
    }

    public void stop() {
        mitmproxyThread.interrupt();
        edgeSwitch.interrupt();
    }

    public void start() {
        mitmproxyThread.start();
        edgeSwitch.start();
    }

    public void launchEdge() {
        ProcessBuilder processBuilder = new ProcessBuilder(Constants.edgePath, Constants.htmlPath);

        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeEdge() {
        ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "-im", "msedge.exe");

        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void launchMitmproxy() {


        ProcessBuilder processBuilder = new ProcessBuilder("mitmdump", "--listen-port", String.valueOf(Constants.mitmproxyPort));
        Process mitmproxyProcess = null;

        try {
            processBuilder.redirectErrorStream(true);
            mitmproxyProcess = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(mitmproxyProcess.getInputStream()));
            String line;

            while (!Thread.currentThread().isInterrupted()) {
                if ((line = reader.readLine()) == null) continue;

                if (line.contains("Sec-MS-GEC")) {

                    int index1 = line.indexOf("Sec-MS-GEC=");
                    int index2 = line.indexOf("&Sec-MS-GEC-Version=");
                    int index3 = line.indexOf("&ConnectionId=");

                    Sec_MS_GEC = line.substring(index1 + 11, index2);
                    Sec_MS_GEC_Version = line.substring(index2 + 20, index3);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert mitmproxyProcess != null;
            mitmproxyProcess.destroy();
            closeEdge();
            System.out.println("mitmproxy stopped.");
        }
    }
}
