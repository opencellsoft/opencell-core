package org.meveo.service.document;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.AccountEntity;
import org.meveo.model.admin.FileType;
import org.meveo.model.document.Document;
import org.meveo.model.document.DocumentCategory;
import org.meveo.service.admin.impl.FileTypeService;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.AccountEntitySearchService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.Date;
import java.util.Objects;

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
        final FileType fetchedFileType = fileTypeService.findById(entity.getFileType().getId());
        if(Objects.isNull(fetchedFileType)){
            throw new BadRequestException("file type with id "+entity.getFileType().getId()+" not found");
        }
        entity.setFileType(fetchedFileType);
        final DocumentCategory fetchedCategory = documentCategoryService.findById(entity.getCategory().getId());
        if(Objects.isNull(fetchedCategory)){
            throw new BadRequestException("Category with id "+entity.getCategory().getId()+" not found");
        }
        if(Objects.nonNull(entity.getLinkedAccountEntity())){
            AccountEntity accountEntity = accountEntitySearchService.findById(entity.getLinkedAccountEntity().getId());
            if(Objects.isNull(accountEntity)){
                throw new BadRequestException("Account Entity with id "+entity.getLinkedAccountEntity().getId()+" not found");
            }
            entity.setLinkedAccountEntity(accountEntity);
        }
        if(Objects.isNull(entity.getCreationDate())){
            entity.setCreationDate(new Date());
        }
        entity.setCategory(fetchedCategory);
        super.create(entity);
    }
}
