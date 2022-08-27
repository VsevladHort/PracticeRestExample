package rest_api.client;

import entities.Good;
import entities.GoodGroup;
import entities.utils.GoodJsonConverter;
import jakarta.xml.bind.DatatypeConverter;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RestApiClient {
    public List<GoodGroup> getGroups(int limit) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/group");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Limit", String.valueOf(limit));
        con1.setRequestProperty("Auth", getAuthorization());
        String response = new String(con1.getInputStream().readAllBytes());
        return Arrays.stream(response.split(";;;")).filter(it -> !it.isEmpty())
                .map(GoodJsonConverter::fromJsonGroup).sorted().collect(Collectors.toList());
    }

    public GoodGroup getGroup(String name) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/group/" + name);
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Limit", String.valueOf(Integer.MAX_VALUE));
        con1.setRequestProperty("Auth", getAuthorization());
        String response = new String(con1.getInputStream().readAllBytes());
        var result = Arrays.stream(response.split(";;;")).filter(it -> !it.isEmpty())
                .map(GoodJsonConverter::fromJsonGroup).sorted().collect(Collectors.toList());
        if (result.isEmpty())
            return null;
        return result.get(0);
    }

    public boolean updateGroup(GoodGroup group) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/group/");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("POST");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(group).getBytes(StandardCharsets.UTF_8));
        var res = 204 == con1.getResponseCode();
        con1.disconnect();
        return res;
    }

    public boolean deleteGroup(String name) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/group/" + name);
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("DELETE");
        con1.setRequestProperty("Auth", getAuthorization());
        return 204 == con1.getResponseCode();
    }

    public boolean addGroup(GoodGroup group) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/group");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("PUT");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(group).getBytes(StandardCharsets.UTF_8));
        if (con1.getResponseCode() == 201) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(con1.getInputStream());
            String json = new String(bufferedInputStream.readAllBytes());
            con1.disconnect();
            return json.equals(group.getName());
        }
        return false;
    }

    public List<Good> getGoods(String groupName, int limit) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Limit", String.valueOf(limit));
        con1.setRequestProperty("Group", groupName);
        con1.setRequestProperty("Auth", getAuthorization());
        String response = new String(con1.getInputStream().readAllBytes());
        return Arrays.stream(response.split(";;;")).filter(it -> !it.isEmpty())
                .map(GoodJsonConverter::fromJsonGood).sorted().collect(Collectors.toList());
    }

    public List<Good> getGoods(int limit) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Limit", String.valueOf(limit));
        con1.setRequestProperty("Group", "");
        con1.setRequestProperty("Auth", getAuthorization());
        String response = new String(con1.getInputStream().readAllBytes());
        return Arrays.stream(response.split(";;;")).filter(it -> !it.isEmpty())
                .map(GoodJsonConverter::fromJsonGood).sorted().collect(Collectors.toList());
    }

    public Good getGood(String name) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good/" + name);
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("GET");
        con1.setRequestProperty("Limit", String.valueOf(Integer.MAX_VALUE));
        con1.setRequestProperty("Group", "");
        con1.setRequestProperty("Auth", getAuthorization());
        if (con1.getResponseCode() == 200) {
            String response = new String(con1.getInputStream().readAllBytes());
            var result = Arrays.stream(response.split(";;;")).filter(it -> !it.isEmpty())
                    .map(GoodJsonConverter::fromJsonGood).sorted().collect(Collectors.toList());
            if (result.isEmpty())
                return null;
            return result.get(0);
        } else
            return null;
    }

    public boolean addGood(String groupName, Good good) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("PUT");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(good, groupName).getBytes(StandardCharsets.UTF_8));
        if (con1.getResponseCode() == 201) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(con1.getInputStream());
            String json = new String(bufferedInputStream.readAllBytes());
            con1.disconnect();
            return good.getName().equals(json);
        }
        return false;
    }

    public boolean deleteGood(String name) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good/" + name);
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setRequestMethod("DELETE");
        con1.setRequestProperty("Auth", getAuthorization());
        var result = 204 == con1.getResponseCode();
        con1.disconnect();
        return result;
    }

    public boolean updateGood(Good good) throws IOException, NoSuchAlgorithmException {
        URL url1 = new URL("https://localhost:1337/api/good/name");
        HttpsURLConnection con1 = (HttpsURLConnection) url1.openConnection();
        con1.setDoOutput(true);
        con1.setRequestMethod("POST");
        con1.setRequestProperty("Auth", getAuthorization());
        con1.getOutputStream().write(GoodJsonConverter.toJson(good).getBytes(StandardCharsets.UTF_8));
        var result = 204 == con1.getResponseCode();
        con1.disconnect();
        return result;
    }

    public void requestServerShutdown() throws IOException {
        URL url = new URL("https://localhost:1337/shutdown");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        System.out.println(con.getResponseCode());
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
}
