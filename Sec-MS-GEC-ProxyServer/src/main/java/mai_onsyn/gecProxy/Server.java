package mai_onsyn.gecProxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {
    private final Getter responseBody;
    private final HttpServer server;

    public Server(Getter responseBody, int port) {
        this.responseBody = responseBody;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configureServer();
    }

    // 配置服务器
    private void configureServer() {
        server.createContext("/api", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String response = responseBody.get();

                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            }
        });
    }

    // 启动服务器
    public void start() {
        server.start();
        System.out.println("Server started. Access it at http://localhost:" + server.getAddress().getPort() + "/api");
    }

    // 停止服务器
    public void stop() {
        server.stop(0);
        System.out.println("Server stopped.");
    }
}