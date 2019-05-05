package io.github.chronosx88.JGUN.futures.builders;

import io.github.chronosx88.JGUN.Dispatcher;
import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.futures.FuturePut;
import org.json.JSONObject;

import java.util.ArrayList;

public class PutBuilder {
    private JSONObject data;
    private ArrayList<String> path;
    private Dispatcher dispatcher;

    public PutBuilder(Dispatcher dispatcher, JSONObject data, ArrayList<String> path) {
        this.dispatcher = dispatcher;
        this.data = data;
        this.path = path;
    }

    public JSONObject getData() {
        return data;
    }

    public ArrayList<String> getPath() {
        return path;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
    }

    public FuturePut build() {
        FuturePut futurePut = new FuturePut(Dup.random());
        dispatcher.addPendingFuture(futurePut);
        dispatcher.sendPutRequest(data);
        return futurePut;
    }
}