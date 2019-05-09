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
            FuturePut futurePut = gun.get("random").get("dVFtzE9CL").put(new JSONObject().put("hello", "world"));
            boolean success = futurePut.await();
            System.out.println("[FuturePut] Success: " + success);
            FutureGet futureGet = gun.get("random").get("dVFtzE9CL").getData();
            JSONObject result = futureGet.await();
            System.out.println("[FutureGet] Result of get: " + result.toString(2));
            FuturePut futurePut1 = gun.get("random").get("dVFtzE9CL").put(new JSONObject().put("hello", "123"));
            System.out.println("[FuturePut1] Putting an item again: " + futurePut1.await());
            FutureGet futureGet1 = gun.get("random").get("dVFtzE9CL").getData();
            JSONObject result1 = futureGet1.await();
            System.out.println("[FutureGet] Result of get: " + result1.toString(2));
            System.out.println("Deleting an item random/dVFtzE9CL");
            gun.get("random").get("dVFtzE9CL").put(null).await();
            JSONObject resultNull = gun.get("random").get("dVFtzE9CL").getData().await();
            if(resultNull == null) {
                System.out.println("Now random/dVFtzE9CL is null!");
            }
            gun.get("random").put(new JSONObject().put("hello", "world")).await();
            System.out.println(gun.get("random").getData().await().toString(2));
        }).start();

    }
}
