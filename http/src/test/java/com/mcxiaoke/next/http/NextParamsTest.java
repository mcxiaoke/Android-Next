package com.mcxiaoke.next.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 15/7/6
 * Time: 13:56
 */
public class NextParamsTest extends BaseTest {

    @Test
    public void create() {
        new NextParams();
    }

    @Test(expected = NullPointerException.class)
    public void nullException() {
        new NextParams(null);
    }

    @Test
    public void addQuery() {
        // null value not allowed
        NextParams p = new NextParams();
        p.query("key1", "value1");
        p.query("key2", null);
        Assert.assertTrue(p.hasQuery("key1"));
        Assert.assertFalse(p.hasQuery("key2"));
    }

    @Test
    public void addForm() {
        // null value not allowed
        NextParams p = new NextParams();
        p.form("key1", "value1");
        p.form("key2", null);
        Assert.assertTrue(p.hasForm("key1"));
        Assert.assertFalse(p.hasForm("key2"));
    }

    @Test
    public void addHeader() {
        // null value not allowed
        NextParams p = new NextParams();
        p.header("key1", "value1");
        p.header("key2", null);
        Assert.assertTrue(p.hasHeader("key1"));
        Assert.assertFalse(p.hasHeader("key2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void queryKeyNullOrEmptyKey() {
        // empty key not allowed
        NextParams p = new NextParams();
        p.query("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void formKeyNullOrEmptyKey() {
        // empty key not allowed
        NextParams p = new NextParams();
        p.form("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void headerKeyNullOrEmptyKey() {
        // empty key not allowed
        NextParams p = new NextParams();
        p.header("", "value");
    }

    @Test
    public void addEmptyOrNull() {
        NextParams p = new NextParams();
        p.queries(null);
        p.queries(new HashMap<String, String>());
        p.forms(null);
        p.forms(new HashMap<String, String>());
        p.headers(null);
        p.headers(new HashMap<String, String>());
    }

    @Test
    public void addQueries() {
        NextParams p = new NextParams();
        p.query("key1", "value1");
        p.query("key2", "value2");
        p.query("key3", null);
        Assert.assertEquals(2, p.queriesSize());
        Assert.assertFalse(p.hasQuery("key3"));
        Assert.assertEquals("value1", p.getQuery("key1"));
        Assert.assertEquals("value2", p.getQuery("key2"));
        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("key1", "value1-new");
        queries.put("k1", "v1");
        queries.put("k2", "v2");
        queries.put("k3", null);
        p.queries(queries);
        Assert.assertEquals(4, p.queriesSize());
        Assert.assertFalse(p.hasQuery("k3"));
        Assert.assertEquals("value1-new", p.getQuery("key1"));
        Assert.assertEquals("v1", p.getQuery("k1"));
        Assert.assertEquals("v2", p.getQuery("k2"));
    }

    @Test
    public void addForms() {
        NextParams p = new NextParams();
        p.form("key1", "value1");
        p.form("key2", "value2");
        p.form("key3", null);
        Assert.assertEquals(2, p.formsSize());
        Assert.assertFalse(p.hasForm("key3"));
        Assert.assertEquals("value1", p.getForm("key1"));
        Assert.assertEquals("value2", p.getForm("key2"));
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("key1", "value1-new");
        forms.put("k1", "v1");
        forms.put("k2", "v2");
        forms.put("k3", null);
        p.forms(forms);
        Assert.assertEquals(4, p.formsSize());
        Assert.assertFalse(p.hasForm("k3"));
        Assert.assertEquals("value1-new", p.getForm("key1"));
        Assert.assertEquals("v1", p.getForm("k1"));
        Assert.assertEquals("v2", p.getForm("k2"));
    }

    @Test
    public void addHeaders() {
        NextParams p = new NextParams();
        p.header("key1", "value1");
        p.header("key2", "value2");
        p.header("key3", null);
        Assert.assertEquals(2, p.headersSize());
        Assert.assertFalse(p.hasHeader("key3"));
        Assert.assertEquals("value1", p.getHeader("key1"));
        Assert.assertEquals("value2", p.getHeader("key2"));
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("key1", "value1-new");
        headers.put("k1", "v1");
        headers.put("k2", "v2");
        headers.put("k3", null);
        p.headers(headers);
        isEquals(4, p.headersSize());
        isFalse(p.hasHeader("k3"));
        isEquals("value1-new", p.getHeader("key1"));
        isEquals("v1", p.getHeader("k1"));
        isEquals("v2", p.getHeader("k2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFile() {
        NextParams p = new NextParams();
        p.file(null, new File("file"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyFile() {
        NextParams p = new NextParams();
        p.file("", new File("file"));
    }

    @Test
    public void addFile() {
        File file1 = new File("file1");
        File file2 = new File("file2");
        File file3 = new File("file3");
        NextParams p = new NextParams();
        p.file("file1", file1);
        p.file("file2", file2);
        p.file("file3", file3);
        isEquals(3, p.partsSize());
        notNull(p.getPart("file1"));
        notNull(p.getPart("file2"));
        notNull(p.getPart("file3"));
        isNull(p.getPart("none"));
    }

    @Test
    public void contentType() {
        String octStream = HttpConsts.APPLICATION_OCTET_STREAM;
        File imageJpeg = new File("image.jpg");
        File imagePng = new File("image.png");
        File plainText = new File("plain.txt");
        File file = new File("file");
        byte[] b1 = new byte[100];
        byte[] b2 = new byte[200];
        Arrays.fill(b1, (byte) 3);
        Arrays.fill(b2, (byte) 4);
        NextParams p = new NextParams();
        p.file("jpeg1", imageJpeg, "image/jpeg");
        p.file("jpeg2", imageJpeg);
        p.file("png1", imagePng, "image/png");
        p.file("png2", imagePng);
        p.file("txt1", plainText, "text/plain");
        p.file("txt2", plainText);
        p.file("file", file);
        p.file("b1", b1);
        p.file("b2", b2, "bin/plain");
        isEquals(9, p.partsSize());
        notNull(p.getPart("jpeg1"));
        notNull(p.getPart("jpeg2"));
        notNull(p.getPart("png1"));
        notNull(p.getPart("png2"));
        notNull(p.getPart("txt1"));
        notNull(p.getPart("txt2"));
        notNull(p.getPart("file"));
        notNull(p.getPart("b1"));
        notNull(p.getPart("b2"));
        isNull(p.getPart("none"));
        isEquals("image/jpeg", p.getPart("jpeg1").getContentType());
        isEquals(octStream, p.getPart("jpeg2").getContentType());
        isEquals("image/png", p.getPart("png1").getContentType());
        isEquals(octStream, p.getPart("png2").getContentType());
        isEquals("text/plain", p.getPart("txt1").getContentType());
        isEquals(octStream, p.getPart("txt2").getContentType());
        isEquals(octStream, p.getPart("file").getContentType());
        isEquals(octStream, p.getPart("b1").getContentType());
        isEquals("bin/plain", p.getPart("b2").getContentType());
    }

    @Test
    public void fileName() {
        File imageJpeg = new File("image.jpg");
        File imagePng = new File("image.png");
        File plainText = new File("plain.txt");
        File file1 = new File("file1");
        File file2 = new File("file2");
        File file3 = new File("file3");
        byte[] b1 = new byte[100];
        byte[] b2 = new byte[200];
        Arrays.fill(b1, (byte) 3);
        Arrays.fill(b2, (byte) 4);
        NextParams p = new NextParams();
        p.file("jpeg", imageJpeg, "image/jpeg");
        p.file("png", imagePng, "image/png", "name.png");
        p.file("txt", plainText, "text/plain");
        p.file("file1", file1);
        p.file("file2", file2, "plain/bin", null);
        p.file("file3", file3, "plain/bin", "file3.dat");
        p.file("b1", b1);
        p.file("b2", b2, "binary", "b2.dat");
        isEquals(8, p.partsSize());
        isTrue(p.hasPart("jpeg"));
        isTrue(p.hasPart("png"));
        isTrue(p.hasPart("txt"));
        isTrue(p.hasPart("file1"));
        isTrue(p.hasPart("file2"));
        isTrue(p.hasPart("file3"));
        isTrue(p.hasPart("b1"));
        isTrue(p.hasPart("b2"));
        isFalse(p.hasPart("none"));
        isEquals(imageJpeg.getName(), p.getPart("jpeg").getFileName());
        isEquals("name.png", p.getPart("png").getFileName());
        isEquals(plainText.getName(), p.getPart("txt").getFileName());
        isEquals(file1.getName(), p.getPart("file1").getFileName());
        isEquals(HttpConsts.DEFAULT_NAME, p.getPart("file2").getFileName());
        isEquals("file3.dat", p.getPart("file3").getFileName());
        isEquals(HttpConsts.DEFAULT_NAME, p.getPart("b1").getFileName());
        isEquals("b2.dat", p.getPart("b2").getFileName());
    }


}
