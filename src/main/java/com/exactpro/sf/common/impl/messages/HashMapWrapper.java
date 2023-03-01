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

package com.exactpro.sf.common.impl.messages;

import java.util.HashMap;
import java.util.Objects;

import com.exactpro.sf.common.messages.MsgMetaData;

public class HashMapWrapper<K, V> extends HashMap<K, V> {
    private final MsgMetaData metaData;

    public HashMapWrapper() {
        this.metaData = new MsgMetaData("Namespace", "Message");
        this.metaData.setDirty(true);
    }

    public HashMapWrapper(MsgMetaData metaData) {
        this.metaData = Objects.requireNonNull(metaData, "'Meta data' parameter");
    }

    private HashMapWrapper(HashMapWrapper<K,V> toCopy) {
        super(toCopy);
        this.metaData = toCopy.metaData;
    }

    @Override
    public HashMapWrapper clone() {
        return new HashMapWrapper<>(this);
    }

    public MsgMetaData getMetaData() {
        return metaData;
    }
}
