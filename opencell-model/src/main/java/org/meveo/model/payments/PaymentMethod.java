package org.meveo.model.payments;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;

@Entity
@Table(name = "ar_payment_token")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "token_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_token_seq"), })
@NamedQueries({
        @NamedQuery(name = "PaymentMethod.updatePreferredPaymentMethod", query = "UPDATE PaymentMethod pm set pm.preferred = false where pm.id <> :id and pm.customerAccount = :ca"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred1", query = "select min(pmg.id) from PaymentMethod pmg where pmg.customerAccount.id = :caId"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred2", query = "UPDATE PaymentMethod pm set pm.preferred = true where pm.customerAccount.id = :caId and pm.id =:id"),
        @NamedQuery(name = "PaymentMethod.updateFirstPaymentMethodToPreferred3", query = "UPDATE PaymentMethod pm set pm.preferred = false where pm.customerAccount.id = :caId and pm.id <>:id"),
        @NamedQuery(name = "PaymentMethod.getNumberOfPaymentMethods", query = "select count(*) from  PaymentMethod pm where pm.customerAccount.id = :caId") })
public abstract class PaymentMethod extends BaseEntity {

    private static final long serialVersionUID = 8726571628074346184L;

    @Column(name = "alias")
    protected String alias;

    @Type(type = "numeric_boolean")
    @Column(name = "is_default")
    protected boolean preferred;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    protected CustomerAccount customerAccount;

    @Column(name = "token_type", insertable = false, updatable = false, length = 12)
    @Enumerated(EnumType.STRING)
    protected PaymentMethodEnum paymentType;
    
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "INFO_1",columnDefinition = "TEXT")
    private String info1;

    @Column(name = "INFO_2",columnDefinition = "TEXT")
    private String info2;

    @Column(name = "INFO_3",columnDefinition = "TEXT")
    private String info3;

    @Column(name = "INFO_4",columnDefinition = "TEXT")
    private String info4;

    @Column(name = "INFO_5",columnDefinition = "TEXT")
    private String info5;

    public PaymentMethod() {
    }

    public PaymentMethod(String alias, boolean preferred, CustomerAccount customerAccount) {
        super();
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public PaymentMethodEnum getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentMethodEnum paymentType) {
        this.paymentType = paymentType;
    }

    
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	public abstract void updateWith(PaymentMethod otherPaymentMethod);
}