/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.mcxiaoke.commons.url;

import android.util.Pair;

import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Builder for urls with url-encoding applied to path, query param, etc.
 * <p/>
 * Escaping rules are from RFC 3986, RFC 1738 and the HTML 4 spec. This means that this diverges from the canonical
 * URI/URL rules for the sake of being what you want to actually make HTTP-useful URLs.
 */
public final class UrlBuilder {

    /**
     * IPv6 address, cribbed from http://stackoverflow.com/questions/46146/what-are-the-java-regular-expressions-for-matching-ipv4-and-ipv6-strings
     */
    private static final Pattern IPV6_PATTERN = Pattern
            .compile(
                    "\\A\\[((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)]\\z");

    /**
     * IPv4 dotted quad
     */
    private static final Pattern IPV4_PATTERN = Pattern
            .compile("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");


    private final String scheme;


    private final String host;


    private final Integer port;

    private final List<Pair<String, String>> queryParams = new ArrayList<Pair<String, String>>();

    private final List<PathSegment> pathSegments = new ArrayList<PathSegment>();

    private final PercentEncoder pathEncoder = UrlPercentEncoders.getPathEncoder();
    private final PercentEncoder regNameEncoder = UrlPercentEncoders.getRegNameEncoder();
    private final PercentEncoder matrixEncoder = UrlPercentEncoders.getMatrixEncoder();
    private final PercentEncoder queryEncoder = UrlPercentEncoders.getQueryEncoder();
    private final PercentEncoder fragmentEncoder = UrlPercentEncoders.getFragmentEncoder();


    private String fragment;

    private boolean forceTrailingSlash = false;

    /**
     * Create a URL with UTF-8 encoding.
     *
     * @param scheme scheme (e.g. http)
     * @param host   host (e.g. foo.com or 1.2.3.4 or [::1])
     * @param port   null or a positive integer
     */
    private UrlBuilder(String scheme, String host, Integer port) {
        this.host = host;
        this.scheme = scheme;
        this.port = port;
    }

    /**
     * Create a URL with an null port and UTF-8 encoding.
     *
     * @param scheme scheme (e.g. http)
     * @param host   host in any of the valid syntaxes: reg-name ( a dns name), ipv4 literal (1.2.3.4), ipv6 literal
     *               ([::1]), excluding IPvFuture since no one uses that in practice
     * @return a url builder
     * @see UrlBuilder#forHost(String scheme, String host, int port)
     */
    public static UrlBuilder forHost(String scheme, String host) {
        return new UrlBuilder(scheme, host, null);
    }

    /**
     * @param scheme scheme (e.g. http)
     * @param host   host in any of the valid syntaxes: reg-name ( a dns name), ipv4 literal (1.2.3.4), ipv6 literal
     *               ([::1]), excluding IPvFuture since no one uses that in practice
     * @param port   port
     * @return a url builder
     */
    public static UrlBuilder forHost(String scheme, String host, int port) {
        return new UrlBuilder(scheme, host, port);
    }

    /**
     * Calls {@link UrlBuilder#fromUrl(java.net.URL, java.nio.charset.CharsetDecoder)} with a UTF-8 CharsetDecoder.
     *
     * @param url url to initialize builder with
     * @return a UrlBuilder containing the host, path, etc. from the url
     * @throws java.nio.charset.CharacterCodingException if char decoding fails
     * @see UrlBuilder#fromUrl(java.net.URL, java.nio.charset.CharsetDecoder)
     */

    public static UrlBuilder fromUrl(URL url) throws CharacterCodingException {
        return fromUrl(url, UrlPercentEncoders.UTF_8.newDecoder());
    }

    /**
     * Create a UrlBuilder initialized with the contents of a {@link java.net.URL}.
     *
     * @param url            url to initialize builder with
     * @param charsetDecoder the decoder to decode encoded bytes with (except for reg names, which are always UTF-8)
     * @return a UrlBuilder containing the host, path, etc. from the url
     * @throws java.nio.charset.CharacterCodingException if decoding percent-encoded bytes fails and charsetDecoder is configured to
     *                                                   report errors
     * @see UrlBuilder#fromUrl(java.net.URL, java.nio.charset.CharsetDecoder)
     */

