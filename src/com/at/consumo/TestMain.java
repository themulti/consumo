package com.at.consumo;

/**
 * Created by at on 8/4/13.
 */
public class TestMain {
    public static void main(String[] args) {
        String password = "vamos saber";
        String password1 = password.replaceAll(".", "*");

        System.out.println(password1);
    }
}
