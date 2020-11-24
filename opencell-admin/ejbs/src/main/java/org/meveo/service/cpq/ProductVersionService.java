package org.meveo.service.cpq;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;
/**
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 *
 * Product version service implementation.
 */
@Stateless
public class ProductVersionService extends
        PersistenceService<ProductVersion> {
    @Inject
    private ProductService productService;
    @Inject
    private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
    
    private final static String PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the product version (%d) is %s, it can not be updated nor removed";
    private final static String PRODUCT_VERSION_MISSING = "Version of the product %s is missing";
    private final static String PRODUCT_VERSION_ERROR_DUPLICATE = "Can not duplicate the version of product from version product (%d)";
    private static final String CAN_NOT_UPDATE_VERSION_PRODUCT_STAUTS = "Can not change the status of the product of version for (%d)";
    /**
     * update product with status DRAFT only
     * @param productVersion
     * @return
     * @when the status is different to DRAFT
     * @throws ProductException
     */
    public ProductVersion updateProductVersion(ProductVersion productVersion) throws BusinessException{
        String productCode=productVersion.getProduct().getCode();
        int currentVersion=productVersion.getCurrentVersion();
        log.info("updating productVersion with product code={} and current version={}",productCode,currentVersion);
        if(!productVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the product with product code={} and current version={}, it must be DRAFT status.", productCode,currentVersion);
            throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE,productVersion.getId(), productVersion.getStatus().toString()));
        }
        update(productVersion);
        return productVersion;
    }
    /**
     * remove the version of the product with status DRAFT only
     * @param  productCode, currentVersion
     * @throws ProductVersionException
     * <br /> when :
     * <ul><li>the version of the product is missing</li>
     * <li>status of the version of product is different of DRAFT</li>
     * </ul>
     * @throws ProductException
     */
    public void removeProductVersion(ProductVersion productVersion) throws BusinessException {
        if(!productVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the status of version of product is not DRAFT, the current version is {}.Can not be deleted", productVersion.getStatus().toString());
            throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, productVersion.getId(), productVersion.getStatus().toString()));
        }
        this.remove(productVersion);
    }
    /**
     * duplicate a version of product with status DRAFT and value of the version is 1
     * @param productVersion entity
     * @param productVersion duplicateHierarchy
     * @return
     * @throws ProductVersionException
     * <br /> when :
     * <ul><li>the version of the product is missing</li>
     * <li>error when saving the new version of the product</li>
     *</ul>
     */
    public ProductVersion duplicate(ProductVersion productVersion, boolean duplicateHierarchy) throws BusinessException{
    	productVersion = refreshOrRetrieve(productVersion);
    	
    	productVersion.getServices().size();
    	productVersion.getTags().size();
    	
    	if(!productVersion.getServices().isEmpty()) {
    		for (ServiceTemplate services : productVersion.getServices()) {
    			services.getServiceRecurringCharges().size();
    			services.getServiceSubscriptionCharges().size();
    			services.getServiceTerminationCharges().size();
    			services.getServiceUsageCharges().size();
			}
    	}
    	var serviceTemplateList = productVersion.getServices();
    	var tagList = new ArrayList<>(productVersion.getTags());

    	detach(productVersion);
    	ProductVersion duplicate = new ProductVersion();
    	 try {
             BeanUtils.copyProperties(duplicate, productVersion);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException("Failed to clone offer template", e);
        }
    	duplicate.setId(null);
     	duplicate.setVersion(0);
     	duplicate.setCurrentVersion(1);
    	duplicate.setStatus(VersionStatusEnum.DRAFT);
    	duplicate.setStatusDate(Calendar.getInstance().getTime());
    	duplicate.setTags(new HashSet<>());
    	duplicate.setServices(new ArrayList<>());

    	// TODO : voir duplicate.getId()

        try {
            this.create(duplicate);
        }catch(BusinessException e) {
            throw new BusinessException(String.format(PRODUCT_VERSION_ERROR_DUPLICATE, duplicate.getId()), e);
        }
        
    	if(duplicateHierarchy) {
			catalogHierarchyBuilderService.duplicateProductVersion(duplicate, serviceTemplateList, tagList, duplicate.getId() + "_");
    	}
    	
        return duplicate;
    }
    
    /**
     * change the status of the product of version
     * @param id
     * @param publish : if true the status will have a {@link VersionStatusEnum.PUBLIED}
     * @return
     * @throws ProductVersionException
     */
    public ProductVersion publishOrCloseVersion(Long id, boolean publish) {
        final ProductVersion productVersion = this.getProductVersion(id);
        if(publish) {
            productVersion.setStatus(VersionStatusEnum.PUBLISHED);
        }else {
            productVersion.setStatus(VersionStatusEnum.CLOSED);
        }
        productVersion.setStatusDate(Calendar.getInstance().getTime());
        try {
            this.update(productVersion);
        }catch(BusinessException e) {
            throw new BusinessException(String.format(CAN_NOT_UPDATE_VERSION_PRODUCT_STAUTS, id), e);
        }
        return productVersion;
    }
    private ProductVersion getProductVersion(Long id){
        final ProductVersion productVersion = this.findById(id);
        if(productVersion == null || productVersion.getId() == null) {
            log.warn("The version product {}  is missing", id);
            throw new BusinessException(String.format(PRODUCT_VERSION_MISSING, id));
        }
        return productVersion;
    }
    @SuppressWarnings("unchecked")
	public ProductVersion findByProductAndVersion(String productCode,int currentVersion) throws BusinessException{
        Product product=productService.findByCode(productCode);
        if(product == null) {
            log.warn("the product with code={} inexistent",productCode);
            throw new EntityDoesNotExistsException(Product.class,productCode);
        }
        Query query = getEntityManager().createNamedQuery("ProductVersion.findByProductAndVersion")
                .setParameter("productCode", productCode).setParameter("currentVersion", currentVersion);
        List<ProductVersion> productVersion=(List<ProductVersion>)query.getResultList();
        return productVersion.isEmpty() ? null : productVersion.get(0);
    }
    /**
     * change the status of product version
     * @param product version
     * @param status
     * @throws ProductVersionException
     */
    public ProductVersion updateProductVersionStatus(ProductVersion productVersion, VersionStatusEnum status) throws BusinessException{
        if(!productVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the product with product code={} and current version={}, it must be DRAFT status.", productVersion.getProduct().getCode(),productVersion.getCurrentVersion());
            throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE,productVersion.getId(), productVersion.getStatus().toString()));
        }else {
            productVersion.setStatus(status);
            productVersion.setStatusDate(Calendar.getInstance().getTime());
        }
        return  update(productVersion);
    }
    
    @SuppressWarnings("unchecked")
	public List<ProductVersion> findByTags(List<Long> tagIds) {
		return this.getEntityManager().createNamedQuery("ProductVersion.findByTags").setParameter("tagIds", tagIds).getResultList();
	}
}