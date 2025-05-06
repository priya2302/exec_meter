package com.example.java.ExecMeter.service;

import com.example.java.ExecMeter.dto.BenchMarkRequest;
import com.example.java.ExecMeter.dto.BenchMarkResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Slf4j
@Service
public class BenchMarkService {

    private final WebClient webClient = WebClient.builder().build();

    public Mono<BenchMarkResult> runMeter(BenchMarkRequest req) {
        log.info("Request received at service layer");

        if ("http".equalsIgnoreCase(req.getType())) {
            return benchMarkHttpEndpoint(req);
        } else {
            return benchMarkMethod(req);
        }
    }

    private Mono<BenchMarkResult> benchMarkMethod(BenchMarkRequest req) {
        return Flux.range(0, req.getIterations())
                .parallel(req.getConcurrency())
                .runOn(Schedulers.parallel())
                .map(i -> {
                    long start = System.nanoTime();
                    dummyWork();
                    return System.nanoTime() - start;
                })
                .sequential()
                .collectList()
                .map(times -> {
                    long totalTime = times.stream().mapToLong(Long::longValue).sum();
                    double avgTime = totalTime / (double) req.getIterations() / 1_000_000.0;
                    return new BenchMarkResult(totalTime / 1_000_000.0, avgTime, 100.0, 0, "COMPLETED");
                });
    }

    private Mono<BenchMarkResult> benchMarkHttpEndpoint(BenchMarkRequest req) {
        int totalIterations = req.getIterations();
        String url = req.getUrl();
        AtomicLong success = new AtomicLong(0);
        AtomicLong fail = new AtomicLong(0);

        List<Mono<Long>> tasks = IntStream.range(0, totalIterations)
                .mapToObj(i -> Mono.defer(() -> {
                    long start = System.nanoTime();
                    return webClient.get().uri(url)
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofSeconds(5))
                            .doOnSuccess(resp -> success.incrementAndGet())
                            .doOnError(err -> fail.incrementAndGet())
                            .map(resp -> System.nanoTime() - start)
                            .onErrorReturn(0L);
                }))
                .toList();

        return Flux.merge(tasks)
                .collectList()
                .map(times -> {
                    long totalTime = times.stream().mapToLong(Long::longValue).sum();
                    double avgTime = totalTime / (double) totalIterations / 1_000_000.0;
                    double totalMs = totalTime / 1_000_000.0;
                    long failedCount = fail.get();
                    double successRate = (success.get() * 100.0) / totalIterations;
                    return new BenchMarkResult(totalMs, avgTime, successRate, failedCount,
                            "COMPLETED (" + success.get() + " OK / " + failedCount + " FAIL)");
                });
    }

    private void dummyWork() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
