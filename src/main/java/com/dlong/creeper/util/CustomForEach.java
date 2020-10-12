package com.dlong.creeper.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class CustomForEach {

    public static class Breaker {
        private volatile boolean shouldBreak = false;

        public void stop() {
            shouldBreak = true;
        }

        boolean get() {
            return shouldBreak;
        }
    }

    public static <T> void forEach(Stream<T> stream, BiConsumer<T, Breaker> consumer) {
        Spliterator<T> spliterator = stream.spliterator();
        boolean hadNext = true;
        Breaker breaker = new Breaker();

        while (hadNext && !breaker.get()) {
            hadNext = spliterator.tryAdvance(elem -> {
                consumer.accept(elem, breaker);
            });
        }
    }

    public static <T> void forEach(Collection<T> collection, BiConsumer<T, Breaker> consumer) {
        Spliterator<T> spliterator = collection.parallelStream().spliterator();
        boolean hadNext = true;
        Breaker breaker = new Breaker();

        while (hadNext && !breaker.get()) {
            hadNext = spliterator.tryAdvance(elem -> {
                consumer.accept(elem, breaker);
            });
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            ints.add(i);
        }
        List<Integer> result = new ArrayList<>();
        AtomicInteger count=new AtomicInteger(0);
        CustomForEach.forEach(ints, (elem, breaker) -> {
            System.out.println(elem);
            result.add(elem);
            count.addAndGet(1);
            if (count.get() >= 5000 ) {
                breaker.stop();
                return;
            }
            System.out.println("end"+elem);
        });
        System.out.println(result);
    }

}