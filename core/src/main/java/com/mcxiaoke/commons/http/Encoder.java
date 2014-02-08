package com.mcxiaoke.commons.http;

import com.mcxiaoke.commons.utils.AssertUtils;
import com.mcxiaoke.commons.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
final class Encoder {

    static final String ENCODING_UTF8 = HttpConsts.ENCODING_UTF8;

    private static final Map<String, String> ENCODING_RULES;

    static {
        Map<String, String> rules = new HashMap<String, String>();
        rules.put("*", "%2A");
        rules.put("+", "%20");
        rules.put("%7E", "~");
        ENCODING_RULES = Collections.unmodifiableMap(rules);
    }

    public static String encode(String plain) {
        AssertUtils.notNull(plain, "Cannot encode null object");
        String encoded = "";
        try {
            encoded = URLEncoder.encode(plain, ENCODING_UTF8);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(
                    "Charset not found while encoding string: " + ENCODING_UTF8, uee);
        }
        for (Map.Entry<String, String> rule : ENCODING_RULES.entrySet()) {
            encoded = applyRule(encoded, rule.getKey(), rule.getValue());
        }
        return encoded;
    }

    private static String applyRule(String encoded, String toReplace,
                                    String replacement) {
        return encoded.replaceAll(Pattern.quote(toReplace), replacement);
    }

    public static String encode(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return HttpConsts.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String encodedParam = Encoder.encode(entry.getKey()).concat(HttpConsts.PAIR_SEPARATOR)
                    .concat(Encoder.encode(entry.getValue()));
            builder.append(HttpConsts.PARAM_SEPARATOR).append(encodedParam);
        }
        return builder.toString().substring(1);
    }

    public static String appendQueryString(String url, Map<String, String> params) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        if (params == null || params.size() == 0) {
            return url;
        }
        String queryString = encode(params);
        if (StringUtils.isEmpty(queryString)) {
            return url;
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(url);
            boolean hasQuery = url.indexOf(HttpConsts.QUERY_STRING_SEPARATOR) != -1;
            builder.append(hasQuery ? HttpConsts.PARAM_SEPARATOR : HttpConsts.QUERY_STRING_SEPARATOR);
            builder.append(queryString);
            return builder.toString();
        }
    }

    public static String cleanUrl(String uriString) {
        if (StringUtils.isEmpty(uriString)) {
            return uriString;
        }
        try {
            URL url = new URL(uriString);
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath()).toString();
        } catch (MalformedURLException e) {
            return uriString;
        }
    }


    public static String streamToString(InputStream is, String encoding) {
        AssertUtils.notNull(is, "Cannot get String from a null object");
        try {
            final char[] buffer = new char[HttpConsts.BUFFER_SIZE];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, encoding);
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);
            in.close();
            return out.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException(
                    "Error while reading response body", ioe);
        }
    }

    private byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[HttpConsts.BUFFER_SIZE];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

}
