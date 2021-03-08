/**
 * 
 */
package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.meveo.model.cpq.Attribute;
import org.meveo.service.base.BusinessService;

/**
 * @author Rachid.AITYAAZZA
 *
 */

@Stateless
public class AttributeService extends BusinessService<Attribute>{
	
	/**
     * Update parent attribute
     */
    public void updateParentAttribute(Long id) {
        Query q=getEntityManager().createNamedQuery("Attribute.updateParentAttribute").setParameter("id", id);
        q.executeUpdate();
    }
 
}
