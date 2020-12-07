package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.base.BusinessService;

/**
 * @author Mbarek-Ay
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * TagType service implementation.
 */

@Stateless
public class TagTypeService extends BusinessService<TagType> {
	 
	private static final String TAG_TYPE_ATTACHED = "Impossible to remove a type of tag %s, it is attached to tag"; 
	
	@Inject
	private TagService tagService;
	
	public void removeTagType(String codeTagType) { 
		boolean isTagTypeAttached;
			TagType tag = this.findByCode(codeTagType);
			if(tag == null) {
				throw new EntityDoesNotExistsException(Tag.class, codeTagType);
			}
			isTagTypeAttached = this.tagService.isTagTypeExist(tag.getId());
			if(isTagTypeAttached) {
				log.warn("Impossible to remove  tag type {}, because it attached to a tag", codeTagType);
				throw new BusinessException(String.format(TAG_TYPE_ATTACHED, codeTagType));
			}
			this.remove(tag.getId());
	}
}