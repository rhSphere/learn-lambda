package com.rhsphere.base.lambda.parallel;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-02 21:14
 */
@Slf4j
public class ParallelTest {
    @Test
    public void parallel() {
        IntStream.rangeClosed(1, 100).parallel().forEach(i -> {
            log.info("{} : {}", LocalDateTime.now(), i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("exception", e);
            }
        });
    }

    @Test
    public void allMethods() throws InterruptedException, ExecutionException {
        int taskCount = 10000;
        int threadCount = 20;

        StopWatch stopWatch = new StopWatch();

        stopWatch.start("thread");
        assertEquals(taskCount, thread(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("threadPool");
        assertEquals(taskCount, threadPool(taskCount, threadCount));
        stopWatch.stop();

        //试试把这段放到forkjoin下面？
        stopWatch.start("stream");
        assertEquals(taskCount, stream(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("forkjoin");
        assertEquals(taskCount, forkJoin(taskCount, threadCount));
        stopWatch.stop();

        stopWatch.start("completableFuture");
        assertEquals(taskCount, completableFuture(taskCount, threadCount));
        stopWatch.stop();

        log.info(stopWatch.prettyPrint());
    }

    private void increment(AtomicInteger atomicInteger) {
        atomicInteger.incrementAndGet();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            log.error("e", e);
        }
    }

    private int thread(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(threadCount);
        IntStream.rangeClosed(1, threadCount).mapToObj(i -> new Thread(() -> {
            IntStream.rangeClosed(1, taskCount / threadCount).forEach(j -> increment(atomicInteger));
            latch.countDown();
        })).forEach(Thread::start);
        latch.await();
        return atomicInteger.get();
    }


    private int threadPool(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        IntStream.rangeClosed(1, taskCount).forEach(i -> executorService.execute(() -> increment(atomicInteger)));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        return atomicInteger.get();
    }

    private int forkJoin(int taskCount, int threadCount) throws InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger)));
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        return atomicInteger.get();
    }


    private int stream(int taskCount, int threadCount) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(threadCount));
        AtomicInteger atomicInteger = new AtomicInteger();
        IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger));
        return atomicInteger.get();
    }

    private int completableFuture(int taskCount, int threadCount) throws ExecutionException, InterruptedException {
        AtomicInteger atomicInteger = new AtomicInteger();
        ForkJoinPool forkJoinPool = new ForkJoinPool(threadCount);
        CompletableFuture.runAsync(() -> IntStream.rangeClosed(1, taskCount).parallel().forEach(i -> increment(atomicInteger)), forkJoinPool).get();
        return atomicInteger.get();
    }


}
