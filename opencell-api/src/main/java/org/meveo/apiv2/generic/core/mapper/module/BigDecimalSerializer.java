package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            jsonGenerator.writeNumber(0); // avoid display 0E-12 for API generic values
        } else {
            jsonGenerator.writeNumber(value);
        }
    }
}
