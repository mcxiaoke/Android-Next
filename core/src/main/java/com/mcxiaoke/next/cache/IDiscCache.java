/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.mcxiaoke.next.cache;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

public interface IDiscCache {

    void put(String key, byte[] data);

    void put(String key, InputStream stream);

    void put(String key, String text);

    String get(String key);

    File getFile(String key);

    byte[] getBytes(String key);

    boolean remove(String key);

    void clear();

    int delete(FileFilter filter);

    File getCacheDir();

    long getCacheSize();
}
