package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.models.GetResult;
import io.github.chronosx88.JGUN.models.requests.GetRequestParams;
import lombok.Getter;

@Getter
public class FutureGet extends BaseCompletableFuture<GetResult> {
    private final GetRequestParams params;

    public FutureGet(String id, GetRequestParams params) {
        super(id);
        this.params = params;
    }
}
