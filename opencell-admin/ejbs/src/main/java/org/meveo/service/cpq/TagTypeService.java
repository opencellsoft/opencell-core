package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.TagException;
import org.meveo.service.cpq.exception.TagTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mbarek-Ay
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * TagType service implementation.
 */

@Stateless
public class TagTypeService extends
		PersistenceService<TagType> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TagTypeService.class);
	private static final String TAG_TYPE_ATTACHED = "Impossible to remove a type of tag %d, it is attached to tag";
	private static final String UNKNOWN_TAG_TYPE = "Unknown Tag type from code %s";
	
	@Inject
	private TagService tagService;
	
	public void removeTagType(Long id) throws TagTypeException {
		LOGGER.info("removing tag type {}", id);
		boolean isTagTypeAttached;
		try {
			isTagTypeAttached = this.tagService.isTagTypeExist(id);
			if(isTagTypeAttached) {
				LOGGER.warn("Impossible to remove  tag type {}, because it attached to a tag", id);
				throw new TagTypeException(String.format(TAG_TYPE_ATTACHED, id));
			}
		} catch (TagException e) {
			LOGGER.error("Error while removing tag type {}", id);
			throw new TagTypeException(e);
		}
		this.remove(id);
	}
	

	/**
	 * @param code
	 * @return
	 * @throws TagTypeException
	 */
	public TagType findByCode(String code) throws TagTypeException {
		try {
			return(TagType) getEntityManager().createNamedQuery("TagType.findByCode").setParameter("code", code).getSingleResult();
		}catch(NoResultException e) {
			throw new TagTypeException(String.format(UNKNOWN_TAG_TYPE, code));
		}
	}
}