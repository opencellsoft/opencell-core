package org.meveo.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.billing.IsoIcd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@ObservableEntity
@Table(name = "account_registration_number")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "account_registration_number_seq"), })
public class RegistrationNumber extends  AuditableEntity {
	
	@Column(name = "registration_id")
	private String registrationNo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icd_id")
	private IsoIcd isoIcd;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_entity_id")
	private AccountEntity accountEntity;
	
	public String getRegistrationNo() {
		return registrationNo;
	}
	
	public RegistrationNumber setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
		return this;
	}
	
	public IsoIcd getIsoIcd() {
		return isoIcd;
	}
	
	public RegistrationNumber setIsoIcd(IsoIcd isoIcd) {
		this.isoIcd = isoIcd;
		return this;
	}
	
	public AccountEntity getAccountEntity() {
		return accountEntity;
	}
	
	public RegistrationNumber setAccountEntity(AccountEntity accountEntity) {
		this.accountEntity = accountEntity;
		return this;
	}
}
