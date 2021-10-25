package org.meveo.model.dunning;

import static javax.persistence.FetchType.LAZY;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "dunning_policy")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_policy_seq")})
@NamedQueries({
        @NamedQuery(name = "DunningPolicy.findByName", query = "SELECT dp FROM DunningPolicy dp where dp.policyName=:policyName")})
public class DunningPolicy extends AuditableEntity {

    @Column(name = "policy_name")
    @NotNull
    private String policyName;

    @Column(name = "policy_description")
    @NotNull
    private String policyDescription;

    @Column(name = "interest_for_delay_sequence")
    private Integer interestForDelaySequence;

    @Column(name = "min_balance_trigger")
    @NotNull
    private Double minBalanceTrigger;

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "dunning_min_balance_trigger_currency", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "min_balance_trigger_currency")
    private List<String> minBalanceTriggerCurrency;

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "dunning_determine_level_by", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "determine_level_by")
    private List<String> determineLevelBy;

    @Column(name = "include_due_invoices_in_threshold")
    @Type(type = "numeric_boolean")
    private boolean includeDueInvoicesInThreshold;

    @Column(name = "total_dunning_levels")
    private Integer totalDunningLevels;

    @Column(name = "include_pay_reminder")
    @Type(type = "numeric_boolean")
    private boolean includePayReminder;

    @Column(name = "attach_invoices_to_emails")
    @Type(type = "numeric_boolean")
    private boolean attachInvoicesToEmails;

    @Column(name = "policy_priority")
    private Integer policyPriority;

    @Column(name = "is_default_policy")
    @Type(type = "numeric_boolean")
    private boolean isDefaultPolicy;

    @Column(name = "is_active_policy")
    @Type(type = "numeric_boolean")
    private boolean isActivePolicy;

    @OneToMany(mappedBy = "dunningPolicy", fetch = LAZY)
    private List<DunningPolicyLevel> dunningLevels;

    @OneToMany(mappedBy = "dunningPolicy", fetch = LAZY)
    private List<DunningPolicyRule> dunningPolicyRules;

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public Integer getInterestForDelaySequence() {
        return interestForDelaySequence;
    }

    public void setInterestForDelaySequence(Integer interestForDelaySequence) {
        this.interestForDelaySequence = interestForDelaySequence;
    }

    public Double getMinBalanceTrigger() {
        return minBalanceTrigger;
    }

    public void setMinBalanceTrigger(Double minBalanceTrigger) {
        this.minBalanceTrigger = minBalanceTrigger;
    }

    public List<String> getMinBalanceTriggerCurrency() {
        return minBalanceTriggerCurrency;
    }

    public void setMinBalanceTriggerCurrency(List<String> minBalanceTriggerCurrency) {
        this.minBalanceTriggerCurrency = minBalanceTriggerCurrency;
    }

    public List<String> getDetermineLevelBy() {
        return determineLevelBy;
    }

    public void setDetermineLevelBy(List<String> determineLevelBy) {
        this.determineLevelBy = determineLevelBy;
    }

    public Boolean getIncludeDueInvoicesInThreshold() {
        return includeDueInvoicesInThreshold;
    }

    public void setIncludeDueInvoicesInThreshold(Boolean includeDueInvoicesInThreshold) {
        this.includeDueInvoicesInThreshold = includeDueInvoicesInThreshold;
    }

    public Integer getTotalDunningLevels() {
        return totalDunningLevels;
    }

    public void setTotalDunningLevels(Integer totalDunningLevels) {
        this.totalDunningLevels = totalDunningLevels;
    }

    public Boolean getIncludePayReminder() {
        return includePayReminder;
    }

    public void setIncludePayReminder(Boolean includePayReminder) {
        this.includePayReminder = includePayReminder;
    }

    public Boolean getAttachInvoicesToEmails() {
        return attachInvoicesToEmails;
    }

    public void setAttachInvoicesToEmails(Boolean attachInvoicesToEmails) {
        this.attachInvoicesToEmails = attachInvoicesToEmails;
    }

    public Integer getPolicyPriority() {
        return policyPriority;
    }

    public void setPolicyPriority(Integer policyPriority) {
        this.policyPriority = policyPriority;
    }

    public Boolean getDefaultPolicy() {
        return isDefaultPolicy;
    }

    public void setDefaultPolicy(Boolean defaultPolicy) {
        isDefaultPolicy = defaultPolicy;
    }

    public Boolean getActivePolicy() {
        return isActivePolicy;
    }

    public void setActivePolicy(Boolean activePolicy) {
        isActivePolicy = activePolicy;
    }

    public List<DunningPolicyLevel> getDunningLevels() {
        return dunningLevels;
    }

    public void setDunningLevels(List<DunningPolicyLevel> dunningLevels) {
        this.dunningLevels = dunningLevels;
    }

    public List<DunningPolicyRule> getDunningPolicyRules() {
        return dunningPolicyRules;
    }

    public void setDunningPolicyRules(List<DunningPolicyRule> dunningPolicyRules) {
        this.dunningPolicyRules = dunningPolicyRules;
    }
}