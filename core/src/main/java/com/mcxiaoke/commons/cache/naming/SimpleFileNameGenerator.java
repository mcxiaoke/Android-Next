package com.mcxiaoke.commons.cache.naming;

/**
 * User: mcxiaoke
 * Date: 14-3-25
 * Time: 15:55
 */
public class SimpleFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(String key) {
        return getSafeFileName(key);
    }

    public static String getSafeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
