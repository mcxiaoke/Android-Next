/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.mcxiaoke.commons.http;

import com.mcxiaoke.commons.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

/**
 * A collection of utilities for {@link java.net.URI URIs}, to workaround bugs within the
 * class or for ease-of-use features.
 *
 * @since 4.0
 */
public class URIUtilsEx {


    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static List<NameValuePair> getQueryParameters(String url,
                                                         final String encoding) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            return parse(new URI(url), encoding);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static Map<String, String> getQueryParameters2(String url,
                                                          final String encoding) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try {
            return parse2(new URI(url), encoding);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static List<NameValuePair> getQueryParameters(final URI uri) {
        if (uri != null) {
            return parse(uri, ENCODING_UTF8);
        }
        return null;
    }

    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static Map<String, String> getQueryParameters2(final URI uri) {
        if (uri != null) {
            return parse2(uri, ENCODING_UTF8);
        }
        return null;
    }

    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static List<NameValuePair> getQueryParameters(final URI uri,
                                                         final String encoding) {
        if (uri != null) {
            return parse(uri, encoding);
        }
        return null;
    }

    /**
     * 从URL中提取Query参数
     *
     * @param url
     * @param encoding
     * @return
     */
    public static Map<String, String> getQueryParameters2(final URI uri,
                                                          final String encoding) {
        if (uri != null) {
            return parse2(uri, encoding);
        }
        return null;
    }

    /**
     * 对Query参数编码为字符串
     *
     * @param parameters
     * @param encoding
     * @return
     */
    public static String getQueryString(List<NameValuePair> parameters) {
        if (parameters != null) {
            return format(parameters, ENCODING_UTF8);
        }
        return null;
    }

    /**
     * 对URL和Query字符串组合编码
     *
     * @param url
     * @param parameters
     * @return
     */
    public static String appendQueryParameters(String url,
                                               final Collection<? extends NameValuePair> parameters) {
        try {
            URIBuilderEx builder = new URIBuilderEx(url);
            builder.addParameters(parameters);
            return builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对URL和Query字符串组合编码
     *
     * @param url
     * @param parameters
     * @return
     */
    public static String appendQueryParameters(String url,
                                               final Map<String, String> parameters) {
        try {
            URIBuilderEx builder = new URIBuilderEx(url);
            builder.addParameters(parameters);
            return builder.build().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对URL和Query字符串组合编码
     *
     * @param url
     * @param parameters
     * @return
     */
    public static String appendQueryParameters2(String url,
                                                final Map<String, String> parameters) {
        final StringBuilder urlBuilder = new StringBuilder();
        if (url == null || parameters == null || parameters.size() == 0) {
            return urlBuilder.toString();
        }

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (urlBuilder.length() > 0) {
                urlBuilder.append(PARAMETER_SEPARATOR);
            }
            urlBuilder.append(encode(entry.getKey()));
            urlBuilder.append(NAME_VALUE_SEPARATOR);
            urlBuilder.append(encode(entry.getValue()));
        }

        if (!url.contains("?")) {
            urlBuilder.insert(0, "?");
        } else {
            urlBuilder.insert(0, PARAMETER_SEPARATOR);
        }

        urlBuilder.insert(0, url);
        return urlBuilder.toString();
    }

    /**
     * 参数编码
     *
     * @param text
     * @return
     */
    public static String encode(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        try {
            text = URLEncoder.encode(text, ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
        }
        return text;
    }

    /**
     * 参数编码，针对OAuth规范特殊处理
     *
     * @param value
     * @return
     */
    public static String percentEncode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, ENCODING_UTF8);
        } catch (UnsupportedEncodingException ignore) {
            throw new RuntimeException(ignore);
        }
        StringBuilder sb = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                sb.append("%2A");
            } else if (focus == '+') {
                sb.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                sb.append('~');
                i += 2;
            } else {
                sb.append(focus);
            }
        }
        return sb.toString();
    }

