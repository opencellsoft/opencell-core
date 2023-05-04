package org.meveo.apiv2.documentCategory.service;

import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.documentCategory.DocumentCategoryDto;
import org.meveo.apiv2.documentCategory.mapper.DocumentCategoryMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.document.DocumentCategory;
import org.meveo.service.document.DocumentCategoryService;

public class DocumentCategoryApiService {
	
	@Inject
	private DocumentCategoryService documentCategoryService;
	
	private DocumentCategoryMapper mapper = new DocumentCategoryMapper();
	
	@TransactionAttribute
	public DocumentCategory create(DocumentCategoryDto postData) {
		DocumentCategory searchDocumentCategory = documentCategoryService.findByCode(postData.getCode());
		if(searchDocumentCategory != null) {
			throw new EntityAlreadyExistsException(DocumentCategory.class, postData.getCode());
		}
		
		DocumentCategory entity = mapper.toEntity(postData);

        documentCategoryService.create(entity);
		
		return entity;
	}

	@TransactionAttribute
	public DocumentCategory update(Long id, DocumentCategoryDto postData) {
		DocumentCategory searchDocumentCategory = documentCategoryService.findById(id);
		if(searchDocumentCategory == null) {
			throw new EntityDoesNotExistsException(DocumentCategory.class, id);
		}
		
		if(!StringUtils.isBlank(postData.getCode())) {
			searchDocumentCategory.setCode(postData.getCode());
		}
		if(!StringUtils.isBlank(postData.getRelativePath())) {
			searchDocumentCategory.setRelativePath(postData.getRelativePath());
		}

        documentCategoryService.update(searchDocumentCategory);
		
		return searchDocumentCategory;
	}

	@TransactionAttribute
	public void delete(Long id) {
		DocumentCategory searchDocumentCategory = documentCategoryService.findById(id);
		if(searchDocumentCategory == null) {
			throw new EntityDoesNotExistsException(DocumentCategory.class, id);
		}
		documentCategoryService.remove(searchDocumentCategory);
	}

}
