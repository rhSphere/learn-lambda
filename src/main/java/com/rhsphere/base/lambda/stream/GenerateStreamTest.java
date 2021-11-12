package com.rhsphere.base.lambda.stream;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-03 11:28
 */
public class GenerateStreamTest {

    @Test
    public void stream() {
        Arrays.asList("1", "2", "3").forEach(System.out::println);
        Arrays.stream(new int[]{1, 2, 3}).forEach(System.out::println);
    }

    @Test
    public void of() {
        String[] arr = {"a", "b", "c", "d"};
        Stream.of(arr).forEach(System.out::println);
        Stream.of("a", "b", "c").forEach(System.out::println);

        Stream
            .of(1, 2, "a")
            .map(item -> item.getClass().getName())
            .forEach(System.out::println);
    }


    @Test
    public void iterate() {
        Stream.iterate(2, i -> i * 2)
            .limit(10)
            .forEach(System.out::println);

        Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.TEN))
            .limit(10)
            .forEach(System.out::println);
    }

    @Test
    public void generate() {
        Stream.generate(() -> "test")
            .limit(3)
            .forEach(System.out::println);

        Stream.generate(Math::random)
            .limit(10)
            .forEach(System.out::println);
    }


    @Test
    public void primitive() {
        IntStream.range(1, 3)
            .forEach(System.out::println);

        IntStream.range(0, 3)
            .mapToObj(i -> "x")
            .forEach(System.out::println);

        IntStream.rangeClosed(1, 3)
            .forEach(System.out::println);

        DoubleStream.of(1.2, 2.3, 3.4)
            .forEach(System.out::println);


        Class<? extends int[]> t1 = IntStream.of(1, 2).toArray().getClass();
        Class<? extends int[]> t2 = Stream.of(1, 2).mapToInt(Integer::intValue).toArray().getClass();
        Class<? extends Object[]> t3 = IntStream.of(1, 2).boxed().toArray().getClass();
        Class<? extends double[]> t4 = IntStream.of(1, 2).asDoubleStream().toArray().getClass();
        Class<? extends long[]> t5 = IntStream.of(1, 2).asLongStream().toArray().getClass();

        Arrays.asList("a", "b", "c").stream()
                .mapToInt(String::length)
                .asLongStream()
                .mapToDouble(x -> x/10.0)
                .boxed()
                .mapToLong(x -> 2L)
                .mapToObj(x -> "")
                .collect(Collectors.toList());
    }

    @Test
    public void generateList() {
        System.out.println(IntStream.rangeClosed(20, 55)
                .mapToObj(i -> "jsfhfcu_2F-B-FCU-" + i).collect(Collectors.joining("\",\"", "[\"", "\"]")));

//        IntStream.rangeClosed(20, 55)
//                .mapToObj(i -> "jsfhfcu_2F-B-FCU-" + i).forEach(System.out::println);
    }


}
