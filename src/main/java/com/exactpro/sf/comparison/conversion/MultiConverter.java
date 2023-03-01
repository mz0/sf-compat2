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
package com.exactpro.sf.comparison.conversion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.exactpro.sf.comparison.conversion.impl.BigDecimalConverter;
import com.exactpro.sf.comparison.conversion.impl.BooleanConverter;
import com.exactpro.sf.comparison.conversion.impl.ByteConverter;
import com.exactpro.sf.comparison.conversion.impl.CharacterConverter;
import com.exactpro.sf.comparison.conversion.impl.DoubleConverter;
import com.exactpro.sf.comparison.conversion.impl.FloatConverter;
import com.exactpro.sf.comparison.conversion.impl.IntegerConverter;
import com.exactpro.sf.comparison.conversion.impl.LocalDateConverter;
import com.exactpro.sf.comparison.conversion.impl.LocalDateTimeConverter;
import com.exactpro.sf.comparison.conversion.impl.LocalTimeConverter;
import com.exactpro.sf.comparison.conversion.impl.LongConverter;
import com.exactpro.sf.comparison.conversion.impl.ShortConverter;
import com.exactpro.sf.comparison.conversion.impl.StringConverter;

public class MultiConverter {
    
    private static final Map<Class<?>, IConverter<?>> CONVERTERS = initConverters();
    
    public static final Set<Class<?>> SUPPORTED_TYPES = Collections.unmodifiableSet(CONVERTERS.keySet());
    
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, Class<T> clazz) {
        IConverter<?> converter = CONVERTERS.get(clazz);
        
        if (converter == null) {
            throw new ConversionException("No converter for type: " + clazz.getSimpleName());
        }
        
        return (T)converter.convert(value);
    }
    
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T, C extends Collection<T>> C convert(Collection<?> collection, Class<T> clazz, Supplier<C> supplier) {
        if (collection == null) {
            return null;
        }
        if (clazz == null) {
            throw new ConversionException("Parameter \'clazz\' must not be null");
        }
        if (supplier == null) {
            throw new ConversionException("Parameter \'supplier\' must not be null");
        }
        return collection.stream()
                .map(value -> convert(value, clazz))
                .collect(Collectors.toCollection(supplier));
    }
    
    protected static Map<Class<?>, IConverter<?>> initConverters() {
        
        Map<Class<?>, IConverter<?>> target = new HashMap<>();
        
        target.put(boolean.class, new BooleanConverter());
        target.put(Boolean.class, new BooleanConverter());
        target.put(byte.class, new ByteConverter());
        target.put(Byte.class, new ByteConverter());
        target.put(short.class, new ShortConverter());
        target.put(Short.class, new ShortConverter());
        target.put(int.class, new IntegerConverter());
        target.put(Integer.class, new IntegerConverter());
        target.put(long.class, new LongConverter());
        target.put(Long.class, new LongConverter());
        target.put(float.class, new FloatConverter());
        target.put(Float.class, new FloatConverter());
        target.put(double.class, new DoubleConverter());
        target.put(Double.class, new DoubleConverter());
        target.put(BigDecimal.class, new BigDecimalConverter());
        target.put(char.class, new CharacterConverter());
        target.put(Character.class, new CharacterConverter());
        target.put(String.class, new StringConverter());
        target.put(LocalDate.class, new LocalDateConverter());
        target.put(LocalTime.class, new LocalTimeConverter());
        target.put(LocalDateTime.class, new LocalDateTimeConverter());
        
        return target;
    }
}
