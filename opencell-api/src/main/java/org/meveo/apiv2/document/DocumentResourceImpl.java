package org.meveo.apiv2.document;

import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.models.Document;
import org.meveo.service.document.DocumentFileService;
import org.meveo.service.document.DocumentService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.*;

@Stateless
@Interceptors({ WsRestApiInterceptor.class })
public class DocumentResourceImpl implements DocumentResource {
    @Inject
    private DocumentService documentService;
    @Inject
    private DocumentFileService documentFileService;

    @Override
    public Response getDocument(@NotNull String  code) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findByCodeAndLastVersion(code))
                .orElseThrow(() -> new NotFoundException("document with code " + code + " does not exist."));
        if(documentFileService.hasFile(documentEntity)){
            final byte[] fileContent = documentFileService.readFileContent(documentEntity);
            return Response.ok().entity(Document.from(documentEntity, Base64.getEncoder().encodeToString(fileContent)))
                    .build();
        }
        return Response.ok().entity(Document.from(documentEntity, null))
                .build();
    }
    
    @Override
    public Response getDocument(@NotNull String  code, Integer version) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findByCodeAndVersion(code,  version))
                .orElseThrow(() -> new NotFoundException("document with code " + code + " and version "+version+" does not exist."));
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
    public Response deleteDocument(String code, Integer version) {
        Optional.ofNullable(documentService.findByCodeAndVersion(code, version)).ifPresentOrElse(document -> {
                documentService.remove(document);
                documentFileService.deleteFile(document);
            }, () -> new NotFoundException("document with code "+code+" and version "+version+" does not exist."));
        return Response.ok().build();
    }

    @Override
    public Response getDocumentFile(String code) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findByCodeAndLastVersion(code))
                .orElseThrow(() -> new NotFoundException("document with code " + code + " does not exist."));
        return Response.ok().entity(Base64.getEncoder().encodeToString(documentFileService.readFileContent(documentEntity)))
                .build();
    }
    
    @Override
    public Response getDocumentFile(String code, Integer version) {
        final org.meveo.model.document.Document documentEntity = Optional.ofNullable(documentService.findByCodeAndVersion(code, version))
                .orElseThrow(() -> new NotFoundException("document with code " + code + " and version "+version+" does not exist."));
        return Response.ok().entity(Base64.getEncoder().encodeToString(documentFileService.readFileContent(documentEntity)))
                .build();
    }
    
    @Override
    public Response updateDocumentFile(String code, String encodedDocumentFile) {
        Optional.ofNullable(documentService.findByCodeAndLastVersion(code)).ifPresentOrElse(document ->
                documentFileService.updateFile(document, Base64.getDecoder().decode(encodedDocumentFile)), () -> new NotFoundException("document with code "+code+" does not exist."));
        return Response.ok().build();
    }

    @Override
    public Response updateDocumentFile(String code, Integer version, String encodedDocumentFile) {
        Optional.ofNullable(documentService.findByCodeAndVersion(code, version)).ifPresentOrElse(document ->
                documentFileService.updateFile(document, Base64.getDecoder().decode(encodedDocumentFile)), () -> new NotFoundException("document with code "+code+" and version "+version+" does not exist."));
        return Response.ok().build();
    }

    @Override
    public Response deleteDocumentFile(String code, Integer version, boolean includingDocument) {
        Optional.ofNullable(documentService.findByCodeAndVersion(code, version))
                .ifPresentOrElse(document -> {
                    if(includingDocument){
                        documentService.remove(document);
                    }
                    documentFileService.deleteFile(document);
                }, () -> new NotFoundException("document with code "+code+" and version "+version+" does not exist."));
        return Response.ok().build();
    }


}
