package org.meveo.model.dunning;

import static jakarta.persistence.FetchType.LAZY;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.EnableEntity;
import org.meveo.model.admin.Currency;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "dunning_policy")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_policy_seq")})
@NamedQueries({
        @NamedQuery(name = "DunningPolicy.findByName", query = "SELECT dp FROM DunningPolicy dp where dp.policyName=:policyName"),
        @NamedQuery(name = "DunningPolicy.listPoliciesByIsActive", query = "SELECT DISTINCT dp FROM DunningPolicy dp left join fetch dp.dunningLevels dpl left join fetch dpl.dunningLevel dl where dp.isActivePolicy=:active"),
        @NamedQuery(name = "DunningPolicy.DeactivateDunningPolicies", query = "UPDATE DunningPolicy dp SET dp.isActivePolicy=false WHERE dp.id IN (:ids)")})
public class DunningPolicy extends EnableEntity {

    private static final long serialVersionUID = 1L;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_balance_trigger_currency_id")
    private Currency minBalanceTriggerCurrency;

    @Column(name = "determine_level_by", nullable = false)
    @Enumerated(EnumType.STRING)
    private DunningDetermineLevelBy determineLevelBy;

    @Column(name = "include_due_invoices_in_threshold")
    @Convert(converter = NumericBooleanConverter.class)
    private boolean includeDueInvoicesInThreshold;

    @Column(name = "total_dunning_levels")
    private Integer totalDunningLevels;

    @Column(name = "include_pay_reminder")
    @Convert(converter = NumericBooleanConverter.class)
    private boolean includePayReminder;

    @Column(name = "attach_invoices_to_emails")
    @Convert(converter = NumericBooleanConverter.class)
    private boolean attachInvoicesToEmails;

    @Column(name = "policy_priority")
    private Integer policyPriority;

    @Column(name = "is_default_policy")
    @Convert(converter = NumericBooleanConverter.class)
    private boolean isDefaultPolicy;

    @Column(name = "is_active_policy")
    @Convert(converter = NumericBooleanConverter.class)
    private boolean isActivePolicy;

    @OneToMany(mappedBy = "dunningPolicy", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningPolicyLevel> dunningLevels;

    @OneToMany(mappedBy = "dunningPolicy", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Currency getMinBalanceTriggerCurrency() {
        return minBalanceTriggerCurrency;
    }

    public void setMinBalanceTriggerCurrency(Currency minBalanceTriggerCurrency) {
        this.minBalanceTriggerCurrency = minBalanceTriggerCurrency;
    }

    public DunningDetermineLevelBy getDetermineLevelBy() {
        return determineLevelBy;
    }

    public void setDetermineLevelBy(DunningDetermineLevelBy determineLevelBy) {
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

    public Boolean getIsDefaultPolicy() {
        return isDefaultPolicy;
    }

    public void setIsDefaultPolicy(Boolean isDefaultPolicy) {
        this.isDefaultPolicy = isDefaultPolicy;
    }

    public Boolean getIsActivePolicy() {
        return isActivePolicy;
    }

    public void setIsActivePolicy(Boolean isActivePolicy) {
        this.isActivePolicy = isActivePolicy;
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