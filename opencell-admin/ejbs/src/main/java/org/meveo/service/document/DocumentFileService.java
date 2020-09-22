package org.meveo.service.document;

import org.assertj.core.util.VisibleForTesting;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.document.Document;
import org.meveo.model.document.DocumentCategory;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Stateless
public class DocumentFileService {

    @Inject
    private ParamBeanFactory paramBeanFactory;
    private static String DOCUMENT_ROOT_DIR;
    private static String RELATIVE_PATH_EL;

    @PostConstruct
    @VisibleForTesting
    public void init() {
        ParamBean instanceParamBean = paramBeanFactory.getInstance();
        String providersRootDir = instanceParamBean.getProperty("providers.rootDir", "./opencelldata");
        String providerRootDir = instanceParamBean.getProperty("provider.rootDir", "default");
        String documentRootDir = instanceParamBean.getProperty("document.rootDir", "documents");
        RELATIVE_PATH_EL = instanceParamBean.getProperty("document.relativePathEL", "");
        DOCUMENT_ROOT_DIR = providersRootDir + File.separator + providerRootDir + File.separator + documentRootDir;
    }
    public void saveFile(org.meveo.model.document.Document documentEntity, byte[] decodedFile) {
        try {
            final String getFileLocationPath = getFileLocationPath(DOCUMENT_ROOT_DIR, documentEntity, documentEntity.getCategory());
            if(Files.notExists(Path.of(getFileLocationPath))){
                (new File(getFileLocationPath)).mkdirs();
            }
            Files.write(Path.of(getFileLocationPath+ File.separator + documentEntity.getFileName()), decodedFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("there was an issue during file creation!");
        }
    }

    public byte[] readFileContent(org.meveo.model.document.Document documentEntity) {
        try {
            final String fileFullPath = getFileFullPath(DOCUMENT_ROOT_DIR, documentEntity, documentEntity.getCategory());
            if(Files.notExists(Path.of(fileFullPath))){
                throw new BadRequestException("directory '"+fileFullPath+"' does not exist ");
            }
            return Files.readAllBytes(Path.of(fileFullPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("there was an issue during file reading!");
        }
    }

    public void deleteFile(Document documentEntity) {
        try {
            final String fileFullPath = getFileFullPath(DOCUMENT_ROOT_DIR, documentEntity, documentEntity.getCategory());
            if(Files.notExists(Path.of(fileFullPath))){
                throw new BadRequestException("directory '"+fileFullPath+"' does not exist ");
            }
            Files.delete(Path.of(fileFullPath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException("there was an issue during file delete!");
        }
    }

    public void updateFile(Document documentEntity, byte[] encodedDocumentFile) {
        deleteFile(documentEntity);
        saveFile(documentEntity, encodedDocumentFile);
    }

    private String getFileFullPath(String documentRootDir, Document document, DocumentCategory documentCategory) {
        String calculatedDocumentRelativePath = computeDocumentRelativePath(document, documentCategory);
        if(Objects.nonNull(calculatedDocumentRelativePath)){
            return documentRootDir + File.separator + calculatedDocumentRelativePath + File.separator + document.getFileName();
        }
        return documentRootDir + File.separator + documentCategory.getRelativePath() + File.separator + document.getFileName();
    }

    @VisibleForTesting
    public String computeDocumentRelativePath(Document document, DocumentCategory documentCategory) {
        Map<Object, Object> contextEl = new HashMap<>();
        contextEl.put("documentCategory", documentCategory);
        contextEl.put("document", document);
        return ValueExpressionWrapper.evaluateExpression(RELATIVE_PATH_EL, contextEl, String.class);
    }

    private String getFileLocationPath(String documentRootDir, Document document, DocumentCategory documentCategory) {
        String calculatedDocumentRelativePath = computeDocumentRelativePath(document, documentCategory);
        if(Objects.nonNull(calculatedDocumentRelativePath)){
            return documentRootDir + File.separator + calculatedDocumentRelativePath;
        }
        return documentRootDir + File.separator + documentCategory.getRelativePath();
    }

    public boolean hasFile(Document documentEntity) {
        final String fileFullPath = getFileFullPath(DOCUMENT_ROOT_DIR, documentEntity, documentEntity.getCategory());
        return Files.exists(Path.of(fileFullPath));
    }
}

