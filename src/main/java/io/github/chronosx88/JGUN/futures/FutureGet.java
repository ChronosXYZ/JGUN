package io.github.chronosx88.JGUN.futures;

import org.json.JSONObject;

public class FutureGet extends BaseFuture<FutureGet> {
    private JSONObject data;
    private GetStatus getStatus;

    enum GetStatus {
        OK, NOT_FOUND
    }

    public FutureGet(String id) {
        super(id);
        self(this);
    }

    public JSONObject getData() {
        return data;
    }

    @Override
    public boolean isSuccess() {
        synchronized (lock) {
            return completed && (getStatus == GetStatus.OK) && (type == FutureType.OK);
        }
    }

    public FutureGet done(JSONObject data) {
        synchronized (lock) {
            if(data != null) {
                if (!data.isEmpty()) {
                    this.getStatus = GetStatus.OK;
                    this.type = FutureType.OK;
                }
            } else {
                this.getStatus = GetStatus.NOT_FOUND;
                this.type = FutureType.FAILED;
                this.reason = "Not found";
            }

            this.data = data;
            if (!completedAndNotify()) {
                return this;
            }
        }
        notifyListeners();
        return this;
    }
}
