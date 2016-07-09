package net.javacasts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
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
        List<Future<String>> results = new ArrayList<Future<String>>();
        long started = System.nanoTime();
        for (int i = 0; i < 4; i++) {
            results.add(processor.longTimeRunningMethod());
        }
        for (Future<String> result : results) {
            LOG.info("Received reponse: " + result.get());
        }
        LOG.info("processing took "
                + Math.round((System.nanoTime() - started) / 1000000000)
                + " seconds");
	System.exit(0);
    }
}
