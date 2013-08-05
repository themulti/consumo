package com.at.consumo;

/**
 * Created by at on 8/4/13.
 */
public class ConsumoSettings {
    String username;
    String password;

    public ConsumoSettings(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
