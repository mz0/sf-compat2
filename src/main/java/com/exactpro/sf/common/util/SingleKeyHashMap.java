/*
 * Copyright 2009-2019 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exactpro.sf.common.util;

import java.util.LinkedHashMap;

public class SingleKeyHashMap<K, V> extends LinkedHashMap<K, V> {

    @Override
    public V put(K key, V value) {
        if (super.put(key, value) != null) {
            throw new IllegalArgumentException("Duplicate key \"" + key + "\"");
        }
        return null;
    }
}
