package org.meveo.model.cpq.commercial;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessCFEntity;


/** 
 * @author Tarik F.
 * @version 11.0
 *
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "InvoicingPlan")
@Table(name = "cpq_invoicing_plan", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_invoicing_plan_seq")})
public class InvoicingPlan extends EnableBusinessCFEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8433284624540252795L;

	@OneToMany(mappedBy = "billingPlan", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InvoicingPlanItem> invoicingPlanItems;

	public List<InvoicingPlanItem> getInvoicingPlanItems() {
		return invoicingPlanItems;
	}

	public void setInvoicingPlanItems(List<InvoicingPlanItem> invoicingPlanItems) {
		this.invoicingPlanItems = invoicingPlanItems;
	}
}
