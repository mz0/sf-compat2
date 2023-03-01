/*
 * Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
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

import static com.exactpro.sf.common.messages.structures.DictionaryConstants.ATTRIBUTE_CREATE_DEFAULT_STRUCTURE;
import static com.exactpro.sf.common.messages.structures.DictionaryConstants.ATTRIBUTE_IS_ADMIN;
import static com.exactpro.sf.common.messages.structures.StructureUtils.getAttributeValue;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.exactpro.sf.common.messages.IHumanMessage;
import com.exactpro.sf.common.messages.IMessage;
import com.exactpro.sf.common.messages.IMessageFactory;
import com.exactpro.sf.common.messages.MsgMetaData;
import com.exactpro.sf.common.messages.structures.IDictionaryStructure;
import com.exactpro.sf.common.messages.structures.IFieldStructure;
import com.exactpro.sf.common.messages.structures.IMessageStructure;
import com.exactpro.sf.configuration.suri.SailfishURI;

/**
 * All custom message factories should extend this one
 */
public abstract class AbstractMessageFactory implements IMessageFactory {
    private String namespace;
    private SailfishURI dictionaryURI;
    private IDictionaryStructure dictionary;

    @Override
    public void init(SailfishURI dictionaryURI, IDictionaryStructure dictionary) {
        this.dictionaryURI = Objects.requireNonNull(dictionaryURI, "dictionaryURI cannot be null");
        this.dictionary = Objects.requireNonNull(dictionary, "dictionary cannot be null");
        this.namespace = dictionary.getNamespace();
    }

    @SuppressWarnings("deprecation") // Apply FIXME comments afte remiving
    @Override
    public void init(String namespace, SailfishURI dictionaryURI) {
        if(StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace cannot be blank");
        }

        this.namespace = namespace;
        this.dictionaryURI = Objects.requireNonNull(dictionaryURI, "dictionaryURI cannot be null");
    }

    @Override
    public IMessage createMessage(String name, String namespace) {
        return createMessage(new MsgMetaData(namespace, name));
    }

    @Override
    public IMessage createMessage(long id, String name, String namespace) {
        return createMessage(new MsgMetaData(namespace, name, id));
    }

    @Override
    public IMessage createMessage(String name) {
        return createMessage(name, namespace);
    }

    @Override
    public IHumanMessage createHumanMessage(String name) {
        return new HumanMessage();
    }
    
    @Override
    public void fillMessageType(IMessage message) {
        // TODO Auto-generated method stub
    }

    @Override
    public Set<String> getUncheckedFields() {
        return Collections.emptySet();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public SailfishURI getDictionaryURI() {
        return dictionaryURI;
    }

    @Override
    public IMessage createMessage(MsgMetaData metaData) {
        if (metaData.getMsgNamespace().equals(namespace)) {
            metaData.setDictionaryURI(dictionaryURI);
            metaData.setProtocol(getProtocol());

            if (dictionary != null) { //FIXME: Remove this check after removing init(String namespace, SailfishURI dictionaryURI) method
                IMessageStructure messageStructure = dictionary.getMessages().get(metaData.getMsgName());
                if (messageStructure != null) {
                    Boolean isAdmin = getAttributeValue(messageStructure, ATTRIBUTE_IS_ADMIN);
                    metaData.setAdmin(BooleanUtils.toBoolean(isAdmin));
                }
            }
        }
        IMessage message = new MapMessage(metaData);
        createComplexFields(message);
        return message;
    }

    protected void createComplexFields(IMessage message) {
        if (dictionary != null) { //FIXME: Remove this check after removing init(String namespace, SailfishURI dictionaryURI) method
            IMessageStructure messageStructure = dictionary.getMessages().get(message.getName());
            if (messageStructure != null) {
                for (IFieldStructure fieldStructure : messageStructure.getFields().values()) {
                    if (Boolean.TRUE.equals(getAttributeValue(fieldStructure, ATTRIBUTE_CREATE_DEFAULT_STRUCTURE)) //TODO: add check to default dictionary validator
                            && fieldStructure.isComplex()
                            && !fieldStructure.isCollection()) {

                        addSubMessage(fieldStructure, message);
                    }
                }
            }
        }
    }

    protected void addSubMessage(IFieldStructure fieldStructure, IMessage parentMessage) {
        parentMessage.addField(fieldStructure.getName(), createMessage(fieldStructure.getReferenceName(), fieldStructure.getNamespace()));
    }
}
