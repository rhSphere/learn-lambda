package com.rhsphere.base.lambda.reference;

import org.junit.Test;

import java.util.Arrays;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-09-18 11:46
 */
public class ReferenceTest {

    static int cmp(String s1, String s2) {
        return s1.compareTo(s2);
    }


    @Test
    public void test1() {
        String[] array = new String[]{"Apple", "Orange", "Banana", "Lemon"};
        Arrays.sort(array, ReferenceTest::cmp);
        System.out.println(String.join(", ", array));
    }



    @Test
    public void test2() {
        String[] array = new String[] { "Apple", "Orange", "Banana", "Lemon" };
        Arrays.sort(array, String::compareTo);
        System.out.println(String.join(", ", array));
    }

}
