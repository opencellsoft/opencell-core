package org.meveo.service.cpq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.service.base.BusinessService;

/**
 * @author Mbarek-Ay
 * @version 10.0
 * 
 * Tag service implementation.
 */

@Stateless
public class TagService extends BusinessService<Tag> {
 
	//private static final String TAG_CODE_UNKNOWN = "Tag code %s is missing";
	private static final String TAG_IS_ATTACHED_TO_PRODUCT = "Impossible to remove a tag(%s), it attached to product code %s";
	
	private static final String QUERY_FIND_TAG_TYPE = "Tag.findByTagType";
	
	@Inject
	private ProductVersionService productVersionService;

	public boolean isTagTypeExist(Long id) {
		log.info("check if the list of tag exist with id tag type is {}", id);
		var tags = this.getEntityManager().createNamedQuery(QUERY_FIND_TAG_TYPE).setParameter("id", id).getResultList();
		if(!tags.isEmpty())
			return true;
		log.info("no tag type {} exist for tag ", id);
		return false;
	}
	
	public void removeTag(Long id, Long idProductVersion) {
		log.info("removing tag {}", id);
		final Tag tag = this.findById(id);
		if(tag == null || tag.getId() == null) {
			throw new EntityDoesNotExistsException(Tag.class, id);
		}
		final ProductVersion version = this.productVersionService.findById(idProductVersion);
		if(version == null || version.getId() == null) {
			throw new EntityDoesNotExistsException(ProductVersion.class, idProductVersion);
		}
		if(version.getProduct() != null ) {
			boolean isTagExist = version.getTags().stream().filter( t -> t.getId() == tag.getId()).findFirst().isPresent();
			if(isTagExist) {
				throw new BusinessException(String.format(TAG_IS_ATTACHED_TO_PRODUCT, id, version.getProduct().getCode()));
			}
			this.remove(tag);
		}else {
			this.remove(tag);
		}
		log.info("removing tag {} successfully!", id);
	}

//	public Tag findByCode(String parentTagCode) {
//		try {
//			return(Tag) getEntityManager().createNamedQuery("Tag.findByCode").setParameter("code", parentTagCode).getSingleResult();
//		}catch(NoResultException e) {
//			LOGGER.error(String.format(TAG_CODE_UNKNOWN, parentTagCode));
//			return null;
//		}
//	}
	
	
	@SuppressWarnings("unchecked")
	public List<Tag> getTagsByBA(BillingAccount ba) {
		QueryBuilder qb = new QueryBuilder(Tag.class, "tag");
		qb.addCriterionEntity("tag.billingAccount", ba);
		try {
			return qb.getQuery(getEntityManager()).getResultList();

		} catch (NoResultException e) {
			return null;
		}
	}
 
	
	 @SuppressWarnings("unchecked")
	    public List<Tag> findByRequestedTagType(List<String> requestedTagType) { 
		 List<Tag> tags=new ArrayList<Tag>();
	    	try {
	    		tags = (List<Tag>)getEntityManager().createNamedQuery("Tag.findByRequestedTagType").setParameter("requestedTagType", requestedTagType).getResultList();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		log.error("findByCriteria error ", e.getMessage());
	    	}

	    	return tags;
	    }
	
	
	
}