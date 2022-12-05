package org.meveo.model.dunning;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.payments.ActionChannelEnum;
import org.meveo.model.payments.ActionModeEnum;
import org.meveo.model.payments.ActionTypeEnum;
import org.meveo.model.scripts.ScriptInstance;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ar_dunning_action")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_dunning_action_seq"), })
public class DunningAction extends BusinessEntity {

    private static final long serialVersionUID = -3093051277357043210L;

    @Convert(converter = NumericBooleanConverter.class)
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dunning_level_dunning_action", joinColumns = @JoinColumn(name = "dunning_action_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dunning_level_id", referencedColumnName = "id"))
    private List<DunningLevel> relatedLevels;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_notification_template_id")
    private EmailTemplate actionNotificationTemplate;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "attach_overdue_invoices")
    private boolean attachOverdueInvoices = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "attach_due_invoices")
    private boolean attachDueInvoices = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dunning_agent_id")
    private DunningAgent assignedTo;

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

    public List<DunningLevel> getRelatedLevels() {
        return relatedLevels;
    }

    public void setRelatedLevels(List<DunningLevel> relatedLevels) {
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

    public DunningAgent getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(DunningAgent assignedTo) {
        this.assignedTo = assignedTo;
    }
}
