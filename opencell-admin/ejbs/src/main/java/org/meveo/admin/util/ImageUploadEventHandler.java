/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.VersionedEntity;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.finance.ReportExtract;
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 **/
public class ImageUploadEventHandler<T extends IEntity> {

    /**
     * application provider.
     */
    private String userProviderCode;

    /**
     * @param userProviderCode application provider.
     */
    public ImageUploadEventHandler(String userProviderCode) {
        this.userProviderCode = userProviderCode;
    }

    /**
     * @param entity entity
     * @return picture file name path.
     */
    public String getPicturePath(T entity) {
        if (entity instanceof OfferTemplateCategory) {
            return ModuleUtil.getPicturePath(userProviderCode, "offerCategory");
        } else if (entity instanceof OfferTemplate) {
            return ModuleUtil.getPicturePath(userProviderCode, "offer");
        } else if (entity instanceof ServiceTemplate) {
            return ModuleUtil.getPicturePath(userProviderCode, "service");
        } else if (entity instanceof ProductTemplate) {
            return ModuleUtil.getPicturePath(userProviderCode, "product");
        } else if (entity instanceof ReportExtract) {
            return ModuleUtil.getPicturePath(userProviderCode, "reportExtract");
        }

        return "";
    }

    /**
     * Handle image upload from browser by saving uploaded file and persisting filename as an entity field.
     * 
     * @param entity Entity to update with uploaded filename
     * @param uploadedFile Uploaded file
     * @return uploaded file name.
     * @throws IOException I/O exception.
     */
    public String handleImageUpload(T entity, UploadedFile uploadedFile) throws IOException {
        if (uploadedFile == null) {
            return null;
        }
        String filename = saveToFile(entity, uploadedFile.getInputstream(), uploadedFile.getFileName());
        return filename;
    }

    /**
     * Save input stream to a file.
     * 
     * @param entity Entity to determine a folder path
     * @param inputStream Input stream of file contents to save
     * @param originalFilename Original filename
     * @return A filename file was saved to
     * @throws IOException I/O Exception.
     */
    private String saveToFile(T entity, InputStream inputStream, String originalFilename) throws IOException {

        String filename = getFileName(entity);

        if (StringUtils.isBlank(filename)) {
            filename = originalFilename;
        }

        String extension = FilenameUtils.getExtension(originalFilename);

        String folder = getPicturePath(entity);
        Path file = Paths.get(folder, filename + "." + extension);
        if (!Files.exists(file)) {
            file = Files.createFile(file);
        }

        try (InputStream input = inputStream) {
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        }

        return filename + "." + extension;
    }

    /**
     * Saves an array of byte as image. Mainly use in API.
     * 
     * @param entity entity
     * @param originalFilename original file name.
     * @param imageData image data.
     * @return new saved file's name.
     * @throws IOException I/O Exception.
     */
    public String saveImage(T entity, String originalFilename, byte[] imageData) throws IOException {

        if (StringUtils.isBlank(originalFilename)) {
            return null;
        }

        String filename = saveToFile(entity, new ByteArrayInputStream(imageData), originalFilename);

        return filename;
    }

    /**
     * @param entity entity
     * @throws IOException I/O exception.
     */
    public void deleteImage(T entity) throws IOException {

        String imagePath = ((IImageUpload) entity).getImagePath();

        if (!StringUtils.isBlank(imagePath)) {
            String folder = getPicturePath(entity);
            Path source = Paths.get(folder, imagePath);
            Files.deleteIfExists(source);
        }
    }

    /**
     * @param newEntity new entity
     * @param sourceFilename source file name
     * @return name of file
     * @throws IOException I/O exception.
     */
    public String duplicateImage(T newEntity, String sourceFilename) throws IOException {

        if (StringUtils.isBlank(sourceFilename)) {
            return null;
        }

        String targetFilename = getFileName(newEntity);

        String folder = getPicturePath(newEntity);

        String extension = FilenameUtils.getExtension(sourceFilename);
        String targetFile = targetFilename + "." + extension;
        Path source = Paths.get(folder, sourceFilename);
        Path target = Paths.get(folder, targetFile);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        return targetFile;

    }

    /**
     * Compose a filename for a given entity. Filename is either code or UUID value.
     * 
     * @param entity Entity compose a filename for
     * @return file name
     */
    private String getFileName(IEntity entity) {
        String filename = null;
        if (entity.getClass().isAnnotationPresent(VersionedEntity.class)) {
            if (entity instanceof ICustomFieldEntity) {
                filename = ((ICustomFieldEntity) entity).getUuid();
            }

        } else if (entity instanceof BusinessEntity) {
            filename = ((BusinessEntity) entity).getCode();
        }

        return filename;
    }

}
