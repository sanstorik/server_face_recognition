package org.sanstorik.http_server;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class Token {
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String USERID_KEY = "user_id";
    private static RsaJsonWebKey key;

    private String token;
    private String username;
    private String password;
    private int userId;

    static {
        try {
            key = RsaJwkGenerator.generateJwk(2048);
            key.setKeyId("rsa_key");
        } catch (JoseException e) {
            e.printStackTrace();
        }
    }


    private Token(String token, String username, String password, int userId) {
        this.token = token;
        this.username = username;
        this.password = password;
        this.userId = userId;
    }


    public String getToken() {
        return token;
    }


    public String getUsername() {
        return username;
    }


    public int getUserId() {
        return userId;
    }


    public static Token cypherToken(String username, String password, int userId) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Sanstorik");
        claims.setAudience("User");
        claims.setExpirationTimeMinutesInTheFuture(20);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(0.05f);
        claims.setSubject("neuralnetwork");

        claims.setClaim(USERNAME_KEY, username);
        claims.setClaim(PASSWORD_KEY, password);
        claims.setClaim(USERID_KEY, userId);


        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key.getPrivateKey());


        jws.setKeyIdHeaderValue(key.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        Token token = null;
        try {
            token = new Token(jws.getCompactSerialization(),
                    username, password, userId);
        } catch (JoseException e) {
            e.printStackTrace();
        }

        return token;
    }


    public static Token decypherToken(String token) {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("Sanstorik")
                .setExpectedAudience("User")
                .setVerificationKey(key.getKey())
                .setJwsAlgorithmConstraints(
                        new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                                AlgorithmIdentifiers.RSA_USING_SHA256))
                .build();

        Token decypheredToken = null;
        try
        {
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            decypheredToken = new Token(token,
                 jwtClaims.getClaimValue(USERNAME_KEY).toString(),
                 jwtClaims.getClaimValue(PASSWORD_KEY).toString(),
                 Integer.valueOf(jwtClaims.getClaimValue(USERID_KEY).toString())
            );
        } catch (InvalidJwtException e) {
            e.printStackTrace();
        }

        return decypheredToken;
    }
}
