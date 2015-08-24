package com.mcxiaoke.next.func.tests;

import android.test.suitebuilder.annotation.SmallTest;
import com.mcxiaoke.next.func.Fn;
import com.mcxiaoke.next.func.functions.Action1;
import com.mcxiaoke.next.func.functions.Func1;
import com.mcxiaoke.next.func.functions.Func2;
import com.mcxiaoke.next.func.functions.Predicate;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: mcxiaoke
 * Date: 15/8/20
 * Time: 12:25
 */
@SmallTest
public class FunctionsTest {

    @Test
    public void testMap() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<String> result = Fn.map(new Func1<Integer, String>() {
            @Override
            public String call(final Integer integer) {
                return "Item " + integer * 10;
            }
        }, list);
        Assert.assertNotNull(result);
        Assert.assertEquals(list.size(), result.size());
        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals("Item " + list.get(i) * 10, result.get(i));
        }

    }

    @Test
    public void testZip() {
        List<String> list1 = Arrays.asList("1", "2", "3");
        List<String> list2 = Arrays.asList("One", "Two", "Three", "Four", "Five");
        List<String> list3 = Arrays.asList("100.1f", "200.2f", "300.3f", "400.4f");
        List<List<String>> result = Fn.zip(list1, list2, list3);
        Assert.assertEquals(list1.get(0), result.get(0).get(0));
        Assert.assertEquals(list3.get(0), result.get(0).get(2));
    }

    @Test
    public void testReduce() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Integer result = Fn.reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(final Integer integer, final Integer o) {
                return integer + o;
            }
        }, list, 10);
        Assert.assertEquals(25, (int) result);

    }

    @Test
    public void testConcat() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(6, 7, 8);
        Collection<Integer> all = new ArrayList<Integer>();
        all.addAll(list);
        all.addAll(list2);
        Collection<Integer> result = Fn.concat(list, list2);
        Assert.assertNotNull(result);
        Assert.assertEquals(all.size(), result.size());
        Iterator<Integer> ia = all.iterator();
        Iterator<Integer> ir = result.iterator();
        while (ia.hasNext()) {
            Assert.assertEquals(ia.next(), ir.next());
        }
    }

    @Test
    public void testFlatMap() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(6, 7, 8);
        List<List<Integer>> input = new ArrayList<List<Integer>>();
        input.add(list);
        input.add(list2);
        Collection<Integer> all = new ArrayList<Integer>();
        all.addAll(list);
        all.addAll(list2);
        List<Integer> result = Fn.flatMap(new Func1<Integer, Integer>() {
            @Override
            public Integer call(final Integer integer) {
                return integer * 2;
            }
        }, input);
        System.err.println(all);
        System.err.println(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(all.size(), result.size());
        Iterator<Integer> ia = all.iterator();
        Iterator<Integer> ir = result.iterator();
        while (ia.hasNext()) {
            Assert.assertEquals(Integer.valueOf(ia.next() * 2), ir.next());
        }
    }

    @Test
    public void testDistinct() {
        List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 2, 5, 5);
        List<Integer> list2 = Arrays.asList(3, 4, 5, 2, 1);
        Collections.sort(list2);
        List<Integer> result = Fn.distinct(list1);
        Collections.sort(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(list2.size(), result.size());
        Iterator<Integer> ia = list2.iterator();
        Iterator<Integer> ir = result.iterator();
        while (ia.hasNext()) {
            Assert.assertEquals(ia.next(), ir.next());
        }
    }

    @Test
    public void testFilter() {
        List<Integer> list = Arrays.asList(1, 2, 9, 8, 7, 3, 5, 4, 6);
        List<Integer> result = Fn.filter(new Predicate<Number>() {
            @Override
            public boolean accept(final Number number) {
                return number.intValue() > 5;
            }
        }, list);
        Assert.assertNotNull(result);
        Assert.assertEquals(4, result.size());
        for (Integer i : result) {
            Assert.assertTrue(i > 5);
        }
    }

    @Test
    public void testAll() {
        List<Integer> list = Arrays.asList(1, 2, 9, 8, 7, 3, 5, 4, 6);
        boolean result1 = Fn.all(new Predicate<Integer>() {
            @Override
            public boolean accept(final Integer integer) {
                return integer > 5;
            }
        }, list);
        boolean result2 = Fn.all(new Predicate<Integer>() {
            @Override
            public boolean accept(final Integer integer) {
                return integer > 0;
            }
        }, list);
        Assert.assertFalse(result1);
        Assert.assertTrue(result2);
    }

    @Test
    public void testAny() {
        List<Integer> list = Arrays.asList(1, 2, 9, 8, 7, 3, 5, 4, 6);
        boolean result1 = Fn.any(new Predicate<Integer>() {
            @Override
            public boolean accept(final Integer integer) {
                return integer > 8;
            }
        }, list);
        boolean result2 = Fn.any(new Predicate<Integer>() {
            @Override
            public boolean accept(final Integer integer) {
                return integer > 10;
            }
        }, list);
        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void testForEach() {
        List<AtomicBoolean> list = new ArrayList<AtomicBoolean>();
        for (int i = 0; i < 10; i++) {
            list.add(new AtomicBoolean(false));
        }
        Fn.forEach(new Action1<AtomicBoolean>() {
            @Override
            public void call(final AtomicBoolean atomicBoolean) {
                atomicBoolean.set(true);
            }
        }, list);
        for (AtomicBoolean a : list) {
            Assert.assertTrue(a.get());
        }
    }

    @Test
    public void testMax() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(6, 7, 8);
        Assert.assertEquals(Collections.max(list), Fn.max(list));
        Assert.assertEquals(Collections.max(list2), Fn.max(list2));
    }

    @Test
    public void testMin() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> list2 = Arrays.asList(6, 7, 8);
        Assert.assertEquals(Collections.min(list), Fn.min(list));
        Assert.assertEquals(Collections.min(list2), Fn.min(list2));
    }

    @Test
    public void testRepeat1() {
        List<Integer> list = Fn.repeat(2015, 10);
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(10, list.size());
        Assert.assertEquals(Integer.valueOf(2015), list.get(0));
        Assert.assertEquals(Integer.valueOf(2015), list.get(9));
    }

    @Test
    public void testRepeat2() {
        List<String> list = Fn.repeat("Hello", 10);
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(10, list.size());
        Assert.assertEquals("Hello", list.get(0));
        Assert.assertEquals("Hello", list.get(9));
    }

    @Test
    public void testRange1() {
        List<Integer> list = Fn.range(10);
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(10, list.size());
        Assert.assertEquals(Integer.valueOf(0), list.get(0));
        Assert.assertEquals(Integer.valueOf(9), list.get(list.size() - 1));

        list = Fn.range(3, 10);
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(7, list.size());
        Assert.assertEquals(Integer.valueOf(3), list.get(0));
        Assert.assertEquals(Integer.valueOf(9), list.get(list.size() - 1));
    }

    @Test
    public void testRange2() {
        List<Integer> list = Fn.range(2, 10, 3);
        // expected: 2,5,8
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Integer.valueOf(2), list.get(0));
        Assert.assertEquals(Integer.valueOf(8), list.get(list.size() - 1));

        list = Fn.range(2, 12, 3);
        // expected: 2,5,8,11
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(4, list.size());
        Assert.assertEquals(Integer.valueOf(2), list.get(0));
        Assert.assertEquals(Integer.valueOf(11), list.get(list.size() - 1));

        list = Fn.range(10, 2, -3);
        // expected: 10,7,4
        System.err.println(list);
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(Integer.valueOf(10), list.get(0));
        Assert.assertEquals(Integer.valueOf(4), list.get(list.size() - 1));
    }
}
