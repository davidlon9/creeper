package com.dlong.creeper.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CustomSpliteratorUsage {

    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        CustomSpliterator<T> customSpliterator = new CustomSpliterator<>(stream.spliterator(), predicate);
        return StreamSupport.stream(customSpliterator, true);
    }

    public static void main(String[] args) {
        Collection<Integer> ints = new HashSet<>();
        ints.add(1);
        Object a=ints;
        Collection<Object> collection= (Collection<Object>) a;
        for (Object o : collection) {
            System.out.println(o);
        }
        System.out.println();
    }
}