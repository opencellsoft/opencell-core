package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.apiv2.refund.ImmutableCardRefund;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.scripts.ScriptInstance;

import javax.annotation.Nullable;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly=true)
@JsonDeserialize(builder = ImmutableDunningAction.Builder.class)
public interface DunningAction {
    @Nullable
    String getCode();

    @Nullable
    String getDescription();

    @Nullable
    String getActionType();

    @Nullable
    String getActionMode();

    @Nullable
    String getActionChannel();

    @Nullable
    Map<String,Long> getScript();

    @Nullable
    Map<String,Long> getActionNotificationTemplate();

    @Value.Default default boolean getAttachOverdueInvoices(){return false;}

    @Value.Default default boolean getAttachDueInvoices(){return false;}

    default org.meveo.model.payments.DunningAction toEntity() {
        org.meveo.model.payments.DunningAction dunningActionEntity = new org.meveo.model.payments.DunningAction();
        dunningActionEntity.setCode(getCode());
        dunningActionEntity.setDescription(getDescription());
        dunningActionEntity.setActionType(ActionTypeEnum.valueOf(getActionType()));
        dunningActionEntity.setActionMode(ActionModeEnum.valueOf(getActionMode()));
        dunningActionEntity.setActionChannel(ActionChannelEnum.valueOf(getActionChannel()));
        if(getScript() != null){
            ScriptInstance scriptInstance = new ScriptInstance();
            scriptInstance.setId(getScript().get("id"));
            dunningActionEntity.setScriptInstance(scriptInstance);
        }

        if(getActionNotificationTemplate() != null){
            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setId(getActionNotificationTemplate().get("id"));
            dunningActionEntity.setActionNotificationTemplate(emailTemplate);
        }
        dunningActionEntity.setAttachOverdueInvoices(getAttachOverdueInvoices());
        dunningActionEntity.setAttachDueInvoices(getAttachDueInvoices());
        return dunningActionEntity;
    }
}
