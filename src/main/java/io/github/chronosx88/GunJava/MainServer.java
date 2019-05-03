package io.github.chronosx88.GunJava;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server(5054);
        server.start();
    }
}
