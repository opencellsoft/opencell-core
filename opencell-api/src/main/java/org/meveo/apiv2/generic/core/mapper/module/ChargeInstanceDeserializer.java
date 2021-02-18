package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.UsageChargeInstance;

import java.io.IOException;

/**
 * A custom deserializer for ChargeInstance
 *
 * @author Thang Nguyen
 * @since Nov 23, 2020
 */

class ChargeInstanceDeserializer extends JsonDeserializer<ChargeInstance> {

    @Override
    public ChargeInstance deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);

        JsonParser parser = codec.treeAsTokens(node);

        switch (node.get("chargeType").asText()){
            case "U" :
                return codec.readValue(parser, UsageChargeInstance.class);
            case "S" :
                return codec.readValue(parser, OneShotChargeInstance.class);
            case "R" :
                return codec.readValue(parser, RecurringChargeInstance.class);
        }

        return null;
    }
}
