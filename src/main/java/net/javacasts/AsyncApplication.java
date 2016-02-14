package net.javacasts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AsyncApplication implements ApplicationRunner {

    @Autowired
    private AsyncProcessor processor;

    public static void main(String[] args) {
        SpringApplication.run(AsyncApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        for (int i = 0; i < 4; i++) {
            System.out.println(processor.longTimeRunningMethod());
        }
    }
}
