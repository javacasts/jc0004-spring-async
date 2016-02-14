Async calls with spring
=======================

What do you need async calls for?
---------------------------------

Imagine you have a site comparing different prices of different online-shops.
It takes around 2 seconds to fetch the prices of each site. You start small,
compair the prices of three online shops, so it will take around 6 seconds for
each product, if you make them one by one. When you grow and check the prices
of 10 shots it will already take you 20 seconds. That's a long time. The thing
is there's no need to wait until the first shop has finished to check the next
one, as you do not need the result of ony of thos to get the results of the
others.  
So instead of running them one by one, you can run them in parallel. Assuming
enough power an network-bandwidth it doesn't matter how many shops you query,
when you query them in parallel the longes will define the time it takes for
the whole result.

A simple example
----------------

We have a little processor that just returns the thread-name after a while (2
seconds in [this example][src]).

```java
public class AsyncProcessor {

    public String longTimeRunningMethod() throws InterruptedException {
        Thread.sleep(2 * 1000);
        return Thread.currentThread().getName();
    }

}
```

We run this using the following code:

```java
    public void run(ApplicationArguments arg0) throws Exception {
        long started = System.nanoTime();
        for (int i = 0; i < 4; i++) {
            LOG.info(processor.longTimeRunningMethod());
        }
        LOG.info("processing took "
                + Math.round((System.nanoTime() - started) / 1000000000)
                + " seconds");
    }
```

This will run the `longTimeRunningMethod` 4 times. The results will be logged
out directly. We also take the time before running `longTimeRunningMethod` 4
times, and write out how long this takes.

When we run it, we see that 4 times the thread-name `main` is written to the
console. The whole application took 8 seconds.

Implement parallel processing
-----------------------------

Luckily spring does provide us with a simple solution to run methods
asynchronous.  First spring needs to be told to use async, this is done by
annotation the application with `@EnableAsync`, then the method is annotated
with `@Async`.  There's only one thing left to do. The method now needs to
return a `Future`.  Future is only a interface, so we will return a
`AsyncReuslt`.  
If we run it like this nothing will have changed. This is because we try to get
the result of each request before starting the next request. So we need to
first start all the queries, then process the result.

We end up with this code to run [the example][async]:

```java
    @Async
    public Future<String> longTimeRunningMethod() throws InterruptedException {
        Thread.sleep(2 * 1000);
        return new AsyncResult<String>(Thread.currentThread().getName());
    }
```

```java
    public void run(ApplicationArguments arg0) throws Exception {
        List<Future<String>> results = new ArrayList<Future<String>>();
        long started = System.nanoTime();
        for (int i = 0; i < 4; i++) {
            results.add(processor.longTimeRunningMethod());
        }
        for (Future<String> result : results) {
            LOG.info(result.get());
        }
        LOG.info("processing took "
                + Math.round((System.nanoTime() - started) / 1000000000)
                + " seconds");
    }
```

If we now run the application, it finishes in 2 seconds.

Configure the Async processor
-----------------------------

As you've seen, the response is also different. At the beginning all calls were
processed in the `main`-thread. Now it's processed in a thread for each call.
Actually with the default-configuration, there's no limit and any call will
start a new Thread that is created. So if you want to use it in a real-live
application you should configure the Execution of asynchronous calls.  
To make this, create a implementation of the `AsyncConfigurer`. It needs you to
provide a `Executor` and an `AsyncUncaughtExceptionHandler`.  
This is how it could [look like][configured].

```java
@Configuration
public class AsyncConfiguration implements AsyncConfigurer {

    private static final Logger LOG = LoggerFactory
            .getLogger(AsyncConfiguration.class);

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setCorePoolSize(3);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            @Override
            public void handleUncaughtException(Throwable throwable,
                    Method method, Object... obj) {
                LOG.error("Exception message:  " + throwable.getMessage());
                LOG.error("Method name:        " + method.getName());
                for (Object param : obj) {
                    LOG.error("Parameter value:    " + param);
                }
            }
        };
    }

}
```

If you start again, it will take 4 seconds this time, because the Threadpool is
limited to 3 Threads. so running 4 times, the fourth is called after the first
three have finished.

[src]:        https://github.com/javacasts/jc0004-spring-async/tree/src "Source example"
[async]:      https://github.com/javacasts/jc0004-spring-async/tree/async "Async example"
[configured]: https://github.com/javacasts/jc0004-spring-async/tree/configured "Configured async example"
