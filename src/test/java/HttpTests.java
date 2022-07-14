import dao.DBService;
import dao.Dao;
import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;
import entities.utils.GoodJsonConverter;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.*;
import rest_api.RestHttpsServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

class HttpTests {
    private static RestHttpsServer server;

    @BeforeAll
    static void beforeAll() {
        server = new RestHttpsServer();
        server.start();
    }

    @Test
    void testLoginSuccessful() throws IOException, NoSuchAlgorithmException {
        URL url = new URL("https://localhost:1337/login");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        StringBuilder request = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("pass".getBytes());
        byte[] digest = md.digest();
        String myPass = DatatypeConverter
                .printHexBinary(digest).toLowerCase(Locale.ROOT);
        md.update("user".getBytes());
        byte[] digest1 = md.digest();
        String myName = DatatypeConverter
                .printHexBinary(digest1).toLowerCase(Locale.ROOT);
        request.append("username").append("=").append(myName).append("&").append("password").append("=").append(myPass);
        con.getOutputStream().write(request.toString().getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(con.getInputStream());
        String response = new String(bufferedInputStream.readAllBytes());
        Assertions.assertEquals(200, con.getResponseCode());
        con.disconnect();
        URL url1 = new URL("https://localhost:1337/api/good/");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Auth", response);
        System.out.println(con1.getResponseCode());
    }

    @Test
    void unauthorizedTest() throws IOException {
        URL url1 = new URL("https://localhost:1337/api/good/");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Auth", "sdadfs");
        Assertions.assertEquals(403, con1.getResponseCode());
    }

    @Test
    void authorizedTest() throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Auth", getAuthorization());
        Assertions.assertEquals(404, con1.getResponseCode());
    }

    @Test
    void testKnownGood() throws IOException, NoSuchAlgorithmException, DaoWrapperException {
        Good good = new Good("name", "desc", 10, "producer", 100);
        DBService.initializeConnection("test");
        Dao dao = new DBService("test");
        dao.createGroup(new GoodGroup("1", "1"));
        dao.createGood("1", good);
        URL url1 = new URL("https://localhost:1337/api/good/name");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Auth", getAuthorization());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(con1.getInputStream());
        String json = new String(bufferedInputStream.readAllBytes());
        var gottenGood = GoodJsonConverter.fromJsonGood(json);
        Assertions.assertEquals(good.getName(), gottenGood.getName());
        Assertions.assertEquals(good.getAmount(), gottenGood.getAmount());
        Assertions.assertEquals(good.getProducer(), gottenGood.getProducer());
        Assertions.assertEquals(good.getDescription(), gottenGood.getDescription());
        Assertions.assertEquals(good.getPrice(), gottenGood.getPrice());
        Assertions.assertEquals(200, con1.getResponseCode());
    }

    @Test
    void testCreateGoodPUT() throws IOException, NoSuchAlgorithmException, DaoWrapperException {
        Good good = new Good("name", "desc", 10, "producer", 100);
        DBService.initializeConnection("test");
        Dao dao = new DBService("test");
        dao.createGroup(new GoodGroup("1", "1"));
        URL url1 = new URL("https://localhost:1337/api/good");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("PUT");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(good, "1").getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(201, con1.getResponseCode());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(con1.getInputStream());
        String json = new String(bufferedInputStream.readAllBytes());
        Assertions.assertEquals(good.getName(), json);
        var gottenGood = dao.getGood(json);
        Assertions.assertEquals(good.getAmount(), gottenGood.getAmount());
        Assertions.assertEquals(good.getProducer(), gottenGood.getProducer());
        Assertions.assertEquals(good.getDescription(), gottenGood.getDescription());
        Assertions.assertEquals(good.getPrice(), gottenGood.getPrice());
        con1.disconnect();
    }

    @Test
    void testUpdateGoodPOST() throws IOException, NoSuchAlgorithmException, DaoWrapperException {
        Good good = new Good("name", "desc", 10, "producer", 100);
        DBService.initializeConnection("test");
        Dao dao = new DBService("test");
        dao.createGroup(new GoodGroup("1", "1"));
        dao.createGood("1", good);
        good.setPrice(100);
        URL url1 = new URL("https://localhost:1337/api/good/name");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("POST");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(good, "1").getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(204, con1.getResponseCode());
        var gottenGood = dao.getGood("name");
        Assertions.assertEquals(good.getAmount(), gottenGood.getAmount());
        Assertions.assertEquals(good.getProducer(), gottenGood.getProducer());
        Assertions.assertEquals(good.getDescription(), gottenGood.getDescription());
        Assertions.assertEquals(good.getPrice(), gottenGood.getPrice());
        con1.disconnect();
    }

    @Test
    void testDeleteGoodDELETE() throws IOException, NoSuchAlgorithmException, DaoWrapperException {
        Good good = new Good("name", "desc", 10, "producer", 100);
        DBService.initializeConnection("test");
        Dao dao = new DBService("test");
        dao.createGroup(new GoodGroup("1", "1"));
        dao.createGood("1", good);
        URL url1 = new URL("https://localhost:1337/api/good/name");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("DELETE");
        con1.setRequestProperty("Auth", getAuthorization());
        Assertions.assertEquals(204, con1.getResponseCode());
        Assertions.assertNull(dao.getGood("name"));
        con1.disconnect();
    }

    @Test
    void testGetUnknownGood() throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL(null, "https://localhost:1337/api/good/1");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Auth", getAuthorization());
        Assertions.assertEquals(404, con1.getResponseCode());
    }


    private String getAuthorization() throws IOException, NoSuchAlgorithmException {
        URL url = new URL("https://localhost:1337/login");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        StringBuilder request = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update("pass".getBytes());
        byte[] digest = md.digest();
        String myPass = DatatypeConverter
                .printHexBinary(digest).toLowerCase(Locale.ROOT);
        md.update("user".getBytes());
        byte[] digest1 = md.digest();
        String myName = DatatypeConverter
                .printHexBinary(digest1).toLowerCase(Locale.ROOT);
        request.append("username").append("=").append(myName).append("&").append("password").append("=").append(myPass);
        con.getOutputStream().write(request.toString().getBytes(StandardCharsets.UTF_8));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(con.getInputStream());
        var res = new String(bufferedInputStream.readAllBytes());
        con.disconnect();
        return res;
    }

    @AfterAll
    static void afterAll() throws IOException {
        URL url = new URL("https://localhost:1337/shutdown");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
    }

    @AfterEach
    void afterEach() {
        var con = DBService.getConnection();
        try (PreparedStatement select1 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS users;
                                     """
                     );
             PreparedStatement select2 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS goods;
                                     """
                     );
             PreparedStatement select3 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS good_groups;
                                     """
                     )) {
            select1.executeUpdate();
            select2.executeUpdate();
            select3.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
