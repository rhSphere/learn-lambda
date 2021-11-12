package com.rhsphere.base.lambda.convert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * @description:
 * @author: ludepeng
 * @date: 2021-09-17 15:47
 */
public class TestEasyUtil {


    @Test
    public void transToCollectionTest() {
        Supplier<List<String>> supplier = ArrayList::new;
        Collection<Integer> source = new LinkedList<>();
        source.add(1);
        source.add(null);
        Function<Integer, String> mapper = String::valueOf;
        List<String> list = CollectionEasyUtil.transToCollection(supplier, source, mapper);

        assertThat(list.size(), is(1));
    }
}
