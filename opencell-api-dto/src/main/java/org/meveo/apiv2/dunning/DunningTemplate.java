package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.communication.MessageTemplateTypeEnum;
import org.meveo.model.payments.ActionChannelEnum;

import javax.annotation.Nullable;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableDunningTemplate.Builder.class)
public interface DunningTemplate {

    @Nullable
    String getCode();

    @Nullable
    Map<String, Long> getLanguage();

    @Nullable
    String getActionChannel();

    @Value.Default default boolean active(){return true;}

    @Value.Default default String getType(){
        return "DUNNING";
    }

    @Nullable
    String getSubject();

    @Nullable
    String getHtmlContent();

    default org.meveo.model.dunning.DunningTemplate toEntity() {
        org.meveo.model.dunning.DunningTemplate dunningTemplate = new org.meveo.model.dunning.DunningTemplate();
        dunningTemplate.setCode(getCode());
        if(getLanguage() != null){
            TradingLanguage tradingLanguage = new TradingLanguage();
            tradingLanguage.setId(getLanguage().get("id"));
            dunningTemplate.setLanguage(tradingLanguage);
        }
        if(getActionChannel() != null){
            dunningTemplate.setChannel(ActionChannelEnum.valueOf(getActionChannel()));
        }
        dunningTemplate.setActive(active());
        if(getType() != null){
            dunningTemplate.setType(MessageTemplateTypeEnum.valueOf(getType()));
        }
        dunningTemplate.setSubject(getSubject());
        dunningTemplate.setHtmlContent(getHtmlContent());
        return dunningTemplate;
    }

}
