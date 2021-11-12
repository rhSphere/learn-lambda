package com.rhsphere.base.lambda.stream;

import com.rhsphere.base.entity.Customer;
import com.rhsphere.base.entity.Order;
import com.rhsphere.base.entity.OrderItem;
import com.rhsphere.base.entity.Product;
import com.rhsphere.base.lambda.collector.MostPopularCollector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.summingDouble;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-03 11:41
 */
public class MostStreamTest {
    private static Random random = new Random();
    private List<Order> orders;

    @Before
    public void data() {
        orders = Order.getData();
        orders.forEach(System.out::println);
        System.out.println("==========================================");
    }

    @Test
    public void filter() {
        System.out.println("//最近半年的金额大于40的订单");
        System.out.println("==before filter");
        System.out.println(orders.stream().count());
        orders.forEach(System.out::println);

        System.out.println("==filter");
        orders.stream()
                .filter(Objects::nonNull)
                .filter(order -> order.getPlacedAt().isAfter(LocalDateTime.now().minusMonths(6)))
                .filter(order -> order.getTotalPrice() > 40)
                .forEach(System.out::println);

        System.out.println("==after filter");
        System.out.println(orders.stream().count());
        System.out.println(orders.stream().count());
    }

    @Test
    public void map() {
        LongAdder longAdder = new LongAdder();
        orders.stream().forEach(order ->
                order.getOrderItemList().forEach(orderItem -> longAdder.add(orderItem.getProductQuantity())));

        Long val =  orders.stream().mapToLong(order -> order.getOrderItemList().stream()
                .mapToLong(OrderItem::getProductQuantity).sum()).sum();

        assertThat(longAdder.longValue(), is(val));

        List list = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> new Product((long) i, "product" + i, i * 100.0)).collect(toList());
        System.out.println(list);
    }

    @Test
    public void sorted() {
        orders.stream().filter(order -> order.getTotalPrice() > 50)
                .sorted(Comparator.comparing(Order::getTotalPrice).reversed())
                .limit(5)
                .forEach(System.out::println);
    }

    @Test
    public void flatMap() {
        // map 从什么转成了什么
        Stream<List<Integer>> stream = Stream.of(Arrays.asList(1,2), Arrays.asList(3, 4, 5));
        stream.flatMap(Collection::stream)
                .forEach(System.out::println);

        System.out.println(orders.stream().mapToDouble(Order::getTotalPrice).sum());

        //所有订单的价格
        // 订单列表 -> 单个订单的订单详情列表 -> 订单详情中所有物品的总价 -> 求和
        System.out.println(orders.stream().flatMap(
                order -> order.getOrderItemList().stream())
                .mapToDouble(item -> item.getProductQuantity() * item.getProductPrice()).sum()
        );

        // 订单列表 -> 列表展开替换成 DoubleStream
        System.out.println(orders.stream()
                .flatMapToDouble(order -> order.getOrderItemList()
                        .stream().mapToDouble(item -> item.getProductPrice() * item.getProductQuantity())).sum());
    }

    @Test
    public void distinct() {
        // map是转成另一个对象
        System.out.println(orders.stream().map(Order::getCustomerName).collect(joining(",")));

        System.out.println(orders.stream().map(Order::getCustomerName).distinct().collect(joining(",")));


        System.out.println(orders.stream().flatMap(order -> order.getOrderItemList().stream())
                .map(OrderItem::getProductName)
                .distinct()
                .collect(joining(",")));
    }


    @Test
    public void skipLimit() {
        orders.stream()
                .sorted(Comparator.comparing(Order::getPlacedAt))
                .map(order -> order.getCustomerName() + "@" + order.getPlacedAt())
                .limit(2)
                .forEach(System.out::println);

        orders.stream()
                .sorted(Comparator.comparing(Order::getPlacedAt))
                .map(order -> order.getCustomerName() + "@" + order.getPlacedAt())
                .skip(2).limit(2)
                .forEach(System.out::println);
    }


    @Test
    public void collect() {
        System.out.println(random.ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(20)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString()
            );

        System.out.println(orders.stream()
                .map(order -> order.getCustomerName()).collect(toSet())
                .stream().collect(Collectors.joining(",", "[", "]")));

        System.out.println(orders.stream().limit(2).collect(Collectors.toCollection(LinkedList::new)).getClass());

        orders.stream()
                .collect(Collectors.toMap(Order::getId, Order::getCustomerName))
                .entrySet().forEach(System.out::println);

        orders.stream()
                .collect(Collectors.toMap(Order::getCustomerName, Order::getPlacedAt, (x, y) -> x.isAfter(y) ? x : y))
                .entrySet().forEach(System.out::println);

        System.out.println(orders.stream().collect(
                Collectors.averagingInt(order -> order.getOrderItemList().stream()
                .collect(Collectors.summingInt(OrderItem::getProductQuantity)))));
    }

    @Test
    public void groupBy() {
        System.out.println("//按照用户名分组，统计下单数量");
        System.out.println(orders.stream().collect(groupingBy(Order::getCustomerName, counting()))
            .entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).collect(toList()));

        System.out.println("//按照用户名分组,统计订单总金额");
        System.out.println(orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomerName, summingDouble(Order::getTotalPrice)))
                .entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).collect(toList()));


        System.out.println("//按照用户名分组,统计商品采购数量");
        System.out.println(orders.stream()
                .collect(groupingBy(Order::getCustomerName,
                        summingInt(order -> order.getOrderItemList().stream()
                                .collect(summingInt(OrderItem::getProductQuantity)))))
                .entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(toList()));

        System.out.println("//统计最受欢迎的商品，倒序后取第一个");
        orders.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .collect(groupingBy(OrderItem::getProductName, summingInt(OrderItem::getProductQuantity)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(System.out::println);

        System.out.println("//统计最受欢迎的商品的另一种方式,直接利用maxBy");
        orders.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .collect(groupingBy(OrderItem::getProductName, summingInt(OrderItem::getProductQuantity)))
                .entrySet()
                .stream()
                .collect(maxBy(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .ifPresent(System.out::println);

        System.out.println("//按照用户名分组，选用户下的总金额最大的订单");
        orders.stream()
                .collect(groupingBy(Order::getCustomerName,
                        collectingAndThen(maxBy(comparingDouble(Order::getTotalPrice)), Optional::get)))
                .forEach((k, v) -> System.out.println(k + "#" + v.getTotalPrice() + "@" + v.getPlacedAt()));

        System.out.println("//根据下单年月分组统计订单ID列表");
        System.out.println(orders.stream()
                .collect(groupingBy(order -> order.getPlacedAt().format(DateTimeFormatter.ofPattern("yyyyMM")),
                        mapping(order -> order.getId(), toList()))));


        System.out.println("//根据下单年月+用户名两次分组，统计订单ID列表");
        System.out.println(orders.stream()
                .collect(groupingBy(order
                                -> order.getPlacedAt().format(DateTimeFormatter.ofPattern("yyyyMM")),
                        groupingBy(order -> order.getCustomerName(), mapping(order -> order.getId(), toList())))));

    }

    @Test
    public void partition() {
        orders.stream().map(order -> order.getCustomerName())
                .collect(toSet()).forEach(System.out::println);

        System.out.println(Customer.getData().stream().collect(
                partitioningBy(customer -> orders.stream().mapToLong(Order::getCustomerId)
                .anyMatch(id -> id == customer.getId()))
        ));
    }

    @Test
    public void maxMin() {
        orders.stream().max(comparing(Order::getTotalPrice)).ifPresent(System.out::println);
        orders.stream().min(comparing(Order::getTotalPrice)).ifPresent(System.out::println);
    }

    @Test
    public void reduce() {
        System.out.println(orders.stream().collect(groupingBy(Order::getCustomerName, summingDouble(Order::getTotalPrice)))
            .entrySet().stream()
            .reduce(BinaryOperator.maxBy(Map.Entry.comparingByValue()))
            .map(Map.Entry::getKey).orElse("N/A"));
    }

    @Test
    public void peek() {
        IntStream.rangeClosed(1, 10)
                .peek(i -> {
                    System.out.println("第一次peek");
                    System.out.println(i);
                })
                .filter(i -> i > 5)
                .peek(i -> {
                    System.out.println("第二次peek");
                    System.out.println(i);
                })
                .filter(i -> i % 2 == 0)
                .forEach(i -> {
                    System.out.println("最终结果");
                    System.out.println(i);
                });
            orders.stream()
                .filter(order -> order.getTotalPrice() > 40)
                .peek(order -> System.out.println(order.toString()))
                .map(Order::getCustomerName)
                .collect(toList());
    }

    @Test
    public void customCollector() //自定义收集器
    {
        //最受欢迎收集器
        assertThat(Stream.of(1, 1, 2, 2, 2, 3, 4, 5, 5).collect(new MostPopularCollector<>()).get(), is(2));
        assertThat(Stream.of('a', 'b', 'c', 'c', 'c', 'd').collect(new MostPopularCollector<>()).get(), is('c'));
        assertThat(Stream.concat(Stream.concat(IntStream.rangeClosed(1, 1000).boxed(), IntStream.rangeClosed(1, 1000).boxed()), Stream.of(2))
                .parallel().collect(new MostPopularCollector<>()).get(), is(2));

    }

    @Test
    public void compare() {
//        Arrays.asList(7, 6, 5, 1).stream().sorted()

    }




}
