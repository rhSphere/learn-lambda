package com.rhsphere.base.lambda.convert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-01-05 15:56
 */
public class ListConverterTest {

    int[] data = {1, 2, 3, 4, 5};

    Integer[] arr = Arrays.stream(data)
        .boxed()
        .toArray(Integer[]::new);

    /**
     * 列是src类型
     * 行是dest类型
     *               int[]                                     list                            Integer[]
     * int[]          x                                boxed().collect(toList())       boxed().toArray(Integer::new)
     * list   mapToInt(Integer::valueOf).toArray()                  x                         toArray(new Integer[0])
     * Integer[] mapToInt(Integer::valueOf).toArray()       Arrays.asList(in)                         x
     */

    // 1.src int[]
    // int[] 转 List<Integer>
    List<Integer> list1 = Arrays.stream(data)
        .boxed()
        .collect(Collectors.toList());


    // int[] 转 Integer[]
    Integer[] integers1 = Arrays.stream(data)
        .boxed()
        .toArray(Integer[]::new);

    // 2.src List<Integer>
    // List<Integer> 转 int[]
    int[] arr1 = list1.stream()
        .mapToInt(Integer::valueOf)
        .toArray();

    // 前两步同上，此时是Stream<Integer>。
    // 然后使用Stream的toArray，传入IntFunction<A[]> generator。
    // 这样就可以返回Integer数组。
    // 不然默认是Object[]。
    // List<Integer> 转 Integer[]
    Integer[] integers2 = list1.toArray(new Integer[0]);
    Number[] integers20 = list1.toArray(new Number[0]);



    // 3.src Integer[]

    // Integer[] 转 int[]
    int[] arr2 = Arrays.stream(integers1)
        .mapToInt(Integer::valueOf)
        .toArray();

    // Integer[] 转 List<Integer>
    List<Integer> list2 = Arrays.asList(integers1);



    String[] strings1 = {"1", "2"};
    // 4.String[] 转 List<String>
    List<String> list3 = Arrays.asList(strings1);


    // 5.List<String> 转 String[]
    String[] strings2 = list3.toArray(new String[0]);
}
