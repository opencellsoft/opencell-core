package org.meveo.apiv2.dunning;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.scripts.ScriptInstance;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
@Value.Style(jdkOnly = true)
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
    Set<Map<String, Long>> getRelatedLevels();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Long> getScript();

    @Nullable
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Long> getActionNotificationTemplate();

    @Value.Default
    default boolean getAttachOverdueInvoices() {
        return false;
    }

    @Value.Default
    default boolean getAttachDueInvoices() {
        return false;
    }

    default org.meveo.model.dunning.DunningAction toEntity() {
        org.meveo.model.dunning.DunningAction dunningActionEntity = new org.meveo.model.dunning.DunningAction();
        dunningActionEntity.setCode(getCode());
        dunningActionEntity.setDescription(getDescription());
        if (getActionType() != null && !getActionType().isBlank()) {
            dunningActionEntity.setActionType(ActionTypeEnum.valueOf(getActionType()));
        }
        if (getActionMode() != null && !getActionMode().isBlank()) {
            dunningActionEntity.setActionMode(ActionModeEnum.valueOf(getActionMode()));
        }
        if (getActionChannel() != null && !getActionChannel().isBlank()) {
            dunningActionEntity.setActionChannel(ActionChannelEnum.valueOf(getActionChannel()));
        }
        if (getScript() != null) {
            ScriptInstance scriptInstance = new ScriptInstance();
            scriptInstance.setId(getScript().get("id"));
            dunningActionEntity.setScriptInstance(scriptInstance);
        }

        if (getActionNotificationTemplate() != null) {
            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setId(getActionNotificationTemplate().get("id"));
            dunningActionEntity.setActionNotificationTemplate(emailTemplate);
        }

        if (getRelatedLevels() != null && !getRelatedLevels().isEmpty()) {
            dunningActionEntity.setRelatedLevels(getRelatedLevels().stream()
                    .map(dunningLevelIds -> {
                        DunningLevel dunningLevel = new DunningLevel();
                        dunningLevel.setId(dunningLevelIds.get("id"));
                        return dunningLevel;
                    })
                    .collect(Collectors.toList()));
        }
        dunningActionEntity.setAttachOverdueInvoices(getAttachOverdueInvoices());
        dunningActionEntity.setAttachDueInvoices(getAttachDueInvoices());
        return dunningActionEntity;
    }

    default ImmutableDunningAction toDunningAction(org.meveo.model.dunning.DunningAction dunningAction) {
        ImmutableDunningAction.Builder immutableDunningAction = ImmutableDunningAction.builder()
                .code(dunningAction.getCode())
                .description(dunningAction.getDescription())
                .actionType(dunningAction.getActionType().name())
                .actionMode(dunningAction.getActionMode().name())
                .actionChannel(dunningAction.getActionChannel().name())
                .attachOverdueInvoices(dunningAction.isAttachOverdueInvoices())
                .attachDueInvoices(dunningAction.isAttachDueInvoices());

        if (dunningAction.getActionNotificationTemplate() != null) {
            immutableDunningAction.actionNotificationTemplate(Collections.singletonMap("id", dunningAction.getActionNotificationTemplate().getId()));
        }
        if (dunningAction.getScriptInstance() != null) {
            immutableDunningAction.script(Collections.singletonMap("id", dunningAction.getScriptInstance().getId()));
        }
        if (dunningAction.getRelatedLevels() != null && !dunningAction.getRelatedLevels().isEmpty()) {
            immutableDunningAction.relatedLevels(dunningAction.getRelatedLevels().stream()
                    .map(dunningLevel -> Collections.singletonMap("id", dunningLevel.getId()))
                    .collect(Collectors.toSet()));
        }
        return immutableDunningAction.build();
    }

}
