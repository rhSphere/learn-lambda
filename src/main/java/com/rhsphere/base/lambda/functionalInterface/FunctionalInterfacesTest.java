package com.rhsphere.base.lambda.functionalInterface;


import com.rhsphere.base.entity.Employee;
import com.rhsphere.base.entity.Product;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-01 22:21
 */
public class FunctionalInterfacesTest {
    @Test
    public void lambdaAnonymousClass() {
        new Thread(() -> {
            System.out.println("hello");
        }).start();
    }


    @Test
    public void functionalInterfaces() {
        Supplier<String> supplier = String::new;
        Supplier<String> stringSupplier = () -> "OK";

        System.out.println(stringSupplier.get());

        Predicate<Integer> pos = i -> i > 0;
        Predicate<Integer> even = i -> (i & 1) == 0;
        assertTrue(pos.and(even).test(2));

        Consumer<String> println = System.out::println;
        println.andThen(println).accept("abcdefg");

        Function<String, String> upperCase = String::toUpperCase;
        Function<String, String> duplicate = s -> s.concat(s);
        assertThat(upperCase.andThen(duplicate).apply("test"), is("TESTTEST"));

        Supplier<Integer> random = () -> ThreadLocalRandom.current().nextInt();
        System.out.println(random.get());

        BinaryOperator<Integer> add = Integer::sum;
        BinaryOperator<Integer> subtract = (a, b) -> a - b;
        assertThat(subtract.apply(add.apply(1, 2), 3), is(0));

    }

    @Test
    public void functionalInterfaces1() {
        Consumer<Product> productConsumer = product -> System.out.println("hello" + product.getName());
        productConsumer.accept(new Product(1l, "apple", 5.0));

        Predicate<String> predicate = s -> s.length() <= 0;
        predicate.test("hello");
        predicate.negate().test("test");

        BiConsumer<String, Integer> consumer = Employee::new;
        consumer.accept("zhangsan", 1);
    }


}
