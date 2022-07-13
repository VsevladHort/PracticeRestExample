package rest_api;

import com.sun.net.httpserver.*;
import dao.DBService;
import jakarta.xml.bind.DatatypeConverter;
import rest_api.utils.JWTUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RestHttpServer {
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());
    private static final String USERNAME = "ee11cbb19052e40b07aac0ca060c23ee";
    private static final String PASSWORD = "1a1dc91c907325c69271ddf0c944bc72";

    static {
        try {
            LOGGER.addHandler(new FileHandler("logs/restserverlogs.txt"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Could not add File handler for processor logging");
        }
    }

    public static void main(String... args) {
        RestHttpServer restHttpServer = new RestHttpServer();
        restHttpServer.start();
    }

    private JWTUtils jwtUtils;
    HttpServer server;
    ExecutorService executor;

    public RestHttpServer() {
        try {
            byte[] b = new byte[16];
            new SecureRandom().nextBytes(b);
            jwtUtils = new JWTUtils(new String(b));
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8765), 0);

            HttpContext contextLogin = server.createContext("/login", new LoginHandler());
            HttpContext contextGood = server.createContext("/api/good/", new GoodHandler());
            HttpContext contextShutdown = server.createContext("/shutdown", new ShutdownHandler());
            contextLogin.setAuthenticator(new Auth());

            executor = Executors.newFixedThreadPool(10);
            server.setExecutor(executor);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
        executor.shutdown();
    }

    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var request = exchange.getRequestBody();
            String body = new String(request.readAllBytes());
            var pairs = Arrays.stream(body.split("&")).filter(it -> !it.isEmpty()).collect(Collectors.toList());
            if (pairs.size() == 2) {
                if (pairs.get(0).split("=")[1].equals(USERNAME) && pairs.get(1).split("=")[1].equals(PASSWORD)) {
                    String token = jwtUtils.createJWT("User", "LoginHandler", "authToken",
                            300_000);
                    exchange.sendResponseHeaders(200, token.getBytes(StandardCharsets.UTF_8).length);
                    exchange.getResponseBody().write(token.getBytes(StandardCharsets.UTF_8));
                } else {
                    exchange.sendResponseHeaders(401, -1);
                }
            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        }
    }

    class GoodHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder builder = new StringBuilder();

            builder.append("HIIIII");

            byte[] bytes = builder.toString().getBytes();
            exchange.sendResponseHeaders(200, bytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    class ShutdownHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            stop();
            exchange.sendResponseHeaders(204, -1);
        }
    }

    class Auth extends Authenticator {
        @Override
        public Result authenticate(HttpExchange httpExchange) {
            if (!"/login".equals(httpExchange.getRequestURI().toString())) {
                String token = jwtUtils.parseJWT(httpExchange.getRequestHeaders().getFirst("Auth"));
                if (token != null)
                    return new Success(new HttpPrincipal(token, "api"));
                else
                    return new Failure(403);
            } else
                return new Success(new HttpPrincipal("userTryingToLogIn", "login"));
        }
    }
}
