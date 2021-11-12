package com.rhsphere.base.lambda.stream;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TenLambda {
    public static void main(String[] args) {
        List<String> features = Arrays.asList("Lambdas", "Default Method", "Stream API", "Date and Time API");
        features.forEach(System.out::println);


        List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");

        System.out.println("Languages which starts with J :");
        filter(languages, (str)->((String)str).startsWith("J"));

        System.out.println("Languages which ends with a ");
        filter(languages, (str)->((String)str).endsWith("a"));

        System.out.println("Print all languages :");
        filter(languages, (str)->true);

        System.out.println("Print no language : ");
        filter(languages, (str)->false);

        System.out.println("Print language whose length greater than 4:");
        filter(languages, (str)->((String)str).length() > 4);


        //==================================//

        Stream<String> stream = Stream.of("I", "love", "you", "too");


        //1，foreach
        ArrayList<String> list = new ArrayList<>(Arrays.asList("I",  "love", "you", "too", "much", "me"));
        list.forEach(str -> {
            if (str.length() > 3)
                System.out.println(str);
        });


        //2.removeIf
        list.removeIf(str -> str.length() > 3);

        //3.replaceAll
        list.replaceAll(str -> {
            if (str.length() > 2)
                return str.toUpperCase();
            return str;
        });
        list.forEach(str -> System.out.print(str + " "));
        System.out.println();

        list.sort(Comparator.comparingInt(String::length));
        list.forEach(str -> System.out.print(str + " "));
        System.out.println();

    }

    public static void filter(List<String> names, Predicate<String> condition) {
        for(String  name: names)  {
            if(condition.test(name)) {
                System.out.println(name + " ");
            }
        }
    }



    private Stream<String> stream = Stream.of("I", "love", "you", "too");

    @Test
    public void filter() {
        stream.filter(s -> s.length() > 3).forEach(System.out::println);
    }

    @Test
    public void distinct() {
        stream.distinct().forEach(System.out::println);
    }


    @Test
    public void sorted() {
        stream.sorted(Comparator.comparing(String::length)).forEach(System.out::println);
    }



    @Test
    public void map() {
        stream.map(s -> s.toUpperCase()).forEach(System.out::println);
    }

    @Test
    public void reduce() {
//        Optional<String> longest = stream.reduce(((s1, s2) -> s1.length() >= s2.length() ? s1 : s2));
        Integer lengthSum = stream.reduce(0, (sum, s) -> sum + s.length(), (a, b) -> a + b);
//        System.out.println(longest.get());
        System.out.println(lengthSum);
    }

    @Test
    public void collect() {
        List<String> list = stream.collect(Collectors.toList());
        //        Set<String> set = stream.collect(Collectors.toSet());
//        System.out.println(set);

//        Map<String, Integer> map = stream.collect(Collectors.toMap(Function.identity(),
//                String::length));
//        map.forEach((k,v) -> System.out.println(k + "=" + v));

//        List<String> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
//        list.forEach(str -> System.out.println(str));

//        ArrayList<String> arrayList = stream.collect(Collectors.toCollection(ArrayList::new));
//        HashSet<String> hashSet = stream.collect(Collectors.toCollection(HashSet::new));
    }

    @Test
    public void join() {
        // 使用Collectors.joining()拼接字符串

        //String joined = stream.collect(Collectors.joining());// "Iloveyou"
        //String joined = stream.collect(Collectors.joining(","));// "I,love,you"
        String joined = stream.collect(Collectors.joining(", ", "{", "}"));// "{I,love,you}"
        System.out.println(joined);
    }

    @Test
    public void flatMap() {
        Stream<List<Integer>> stream1 = Stream.of(Arrays.asList(1, 2));
        stream1.flatMap(list -> list.stream()).forEach(System.out::println);
    }

}
