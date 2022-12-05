package org.meveo.model.cpq.commercial;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.quote.QuoteLot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 
 * @author Tarik F.
 * @version 11.0
 */
@Entity
@Table(name = "cpq_order_lot", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "order_id" }))
@CustomFieldEntity(cftCodePrefix = "OrderLot",inheritCFValuesFrom = "quoteLot")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_order_lot_seq")})
public class OrderLot extends BusinessCFEntity{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@NotNull
	private CommercialOrder order;
	
	@Column(name = "name", length = 50)
	@Size(max = 50)
	private String name;
	
	
    /**
   	 * quote lot attached to this orderLot
   	 */
       
   	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_lot_id")
   	private QuoteLot quoteLot;

   	
   	@Override
   	public ICustomFieldEntity[] getParentCFEntities() {
   		if (quoteLot != null) {
   			return new ICustomFieldEntity[] { quoteLot };
   		}
   		return null;
   	}
   	
	/**
	 * @return the order
	 */
	public CommercialOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(CommercialOrder order) {
		this.order = order;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public QuoteLot getQuoteLot() {
		return quoteLot;
	}

	public void setQuoteLot(QuoteLot quoteLot) {
		this.quoteLot = quoteLot;
	}
	
	
	
	
}
