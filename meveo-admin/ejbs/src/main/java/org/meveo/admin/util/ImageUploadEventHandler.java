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
import org.primefaces.model.UploadedFile;

/**
 * @author Edward P. Legaspi
 **/
public class ImageUploadEventHandler<T extends IEntity> {

	public String getPicturePath(T entity, String providerCode) {
		if (entity instanceof OfferTemplateCategory) {
			return ModuleUtil.getPicturePath(providerCode, "offerCategory");
		} else if (entity instanceof OfferTemplate) {
			return ModuleUtil.getPicturePath(providerCode, "offer");
		} else if (entity instanceof ServiceTemplate) {
			return ModuleUtil.getPicturePath(providerCode, "service");
		} else if (entity instanceof ProductTemplate) {
			return ModuleUtil.getPicturePath(providerCode, "product");
		}

		return "";
	}

	public void handleImageUpload(T entity, String code, UploadedFile uploadedFile, String providerCode) throws IOException {
		if (uploadedFile != null) {
			String newExtension = FilenameUtils.getExtension(uploadedFile.getFileName());
			String oldExtension = FilenameUtils.getExtension(((IImageUpload) entity).getImagePath());
			String filePath = processImageUpload(entity, code, uploadedFile, providerCode);
			handleChangeExtension(entity, oldExtension, newExtension, providerCode);
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
	 * @param providerCode
	 * @return
	 * @throws IOException
	 */
	private String processImageUpload(T entity, String code, UploadedFile uploadedFile, String providerCode) throws IOException {
		String filename = code;
		if (org.meveo.commons.utils.StringUtils.isBlank(code)) {
			filename = FilenameUtils.getBaseName(uploadedFile.getFileName());
		}

		String extension = FilenameUtils.getExtension(uploadedFile.getFileName());

		String folder = getPicturePath(entity, providerCode);
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
	 * @param providerCode
	 * @throws IOException
	 */
	private void handleChangeExtension(T entity, String oldExtension, String newFileExt, String providerCode) throws IOException {
		String folder = getPicturePath(entity, providerCode);

		String imagePath = ((IImageUpload) entity).getImagePath();
		if (oldExtension != null && !oldExtension.equals(newFileExt)) {
			Files.deleteIfExists(Paths.get(folder, imagePath));
		}
	}

	public void saveImageUpload(T entity, String providerCode) throws IOException {
		// check if image path is the same as entity.code
		String folder = getPicturePath(entity, providerCode);

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
	 * @param providerCode
	 * @throws IOException
	 */
	public void saveImageUpload(T entity, String filename, byte[] imageData, String providerCode) throws IOException {
		// check if image path is the same as entity.code
		String folder = getPicturePath(entity, providerCode);

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

	public void deleteImage(T entity, String providerCode) throws IOException {
		String folder = getPicturePath(entity, providerCode);

		String imagePath = ((IImageUpload) entity).getImagePath();
		if (!org.meveo.commons.utils.StringUtils.isBlank(imagePath)) {
			Path source = Paths.get(folder, imagePath);
			Files.deleteIfExists(source);
		}
	}

	public String duplicateImage(T entity, String sourceFilename, String targetFilename, String providerCode) throws IOException {
		String folder = getPicturePath(entity, providerCode);
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
