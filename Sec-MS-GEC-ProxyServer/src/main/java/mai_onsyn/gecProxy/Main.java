package mai_onsyn.gecProxy;

import java.util.Objects;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {

        Getter getter = new Getter();
        Server server = new Server(getter, Constants.serverPort);
        getter.start();

        Thread serverThread = new Thread(server::start, "Server-Thread");

        Thread cmdThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type \"exit\" to close the server");

            while (true) {
                String command = scanner.next();
                if (Objects.equals(command, "exit")) {
                    serverThread.interrupt();
                    server.stop();
                    System.out.println("closing mitmproxy...");
                    getter.stop();
                    break;
                }
            }
        }, "cmd-Thread");

        serverThread.start();
        cmdThread.start();
    }

}
