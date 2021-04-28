package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.meveo.model.payments.*;

import java.io.IOException;

class PaymentDeserializer extends JsonDeserializer<PaymentMethod> {

    @Override
    public PaymentMethod deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);
        if(node.get("id") != null){
            Long id = node.get("id").longValue();
            PaymentMethod paymentMethod = new PaymentMethod() {
                @Override
                public void updateWith(PaymentMethod otherPaymentMethod) {
                    return;
                }
            };
            paymentMethod.setId(id);
            return PaymentMethod.class.cast(paymentMethod);
        }else if(node.get("paymentType") != null){
            JsonParser p = codec.treeAsTokens(node);
            switch (node.get("paymentType").asText()){
                case "CHECK" :
                    return codec.readValue(p, CheckPaymentMethod.class);
                case "CARD" :
                    return codec.readValue(p, CardPaymentMethod.class);
                case "DIRECTDEBIT" :
                    return codec.readValue(p, DDPaymentMethod.class);
                case "WIRETRANSFER" :
                    return codec.readValue(p, WirePaymentMethod.class);
            }
        }
        return null;
    }
}
