/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcxiaoke.next.http;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public final class HttpMethod {

    /**
     * HTTP METHOD
     */
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";

    public static final Set<String> METHODS = new LinkedHashSet<String>(Arrays.asList(
            "OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "PATCH"));

    public static boolean invalidatesCache(String method) {
        return method.equals("POST")
                || method.equals("PATCH")
                || method.equals("PUT")
                || method.equals("DELETE");
    }

    public static boolean hasRequestBody(String method) {
        return method.equals("POST")
                || method.equals("PUT")
                || method.equals("PATCH")
                || method.equals("DELETE"); // Permitted as spec is ambiguous.
    }

    private HttpMethod() {
    }
}
