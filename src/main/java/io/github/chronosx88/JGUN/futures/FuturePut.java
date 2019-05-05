package io.github.chronosx88.JGUN.futures;

public class FuturePut extends BaseFuture<FuturePut> {
    public FuturePut(String id) {
        super(id);
        self(this);
    }

    public FuturePut done(boolean success) {
        synchronized (lock) {
            if(success) {
                this.type = FutureType.OK;
            } else {
                this.type = FutureType.FAILED;
            }

            if (!completedAndNotify()) {
                return this;
            }
        }
        notifyListeners();
        return this;
    }
}
