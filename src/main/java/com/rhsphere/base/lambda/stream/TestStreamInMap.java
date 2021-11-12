package com.rhsphere.base.lambda.stream;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestStreamInMap {
    public static void main(String[] args) {

        //1，1.7之前的遍历
        HashMap<Integer, String> map = Maps.newHashMap();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue()
            );
        }

        //2，forEach
        map.forEach((k, v) -> System.out.println(k + "=" + v));

        //3. getOrDefault
        System.out.println(map.getOrDefault(4, "NoVal"));

        //4，replaceAll
        map.replaceAll((k, v) -> v.toUpperCase());

        //5.判断和添加二合一
        Map<Integer, Set<String>> map1 = new HashMap<>();

        if (map1.containsKey(1)) {
            map1.get(1).add("one");
        } else {
            Set<String> valSet =  new HashSet<String>();
            valSet.add("one");
            map1.put(1, valSet);
        }

        map1.computeIfAbsent(1, v -> new HashSet<String>()).add("yi");
        map1.forEach((k, v) -> System.out.println(k + "=" + v));




    }

}