    public static UrlBuilder fromUrl(URL url, CharsetDecoder charsetDecoder) throws
            CharacterCodingException {

        PercentDecoder decoder = new PercentDecoder(charsetDecoder);
        // reg names must be encoded UTF-8
        PercentDecoder regNameDecoder;
        if (charsetDecoder.charset().equals(UrlPercentEncoders.UTF_8)) {
            regNameDecoder = decoder;
        } else {
            regNameDecoder = new PercentDecoder(UrlPercentEncoders.UTF_8.newDecoder());
        }

        Integer port = url.getPort();
        if (port == -1) {
            port = null;
        }

        UrlBuilder builder = new UrlBuilder(url.getProtocol(), regNameDecoder.decode(url.getHost()), port);

        buildFromPath(builder, decoder, url);

        buildFromQuery(builder, decoder, url);

        if (url.getRef() != null) {
            builder.fragment(decoder.decode(url.getRef()));
        }

        return builder;
    }

    /**
     * Add a path segment.
     *
     * @param segment a path segment
     * @return this
     */

    public UrlBuilder pathSegment(String segment) {
        pathSegments.add(new PathSegment(segment));
        return this;
    }

    /**
     * Add multiple path segments. Equivalent to successive calls to {@link UrlBuilder#pathSegment(String)}.
     *
     * @param segments path segments
     * @return this
     */

    public UrlBuilder pathSegments(String... segments) {
        for (String segment : segments) {
            pathSegment(segment);
        }

        return this;
    }

    /**
     * Add a query parameter. Query parameters will be encoded in the order added.
     *
     * @param name  param name
     * @param value param value
     * @return this
     */

    public UrlBuilder queryParam(String name, String value) {
        queryParams.add(Pair.create(name, value));
        return this;
    }

    /**
     * Add a matrix param to the last added path segment. If no segments have been added, the param will be added to the
     * root. Matrix params will be encoded in the order added.
     *
     * @param name  param name
     * @param value param value
     * @return this
     */

    public UrlBuilder matrixParam(String name, String value) {
        if (pathSegments.isEmpty()) {
            // create an empty path segment to represent a matrix param applied to the root
            pathSegment("");
        }

        PathSegment seg = pathSegments.get(pathSegments.size() - 1);
        seg.matrixParams.add(Pair.create(name, value));
        return this;
    }

    /**
     * Set the fragment.
     *
     * @param fragment fragment string
     * @return this
     */

    public UrlBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    /**
     * Force the generated URL to have a trailing slash at the end of the path.
     *
     * @return this
     */

    public UrlBuilder forceTrailingSlash() {
        forceTrailingSlash = true;
        return this;
    }

    /**
     * Encode the current builder state into a URL string.
     *
     * @return a well-formed URL string
     * @throws java.nio.charset.CharacterCodingException if character encoding fails and the encoder is configured to report errors
     */
    public String toUrlString() throws CharacterCodingException {
        StringBuilder buf = new StringBuilder();

        buf.append(scheme);
        buf.append("://");

        buf.append(encodeHost(host));
        if (port != null) {
            buf.append(':');
            buf.append(port);
        }

        for (PathSegment pathSegment : pathSegments) {
            buf.append('/');
            buf.append(pathEncoder.encode(pathSegment.segment));

            for (Pair<String, String> matrixParam : pathSegment.matrixParams) {
                buf.append(';');
                buf.append(matrixEncoder.encode(matrixParam.first));
                buf.append('=');
                buf.append(matrixEncoder.encode(matrixParam.second));
            }
        }

        if (forceTrailingSlash) {
            buf.append('/');
        }

        if (!queryParams.isEmpty()) {
            buf.append("?");
            Iterator<Pair<String, String>> qpIter = queryParams.iterator();
            while (qpIter.hasNext()) {
                Pair<String, String> queryParam = qpIter.next();
                buf.append(queryEncoder.encode(queryParam.first));
                buf.append('=');
                buf.append(queryEncoder.encode(queryParam.second));
                if (qpIter.hasNext()) {
                    buf.append('&');
                }
            }
        }

        if (fragment != null) {
            buf.append('#');
            buf.append(fragmentEncoder.encode(fragment));
        }

        return buf.toString();
    }

