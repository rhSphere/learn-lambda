package com.rhsphere.base.lambda.convert;

import org.apache.commons.lang.ArrayUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-09-01 15:46
 */
public class CollectionEasyUtil {


    /**
     * Collection判空
     */
    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }


    /**
     * Map判空
     */
    public static <K, V> boolean isEmpty(Map<K, V> sourceMap) {
        return Objects.isNull(sourceMap) || sourceMap.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> sourceMap) {
        return !isEmpty(sourceMap);
    }


    /**
     * 基本类型数组判空
     */
    public static boolean isEmpty(long[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static boolean isNotEmpty(long[] array) {
        return !isEmpty(array);
    }


    /**
     * 对象数组判空
     */
    public static <T> boolean isEmpty(T[] array) {
        return ArrayUtils.isEmpty(array);
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }


    /**
     * 转换为指定的集合，常用Set/List，HashSet/ArrayList，LinkedSet/LinkedList
     *
     * @param resultColl 指定集合容器
     * @param source     数据源
     * @param mapper     字段执行函数
     * @return C
     */
    public static <E, R, C extends Collection<R>> C transToCollection(
        Supplier<C> resultColl, Collection<E> source, Function<? super E, ? extends R> mapper) {
        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream()
            .filter(Objects::nonNull)
            .map(mapper)
            .collect(Collectors.toCollection(resultColl));
    }


    /**
     * 转换为Map-Value, 重复KEY将抛出异常
     *
     * @param mapColl   结果收集容器
     * @param source    数据源
     * @param kFunction key执行函数
     * @param vFunction value执行函数
     * @return
     */
    public static <E, K, V, M extends Map<K, V>> M transToMap(
        Supplier<M> mapColl, Collection<E> source, Function<? super E, ? extends K> kFunction,
        Function<? super E, ? extends V> vFunction) {

        if (isEmpty(source)) {
            return mapColl.get();
        }
        return source.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(kFunction, vFunction, nonDuplicateKey(), mapColl));
    }

    /**
     * Returns a merge function, suitable for use in
     * {@link Map#merge(Object, Object, BiFunction) Map.merge()} or
     * throws {@code IllegalStateException}.  This can be used to enforce the
     * assumption that the elements being collected are distinct.
     *
     * @param <T> the type of input arguments to the merge function
     * @return a merge function which always throw {@code IllegalStateException}
     */
    private static <T> BinaryOperator<T> nonDuplicateKey() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("转换Map时不允许重复Key: [%s]", u));
        };
    }


    /**
     * 转换为Map-Value, 重复KEY时新值覆盖旧值
     *
     * @param mapColl   支持返回LinkedHashMap/HashMap
     * @param source
     * @param kFunction
     * @param vFunction
     * @return
     */
    public static <E, K, V, M extends Map<K, V>> M transToMapEnhance(
        Supplier<M> mapColl, Iterable<E> source, Function<? super E, ? extends K> kFunction,
        Function<? super E, ? extends V> vFunction) {

        if (Objects.isNull(source)) {
            return mapColl.get();
        }

        return StreamSupport.stream(source.spliterator(), Boolean.FALSE)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(kFunction, vFunction, (oldValue, newValue) -> newValue, mapColl));
    }


    /**
     * 分组转换为指定Map<K, 指定的List<V>>，例如TreeMap<K, LinkedList<V>>/LinkedHashMap<K, LinkedList<V>>
     * 并且可对原始数组元素进行计算(转化)为其他对象
     *
     * @param mapColl        结果收集容器
     * @param vColl          Map结果中value的收集容器
     * @param source         数据源
     * @param kGroupFunction key执行函数
     * @param vFunction      value执行函数
     * @return
     */
    public static <E, K, V, M extends Map<K, C>, C extends Collection<V>> M groupIndexToMap(
        Supplier<M> mapColl, Supplier<C> vColl, Collection<E> source,
        Function<? super E, ? extends K> kGroupFunction,
        Function<? super E, ? extends V> vFunction) {

        if (isEmpty(source)) {
            return mapColl.get();
        }

        return source.stream()
            .filter(Objects::nonNull)
            .collect(Collectors
                .groupingBy(kGroupFunction, mapColl,
                    Collectors.mapping(vFunction, Collectors.toCollection(vColl))));
    }


    /**
     * 两层嵌套Collection(内外层任意)折叠平铺到指定收集容器, 支持常用集合, 例如ArrayList, HashSet等
     * 多层嵌套的可以通过重复调用此方法完成平铺
     * 并支持元素转换
     *
     * @param resultColl    结果容器
     * @param source        两层嵌套Collection数据源
     * @param flatMapper    外层元素获取内Collection的执行函数
     * @param convertMapper 元素转换函数
     * @return 平铺后收集到的结果容器
     */
    public static <E, F, U, T extends Collection<F>, R extends Collection<U>> R enhanceTransToCollWithFlatMap(
        Supplier<R> resultColl, Collection<E> source,
        Function<? super E, ? extends T> flatMapper,
        Function<? super F, ? extends U> convertMapper) {

        if (isEmpty(source)) {
            return resultColl.get();
        }
        return source.stream()
            .filter(Objects::nonNull)
            .map(flatMapper)
            .flatMap(Collection::stream)
            .map(convertMapper)
            .collect(Collectors.toCollection(resultColl));
    }


    /**
     * 升序排序, null值放到最后
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getAscComparator(Function<T, ? extends U> keyExtractor) {
//        return (Comparator<T> & Serializable) (c1, c2) -> {
//            U u2 = keyExtractor.apply(c2);
//            if (Objects.isNull(u2)) {
//                return -1;
//            }
//            U u1 = keyExtractor.apply(c1);
//            if (Objects.isNull(u1)) {
//                return 1;
//            }
//            return u1.compareTo(u2);
//        };

        // TODO: 优化
        return Comparator.comparing(keyExtractor, Comparator.nullsLast(Comparator.naturalOrder()));


    }

    /**
     * 降序排序, null值放到最后
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getDescComparator(Function<? super T, ? extends U> keyExtractor) {
        return (Comparator<T> & Serializable) (c1, c2) -> {
            U u2 = keyExtractor.apply(c2);
            if (Objects.isNull(u2)) {
                return -1;
            }
            U u1 = keyExtractor.apply(c1);
            if (Objects.isNull(u1)) {
                return 1;
            }
            return u2.compareTo(u1);
        };
    }

    /**
     * 升序排序, null值放到最前面
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getAscComparatorWithNullFirst(Function<? super T, ? extends U> keyExtractor) {
        return (Comparator<T> & Serializable) (c1, c2) -> {
            U u2 = keyExtractor.apply(c2);
            if (Objects.isNull(u2)) {
                return 1;
            }
            U u1 = keyExtractor.apply(c1);
            if (Objects.isNull(u1)) {
                return -1;
            }
            return u1.compareTo(u2);
        };
    }

    /**
     * 降序排序, null值放到最前面
     *
     * @param keyExtractor 排序字段
     * @return 比较器
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> getDescComparatorWithNullFirst(
        Function<? super T, ? extends U> keyExtractor) {

        // the resulting value will be cast to Comparator and Serializable
        // 结果值将被转换为 Comparator 和 Serializable（即可序列化的比较器）
        return (Comparator<T> & Serializable) (c1, c2) -> {
            U u2 = keyExtractor.apply(c2);
            if (Objects.isNull(u2)) {
                return 1;
            }
            U u1 = keyExtractor.apply(c1);
            if (Objects.isNull(u1)) {
                return -1;
            }
            return u2.compareTo(u1);
        };
    }

    /**
     * 根据指定排序器对数据源排序, 返回原始数据类型
     * 默认排序行为: 根据排序字段的compareTo()方法
     *
     * @param coll            数据源
     * @param sortFieldMapper 排序字段函数
     * @param resultMapper    结果元素转换函数
     * @param <T>             原始元素类型
     * @param <F>             排序字段类型
     * @param <R>             结果元素类型
     * @return
     */
    public static <T, F extends Comparable<? super F>, R> List<R> customSortAndTransList(
        Collection<T> coll, Function<? super T, ? extends F> sortFieldMapper,
        Function<? super T, ? extends R> resultMapper) {
        return customSortAndTransList(coll, sortFieldMapper, Comparator.comparing(Function.identity()), resultMapper);
    }

    /**
     * 根据指定排序器对数据源排序, 返回原始数据类型
     *
     * @param coll            数据源
     * @param sortFieldMapper 排序字段函数
     * @param sortAction      排序行为
     * @param resultMapper    结果元素转换函数
     * @param <T>             原始元素类型
     * @param <F>             排序字段类型
     * @param <R>             结果元素类型
     * @return
     */
    public static <T, F extends Comparable<? super F>, R> List<R> customSortAndTransList(
        Collection<T> coll, Function<? super T, ? extends F> sortFieldMapper,
        Comparator<F> sortAction, Function<? super T, ? extends R> resultMapper) {

        return coll.stream()
            .sorted(useCustomFieldComparator(sortFieldMapper, sortAction))
            .map(resultMapper)
            .collect(Collectors.toList());
    }

    /**
     * 自定义比较器, 用对象字段作为比较依据BinaryOperator
     *
     * @param mapper
     * @param comparator
     * @return
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> useCustomFieldComparator(
        Function<? super T, ? extends U> mapper,
        Comparator<U> comparator) {
        return (Comparator<T> & Serializable) (c1, c2) ->
            comparator.compare(mapper.apply(c1), mapper.apply(c2));
    }

}
