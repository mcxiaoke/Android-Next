package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:41
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Creates {@link java.net.HttpURLConnection HTTP connections} for
 * {@link java.net.URL urls}.
 */
public interface ConnectionFactory {
    /**
     * Open an {@link java.net.HttpURLConnection} for the specified {@link java.net.URL}.
     *
     * @throws java.io.IOException
     */
    HttpURLConnection create(URL url) throws IOException;

    /**
     * Open an {@link java.net.HttpURLConnection} for the specified {@link java.net.URL}
     * and {@link java.net.Proxy}.
     *
     * @throws java.io.IOException
     */
    HttpURLConnection create(URL url, Proxy proxy) throws IOException;

    /**
     * A {@link ConnectionFactory} which uses the built-in
     * {@link java.net.URL#openConnection()}
     */
    ConnectionFactory DEFAULT = new ConnectionFactory() {
        public HttpURLConnection create(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }

        public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
            return (HttpURLConnection) url.openConnection(proxy);
        }
    };
}
