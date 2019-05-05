/*
 * Copyright 2019 Thomas Bocek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.chronosx88.JGUN.futures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The base for all BaseFuture implementations. Be aware of possible deadlocks. Never await from a listener. This class
 * is heavily inspired by MINA and Netty.
 *
 * @param <K>
 *            The class that extends BaseFuture and is used to return back the type for method calls.
 * @author Thomas Bocek
 */
public abstract class BaseFuture<K extends BaseFuture> {
    private static final Logger LOG = LoggerFactory.getLogger(BaseFuture.class);

    public enum FutureType {
        INIT, OK, FAILED, CANCEL
    }

    // Listeners that gets notified if the future finished
    private final List<BaseFutureListener<? extends BaseFuture>> listeners = new ArrayList<>(1);

    // While a future is running, the process may add cancellations for faster
    // cancel operations, e.g. cancel connection attempt
    private volatile Cancelable cancel = null;

    private final CountDownLatch listenersFinished = new CountDownLatch(1);

    protected final Object lock;

    // set the ready flag if operation completed
    protected boolean completed = false;

    // by default false, change in case of success. An unfinished operation is
    // always set to failed
    protected FutureType type = FutureType.INIT;

    protected String reason = "unknown";

    private K self;

    private String futureID;

    /**
     * Default constructor that sets the lock object, which is used for synchronization to this instance.
     */
    public BaseFuture(String futureID) {
        this.lock = this;
        this.futureID = futureID;
    }

    /**
     * @param self2
     *            Set the type so that we are able to return it to the user. This is for making the API much more
     *            usable.
     */
    protected void self(final K self2) {
        this.self = self2;
    }

    /**
     * @return The object that stored this object. This is necessary for the builder pattern when using generics.
     */
    protected K self() {
        return self;
    }

    public K await() throws InterruptedException {
        synchronized (lock) {
            while (!completed) {
                lock.wait();
            }
        }
        return self;
    }

