package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
final class Encoder {

    static final String ENCODING_UTF8 = Consts.ENCODING_UTF8;

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

    public static String encode(List<NameValuePair> params) {
        if (params == null || params.size() == 0) {
            return Consts.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        for (NameValuePair param : params) {
            String encodedParam = Encoder.encode(param.getName()).concat(Consts.PAIR_SEPARATOR)
                    .concat(Encoder.encode(param.getValue()));
            builder.append(Consts.PARAM_SEPARATOR).append(encodedParam);
        }
        return builder.toString().substring(1);
    }

    public static String appendQuery(String url, List<NameValuePair> params) {
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
            boolean hasQuery = url.indexOf(Consts.QUERY_STRING_SEPARATOR) != -1;
            builder.append(hasQuery ? Consts.PARAM_SEPARATOR : Consts.QUERY_STRING_SEPARATOR);
            builder.append(queryString);
            return builder.toString();
        }
    }

    public static List<NameValuePair> toNameValuePairs(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
            pairs.add(pair);
        }
        return pairs;
    }

    private String encode(Map<String, String> params, String encoding) {
        if (params == null || params.size() == 0) {
            return Consts.EMPTY_STRING;
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), encoding));
                encodedParams.append(Consts.PAIR_SEPARATOR);
                encodedParams.append(URLEncoder.encode(entry.getValue(), encoding));
                encodedParams.append(Consts.PARAM_SEPARATOR);
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + encoding, uee);
        }
    }

}
