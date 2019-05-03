package io.github.chronosx88.GunJava;

import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException {
        Client client = new Client(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 5054);
        client.connect();
    }
}