    /**
     * A convenience method for creating a new {@link java.net.URI} whose scheme, host
     * and port are taken from the target host, but whose path, query and
     * fragment are taken from the existing URI. The fragment is only used if
     * dropFragment is false. The path is set to "/" if not explicitly
     * specified.
     *
     * @param uri          Contains the path, query and fragment to use.
     * @param target       Contains the scheme, host and port to use.
     * @param dropFragment True if the fragment should not be copied.
     * @throws java.net.URISyntaxException If the resulting URI is invalid.
     */
    public static URI rewriteURI(final URI uri, final HttpHost target,
                                 boolean dropFragment) throws URISyntaxException {
        if (uri == null) {
            throw new IllegalArgumentException("URI may not be null");
        }
        URIBuilderEx builder = new URIBuilderEx(uri);
        if (target != null) {
            builder.setScheme(target.getSchemeName());
            builder.setHost(target.getHostName());
            builder.setPort(target.getPort());
        } else {
            builder.setScheme(null);
            builder.setHost(null);
            builder.setPort(-1);
        }
        if (dropFragment) {
            builder.setFragment(null);
        }
        if (builder.getPath() == null || builder.getPath().length() == 0) {
            builder.setPath("/");
        }
        return builder.build();
    }

    /**
     * A convenience method for
     * {@link com.mcxiaoke.commons.http.URIUtilsEx#rewriteURI(java.net.URI, org.apache.http.HttpHost, boolean)} that always keeps
     * the fragment.
     */
    public static URI rewriteURI(final URI uri, final HttpHost target)
            throws URISyntaxException {
        return rewriteURI(uri, target, false);
    }

    /**
     * A convenience method that creates a new {@link java.net.URI} whose scheme, host,
     * port, path, query are taken from the existing URI, dropping any fragment
     * or user-information. The path is set to "/" if not explicitly specified.
     * The existing URI is returned unmodified if it has no fragment or
     * user-information and has a path.
     *
     * @param uri original URI.
     * @throws java.net.URISyntaxException If the resulting URI is invalid.
     */
    public static URI rewriteURI(final URI uri) throws URISyntaxException {
        if (uri == null) {
            throw new IllegalArgumentException("URI may not be null");
        }
        if (uri.getFragment() != null || uri.getUserInfo() != null
                || (uri.getPath() == null || uri.getPath().length() == 0)) {
            URIBuilderEx builder = new URIBuilderEx(uri);
            builder.setFragment(null).setUserInfo(null);
            if (builder.getPath() == null
                    || builder.getPath().length() == 0) {
                builder.setPath("/");
            }
            return builder.build();
        } else {
            return uri;
        }
    }

    /**
     * Resolves a URI reference against a base URI. Work-around for bug in
     * java.net.URI
     * (<http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708535>)
     *
     * @param baseURI   the base URI
     * @param reference the URI reference
     * @return the resulting URI
     */
    public static URI resolve(final URI baseURI, final String reference) {
        return URIUtilsEx.resolve(baseURI, URI.create(reference));
    }

    /**
     * Resolves a URI reference against a base URI. Work-around for bugs in
     * java.net.URI (e.g.
     * <http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4708535>)
     *
     * @param baseURI   the base URI
     * @param reference the URI reference
     * @return the resulting URI
     */
    public static URI resolve(final URI baseURI, URI reference) {
        if (baseURI == null) {
            throw new IllegalArgumentException("Base URI may nor be null");
        }
        if (reference == null) {
            throw new IllegalArgumentException("Reference URI may nor be null");
        }
        String s = reference.toString();
        if (s.startsWith("?")) {
            return resolveReferenceStartingWithQueryString(baseURI, reference);
        }
        boolean emptyReference = s.length() == 0;
        if (emptyReference) {
            reference = URI.create("#");
        }
        URI resolved = baseURI.resolve(reference);
        if (emptyReference) {
            String resolvedString = resolved.toString();
            resolved = URI.create(resolvedString.substring(0,
                    resolvedString.indexOf('#')));
        }
        return removeDotSegments(resolved);
    }

