package org.meveo.apiv2.document;

import org.meveo.apiv2.models.Document;
import org.meveo.service.document.DocumentFileService;
import org.meveo.service.document.DocumentService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

@Stateless
public class DocumentResourceImpl implements DocumentResource {
    @Inject
    private DocumentService documentService;
    @Inject
    private DocumentFileService documentFileService;

    @Override
    public Response getDocument(@NotNull Long id) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findById(id, Arrays.asList("fileType", "category", "linkedAccountEntity")))
                .orElseThrow(() -> new NotFoundException("document with id " + id + " does not exist."));
        if(documentFileService.hasFile(documentEntity)){
            final byte[] fileContent = documentFileService.readFileContent(documentEntity);
            return Response.ok().entity(Document.from(documentEntity, Base64.getEncoder().encodeToString(fileContent)))
                    .build();
        }
        return Response.ok().entity(Document.from(documentEntity, null))
                .build();
    }

    private void validateDocument(Document document) {
        if(Objects.isNull(document.getCode())){
            throw new BadRequestException("Code attribute is mandatory for this operation");
        }
        if(Objects.isNull(document.getFileType())){
            throw new BadRequestException("FileType attribute is mandatory for this operation");
        }
        if(Objects.isNull(document.getCategory())){
            throw new BadRequestException("Category attribute is mandatory for this operation");
        }
        if(Objects.isNull(document.getFileName())){
            throw new BadRequestException("FileName attribute is mandatory for this operation");
        }
        if(Objects.isNull(document.getStatus())){
            throw new BadRequestException("Status attribute is mandatory for this operation");
        }
        if(Objects.isNull(document.getEncodedFile())){
            throw new BadRequestException("the file content is mandatory for this operation");
        }
    }

    @Override
    public Response createDocument(Document document) {
        validateDocument(document);
        final org.meveo.model.document.Document documentEntity = document.toEntity();
        documentService.create(documentEntity);
        documentFileService.saveFile(documentEntity, Base64.getDecoder().decode(document.getEncodedFile()));
        return Response.ok().entity(Collections.singletonMap("id", documentEntity.getId()))
                .build();
    }

    @Override
    public Response deleteDocument(Long id) {
        Optional.ofNullable(documentService.findById(id, Collections.singletonList("category"))).ifPresentOrElse(document -> {
                documentService.remove(document);
                documentFileService.deleteFile(document);
            }, () -> new NotFoundException("document with id "+id+" does not exist."));
        return Response.noContent().build();
    }

    @Override
    public Response getDocumentFile(Long id) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findById(id, Arrays.asList("fileType", "category")))
                .orElseThrow(() -> new NotFoundException("document with id " + id + " does not exist."));
        return Response.ok().entity(Base64.getEncoder().encodeToString(documentFileService.readFileContent(documentEntity)))
                .build();
    }

    @Override
    public Response updateDocumentFile(Long id, String encodedDocumentFile) {
        Optional.ofNullable(documentService.findById(id, Collections.singletonList("category"))).ifPresentOrElse(document ->
                documentFileService.updateFile(document, Base64.getDecoder().decode(encodedDocumentFile)), () -> new NotFoundException("document with id "+id+" does not exist."));
        return Response.noContent().build();
    }

    @Override
    public Response deleteDocumentFile(Long id, boolean includingDocument) {
        Optional.ofNullable(documentService.findById(id, Collections.singletonList("category")))
                .ifPresentOrElse(document -> {
                    if(includingDocument){
                        documentService.remove(document);
                    }
                    documentFileService.deleteFile(document);
                }, () -> new NotFoundException("document with id "+id+" does not exist."));
        return Response.noContent().build();
    }


}
