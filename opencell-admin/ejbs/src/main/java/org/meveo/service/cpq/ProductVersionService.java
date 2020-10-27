package org.meveo.service.cpq;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.ProductVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * Product version service implementation.
 */

@Stateless
public class ProductVersionService extends
		PersistenceService<ProductVersion> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProductVersionService.class);
	private final static String PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the product (%d) is %s, it can not be updated nor removed";
	private final static String PRODUCT_VERSION_MISSING = "Version of the product %s is missing";
	private final static String PRODUCT_VERSION_ERROR_DUPLICATE = "Can not duplicate the version of product from version product (%d)";
	private static final String CAN_NOT_UPDATE_VERSION_PRODUCT_STAUTS = "Can not change the status of the product of version for (%d)";


	/**
	 * update product with status DRAFT only
	 * @param productVersion
	 * @return
	 * @throws ProductVersionException when the status is different to DRAFT
	 */
	public ProductVersion updateProductVersion(ProductVersion productVersion) throws ProductVersionException{
		LOGGER.info("updating product {}", productVersion.getId());
		
		if(!productVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
			LOGGER.warn("the product version {} can not be updated, because of its status => {}, it must be DRAFT status.", productVersion.getId(), productVersion.getStatus().toString());
			throw new ProductVersionException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, productVersion.getId(), productVersion.getStatus().toString()));
		}
		update(productVersion);
		LOGGER.info("the product ({}) updated successfully", productVersion.getId());
		return productVersion;
	}
	
	/**
	 * remove the version of the product with status DRAFT only
	 * @param id
	 * @throws ProductVersionException
	 * <br /> when : 
	 * <ul><li>the version of the product is missing</li>
	 * <li>status of the version of product is different of DRAFT</li>
	 * </ul>
	 */
	public void removeProductVersion(Long id) throws ProductVersionException {
		LOGGER.info("deleting version of product id {}", id);
		final ProductVersion productVersion = this.getProductVersion(id);
		if(!productVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
			LOGGER.warn("the status of version of product is not DRAFT, the current version is {}.Can not be deleted", productVersion.getStatus().toString());
			throw new ProductVersionException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, productVersion.getId(), productVersion.getStatus().toString()));
		}
		this.remove(productVersion);
		LOGGER.info("status of the product {} is deleted successfully", id);
	}
	
	/**
	 * duplicate a version of product with status DRAFT and value of the version is 1 
	 * @param id
	 * @return
	 * @throws ProductVersionException
	 * <br /> when : 
	 * <ul><li>the version of the product is missing</li>
	 * <li>error when saving the new version of the product</li>
	 *</ul>
	 */
	public ProductVersion duplicate(Long id) throws ProductVersionException{
		
		final ProductVersion duplicateVersion = this.getProductVersion(id);
		
		this.detach(duplicateVersion);
		duplicateVersion.setId(null);
		duplicateVersion.setCurrentVersion(1);
		duplicateVersion.setVersion(1);
		duplicateVersion.setStatus(VersionStatusEnum.DRAFT);
		duplicateVersion.setStatusDate(Calendar.getInstance().getTime());
		try {
			this.create(duplicateVersion);
		}catch(BusinessException e) {
			throw new ProductVersionException(String.format(PRODUCT_VERSION_ERROR_DUPLICATE, id), e);
		}
		return duplicateVersion;
	}
	
	/**
	 * change the status of the product of version
	 * @param id
	 * @param publish : if true the status will have a {@link VersionStatusEnum.PUBLIED}
	 * @return
	 * @throws ProductVersionException
	 */
	public ProductVersion publishOrCloseVersion(Long id, boolean publish) throws ProductVersionException {
		final ProductVersion productVersion = this.getProductVersion(id);
		if(publish) {
			productVersion.setStatus(VersionStatusEnum.PUBLIED);
		}else {
			productVersion.setStatus(VersionStatusEnum.CLOSED);
		}
		productVersion.setStatusDate(Calendar.getInstance().getTime());
		try {
			this.update(productVersion);
		}catch(BusinessException e) {
			throw new ProductVersionException(String.format(CAN_NOT_UPDATE_VERSION_PRODUCT_STAUTS, id), e);
		}
		return productVersion;
	}
	
	private ProductVersion getProductVersion(Long id) throws ProductVersionException{
		final ProductVersion productVersion = this.findById(id);
		if(productVersion == null || productVersion.getId() == null) {
			LOGGER.warn("The version product {}  is missing", id);
			throw new ProductVersionException(String.format(PRODUCT_VERSION_MISSING, id));
		}
		return productVersion;
	}
	

	@SuppressWarnings("unchecked")
	public List<ProductVersion> findByTags(List<Long> tagIds) {
		return this.getEntityManager().createNamedQuery("ProductVersion.findByTags").setParameter("tagIds", tagIds).getResultList();
	}
	
	
}