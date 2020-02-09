package com.adbhut.web;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GithubLookupService {
    private final RestTemplate restTemplate;
    private Executor executors;

    public GithubLookupService(RestTemplateBuilder restTemplateBuilder, Executor executors) {
       // use this line when testing this by connecting github api as it may be blocked due api limit.
        //this.restTemplate = restTemplateBuilder.basicAuthentication("usernme", "password").build();
        this.restTemplate = restTemplateBuilder.build();
        this.executors = executors;
    }

    @Async
    public CompletableFuture<User> findUser(String user) {
     //   log.info("Looking up with Spring async" + user);
        User results = lookup(user);
        return CompletableFuture.completedFuture(results);
    }

    public CompletableFuture<User> findUserWithJava(String user) {
        log.info("Looking up ysing java completablefuture" + user);
        return CompletableFuture.supplyAsync(() -> this.lookup(user), executors);
    }

    public User lookup(String user) {
        String url = String.format("https://api.github.com/users/%s", user);
        User results = null;
        try {
            results = restTemplate.getForObject(url, User.class);
            // Artificial delay of 1s for demonstration purposes
            Thread.sleep(1000L);
        } catch (Exception e) {
            log.error("Error thrown", e);
        }
        return results;
    }
}
