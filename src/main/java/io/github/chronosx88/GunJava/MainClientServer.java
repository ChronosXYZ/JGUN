package io.github.chronosx88.GunJava;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainClientServer {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException {
        Server server = new Server(21334);
        server.start();
        Client client = new Client(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 21334);
        client.connect();
    }
}
