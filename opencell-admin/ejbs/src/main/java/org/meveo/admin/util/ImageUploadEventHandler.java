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
import org.meveo.model.crm.Provider;
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 **/
public class ImageUploadEventHandler<T extends IEntity> {

    private Provider appProvider;

    public ImageUploadEventHandler(Provider appProvider) {
        this.appProvider = appProvider;
    }

    public String getPicturePath(T entity) {
        if (entity instanceof OfferTemplateCategory) {
            return ModuleUtil.getPicturePath(appProvider.getCode(), "offerCategory");
        } else if (entity instanceof OfferTemplate) {
            return ModuleUtil.getPicturePath(appProvider.getCode(), "offer");
        } else if (entity instanceof ServiceTemplate) {
            return ModuleUtil.getPicturePath(appProvider.getCode(), "service");
        } else if (entity instanceof ProductTemplate) {
            return ModuleUtil.getPicturePath(appProvider.getCode(), "product");
        }

        return "";
    }

    /**
     * Handle image upload from browser by saving uploaded file and persisting filename as an entity field
     * 
     * @param entity Entity to update with uploaded filename
     * @param uploadedFile Uploaded file
     * @throws IOException
     */
    public String handleImageUpload(T entity, UploadedFile uploadedFile) throws IOException {
        if (uploadedFile == null) {
            return null;
        }
        String filename = saveToFile(entity, uploadedFile.getInputstream(), uploadedFile.getFileName());
        return filename;
    }

    /**
     * Save input stream to a file
     * 
     * @param entity Entity to determine a folder path
     * @param inputStream Input stream of file contents to save
     * @param originalFilename Original filename
     * @return A filename file was saved to
     * @throws IOException
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
     * @param entity
     * @param imageData
     * @throws IOException
     */
    public String saveImage(T entity, String originalFilename, byte[] imageData) throws IOException {

        if (StringUtils.isBlank(originalFilename)) {
            return null;
        }

        String filename = saveToFile(entity, new ByteArrayInputStream(imageData), originalFilename);

        return filename;
    }

    public void deleteImage(T entity) throws IOException {

        String imagePath = ((IImageUpload) entity).getImagePath();

        if (!StringUtils.isBlank(imagePath)) {
            String folder = getPicturePath(entity);
            Path source = Paths.get(folder, imagePath);
            Files.deleteIfExists(source);
        }
    }

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
     * @return Filename
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
