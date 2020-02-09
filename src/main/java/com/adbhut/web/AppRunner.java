package com.adbhut.web;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppRunner implements CommandLineRunner {
    private static final List<String> users = Arrays.asList("PivotalSoftware", "CloudFoundry", "Spring-Projects");
    private final GithubLookupService githubLookupService;
    private Executor executors;

    public AppRunner(GithubLookupService githubLookupService, Executor executors) {
        this.githubLookupService = githubLookupService;
        this.executors = executors;
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 5; i++) {
            joinWithSpring(true);
        }

        for (int i = 0; i < 5; i++) {
            joinWithSpring(false);

        }

        for (int i = 0; i < 5; i++) {
            joinWithCompletableFuture(true);
        }

        for (int i = 0; i < 5; i++) {
            joinWithCompletableFuture(false);

        }

    }

    public void joinWithCompletableFuture(boolean warmup) throws InterruptedException, ExecutionException {
        if (warmup) {
            // Kick of multiple, asynchronous lookups
            List<CompletableFuture<User>> userFutures = users.stream()
                    .map(user -> CompletableFuture.supplyAsync(() -> githubLookupService.lookup(user), executors))
                    .collect(toList());
            // Wait until they are all done
            userFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());
        } else {
            // Start the clock
            long start = System.nanoTime();

            List<CompletableFuture<User>> userFutures = users.stream()
                    .map(user -> CompletableFuture.supplyAsync(() -> githubLookupService.lookup(user), executors))
                    .collect(toList());
            // Wait until they are all done
            List<User> results = userFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());

            // Print results, including elapsed time
            long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
            log.info("Java Elapsed time: " + retrievalTime);
            results.forEach(result -> log.info("--> " + result));
        }
    }

    public void joinWithSpring(boolean warmup) throws InterruptedException, ExecutionException {
        if (warmup) {
            // Kick of multiple, asynchronous lookups
            List<CompletableFuture<User>> userFutures = users.stream()
                    .map(githubLookupService::findUser)
                    .collect(toList());

            // Wait until they are all done
            userFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());
            // Wait until they are all done

        } else {
            // Start the clock
            long start = System.nanoTime();

            // Kick of multiple, asynchronous lookups
            List<CompletableFuture<User>> userFutures = users.stream()
                    .map(githubLookupService::findUser)
                    .collect(toList());

            // Wait until they are all done
            // Wait until they are all done
            List<User> results = userFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(toList());

            // Print results, including elapsed time
            long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
            log.info("Spring Elapsed time: " + retrievalTime);
            results.forEach(result -> log.info("--> " + result));
        }
    }

}
