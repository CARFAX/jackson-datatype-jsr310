/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Base class for serializers used for {@link java.time.Instant} and related types.
 */
@SuppressWarnings("serial")
public abstract class InstantSerializerBase<T extends Temporal>
    extends JSR310FormattedSerializerBase<T>
{
    private final DateTimeFormatter defaultFormat;
    
    private final ToLongFunction<T> getEpochMillis;

    private final ToLongFunction<T> getEpochSeconds;

    private final ToIntFunction<T> getNanoseconds;

    protected InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis,
                                    ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds,
                                    DateTimeFormatter formatter)
    {
        // Bit complicated, just because we actually want to "hide" default formatter,
        // so that it won't accidentally force use of textual presentation
        super(supportedType, null);
        defaultFormat = formatter;
        this.getEpochMillis = getEpochMillis;
        this.getEpochSeconds = getEpochSeconds;
        this.getNanoseconds = getNanoseconds;
    }

    protected InstantSerializerBase(InstantSerializerBase<T> base,
            Boolean useTimestamp, DateTimeFormatter dtf)
    {
        super(base, useTimestamp, dtf);
        defaultFormat = base.defaultFormat;
        getEpochMillis = base.getEpochMillis;
        getEpochSeconds = base.getEpochSeconds;
        getNanoseconds = base.getNanoseconds;
    }

    @Override
    protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp,
            DateTimeFormatter dtf);

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                generator.writeNumber(DecimalUtils.toDecimal(
                        getEpochSeconds.applyAsLong(value), getNanoseconds.applyAsInt(value)
                ));
            } else {
                generator.writeNumber(getEpochMillis.applyAsLong(value));
            }
        } else {
            String str;
            
            if (_formatter != null) {
                str = _formatter.format(value);;
            } else if (defaultFormat != null) {
                str = defaultFormat.format(value);;
            } else {
                str = value.toString();
            }
            generator.writeString(str);
        }
    }
}
