package net.javacasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsyncApplication implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory
            .getLogger(AsyncApplication.class);

    @Autowired
    private AsyncProcessor processor;

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        long started = System.nanoTime();
        for (int i = 0; i < 4; i++) {
            LOG.info(processor.longTimeRunningMethod());
        }
        LOG.info("processing took "
                + Math.round((System.nanoTime() - started) / 1000000000)
                + " seconds");
    }
}
