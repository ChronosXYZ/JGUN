package io.github.chronosx88.JGUN.examples;

import io.github.chronosx88.JGUN.Gun;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.nodes.GunSuperPeer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class MainClientServer {
    public static void main(String[] args) {
        GunSuperPeer gunSuperNode = new GunSuperPeer(21334);
        gunSuperNode.start();
        new Thread(() -> {
            Gun gun = null;
            try {
                gun = new Gun(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 21334, new InMemoryGraph());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            gun.get("random").on(data -> {
                if(data != null) {
                    System.out.println("New change in \"random\"! " + data.toString(2));
                }
            });
            gun.get("random").get("dVFtzE9CL").on(data -> {
                if(data != null) {
                    System.out.println("New change in \"random/dVFtzE9CL\"! " + data.toString(2));
                } else {
                    System.out.println("Now random/dVFtzE9CL is null!");
                }

            });
            FuturePut futurePut = gun.get("random").get("dVFtzE9CL").put(new JSONObject().put("hello", "world"));
            boolean success = futurePut.await();
            System.out.println("[FuturePut] Success: " + success);
            FuturePut futurePut1 = gun.get("random").get("dVFtzE9CL").put(new JSONObject().put("hello", "123"));
            System.out.println("[FuturePut1] Putting an item again: " + futurePut1.await());
            System.out.println("Deleting an item random/dVFtzE9CL");
            gun.get("random").get("dVFtzE9CL").put(null).await();
            gun.get("random").put(new JSONObject().put("hello", "world")).await();
        }).start();

    }
}
