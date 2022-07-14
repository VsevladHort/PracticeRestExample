package rest_api;

import com.sun.net.httpserver.*;
import dao.DBService;
import dao.Dao;
import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.utils.GoodJsonConverter;
import org.json.JSONObject;
import rest_api.utils.AuthToken;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RestHttpServer {
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());
//    private static final String USERNAME = "ee11cbb19052e40b07aac0ca060c23ee";
//    private static final String PASSWORD = "1a1dc91c907325c69271ddf0c944bc72";

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

    private AuthToken jwtUtils;
    HttpServer server;
    ExecutorService executor;

    public RestHttpServer() {
        try {
            byte[] b = new byte[16];
            new SecureRandom().nextBytes(b);
            byte[] encoded = Base64.getEncoder().encode(b);
            jwtUtils = new AuthToken(new String(encoded));
            server = HttpServer.create();
            server.bind(new InetSocketAddress(1337), 0);

            HttpContext contextLogin = server.createContext("/login", new LoginHandler());
            HttpContext contextGood = server.createContext("/api/good", new GoodHandler());
            server.createContext("/shutdown", new ShutdownHandler());
            contextLogin.setAuthenticator(new Auth());
            contextGood.setAuthenticator(new Auth());

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
        if (executor != null)
            executor.shutdown();
    }

    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(401, -1);
                return;
            }
            var request = exchange.getRequestBody();
            try {
                DBService.initializeConnection("test");
            } catch (DaoWrapperException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            Dao dao = null;
            try {
                dao = new DBService("test");
            } catch (DaoWrapperException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
            String body = new String(request.readAllBytes());
            var pairs = Arrays.stream(body.split("&")).filter(it -> !it.isEmpty()).collect(Collectors.toList());
            if (pairs.size() == 2) {
                try {
                    if (dao != null) {
                        if (dao.getUsernames().contains(pairs.get(0).split("=")[1]) &&
                                pairs.get(1).split("=")[1].equals(dao.getUserPass(pairs.get(0).split("=")[1]))) {
                            String token = jwtUtils.createJWT("User", "LoginHandler", "authToken",
                                    300_000);
                            exchange.sendResponseHeaders(200, token.getBytes(StandardCharsets.UTF_8).length);
                            exchange.getResponseBody().write(token.getBytes(StandardCharsets.UTF_8));
                        } else {
                            exchange.sendResponseHeaders(401, -1);
                        }
                    }
                } catch (DaoWrapperException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            } else {
                exchange.sendResponseHeaders(401, -1);
            }
        }
    }

    static class GoodHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            var uri = exchange.getRequestURI().toString();
            var index = uri.indexOf("/api/good") + "/api/good".length() + 1;
            if (index >= uri.length())
                uri = uri + '/';
            var dangle = uri.substring(index);
            Dao dao;
            try {
                DBService.initializeConnection("test");
                dao = new DBService("test");
            } catch (DaoWrapperException e) {
                exchange.sendResponseHeaders(500, -1);
                return;
            }
            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    if (dangle.isEmpty()) {
                        exchange.sendResponseHeaders(404, -1);
                    } else {
                        try {
                            Good good = dao.getGood(dangle);
                            if (good != null) {
                                OutputStream os = exchange.getResponseBody();
                                byte[] write = GoodJsonConverter.toJson(good).getBytes(StandardCharsets.UTF_8);
                                exchange.sendResponseHeaders(200, write.length);
                                os.write(write);
                                os.close();
                            } else {
                                exchange.sendResponseHeaders(404, -1);
                            }
                        } catch (DaoWrapperException e) {
                            exchange.sendResponseHeaders(500, -1);
                        }
                    }
                }
                case "PUT" -> {
                    String putJson = new String(exchange.getRequestBody().readAllBytes());
                    JSONObject jsonObject = new JSONObject(putJson);
                    var name = jsonObject.getString("name");
                    var desc = jsonObject.getString("description");
                    var producer = jsonObject.getString("producer");
                    var price = jsonObject.getDouble("price");
                    var amount = jsonObject.getInt("amount");
                    var group = jsonObject.getString("group");
                    if (name.isEmpty() || price < 0 || amount < 0 || group.isEmpty()) {
                        exchange.sendResponseHeaders(409, -1);
                        return;
                    }
                    Good good = new Good(name);
                    good.setDescription(desc);
                    good.setProducer(producer);
                    good.setAmount(amount);
                    good.setPrice(price);
                    try {
                        if (dao.getGroup(group) == null) {
                            exchange.sendResponseHeaders(404, -1);
                            return;
                        }
                        dao.createGood(group, good);
                    } catch (DaoWrapperException e) {
                        exchange.sendResponseHeaders(500, -1);
                    }
                    byte[] value = good.getName().getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(201, value.length);
                    exchange.getResponseBody().write(value);
                }
                case "DELETE" -> {
                    if (dangle.isEmpty()) {
                        exchange.sendResponseHeaders(404, -1);
                    } else {
                        try {
                            if (dao.deleteGood(dangle)) {
                                exchange.sendResponseHeaders(204, -1);
                            } else {
                                exchange.sendResponseHeaders(404, -1);
                            }
                        } catch (DaoWrapperException e) {
                            exchange.sendResponseHeaders(500, -1);
                        }
                    }
                }
                case "POST" -> {
                    if (dangle.isEmpty()) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    String putJson = new String(exchange.getRequestBody().readAllBytes());
                    JSONObject jsonObject = new JSONObject(putJson);
                    var name = jsonObject.getString("name");
                    var desc = jsonObject.getString("description");
                    var producer = jsonObject.getString("producer");
                    var price = jsonObject.getDouble("price");
                    var amount = jsonObject.getInt("amount");
                    if (name.isEmpty() || price < 0 || amount < 0) {
                        exchange.sendResponseHeaders(409, -1);
                        return;
                    }
                    Good good;
                    try {
                        good = dao.getGood(name);
                    } catch (DaoWrapperException e) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    good.setDescription(desc);
                    good.setProducer(producer);
                    good.setAmount(amount);
                    good.setPrice(price);
                    try {
                        dao.updateGood(good);
                    } catch (DaoWrapperException e) {
                        exchange.sendResponseHeaders(500, -1);
                        return;
                    }
                    byte[] value = good.getName().getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(201, value.length);
                    exchange.getResponseBody().write(value);
                }
                default -> exchange.sendResponseHeaders(404, -1);
            }
        }
    }

    class ShutdownHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(204, -1);
            stop();
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
