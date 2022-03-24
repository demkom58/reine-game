package com.reine.client;

public class ClientStarter {
    public static void main(String[] args) {
        try (Client client = new Client()) {
            client.start();
        }
    }
}
