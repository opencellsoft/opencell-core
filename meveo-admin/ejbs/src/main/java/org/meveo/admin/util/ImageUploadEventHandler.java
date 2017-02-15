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
import org.meveo.model.IEntity;
import org.meveo.model.catalog.IImageUpload;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.security.MeveoUser;
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 **/
public class ImageUploadEventHandler<T extends IEntity> {
    
    private MeveoUser currentUser;
    
    public ImageUploadEventHandler(MeveoUser currentUser) {
        this.currentUser = currentUser;
    }

	public String getPicturePath(T entity) {
		if (entity instanceof OfferTemplateCategory) {
			return ModuleUtil.getPicturePath(currentUser.getProviderCode(), "offerCategory");
		} else if (entity instanceof OfferTemplate) {
			return ModuleUtil.getPicturePath(currentUser.getProviderCode(), "offer");
		} else if (entity instanceof ServiceTemplate) {
			return ModuleUtil.getPicturePath(currentUser.getProviderCode(), "service");
		} else if (entity instanceof ProductTemplate) {
			return ModuleUtil.getPicturePath(currentUser.getProviderCode(), "product");
		}

		return "";
	}

	public void handleImageUpload(T entity, String code, UploadedFile uploadedFile) throws IOException {
		if (uploadedFile != null) {
			String newExtension = FilenameUtils.getExtension(uploadedFile.getFileName());
			String oldExtension = FilenameUtils.getExtension(((IImageUpload) entity).getImagePath());
			String filePath = processImageUpload(entity, code, uploadedFile);
			handleChangeExtension(entity, oldExtension, newExtension);
			((IImageUpload) entity).setImagePath(filePath);
		}
	}

	/**
	 * Creates the file in the local filesystem. If code is not null use it as
	 * filename.
	 * 
	 * @param entity
	 * @param code
	 * @param uploadedFile
	 * @return
	 * @throws IOException
	 */
	private String processImageUpload(T entity, String code, UploadedFile uploadedFile) throws IOException {
		String filename = code;
		if (org.meveo.commons.utils.StringUtils.isBlank(code)) {
			filename = FilenameUtils.getBaseName(uploadedFile.getFileName());
		}

		String extension = FilenameUtils.getExtension(uploadedFile.getFileName());

		String folder = getPicturePath(entity);
		Path file = Paths.get(folder, filename + "." + extension);
		if (!Files.exists(file)) {
			file = Files.createFile(file);
		}

		try (InputStream input = uploadedFile.getInputstream()) {
			Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
		}

		return filename + "." + extension;
	}

	/**
	 * Remove old file if extension has been changed.
	 * 
	 * @param entity
	 * @param newFileExt
	 * @throws IOException
	 */
	private void handleChangeExtension(T entity, String oldExtension, String newFileExt) throws IOException {
		String folder = getPicturePath(entity);

		String imagePath = ((IImageUpload) entity).getImagePath();
		if (oldExtension != null && !oldExtension.equals(newFileExt)) {
			Files.deleteIfExists(Paths.get(folder, imagePath));
		}
	}

	public void saveImageUpload(T entity) throws IOException {
		// check if image path is the same as entity.code
		String folder = getPicturePath(entity);

		String imagePath = ((IImageUpload) entity).getImagePath();
		String code = ((BusinessEntity) entity).getCode();
		if (!org.meveo.commons.utils.StringUtils.isBlank(imagePath)) {
			Path source = Paths.get(folder, imagePath);
			String extension = FilenameUtils.getExtension(imagePath);
			Path target = Paths.get(folder, code + "." + extension);

			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			((IImageUpload) entity).setImagePath(code + "." + extension);
		}
	}

	/**
	 * Saves an array of byte as image. Mainly use in API.
	 * 
	 * @param entity
	 * @param imageData
	 * @throws IOException
	 */
	public void saveImageUpload(T entity, String filename, byte[] imageData) throws IOException {
		// check if image path is the same as entity.code
		String folder = getPicturePath(entity);

		if (!org.meveo.commons.utils.StringUtils.isBlank(filename)) {
			String extension = FilenameUtils.getExtension(filename);
			String imagePath = ((IImageUpload) entity).getImagePath();
			String code = ((BusinessEntity) entity).getCode();
			// check if different file extension (update)
			if (!StringUtils.isBlank(imagePath)) {
				if (!FilenameUtils.getExtension(imagePath).equals(FilenameUtils.getExtension(filename))) {
					extension = FilenameUtils.getExtension(filename);
					Files.deleteIfExists(Paths.get(folder, imagePath));
				}
			}

			Path target = Paths.get(folder, code + "." + extension);
			try (InputStream input = new ByteArrayInputStream(imageData)) {
				Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
			}
			((IImageUpload) entity).setImagePath(code + "." + extension);
		}
	}

	public void deleteImage(T entity) throws IOException {
		String folder = getPicturePath(entity);

		String imagePath = ((IImageUpload) entity).getImagePath();
		if (!org.meveo.commons.utils.StringUtils.isBlank(imagePath)) {
			Path source = Paths.get(folder, imagePath);
			Files.deleteIfExists(source);
		}
	}

	public String duplicateImage(T entity, String sourceFilename, String targetFilename) throws IOException {
		String folder = getPicturePath(entity);
		Path target = null;		

		if (!org.meveo.commons.utils.StringUtils.isBlank(sourceFilename)) {
			String extension = FilenameUtils.getExtension(sourceFilename);
			String targetFile = targetFilename + "." + extension;
			Path source = Paths.get(folder, sourceFilename);
			target = Paths.get(folder, targetFile);
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

			return targetFile;
		}

		return null;
	}
}
