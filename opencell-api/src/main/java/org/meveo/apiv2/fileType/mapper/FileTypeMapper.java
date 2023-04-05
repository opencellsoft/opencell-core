package org.meveo.apiv2.fileType.mapper;

import org.meveo.apiv2.fileType.FileTypeDto;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.admin.FileType;

public class FileTypeMapper extends ResourceMapper<FileTypeDto, FileType>{
	
	@Override
	public FileTypeDto toResource(FileType entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileType toEntity(FileTypeDto resource) {
		var fileType = new FileType();
		
		fileType.setCode(resource.getCode());
		
		
		return fileType;
	}


}
