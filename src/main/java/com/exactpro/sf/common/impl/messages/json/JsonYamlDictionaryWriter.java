/*
 * Copyright 2009-2020 Exactpro (Exactpro Systems Limited)
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
package com.exactpro.sf.common.impl.messages.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.exactpro.sf.common.impl.messages.json.configuration.JsonYamlDictionary;
import com.exactpro.sf.common.util.EPSCommonException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

public class JsonYamlDictionaryWriter {

    private static ObjectMapper objectMapper;

    public static void write(JsonYamlDictionary dictionary, OutputStream output, boolean asYaml) {
        if (asYaml) {
            objectMapper = new ObjectMapper(
                    new YAMLFactory()
                            .disable(YAMLGenerator.Feature.USE_NATIVE_OBJECT_ID)
                            .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID)
            );
        } else {
            objectMapper = new ObjectMapper();
        }

        try {
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(output, dictionary);
        } catch (IOException exception) {
            throw new EPSCommonException("Failed to write dictionary:" + dictionary.getName(), exception);
        }
    }

    public static void write(JsonYamlDictionary dictionary, File outputFile, boolean asYaml) {

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            write(dictionary, outputStream, asYaml);
        } catch (IOException e) {
            throw new EPSCommonException("Failed to write dictionary:" + dictionary.getName(), e);
        }

    }

}
