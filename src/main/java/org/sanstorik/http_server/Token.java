package org.sanstorik.http_server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class Token {
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private String token;
    private String username;
    private String password;
    private Date expireDate;


    private Token(String token, String username, String password, Date creationTime) {
        this.token = token;
        this.username = username;
        this.password = password;
        this.expireDate = creationTime;
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


    public boolean isExpired() {
        return expireDate.before(new Date());
    }


    public static Token cypherToken(String username, String password) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getenv("TOKEN_KEY"));
            Date expiresAt = new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000);

            String token = JWT.create()
                    .withClaim(USERNAME_KEY, username)
                    .withClaim(PASSWORD_KEY, password)
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

            return new Token(token, username, password, expiresAt);
        } catch (UnsupportedEncodingException | JWTCreationException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Token decypherToken(String token) {
        Token decypheredToken = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getenv("TOKEN_KEY"));

            DecodedJWT decoded = JWT.require(algorithm)
                    .build().verify(token);

            decypheredToken = new Token(
                    token,
                    decoded.getClaim(USERNAME_KEY).toString(),
                    decoded.getClaim(PASSWORD_KEY).toString(),
                    decoded.getExpiresAt()
            );
        } catch (UnsupportedEncodingException | JWTVerificationException e) {
            e.printStackTrace();
        }

        return decypheredToken;
    }
}
