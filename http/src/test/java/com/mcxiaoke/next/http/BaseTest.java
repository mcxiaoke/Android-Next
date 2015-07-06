package com.mcxiaoke.next.http;

import org.junit.Assert;

/**
 * User: mcxiaoke
 * Date: 15/7/6
 * Time: 14:41
 */
public class BaseTest {

    void isTrue(boolean condition) {
        Assert.assertTrue(condition);
    }

    void isFalse(boolean condition) {
        Assert.assertFalse(condition);
    }

    void notNull(Object object) {
        Assert.assertNotNull(object);
    }

    void isNull(Object object) {
        Assert.assertNull(object);
    }

    void isEquals(boolean expected, boolean actual) {
        Assert.assertEquals(expected, actual);
    }

    void isEquals(int expected, int actual) {
        Assert.assertEquals(expected, actual);
    }

    void isEquals(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }
}