    /**
     * Resolves a reference starting with a query string.
     *
     * @param baseURI   the base URI
     * @param reference the URI reference starting with a query string
     * @return the resulting URI
     */
    private static URI resolveReferenceStartingWithQueryString(
            final URI baseURI, final URI reference) {
        String baseUri = baseURI.toString();
        baseUri = baseUri.indexOf('?') > -1 ? baseUri.substring(0,
                baseUri.indexOf('?')) : baseUri;
        return URI.create(baseUri + reference.toString());
    }

    /**
     * Removes dot segments according to RFC 3986, section 5.2.4
     *
     * @param uri the original URI
     * @return the URI without dot segments
     */
    private static URI removeDotSegments(URI uri) {
        String path = uri.getPath();
        if ((path == null) || (path.indexOf("/.") == -1)) {
            // No dot segments to remove
            return uri;
        }
        String[] inputSegments = path.split("/");
        Stack<String> outputSegments = new Stack<String>();
        for (int i = 0; i < inputSegments.length; i++) {
            if ((inputSegments[i].length() == 0)
                    || (".".equals(inputSegments[i]))) {
                // Do nothing
            } else if ("..".equals(inputSegments[i])) {
                if (!outputSegments.isEmpty()) {
                    outputSegments.pop();
                }
            } else {
                outputSegments.push(inputSegments[i]);
            }
        }
        StringBuilder outputBuffer = new StringBuilder();
        for (String outputSegment : outputSegments) {
            outputBuffer.append('/').append(outputSegment);
        }
        try {
            return new URI(uri.getScheme(), uri.getAuthority(),
                    outputBuffer.toString(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Extracts target host from the given {@link java.net.URI}.
     *
     * @param uri
     * @return the target host if the URI is absolute or
     * <code>null</null> if the URI is
     * relative or does not contain a valid host name.
     * @since 4.1
     */
    public static HttpHost extractHost(final URI uri) {
        if (uri == null) {
            return null;
        }
        HttpHost target = null;
        if (uri.isAbsolute()) {
            int port = uri.getPort(); // may be overridden later
            String host = uri.getHost();
            if (host == null) { // normal parse failed; let's do it ourselves
                // authority does not seem to care about the valid character-set
                // for host names
                host = uri.getAuthority();
                if (host != null) {
                    // Strip off any leading user credentials
                    int at = host.indexOf('@');
                    if (at >= 0) {
                        if (host.length() > at + 1) {
                            host = host.substring(at + 1);
                        } else {
                            host = null; // @ on its own
                        }
                    }
                    // Extract the port suffix, if present
                    if (host != null) {
                        int colon = host.indexOf(':');
                        if (colon >= 0) {
                            int pos = colon + 1;
                            int len = 0;
                            for (int i = pos; i < host.length(); i++) {
                                if (Character.isDigit(host.charAt(i))) {
                                    len++;
                                } else {
                                    break;
                                }
                            }
                            if (len > 0) {
                                try {
                                    port = Integer.parseInt(host.substring(pos,
                                            pos + len));
                                } catch (NumberFormatException ex) {
                                }
                            }
                            host = host.substring(0, colon);
                        }
                    }
                }
            }
            String scheme = uri.getScheme();
            if (host != null) {
                target = new HttpHost(host, port, scheme);
            }
        }
        return target;
    }

    /**
     * Returns a list of {@link org.apache.http.NameValuePair NameValuePairs} as built from the
     * URI's query portion. For example, a URI of
     * http://example.org/path/to/file?a=1&b=2&c=3 would return a list of three
     * NameValuePairs, one for a=1, one for b=2, and one for c=3.
     * <p/>
     * This is typically useful while parsing an HTTP PUT.
     *
     * @param uri      uri to parse
     * @param encoding encoding to use while parsing the query
     */
    public static List<NameValuePair> parse(final URI uri, final String encoding) {
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            List<NameValuePair> result = new ArrayList<NameValuePair>();
            Scanner scanner = new Scanner(query);
            parse(result, scanner, encoding);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Returns a list of {@link org.apache.http.NameValuePair NameValuePairs} as built from the
     * URI's query portion. For example, a URI of
     * http://example.org/path/to/file?a=1&b=2&c=3 would return a list of three
     * NameValuePairs, one for a=1, one for b=2, and one for c=3.
     * <p/>
     * This is typically useful while parsing an HTTP PUT.
     *
     * @param uri      uri to parse
     * @param encoding encoding to use while parsing the query
     */
    public static Map<String, String> parse2(final URI uri,
                                             final String encoding) {
        final String query = uri.getRawQuery();
        if (query != null && query.length() > 0) {
            Map<String, String> result = new HashMap<String, String>();
            Scanner scanner = new Scanner(query);
            parse2(result, scanner, encoding);
            return result;
        } else {
            return null;
        }
    }

    /**
     * Returns true if the entity's Content-Type header is
     * <code>application/x-www-form-urlencoded</code>.
     */
    public static boolean isEncoded(final HttpEntity entity) {
        Header h = entity.getContentType();
        if (h != null) {
            HeaderElement[] elems = h.getElements();
            if (elems.length > 0) {
                String contentType = elems[0].getName();
                return contentType.equalsIgnoreCase(CONTENT_TYPE);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Adds all parameters within the Scanner to the list of
     * <code>parameters</code>, as encoded by <code>encoding</code>. For
     * example, a scanner containing the string <code>a=1&b=2&c=3</code> would
     * add the {@link org.apache.http.NameValuePair NameValuePairs} a=1, b=2, and c=3 to the
     * list of parameters.
     *
     * @param parameters List to add parameters to.
     * @param scanner    Input that contains the parameters to parse.
     * @param charset    Encoding to use when decoding the parameters.
     */
    public static void parse(final List<NameValuePair> parameters,
                             final Scanner scanner, final String charset) {
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            String name = null;
            String value = null;
            String token = scanner.next();
            int i = token.indexOf(NAME_VALUE_SEPARATOR);
            if (i != -1) {
                name = decodeFormFields(token.substring(0, i).trim(), charset);
                value = decodeFormFields(token.substring(i + 1).trim(), charset);
            } else {
                name = decodeFormFields(token.trim(), charset);
            }
            parameters.add(new BasicNameValuePair(name, value));
        }
    }

    /**
     * Adds all parameters within the Scanner to the list of
     * <code>parameters</code>, as encoded by <code>encoding</code>. For
     * example, a scanner containing the string <code>a=1&b=2&c=3</code> would
     * add the {@link org.apache.http.NameValuePair NameValuePairs} a=1, b=2, and c=3 to the
     * list of parameters.
     *
     * @param parameters List to add parameters to.
     * @param scanner    Input that contains the parameters to parse.
     * @param charset    Encoding to use when decoding the parameters.
     */
    public static void parse2(final Map<String, String> parameters,
                              final Scanner scanner, final String charset) {
        scanner.useDelimiter(PARAMETER_SEPARATOR);
        while (scanner.hasNext()) {
            String key = null;
            String value = null;
            String token = scanner.next();
            int i = token.indexOf(NAME_VALUE_SEPARATOR);
            if (i != -1) {
                key = decodeFormFields(token.substring(0, i).trim(), charset);
                value = decodeFormFields(token.substring(i + 1).trim(), charset);
            } else {
                key = decodeFormFields(token.trim(), charset);
            }
            parameters.put(key, value);
        }
    }

    private static final char[] DELIM = new char[]{'&'};

    /**
     * Returns a list of {@link org.apache.http.NameValuePair NameValuePairs} as parsed from the
     * given string using the given character encoding.
     *
     * @param s       text to parse.
     * @param charset Encoding to use when decoding the parameters.
     * @since 4.2
     */
    public static List<NameValuePair> parse(final String s,
                                            final Charset charset) {
        if (s == null) {
            return Collections.emptyList();
        }
        BasicHeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
        CharArrayBuffer buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        ParserCursor cursor = new ParserCursor(0, buffer.length());
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            NameValuePair nvp = parser
                    .parseNameValuePair(buffer, cursor, DELIM);
            if (nvp.getName().length() > 0) {
                list.add(new BasicNameValuePair(decodeFormFields(nvp.getName(),
                        charset), decodeFormFields(nvp.getValue(), charset)));
            }
        }
        return list;
    }

    /**
     * Returns a String that is suitable for use as an
     * <code>application/x-www-form-urlencoded</code> list of parameters in an
     * HTTP PUT or HTTP POST.
     *
     * @param parameters The parameters to include.
     * @param encoding   The encoding to use.
     */
    public static String format(final List<? extends NameValuePair> parameters,
                                final String encoding) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encodeFormFields(parameter.getName(),
                    encoding);
            final String encodedValue = encodeFormFields(parameter.getValue(),
                    encoding);
            if (result.length() > 0) {
                result.append(PARAMETER_SEPARATOR);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    /**
     * Returns a String that is suitable for use as an
     * <code>application/x-www-form-urlencoded</code> list of parameters in an
     * HTTP PUT or HTTP POST.
     *
     * @param parameters The parameters to include.
     * @param charset    The encoding to use.
     * @since 4.2
     */
    public static String format(
            final Iterable<? extends NameValuePair> parameters,
            final Charset charset) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : parameters) {
            final String encodedName = encodeFormFields(parameter.getName(),
                    charset);
            final String encodedValue = encodeFormFields(parameter.getValue(),
                    charset);
            if (result.length() > 0) {
                result.append(PARAMETER_SEPARATOR);
            }
            result.append(encodedName);
            if (encodedValue != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(encodedValue);
            }
        }
        return result.toString();
    }

    /**
     * Emcode/escape a portion of a URL, to use with the query part ensure
     * {@code plusAsBlank} is true.
     *
     * @param content     the portion to decode
     * @param charset     the charset to use
     * @param blankAsPlus if {@code true}, then convert space to '+' (e.g. for
     *                    www-url-form-encoded content), otherwise leave as is.
     * @return
     */
    private static String urlencode(final String content,
                                    final Charset charset, final BitSet safechars,
                                    final boolean blankAsPlus) {
        if (content == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        ByteBuffer bb = charset.encode(content);
        while (bb.hasRemaining()) {
            int b = bb.get() & 0xff;
            if (safechars.get(b)) {
                buf.append((char) b);
            } else if (blankAsPlus && b == ' ') {
                buf.append('+');
            } else {
                buf.append("%");
                char hex1 = Character.toUpperCase(Character.forDigit(
                        (b >> 4) & 0xF, RADIX));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF,
                        RADIX));
                buf.append(hex1);
                buf.append(hex2);
            }
        }
        return buf.toString();
    }

    /**
     * Decode/unescape a portion of a URL, to use with the query part ensure
     * {@code plusAsBlank} is true.
     *
     * @param content     the portion to decode
     * @param charset     the charset to use
     * @param plusAsBlank if {@code true}, then convert '+' to space (e.g. for
     *                    www-url-form-encoded content), otherwise leave as is.
     * @return
     */
    private static String urldecode(final String content,
                                    final Charset charset, final boolean plusAsBlank) {
        if (content == null) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(content.length());
        CharBuffer cb = CharBuffer.wrap(content);
        while (cb.hasRemaining()) {
            char c = cb.get();
            if (c == '%' && cb.remaining() >= 2) {
                char uc = cb.get();
                char lc = cb.get();
                int u = Character.digit(uc, 16);
                int l = Character.digit(lc, 16);
                if (u != -1 && l != -1) {
                    bb.put((byte) ((u << 4) + l));
                } else {
                    bb.put((byte) '%');
                    bb.put((byte) uc);
                    bb.put((byte) lc);
                }
            } else if (plusAsBlank && c == '+') {
                bb.put((byte) ' ');
            } else {
                bb.put((byte) c);
            }
        }
        bb.flip();
        return charset.decode(bb).toString();
    }

    /**
     * Decode/unescape www-url-form-encoded content.
     *
     * @param content the content to decode, will decode '+' as space
     * @param charset the charset to use
     * @return
     */
    private static String decodeFormFields(final String content,
                                           final String charset) {
        if (content == null) {
            return null;
        }
        return urldecode(content, charset != null ? Charset.forName(charset)
                : UTF_8, true);
    }

    /**
     * Decode/unescape www-url-form-encoded content.
     *
     * @param content the content to decode, will decode '+' as space
     * @param charset the charset to use
     * @return
     */
    private static String decodeFormFields(final String content,
                                           final Charset charset) {
        if (content == null) {
            return null;
        }
        return urldecode(content, charset != null ? charset : UTF_8, true);
    }

    /**
     * Encode/escape www-url-form-encoded content.
     * <p/>
     * Uses the {@link #URLENCODER} set of characters, rather than the
     * {@link #UNRSERVED} set; this is for compatibilty with previous releases,
     * URLEncoder.encode() and most browsers.
     *
     * @param content the content to encode, will convert space to '+'
     * @param charset the charset to use
     * @return
     */
    private static String encodeFormFields(final String content,
                                           final String charset) {
        if (content == null) {
            return null;
        }
        return urlencode(content, charset != null ? Charset.forName(charset)
                : UTF_8, URLENCODER, true);
    }

    /**
     * Encode/escape www-url-form-encoded content.
     * <p/>
     * Uses the {@link #URLENCODER} set of characters, rather than the
     * {@link #UNRSERVED} set; this is for compatibilty with previous releases,
     * URLEncoder.encode() and most browsers.
     *
     * @param content the content to encode, will convert space to '+'
     * @param charset the charset to use
     * @return
     */
    private static String encodeFormFields(final String content,
                                           final Charset charset) {
        if (content == null) {
            return null;
        }
        return urlencode(content, charset != null ? charset : UTF_8,
                URLENCODER, true);
    }

    /**
     * Encode a String using the {@link #USERINFO} set of characters.
     * <p/>
     * Used by URIBuilder to encode the userinfo segment.
     *
     * @param content the string to encode, does not convert space to '+'
     * @param charset the charset to use
     * @return the encoded string
     */
    static String encUserInfo(final String content, final Charset charset) {
        return urlencode(content, charset, USERINFO, false);
    }

    /**
     * Encode a String using the {@link #FRAGMENT} set of characters.
     * <p/>
     * Used by URIBuilder to encode the userinfo segment.
     *
     * @param content the string to encode, does not convert space to '+'
     * @param charset the charset to use
     * @return the encoded string
     */
    static String encFragment(final String content, final Charset charset) {
        return urlencode(content, charset, FRAGMENT, false);
    }

    /**
     * Encode a String using the {@link #PATHSAFE} set of characters.
     * <p/>
     * Used by URIBuilder to encode path segments.
     *
     * @param content the string to encode, does not convert space to '+'
     * @param charset the charset to use
     * @return the encoded string
     */
    static String encPath(final String content, final Charset charset) {
        return urlencode(content, charset, PATHSAFE, false);
    }

    /**
     * Unreserved characters, i.e. alphanumeric, plus: {@code _ - ! . ~ ' ( ) *}
     * <p/>
     * This list is the same as the {@code unreserved} list in <a
     * href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>
     */
    private static final BitSet UNRESERVED = new BitSet(256);
    /**
     * Punctuation characters: , ; : $ & + =
     * <p/>
     * These are the additional characters allowed by userinfo.
     */
    private static final BitSet PUNCT = new BitSet(256);
    /**
     * Characters which are safe to use in userinfo, i.e. {@link #UNRESERVED}
     * plus {@link #PUNCT}uation
     */
    private static final BitSet USERINFO = new BitSet(256);
    /**
     * Characters which are safe to use in a path, i.e. {@link #UNRESERVED} plus
     * {@link #PUNCT}uation plus / @
     */
    private static final BitSet PATHSAFE = new BitSet(256);
    /**
     * Characters which are safe to use in a fragment, i.e. {@link #RESERVED}
     * plus {@link #UNRESERVED}
     */
    private static final BitSet FRAGMENT = new BitSet(256);

    /**
     * Reserved characters, i.e. {@code ;/?:@&=+$,[]}
     * <p/>
     * This list is the same as the {@code reserved} list in <a
     * href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a> as augmented by
     * <a href="http://www.ietf.org/rfc/rfc2732.txt">RFC 2732</a>
     */
    private static final BitSet RESERVED = new BitSet(256);

    /**
     * Safe characters for x-www-form-urlencoded data, as per
     * java.net.URLEncoder and browser behaviour, i.e. alphanumeric plus
     * {@code "-", "_", ".", "*"}
     */
    private static final BitSet URLENCODER = new BitSet(256);

    static {
        // unreserved chars
        // alpha characters
        for (int i = 'a'; i <= 'z'; i++) {
            UNRESERVED.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            UNRESERVED.set(i);
        }
        // numeric characters
        for (int i = '0'; i <= '9'; i++) {
            UNRESERVED.set(i);
        }
        UNRESERVED.set('_'); // these are the charactes of the "mark" list
        UNRESERVED.set('-');
        UNRESERVED.set('.');
        UNRESERVED.set('*');
        URLENCODER.or(UNRESERVED); // skip remaining unreserved characters
        UNRESERVED.set('!');
        UNRESERVED.set('~');
        UNRESERVED.set('\'');
        UNRESERVED.set('(');
        UNRESERVED.set(')');
        // punct chars
        PUNCT.set(',');
        PUNCT.set(';');
        PUNCT.set(':');
        PUNCT.set('$');
        PUNCT.set('&');
        PUNCT.set('+');
        PUNCT.set('=');
        // Safe for userinfo
        USERINFO.or(UNRESERVED);
        USERINFO.or(PUNCT);

        // URL path safe
        PATHSAFE.or(UNRESERVED);
        PATHSAFE.set('/'); // segment separator
        PATHSAFE.set(';'); // param separator
        PATHSAFE.set(':'); // rest as per list in 2396, i.e. : @ & = + $ ,
        PATHSAFE.set('@');
        PATHSAFE.set('&');
        PATHSAFE.set('=');
        PATHSAFE.set('+');
        PATHSAFE.set('$');
        PATHSAFE.set(',');

        RESERVED.set(';');
        RESERVED.set('/');
        RESERVED.set('?');
        RESERVED.set(':');
        RESERVED.set('@');
        RESERVED.set('&');
        RESERVED.set('=');
        RESERVED.set('+');
        RESERVED.set('$');
        RESERVED.set(',');
        RESERVED.set('['); // added by RFC 2732
        RESERVED.set(']'); // added by RFC 2732

        FRAGMENT.or(RESERVED);
        FRAGMENT.or(UNRESERVED);
    }

    private static final int RADIX = 16;

    public static final int CR = 13; // <US-ASCII CR, carriage return (13)>
    public static final int LF = 10; // <US-ASCII LF, linefeed (10)>
    public static final int SP = 32; // <US-ASCII SP, space (32)>
    public static final int HT = 9; // <US-ASCII HT, horizontal-tab (9)>

    public static final String ENCODING_UTF8 = "UTF-8";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final Charset ASCII = Charset.forName("US-ASCII");
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String NAME_VALUE_SEPARATOR = "=";
    public static final String QUERY_STRING_SEPARATOR = "?";
    public static final String EMPTY_STRING = "";

    /**
     * This class should not be instantiated.
     */
    private URIUtilsEx() {
    }

}
