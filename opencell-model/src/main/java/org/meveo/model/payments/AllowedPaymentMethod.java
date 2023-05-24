package org.meveo.model.payments;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.billing.UntdidPaymentMeans;
import org.meveo.model.catalog.Channel;
import org.meveo.model.crm.Provider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@ModuleItem
@CustomFieldEntity(cftCodePrefix = "PaymentGateway")
@Table(name = "ar_allowed_payment_method", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "ar_allowed_payment_method_seq"), })
public class AllowedPaymentMethod extends EnableBusinessCFEntity {
	
	
	@Enumerated(EnumType.STRING)
	@Column(name = "supported_payment_method")
	private PaymentMethodEnum supportedPaymentMethod;
	
	@Type(type = "json")
	@Column(name = "description_i18n", columnDefinition = "jsonb")
	private Map<String, String> descriptionI18n;
	@ManyToMany
	@JoinTable(name = "allowed_payment_method_channel", joinColumns = @JoinColumn(name = "allowed_payment_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "channel_id", referencedColumnName = "id"))
	private Set<Channel> channels = new HashSet<>();
	@ManyToOne
	@Column(name = "payment_means_id", nullable = false)
	private UntdidPaymentMeans paymentMeans;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "capacity", nullable = false)
	private CapacityEnum capacity;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type")
	private PaymentTypEnum paymentType;
	@Column(name = "limit_amount")
	private BigDecimal limitAmount;
	
	@ManyToOne
	@Column(name = "provider_id")
	private Provider provider;
	
	public PaymentMethodEnum getSupportedPaymentMethod() {
		return supportedPaymentMethod;
	}
	
	public void setSupportedPaymentMethod(PaymentMethodEnum supportedPaymentMethod) {
		this.supportedPaymentMethod = supportedPaymentMethod;
	}
	
	public Map<String, String> getDescriptionI18n() {
		return descriptionI18n;
	}
	
	public void setDescriptionI18n(Map<String, String> descriptionI18n) {
		this.descriptionI18n = descriptionI18n;
	}
	
	public Set<Channel> getChannels() {
		return channels;
	}
	
	public void setChannels(Set<Channel> channels) {
		this.channels = channels;
	}
	
	public UntdidPaymentMeans getPaymentMeans() {
		return paymentMeans;
	}
	
	public void setPaymentMeans(UntdidPaymentMeans paymentMeans) {
		this.paymentMeans = paymentMeans;
	}
	
	public CapacityEnum getCapacity() {
		return capacity;
	}
	
	public void setCapacity(CapacityEnum capacity) {
		this.capacity = capacity;
	}
	
	public BigDecimal getLimitAmount() {
		return limitAmount;
	}
	
	public void setLimitAmount(BigDecimal limitAmount) {
		this.limitAmount = limitAmount;
	}
	
	public Provider getProvider() {
		return provider;
	}
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	public PaymentTypEnum getPaymentType() {
		return paymentType;
	}
	
	public void setPaymentType(PaymentTypEnum paymentType) {
		this.paymentType = paymentType;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AllowedPaymentMethod)) return false;
		if (!super.equals(o)) return false;
		AllowedPaymentMethod that = (AllowedPaymentMethod) o;
		return getSupportedPaymentMethod() == that.getSupportedPaymentMethod() && Objects.equals(getDescriptionI18n(), that.getDescriptionI18n()) && getCapacity() == that.getCapacity() && getPaymentType() == that.getPaymentType() && Objects.equals(getLimitAmount(), that.getLimitAmount());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getSupportedPaymentMethod(), getDescriptionI18n(), getCapacity(), getPaymentType(), getLimitAmount());
	}
}
