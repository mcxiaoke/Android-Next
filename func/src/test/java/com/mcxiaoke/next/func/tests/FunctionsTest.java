package com.mcxiaoke.next.func.tests;

import android.test.suitebuilder.annotation.SmallTest;
import com.mcxiaoke.next.func.Functions;
import com.mcxiaoke.next.func.functions.Func2;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 15/8/20
 * Time: 12:25
 */
@SmallTest
public class FunctionsTest {

    @Test
    public void testReduce() {
        List<Integer> list = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});
        Integer result = Functions.reduce(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(final Integer integer, final Integer o) {
                return integer + o;
            }
        }, list,10);
        Assert.assertEquals(25, (int) result);

    }
}
