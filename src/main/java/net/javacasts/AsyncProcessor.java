package net.javacasts;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

@Component
public class AsyncProcessor {

    @Async
    public Future<String> longTimeRunningMethod() throws InterruptedException {
        Thread.sleep(2 * 1000);
        return new AsyncResult<String>(Thread.currentThread().getName());
    }

}
