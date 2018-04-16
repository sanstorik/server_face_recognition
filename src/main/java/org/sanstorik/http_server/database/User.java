package org.sanstorik.http_server.database;

public class User {
    private String username;
    private String password;
    private String imageUrl;
    private String jsonUrl;
    private int userId;


    public User(String username, String password, String imageUrl, String jsonUrl, int userId) {
        this.username = username;
        this.password = password;
        this.imageUrl = imageUrl;
        this.jsonUrl = jsonUrl;
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public String getJsonUrl() {
        return jsonUrl;
    }


    public int getUserId() {
        return userId;
    }
}
