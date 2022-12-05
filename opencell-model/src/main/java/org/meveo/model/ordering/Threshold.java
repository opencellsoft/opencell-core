package org.meveo.model.ordering;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "open_order_threshold")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_threshold_seq"),})
@NamedQueries({ @NamedQuery(name = "Threshold.deleteByOpenOrderTemplate", query = "delete from Threshold t where t.openOrderTemplate.id =:openOrderTemplateId "),
        @NamedQuery(name = "Threshold.deleteByOpenOrder", query = "delete from Threshold t where t.openOrder.id =:openOrderId ")})
public class Threshold extends BaseEntity {

    @Column(name = "sequence")
    @NotNull
    @Min(value = 1, message = "Field sequence should be positive value")
    private Integer sequence;

    @Column(name = "percentage")
    @NotNull
    @Min(value = 0)
    @Max(value = 100)
    private Integer percentage;

    @ElementCollection(targetClass = ThresholdRecipientsEnum.class)
    @CollectionTable(name = "open_order_threshold_recipients", joinColumns = @JoinColumn(name = "threshold_id"))
    @Column(name = "recipient")
    @Enumerated(EnumType.STRING)
    private List<ThresholdRecipientsEnum> recipients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_order_template_id", updatable = false)
    private OpenOrderTemplate openOrderTemplate;
    
    @Column(name = "external_recipient")
    private String externalRecipient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_order_quote_id")
    private OpenOrderQuote openOrderQuote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_order_id")
    private OpenOrder openOrder;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public List<ThresholdRecipientsEnum> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<ThresholdRecipientsEnum> recipients) {
        this.recipients = recipients;
    }

    public OpenOrderTemplate getOpenOrderTemplate() {
        return openOrderTemplate;
    }

    public void setOpenOrderTemplate(OpenOrderTemplate openOrderTemplate) {
        this.openOrderTemplate = openOrderTemplate;
    }

	public String getExternalRecipient() {
		return externalRecipient;
	}

	public void setExternalRecipient(String externalRecipient) {
		this.externalRecipient = externalRecipient;
	}

	public OpenOrderQuote getOpenOrderQuote() {
		return openOrderQuote;
	}

	public void setOpenOrderQuote(OpenOrderQuote openOrderQuote) {
		this.openOrderQuote = openOrderQuote;
	}

    public OpenOrder getOpenOrder() {
        return openOrder;
    }

    public void setOpenOrder(OpenOrder openOrder) {
        this.openOrder = openOrder;
    }
}
