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
import org.meveo.model.IEntity;
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
			if (entity instanceof OfferTemplateCategory) {
				String filePath = processImageUpload(entity, code, uploadedFile, providerCode);
				OfferTemplateCategory obj = (OfferTemplateCategory) entity;
				obj.setImagePath(filePath);

			} else if (entity instanceof OfferTemplate) {
				String filePath = processImageUpload(entity, code, uploadedFile, providerCode);
				OfferTemplate obj = (OfferTemplate) entity;
				obj.setImagePath(filePath);

			} else if (entity instanceof ServiceTemplate) {
				String filePath = processImageUpload(entity, code, uploadedFile, providerCode);
				ServiceTemplate obj = (ServiceTemplate) entity;
				obj.setImagePath(filePath);

			} else if (entity instanceof ProductTemplate) {
				String filePath = processImageUpload(entity, code, uploadedFile, providerCode);
				ProductTemplate obj = (ProductTemplate) entity;
				obj.setImagePath(filePath);
			}

			String extension = FilenameUtils.getExtension(uploadedFile.getFileName());
			handleChangeExtension(entity, extension, providerCode);
		}
	}

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
	private void handleChangeExtension(T entity, String newFileExt, String providerCode) throws IOException {
		String folder = getPicturePath(entity, providerCode);
		if (entity instanceof OfferTemplateCategory) {
			OfferTemplateCategory obj = (OfferTemplateCategory) entity;
			if (!FilenameUtils.getExtension(obj.getImagePath()).equals(newFileExt)) {
				Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
			}
		} else if (entity instanceof OfferTemplate) {
			OfferTemplate obj = (OfferTemplate) entity;
			if (!FilenameUtils.getExtension(obj.getImagePath()).equals(newFileExt)) {
				Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
			}
		} else if (entity instanceof ServiceTemplate) {
			ServiceTemplate obj = (ServiceTemplate) entity;
			if (!FilenameUtils.getExtension(obj.getImagePath()).equals(newFileExt)) {
				Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
			}
		} else if (entity instanceof ProductTemplate) {
			ProductTemplate obj = (ProductTemplate) entity;
			if (!FilenameUtils.getExtension(obj.getImagePath()).equals(newFileExt)) {
				Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
			}
		}
	}

	public void saveImageUpload(T entity, String providerCode) throws IOException {
		// check if image path is the same as entity.code
		String folder = getPicturePath(entity, providerCode);
		if (entity instanceof OfferTemplateCategory) {
			OfferTemplateCategory obj = (OfferTemplateCategory) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				String extension = FilenameUtils.getExtension(obj.getImagePath());
				Path target = Paths.get(folder, obj.getCode() + "." + extension);

				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof OfferTemplate) {
			OfferTemplate obj = (OfferTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				String extension = FilenameUtils.getExtension(obj.getImagePath());
				Path target = Paths.get(folder, obj.getCode() + "." + extension);

				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof ServiceTemplate) {
			ServiceTemplate obj = (ServiceTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				String extension = FilenameUtils.getExtension(obj.getImagePath());
				Path target = Paths.get(folder, obj.getCode() + "." + extension);

				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof ProductTemplate) {
			ProductTemplate obj = (ProductTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				String extension = FilenameUtils.getExtension(obj.getImagePath());
				Path target = Paths.get(folder, obj.getCode() + "." + extension);

				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
				obj.setImagePath(obj.getCode() + "." + extension);
			}
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
		if (entity instanceof OfferTemplateCategory) {
			OfferTemplateCategory obj = (OfferTemplateCategory) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(filename)) {
				String extension = FilenameUtils.getExtension(filename);

				// check if different file extension (update)
				if (!StringUtils.isBlank(obj.getImagePath())) {
					if (!FilenameUtils.getExtension(obj.getImagePath()).equals(FilenameUtils.getExtension(filename))) {
						extension = FilenameUtils.getExtension(filename);
						Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
					}
				}

				Path target = Paths.get(folder, obj.getCode() + "." + extension);
				try (InputStream input = new ByteArrayInputStream(imageData)) {
					Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
				}
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof OfferTemplate) {
			OfferTemplate obj = (OfferTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(filename)) {
				String extension = FilenameUtils.getExtension(filename);

				// check if different file extension (update)
				if (!StringUtils.isBlank(obj.getImagePath())) {
					if (!FilenameUtils.getExtension(obj.getImagePath()).equals(FilenameUtils.getExtension(filename))) {
						extension = FilenameUtils.getExtension(filename);
						Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
					}
				}

				Path target = Paths.get(folder, obj.getCode() + "." + extension);
				try (InputStream input = new ByteArrayInputStream(imageData)) {
					Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
				}
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof ServiceTemplate) {
			ServiceTemplate obj = (ServiceTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(filename)) {
				String extension = FilenameUtils.getExtension(filename);

				// check if different file extension (update)
				if (!StringUtils.isBlank(obj.getImagePath())) {
					if (!FilenameUtils.getExtension(obj.getImagePath()).equals(FilenameUtils.getExtension(filename))) {
						extension = FilenameUtils.getExtension(filename);
						Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
					}
				}

				Path target = Paths.get(folder, obj.getCode() + "." + extension);
				try (InputStream input = new ByteArrayInputStream(imageData)) {
					Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
				}
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		} else if (entity instanceof ProductTemplate) {
			ProductTemplate obj = (ProductTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(filename)) {
				String extension = FilenameUtils.getExtension(filename);

				// check if different file extension (update)
				if (!StringUtils.isBlank(obj.getImagePath())) {
					if (!FilenameUtils.getExtension(obj.getImagePath()).equals(FilenameUtils.getExtension(filename))) {
						extension = FilenameUtils.getExtension(filename);
						Files.deleteIfExists(Paths.get(folder, obj.getImagePath()));
					}
				}

				Path target = Paths.get(folder, obj.getCode() + "." + extension);
				try (InputStream input = new ByteArrayInputStream(imageData)) {
					Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
				}
				obj.setImagePath(obj.getCode() + "." + extension);
			}
		}
	}

	public void deleteImage(T entity, String providerCode) throws IOException {
		String folder = getPicturePath(entity, providerCode);
		if (entity instanceof OfferTemplateCategory) {
			OfferTemplateCategory obj = (OfferTemplateCategory) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				Files.deleteIfExists(source);
			}
		} else if (entity instanceof OfferTemplate) {
			OfferTemplate obj = (OfferTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				Files.deleteIfExists(source);
			}
		} else if (entity instanceof ServiceTemplate) {
			ServiceTemplate obj = (ServiceTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				Files.deleteIfExists(source);
			}
		} else if (entity instanceof ProductTemplate) {
			ProductTemplate obj = (ProductTemplate) entity;
			if (!org.meveo.commons.utils.StringUtils.isBlank(obj.getImagePath())) {
				Path source = Paths.get(folder, obj.getImagePath());
				Files.deleteIfExists(source);
			}
		}
	}
}
