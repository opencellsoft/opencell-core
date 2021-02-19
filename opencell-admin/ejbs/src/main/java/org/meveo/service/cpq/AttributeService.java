/**
 * 
 */
package org.meveo.service.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.service.base.BusinessService;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class AttributeService extends BusinessService<Attribute>{

 
	public List<Attribute> findByGroupedAttribute(GroupedAttributes groupedAttributes) {
		QueryBuilder builder = new QueryBuilder("from " + Attribute.class.getSimpleName() + " att where att.groupedAttributes=:groupedAttributes");
		
		Query query = builder.getQuery(getEntityManager());
		query.setParameter("groupedAttributes", groupedAttributes);

		try {
			return (List<Attribute>) query.getResultList();
		} catch (NoResultException e) {
			return new ArrayList<Attribute>();
		}
	}
 
}