    /**
     * Populate a url builder based on the query of a url
     *
     * @param builder builder
     * @param decoder decoder
     * @param url     url
     * @throws java.nio.charset.CharacterCodingException
     */
    private static void buildFromQuery(UrlBuilder builder, PercentDecoder decoder, URL url) throws
            CharacterCodingException {
        if (url.getQuery() != null) {
            String q = url.getQuery();

            for (String queryChunk : q.split("&")) {
                String[] queryParamChunks = queryChunk.split("=");

                if (queryParamChunks.length != 2) {
                    throw new IllegalArgumentException("Malformed query param: <" + queryChunk + ">");
                }

                builder.queryParam(decoder.decode(queryParamChunks[0]), decoder.decode(queryParamChunks[1]));
            }
        }
    }

    /**
     * Populate the path segments of a url builder from a url
     *
     * @param builder builder
     * @param decoder decoder
     * @param url     url
     * @throws java.nio.charset.CharacterCodingException
     */
    private static void buildFromPath(UrlBuilder builder, PercentDecoder decoder, URL url) throws
            CharacterCodingException {
        for (String pathChunk : url.getPath().split("/")) {
            if (pathChunk.equals("")) {
                continue;
            }

            if (pathChunk.charAt(0) == ';') {
                builder.pathSegment("");
                // empty path segment, but matrix params
                for (String matrixChunk : pathChunk.substring(1).split(";")) {
                    buildFromMatrixParamChunk(decoder, builder, matrixChunk);
                }

                continue;
            }

            // otherwise, path chunk is non empty and does not start with a ';'

            String[] matrixChunks = pathChunk.split(";");

            // first chunk is always the path segment. If there is a trailing ; and no matrix params, the ; will
            // not be included in the final url.
            builder.pathSegment(decoder.decode(matrixChunks[0]));

            // if there any other chunks, they're matrix param pairs
            for (int i = 1; i < matrixChunks.length; i++) {
                buildFromMatrixParamChunk(decoder, builder, matrixChunks[i]);
            }
        }
    }

    private static void buildFromMatrixParamChunk(PercentDecoder decoder, UrlBuilder ub, String pathMatrixChunk) throws
            CharacterCodingException {
        String[] mtxPair = pathMatrixChunk.split("=");
        if (mtxPair.length != 2) {
            throw new IllegalArgumentException(
                    "Malformed matrix param: <" + pathMatrixChunk + ">");
        }

        String mtxName = mtxPair[0];
        String mtxVal = mtxPair[1];
        ub.matrixParam(decoder.decode(mtxName), decoder.decode(mtxVal));
    }

    /**
     * @param host original host string
     * @return host encoded as in RFC 3986 section 3.2.2
     */

    private String encodeHost(String host) throws CharacterCodingException {
        // matching order: IP-literal, IPv4, reg-name
        if (IPV4_PATTERN.matcher(host).matches() || IPV6_PATTERN.matcher(host).matches()) {
            return host;
        }

        // it's a reg-name, which MUST be encoded as UTF-8 (regardless of the rest of the URL)
        return regNameEncoder.encode(host);
    }

    /**
     * Bundle of a path segment name and any associated matrix params.
     */
    private static class PathSegment {
        private final String segment;
        private final List<Pair<String, String>> matrixParams = new ArrayList<Pair<String, String>>();

        PathSegment(String segment) {
            this.segment = segment;
        }
    }
}
