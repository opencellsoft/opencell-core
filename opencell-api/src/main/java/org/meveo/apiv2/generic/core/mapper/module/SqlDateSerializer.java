package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Date;
import java.time.ZoneOffset;

public class SqlDateSerializer extends StdSerializer<Date> {


    protected SqlDateSerializer(Class<Date> type) {
        super(type);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.toLocalDate().atStartOfDay().plusHours(6).toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
