package org.meveo.service.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mbarek-Ay
 * @version 10.0
 * 
 * Tag service implementation.
 */

@Stateless
public class TagService extends
		PersistenceService<Tag> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TagService.class);
	private static final String TAG_UNKNOWN = "Tag %d is missing";
	private static final String TAG_CODE_UNKNOWN = "Tag code %s is missing";
	private static final String TAG_VERSION_UNKNOWN = "Tag version %d is missing";
	private static final String TAG_IS_ATTACHED_TO_PRODUCT = "Impossible to remove a tag(%s), it attached to product code %s";
	
	private static final String QUERY_FIND_TAG_TYPE = "Tag.findByTagType";
	
	@Inject
	private ProductVersionService productVersionService;

	public boolean isTagTypeExist(Long id) throws TagException {
		LOGGER.info("check if the list of tag exist with id tag type is {}", id);
		var tags = this.getEntityManager().createNamedQuery(QUERY_FIND_TAG_TYPE).setParameter("id", id).getResultList();
		if(!tags.isEmpty())
			return true;
		LOGGER.info("no tag type {} exist for tag ", id);
		return false;
	}
	
	public void removeTag(Long id, Long idProductVersion) throws TagException {
		LOGGER.info("removing tag {}", id);
		final Tag tag = this.findById(id);
		if(tag == null || tag.getId() == null) {
			throw new TagException(String.format(TAG_UNKNOWN, id));
		}
		final ProductVersion version = this.productVersionService.findById(idProductVersion);
		if(version == null || version.getId() == null) {
			throw new TagException(String.format(TAG_VERSION_UNKNOWN, id));
		}
		if(version.getProduct() != null ) {
			boolean isTagExist = version.getTags().stream().filter( t -> t.getId() == tag.getId()).findFirst().isPresent();
			if(isTagExist) {
				throw new TagException(String.format(TAG_IS_ATTACHED_TO_PRODUCT, id, version.getProduct().getCode()));
			}
			this.remove(tag);
		}else {
			this.remove(tag);
		}
		LOGGER.info("removing tag {} successfully!", id);
	}

	public Tag findByCode(String parentTagCode) {
		try {
			return(Tag) getEntityManager().createNamedQuery("Tag.findByCode").setParameter("code", parentTagCode).getSingleResult();
		}catch(NoResultException e) {
			LOGGER.error(String.format(TAG_CODE_UNKNOWN, parentTagCode));
			return null;
		}
	}
	
	
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
	
	
}