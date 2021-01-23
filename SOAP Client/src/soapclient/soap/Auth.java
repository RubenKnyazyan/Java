package soapclient.soap;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

public class Auth {

    public Auth(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
//        return this.encodeHexString(this.password);
        return  this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String login;
    private String password;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    String encodeHexString(String Text) {
        return Hex.encodeHexString(Text.getBytes(StandardCharsets.UTF_8));
    }
}
