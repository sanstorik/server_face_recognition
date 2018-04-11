package org.sanstorik.http_server;

public class Token {
    private String token = "";
    private String username = "";
    private String password = "";

    public Token(String token, String username, String password) {
        this.token = token;
        this.username = username;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
