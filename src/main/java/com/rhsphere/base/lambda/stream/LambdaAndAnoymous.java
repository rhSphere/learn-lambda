package com.rhsphere.base.lambda.stream;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LambdaAndAnoymous {

    public static void main(String[] args) {
        //1.匿名内部类
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread run");
            }
        }).start();

        //2，lambda表达式
        new Thread(() -> System.out.println("lambda thread run")).start();

        //3，lambda代码块
        new Thread(
            () -> {
                System.out.println("hello");
                System.out.println("hi");
        }).start();


        List<String> tmp = Arrays.asList("I", "Love", "you", "too");
        List<String> list = Lists.newArrayList(tmp);

        //匿名函数
        list.sort((o1, o2) -> {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;

            return o1.length() - o2.length();
        });
        list.forEach(str -> System.out.print(str + " "));


        //lambda表达式
        list.add("ha");
        Collections.sort(list, (s1, s2) -> {
            if (s1 == null)
                return -1;
            if (s2 == null)
                return 1;
            return s1.length() - s2.length();
        });
        list.forEach(str -> System.out.print(str + " "));



        //this指针
        Hello instance = new Hello();
        instance.r1.run();
        instance.r2.run();

    }

    public static class Hello {
        Runnable r1 = () -> {
            System.out.println(this);
        };

        Runnable r2 = () -> {
            System.out.println(toString());
        };

        @Override
        public String toString() {
            return "hahah toString()";
        }
    }


}
