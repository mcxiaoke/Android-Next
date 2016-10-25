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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public enum HttpMethod {

    GET, POST, PUT, DELETE, HEAD, PATCH, OPTIONS, TRACE;

    public static final Set<String> METHODS = new HashSet<String>(Arrays.asList(
            "OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "PATCH"));

    public static boolean isValid(final String method) {
        return METHODS.contains(method);
    }

    public static boolean supportBody(final String method) {
        return supportBody(HttpMethod.valueOf(method));
    }

    public static boolean supportBody(final HttpMethod method) {
        return HttpMethod.POST.equals(method)
                || HttpMethod.PUT.equals(method)
                || HttpMethod.PATCH.equals(method);
    }
}
