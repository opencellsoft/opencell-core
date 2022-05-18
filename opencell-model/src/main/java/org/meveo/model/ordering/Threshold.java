package org.meveo.model.ordering;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "open_order_threshold")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "open_order_threshold_seq"),})

public class Threshold extends BaseEntity {

    @Column(name = "sequence")
    @NotNull
    private Integer sequence;

    @Column(name = "percentage")
    @NotNull
    private Integer percentage;

    @ElementCollection(targetClass = ThresholdRecipientsEnum.class)
    @CollectionTable(name = "open_order_threshold_recipients", joinColumns = @JoinColumn(name = "threshold_id"))
    @Column(name = "recipient")
    @Enumerated(EnumType.STRING)
    private List<ThresholdRecipientsEnum> recipients;

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
}
