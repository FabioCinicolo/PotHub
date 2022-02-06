package org.pothub.idps;

public class Tokens {

    private String id_token;

    private String refresh_token;

    private String access_token;

    public Tokens(String id_token, String refresh_token, String access_token) {
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.access_token = access_token;
    }
}
