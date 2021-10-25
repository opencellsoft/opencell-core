package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.scripts.ScriptInstance;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ar_dunning_action")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_dunning_action_seq"), })
public class DunningAction extends BusinessEntity {

    @Type(type = "numeric_boolean")
    @Column(name = "is_action_active")
    private boolean isActiveAction = true;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionTypeEnum actionType;

    @Column(name = "action_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionModeEnum actionMode = ActionModeEnum.AUTOMATIC;

    @Column(name = "action_channel")
    @Enumerated(EnumType.STRING)
    private ActionChannelEnum actionChannel = ActionChannelEnum.EMAIL;

    @ElementCollection(targetClass = DunningLevelEnum.class)
    @CollectionTable(name = "dunning_level", joinColumns = @JoinColumn(name = "level_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "related_levels")
    private List<DunningLevelEnum> relatedLevels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_notification_template_id")
    private EmailTemplate actionNotificationTemplate;

    @Type(type = "numeric_boolean")
    @Column(name = "attach_overdue_invoices")
    private boolean attachOverdueInvoices = true;

    @Type(type = "numeric_boolean")
    @Column(name = "attach_due_invoices")
    private boolean attachDueInvoices = false;

    public boolean isActiveAction() {
        return isActiveAction;
    }

    public void setActiveAction(boolean activeAction) {
        isActiveAction = activeAction;
    }

    public ActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public ActionModeEnum getActionMode() {
        return actionMode;
    }

    public void setActionMode(ActionModeEnum actionMode) {
        this.actionMode = actionMode;
    }

    public ActionChannelEnum getActionChannel() {
        return actionChannel;
    }

    public void setActionChannel(ActionChannelEnum actionChannel) {
        this.actionChannel = actionChannel;
    }

    public List<DunningLevelEnum> getRelatedLevels() {
        return relatedLevels;
    }

    public void setRelatedLevels(List<DunningLevelEnum> relatedLevels) {
        this.relatedLevels = relatedLevels;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    public EmailTemplate getActionNotificationTemplate() {
        return actionNotificationTemplate;
    }

    public void setActionNotificationTemplate(EmailTemplate actionNotificationTemplate) {
        this.actionNotificationTemplate = actionNotificationTemplate;
    }

    public boolean isAttachOverdueInvoices() {
        return attachOverdueInvoices;
    }

    public void setAttachOverdueInvoices(boolean attachOverdueInvoices) {
        this.attachOverdueInvoices = attachOverdueInvoices;
    }

    public boolean isAttachDueInvoices() {
        return attachDueInvoices;
    }

    public void setAttachDueInvoices(boolean attachDueInvoices) {
        this.attachDueInvoices = attachDueInvoices;
    }
}