    public K awaitUninterruptibly() {
        synchronized (lock) {
            while (!completed) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    LOG.debug("interrupted, but ignoring", e);
                }
            }
        }
        return self;
    }

    public boolean await(final long timeoutMillis) throws InterruptedException {
        return await0(timeoutMillis, true);
    }

    public boolean awaitUninterruptibly(final long timeoutMillis) {
        try {
            return await0(timeoutMillis, false);
        } catch (final InterruptedException e) {
            throw new RuntimeException("This should never ever happen.");
        }
    }

    /**
     * Internal await operation that also checks for potential deadlocks.
     *
     * @param timeoutMillis
     *            The time to wait
     * @param interrupt
     *            Flag to indicate if the method can throw an InterruptedException
     * @return True if this future has finished in timeoutMillis time, false otherwise
     * @throws InterruptedException
     *             If the flag interrupt is true and this thread has been interrupted.
     */
    private boolean await0(final long timeoutMillis, final boolean interrupt) throws InterruptedException {
        final long startTime = (timeoutMillis <= 0) ? 0 : System.currentTimeMillis();
        long waitTime = timeoutMillis;
        synchronized (lock) {
            if (completed) {
                return completed;
            } else if (waitTime <= 0) {
                return completed;
            }
            while (true) {
                try {
                    lock.wait(waitTime);
                } catch (final InterruptedException e) {
                    if (interrupt) {
                        throw e;
                    }
                }
                if (completed) {
                    return true;
                } else {
                    waitTime = timeoutMillis - (System.currentTimeMillis() - startTime);
                    if (waitTime <= 0) {
                        return completed;
                    }
                }
            }
        }
    }

    public boolean isCompleted() {
        synchronized (lock) {
            return completed;
        }
    }

    public boolean isSuccess() {
        synchronized (lock) {
            return completed && (type == FutureType.OK);
        }
    }

    public boolean isFailed() {
        synchronized (lock) {
            // failed means failed or canceled
            return completed && (type != FutureType.OK);
        }
    }

    public boolean isCanceled() {
        synchronized (lock) {
            return completed && (type == FutureType.CANCEL);
        }
    }

    public K failed(final BaseFuture origin) {
        return failed(origin.failedReason());
    }

    public K failed(final String failed, final BaseFuture origin) {
        StringBuilder sb = new StringBuilder(failed);
        return failed(sb.append(" <-> ").append(origin.failedReason()).toString());
    }

    public K failed(final Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        return failed(stringWriter.toString());
    }

    public K failed(final String failed, final Throwable t) {
        if (t == null) {
            return failed("n/a");
        }
        StringBuilder sb = new StringBuilder(failed);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);
        return failed(sb.append(" <-> ").append(stringWriter.toString()).toString());
    }

    public K failed(final String failed) {
        synchronized (lock) {
            if (!completedAndNotify()) {
                return self;
            }
            this.reason = failed;
            this.type = FutureType.FAILED;
        }
        notifyListeners();
        return self;
    }

    public String failedReason() {
        final StringBuffer sb = new StringBuffer("Future (compl/canc):");
        synchronized (lock) {
            sb.append(completed).append("/")
                    .append(", ").append(type.name())
                    .append(", ").append(reason);
            return sb.toString();
        }
    }

    public FutureType type() {
        synchronized (lock) {
            return type;
        }
    }

    /**
     * Make sure that the calling method has synchronized (lock).
     *
     * @return True if notified. It will notify if completed is not set yet.
     */
    protected boolean completedAndNotify() {
        if (!completed) {
            completed = true;
            lock.notifyAll();
            return true;
        } else {
            return false;
        }
    }

    public K awaitListeners() throws InterruptedException {
        boolean wait = false;
        synchronized (lock) {
            while (!completed) {
                lock.wait();
            }
            if(listeners.size() > 0) {
                wait = true;
            }
        }
        if(wait) {
            listenersFinished.await();
        }
        return self;
    }

    public K awaitListenersUninterruptibly() {
        boolean wait = false;
        synchronized (lock) {
            while (!completed) {
                try {
                    lock.wait();
                } catch (final InterruptedException e) {
                    LOG.debug("interrupted, but ignoring", e);
                }
            }
            if(listeners.size() > 0) {
                wait = true;
            }
        }
        while(wait) {
            try {
                listenersFinished.await();
                wait = false;
            } catch (InterruptedException e) {
                LOG.debug("interrupted, but ignoring", e);
            }
        }
        return self;
    }

    public K addListener(final BaseFutureListener<? extends BaseFuture> listener) {
        boolean notifyNow = false;
        synchronized (lock) {
            if (completed) {
                notifyNow = true;
            } else {
                listeners.add(listener);
            }
        }
        // called only once
        if (notifyNow) {
            callOperationComplete(listener);
        }
        return self;
    }

    /**
     * Call operation complete or call fail listener. If the fail listener fails, its printed as a stack trace.
     *
     * @param listener
     *            The listener to call
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void callOperationComplete(final BaseFutureListener listener) {
        try {
            listener.onComplete(this);
        } catch (final Exception e) {
            try {
                listener.onError(this, e);
            } catch (final Exception e1) {
                LOG.error("Unexpected exception in exceptionCaught()", e1);
            }
        }
    }


    /**
     * Always call this from outside synchronized(lock)!
     */
    protected void notifyListeners() {
        // if this is synchronized, it will deadlock, so do not lock this!
        // There won't be any visibility problem or concurrent modification
        // because 'ready' flag will be checked against both addListener and
        // removeListener calls.
        //
        // This method doesn't need synchronization because:
        // 1) This method is always called after synchronized (this) block.
        //    Hence any listener list modification happens-before this method.
        // 2) This method is called only when 'done' is true.  Once 'done'
        //    becomes true, the listener list is never modified - see add/removeListener()
        for (final BaseFutureListener<? extends BaseFuture> listener : listeners) {
            callOperationComplete(listener);
        }

        listeners.clear();
        listenersFinished.countDown();
        // all events are one time events. It cannot happen that you get
        // notified twice
    }

    public K removeListener(final BaseFutureListener<? extends BaseFuture> listener) {
        synchronized (lock) {
            if (!completed) {
                listeners.remove(listener);
            }
        }
        return self;
    }

    public K setCancel(final Cancelable cancel) {
        synchronized (lock) {
            if (!completed) {
                this.cancel = cancel;
            }
        }
        return self;
    }

    public void cancel() {
        synchronized (lock) {
            if (!completedAndNotify()) {
                return;
            }
            this.type = FutureType.CANCEL;
        }
        if(cancel != null) {
            cancel.cancel();
        }
        notifyListeners();
    }

    public String getFutureID() {
        return futureID;
    }
}