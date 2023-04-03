package org.meveo.apiv2.fileType.service;


import javax.ejb.TransactionAttribute;
import javax.inject.Inject;

import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.fileType.mapper.FileTypeMapper;
import org.meveo.apiv2.fileType.FileTypeDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.FileType;
import org.meveo.service.admin.impl.FileTypeService;

public class FileTypeApiService {
	
	@Inject
	private FileTypeService fileTypeService;
	
	private FileTypeMapper mapper = new FileTypeMapper();
	
	@TransactionAttribute
	public FileType create(FileTypeDto postData) {
		FileType searchFileType = fileTypeService.findByCode(postData.getCode());
		if(searchFileType != null) {
			throw new EntityAlreadyExistsException(FileType.class, postData.getCode());
		}
		
		FileType entity = mapper.toEntity(postData);

        fileTypeService.create(entity);
		
		return entity;
	}

	@TransactionAttribute
	public FileType update(Long id, FileTypeDto postData) {
		FileType searchFileType = fileTypeService.findById(id);
		if(searchFileType == null) {
			throw new EntityDoesNotExistsException(FileType.class, id);
		}
		
		if(!StringUtils.isBlank(postData.getCode())) {
			searchFileType.setCode(postData.getCode());
		}

        fileTypeService.update(searchFileType);
		
		return searchFileType;
	}

	@TransactionAttribute
	public void delete(Long id) {
		FileType searchFileType = fileTypeService.findById(id);
		if(searchFileType == null) {
			throw new EntityDoesNotExistsException(FileType.class, id);
		}
		fileTypeService.remove(searchFileType);
	}

}
