package org.meveo.model.crm;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.Seller;
import org.meveo.model.sequence.GenericSequence;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Entity
@ExportIdentifier({ "code", "seller.code" })
@Table(name = "crm_customer_sequence")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "crm_customer_sequence_seq"), })
public class CustomerSequence extends BusinessEntity {

	private static final long serialVersionUID = 181203276349593823L;

	@Embedded
	private GenericSequence genericSequence = new GenericSequence();

	/**
	 * This field is only use in CustomerSequence.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;

	public GenericSequence getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequence genericSequence) {
		this.genericSequence = genericSequence;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

}
