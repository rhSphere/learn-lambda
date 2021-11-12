package com.rhsphere.base.lambda.stream;

import com.rhsphere.base.entity.Product;
import org.junit.Test;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @description:
 * @author: ludepeng
 * @date: 2020-12-02 11:12
 */
public class CoolStreamTest {

    private Map<Long, Product> cache = new ConcurrentHashMap<>();


    private static double calc(List<Integer> list) {
        List<Point2D> point2DList = new ArrayList<>();
        for (Integer i : list) {
            point2DList.add(new Point2D.Double((double) i % 3, (double) i / 3));
        }

        double total = 0;
        int cnt = 0;
        for (Point2D point2D : point2DList) {
            if (point2D.getY() > 1) {
                double d = point2D.distance(0, 0);
                total += d;
                cnt++;
            }
        }
        return cnt > 0 ? total / cnt : 0;
    }

    @Test
    public void stream() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        double avg = calc(list);

        double res = list.stream()
                .map(i -> new Point2D.Double((double) i % 3, (double) i / 3))
                .filter(p -> p.getY() > 1)
                .mapToDouble(p -> p.distance(0, 0))
                .average()
                .orElse(0);
        assertThat(res, is(avg));
    }


    @Test
    public void cache() {
        getProductAndCache(1L);
        getProductAndCache(100L);

        System.out.println(cache);
        assertThat(cache.size(), is(1));
        assertTrue(cache.containsKey(1L));
    }

    @Test
    public void cacheCool() {
        getProductAdnCacheCool(1L);
        getProductAdnCacheCool(100L);

        System.out.println(cache);
        assertThat(cache.size(), is(1));
        assertTrue(cache.containsKey(1L));
    }


    private Product getProductAdnCacheCool(Long id) {
        // todo
        // computeIfAbsent在get(id) == null的时候，
        // 1.计算出一个newValue出来，2.map.put(key,newValue)
        return cache.computeIfAbsent(id, i -> //当Key不存在的时候提供一个Function来代表根据Key获取Value的过程
                Product.getData().stream()
                        .filter(p -> p.getId().equals(i)) //过滤
                        .findFirst() //找第一个，得到Optional<Product>
                        .orElse(null)); //如果找不Product到则使用null
    }


    private Product getProductAndCache(Long id) {
        Product product = null;
        if (cache.containsKey(id)) {
            product = cache.get(id);
        } else {
            for (Product p : Product.getData()) {
                if (p.getId().equals(id)) {
                    product = p;
                    break;
                }
            }
            if (product != null) cache.put(id, product);
        }
        return product;
    }


    @Test
    public void filesExampleWrong() throws IOException {
        try (Stream<Path> pathStream = Files.walk(Paths.get("."))) {
            pathStream.filter(Files::isRegularFile)
                .filter(
                    FileSystems.getDefault()
                        .getPathMatcher("glob:**/*.class)")::matches)
                .flatMap(ThrowingFunction.unchecked(path ->
                    Files.readAllLines(path)
                        .stream()
                        .filter(line -> Pattern.compile("public class")
                            .matcher(line)
                            .find())
                        .map(line -> path.getFileName() + " >> " + line)))
                .forEach(System.out::println);
        }
    }


    @Test
    public void filesExample() throws IOException {
        //无限深度，递归遍历文件夹
        try (Stream<Path> pathStream = Files.walk(Paths.get("."))) {
            pathStream.filter(Files::isRegularFile) //只查普通文件
                .filter(FileSystems.getDefault().getPathMatcher("glob:**/*.java")::matches) //搜索java源码文件
                .flatMap(ThrowingFunction.unchecked(path ->
                    Files.readAllLines(path).stream() //读取文件内容，转换为Stream<List>
                        .filter(line -> Pattern.compile("public class").matcher(line).find()) //使用正则过滤带有public class的行
                        .map(line -> path.getFileName() + " >> " + line))) //把这行文件内容转换为文件名+行
                .forEach(System.out::println); //打印所有的行
        }
    }

    public interface ThrowingFunction<T, R, E extends Throwable> {
        static <T, R, E extends Throwable> Function<T, R> unchecked(ThrowingFunction<T, R, E> f) {
            return t -> {
                try {
                    return f.apply(t);
                } catch (Throwable e) {
                    throw new RuntimeException();
                }
            };
        }

        R apply(T t) throws E;
    }

}
