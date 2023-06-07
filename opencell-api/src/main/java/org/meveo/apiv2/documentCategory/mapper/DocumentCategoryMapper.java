package org.meveo.apiv2.documentCategory.mapper;

import org.meveo.apiv2.documentCategory.DocumentCategoryDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.document.DocumentCategory;

public class DocumentCategoryMapper extends ResourceMapper<DocumentCategoryDto, DocumentCategory>{
	
	@Override
	public DocumentCategoryDto toResource(DocumentCategory entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DocumentCategory toEntity(DocumentCategoryDto resource) {
		var documentCategory = new DocumentCategory();
		
		documentCategory.setCode(resource.getCode());
		documentCategory.setRelativePath(resource.getRelativePath());
		
		
		return documentCategory;
	}


}
