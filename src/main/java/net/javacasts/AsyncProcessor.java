package net.javacasts;

import org.springframework.stereotype.Component;

@Component
public class AsyncProcessor {

    public String longTimeRunningMethod() throws InterruptedException {
        Thread.sleep(2 * 1000);
        return Thread.currentThread().getName();
    }

}
