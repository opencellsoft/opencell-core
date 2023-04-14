package org.meveo.service.document;

import java.util.Date;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.BadRequestException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.FileType;
import org.meveo.model.document.Document;
import org.meveo.model.document.DocumentCategory;
import org.meveo.service.admin.impl.FileTypeService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.AccountEntitySearchService;

@Stateless
public class DocumentService extends BusinessService<Document> {
    
	@Inject 
	private DocumentCategoryService documentCategoryService;
    
	@Inject 
    private FileTypeService fileTypeService;
    
	@Inject 
    private AccountEntitySearchService accountEntitySearchService;

    @Override
    public void create(Document entity) throws BusinessException {    	
        entity.setFileType(getFileTypeByIdOrCode(entity));
        entity.setCategory(getDocumentCateory(entity));

        if(Objects.isNull(entity.getDocumentVersion())){
        	Integer documentVersion = 0 ;
        	
            try {
	        	Document document = findByCodeAndLastVersion(entity.getCode());
	        	documentVersion = document.getDocumentVersion()+1;
        	} catch (NoResultException e) {
        		documentVersion = 0;
        	}

        	entity.setDocumentVersion(documentVersion);
        	entity.setFileName(entity.getCode() + "_" + entity.getDocumentVersion() + "_" + entity.getFileName());
        }

        if(Objects.nonNull(entity.getLinkedAccountEntity())){
            AccountEntity accountEntity = accountEntitySearchService.findById(entity.getLinkedAccountEntity().getId());
            
            if(Objects.isNull(accountEntity)){
                throw new BadRequestException("Account Entity with id " + entity.getLinkedAccountEntity().getId() + " not found");
            }
            
            entity.setLinkedAccountEntity(accountEntity);
        }
        
        if(Objects.isNull(entity.getCreationDate())){
            entity.setCreationDate(new Date());
        }
        super.create(entity);
    }

    public Document findByFileNameAndType(String fileName, Long fileTypeId) {
        return getEntityManager().createNamedQuery("Document.findByFileNameAndType", Document.class)
                .setParameter("fileName", fileName)
                .setParameter("fileTypeId", fileTypeId)
                .getSingleResult();
    }
    
    /**
     * Get File Type using id or code
     * @param pDocument {@link Document}
     * @return {@link FileType}
     */
	private FileType getFileTypeByIdOrCode(Document pDocument) {
		FileType fetchedFileType = null;
		
		if(pDocument.getFileType().getId() != null) {
			fetchedFileType = fileTypeService.findById(pDocument.getFileType().getId());
    	} else if(pDocument.getFileType().getCode() != null) {
    		fetchedFileType = fileTypeService.findByCode(pDocument.getFileType().getCode());
        }
		
		if(Objects.isNull(fetchedFileType)){
            throw new BadRequestException("file type with id " + pDocument.getFileType().getId() + " or code " + pDocument.getFileType().getCode() + " not found");
        }
		
		return fetchedFileType;
	}
	
	/**
	 * Get Document Category using id or code
	 * @param pDocument {@link Document}
	 * @return {@link DocumentCategory}
	 */
	private DocumentCategory getDocumentCateory(Document pDocument) {
		DocumentCategory fetchedCategory = null;
        
        if(pDocument.getCategory().getId() != null) {
        	fetchedCategory = documentCategoryService.findById(pDocument.getCategory().getId());
    	} else if(pDocument.getCategory().getCode() != null) {
        	fetchedCategory = documentCategoryService.findByCode(pDocument.getCategory().getCode());
        }
        
        if(Objects.isNull(fetchedCategory)){
            throw new BadRequestException("Category with id " + pDocument.getCategory().getId() + " or code " + pDocument.getCategory().getCode() + "not found");
        }
        
		return fetchedCategory;
	}
    public Document findByCodeAndLastVersion(String code) {

        return getEntityManager().createNamedQuery("Document.findByCodeAndLastVersion", Document.class)
                .setParameter("code", code)
                .getSingleResult();
    }
    
    public Document findByCodeAndVersion(String code, Integer version) {

        return getEntityManager().createNamedQuery("Document.findByCodeAndVersion", Document.class)
                .setParameter("code", code)
                .setParameter("version", version)
                .getSingleResult();
    }
}
