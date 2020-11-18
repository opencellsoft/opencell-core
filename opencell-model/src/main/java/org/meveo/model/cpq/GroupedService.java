package org.meveo.model.cpq;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

/**
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Entity
@Table(name = "cpq_grouped_service", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_grouped_service_seq"), }) 
public class GroupedService extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	 
	/**
	 * the attached product
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product product;

	@Column(name = "mandatory", nullable = false)
	@NotNull
	private Boolean mandatory;

	@Column(name = "display", nullable = false)
	@NotNull
	private Boolean display;

    @Column(name = "sequence")
    private Integer sequence;
	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}


	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}


	/**
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}


	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	 
	




}
