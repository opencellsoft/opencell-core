package org.meveo.model.securityDeposit;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "security_deposit_templat")
@Entity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_templat_seq"),})
public class SecurityDepositTemplat extends BusinessEntity {

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Type(type = "numeric_boolean")
    @Column(name = "allow_validity_date")
    private boolean allowValidityDate;

    @Type(type = "numeric_boolean")
    @Column(name = "allow_validity_period")
    private boolean allowValidityPeriod;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Column(name = "status")
    private SecurityTemplateStatusEnum status;

    @Column(name = "number_instantiation")
    private Integer numberOfInstantiation;



}


