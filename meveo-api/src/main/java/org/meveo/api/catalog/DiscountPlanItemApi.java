package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Aug 1, 2016 9:46:32 PM
 *
 */
@Stateless
public class DiscountPlanItemApi extends BaseApi {
	
	@Inject
	private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;
    
    @Inject
    private InvoiceCategoryService invoiceCategoryService;
    
    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    /**
     * creates a discount plan item
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void create(DiscountPlanItemDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if(StringUtils.isBlank(postData.getDiscountPlanCode())){
        	missingParameters.add("discountPlanCode");
        }
        if(StringUtils.isBlank(postData.getInvoiceCategoryCode())){
        	missingParameters.add("invoiceCategoryCode");
        }
        if(postData.getPercent()==null){
        	missingParameters.add("percent");
        }
        
        handleMissingParameters();
        
        Provider currentProvider=currentUser.getProvider();
        if (discountPlanItemService.findByCode(postData.getCode(), currentProvider) != null) {
            throw new EntityAlreadyExistsException(DiscountPlanItem.class, postData.getCode());
        }
        DiscountPlanItem discountPlanItem=new DiscountPlanItem();
        discountPlanItem.setCode(postData.getCode());
        
        DiscountPlan discountPlan=discountPlanService.findByCode(postData.getDiscountPlanCode(), currentProvider);
        if(discountPlan==null){
        	throw new EntityDoesNotExistsException(DiscountPlan.class, postData.getDiscountPlanCode());
        }
        discountPlanItem.setDiscountPlan(discountPlan);
        InvoiceCategory invoiceCategory=invoiceCategoryService.findByCode(postData.getInvoiceCategoryCode(), currentProvider);
    	if(invoiceCategory==null){
    		throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategoryCode());
    	}
    	discountPlanItem.setInvoiceCategory(invoiceCategory);
        
    	if(!StringUtils.isBlank(postData.getInvoiceSubCategoryCode())){
    		InvoiceSubCategory invoiceSubCategory=invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategoryCode(),currentProvider);
    		discountPlanItem.setInvoiceSubCategory(invoiceSubCategory);
    	}
    	discountPlanItem.setPercent(postData.getPercent());
    	discountPlanItem.setAccountingCode(postData.getAccountingCode());
    	discountPlanItem.setExpressionEl(postData.getExpressionEl());
        discountPlanItemService.create(discountPlanItem, currentUser);
    }

    /**
     * updates the description of an existing discount plan item
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void update(DiscountPlanItemDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();
        Provider currentProvider=currentUser.getProvider();
        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode(), currentProvider);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, postData.getCode());
        }
        
        if(!StringUtils.isBlank(postData.getDiscountPlanCode())){
        	String discountPlan=discountPlanItem.getDiscountPlan().getCode();
        	if(!discountPlan.equalsIgnoreCase(postData.getDiscountPlanCode())){
        		throw new MeveoApiException("Parent discountPlan "+discountPlan+" of item "+postData.getCode()+" NOT match with DTO discountPlan "+postData.getDiscountPlanCode());
        	}
        }
        
        if(!StringUtils.isBlank(postData.getInvoiceCategoryCode())){
        	InvoiceCategory invoiceCategory=invoiceCategoryService.findByCode(postData.getInvoiceCategoryCode(), currentProvider);
        	if(invoiceCategory==null){
        		throw new EntityDoesNotExistsException(InvoiceCategory.class, postData.getInvoiceCategoryCode());
        	}
        	discountPlanItem.setInvoiceCategory(invoiceCategory);
        }
        
        if(!StringUtils.isBlank(postData.getInvoiceSubCategoryCode())){
    		InvoiceSubCategory invoiceSubCategory=invoiceSubCategoryService.findByCode(postData.getInvoiceSubCategoryCode(),currentProvider);
    		discountPlanItem.setInvoiceSubCategory(invoiceSubCategory);
    	}
        if(postData.getPercent()!=null){
        	discountPlanItem.setPercent(postData.getPercent());
        }
        if(postData.getAccountingCode()!=null){
        	discountPlanItem.setAccountingCode(postData.getAccountingCode());
        }
        if(postData.getExpressionEl()!=null){
        	discountPlanItem.setExpressionEl(postData.getExpressionEl());
        }

        discountPlanItemService.update(discountPlanItem, currentUser);
    }

    /**
     * find a discount plan item by code
     * 
     * @param discountPlanCode
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public DiscountPlanItemDto find(String discountPlanItemCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode, provider);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }

       return new DiscountPlanItemDto(discountPlanItem);
    }

    /**
     * delete a discount plan item by code
     * 
     * @param discountPlanItemCode
     * @param provider
     * @throws MeveoApiException
     */
    public void remove(String discountPlanItemCode, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode, provider);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }
        discountPlanItemService.remove(discountPlanItem);
    }

    /**
     * create if the the discount plan item code is not existing, updates if exists
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(DiscountPlanItemDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        if (discountPlanItemService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    /**
     * retrieves all discount plan item of the user
     * 
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public List<DiscountPlanItemDto> list(Provider provider) throws MeveoApiException {
    	List<DiscountPlanItemDto> discountPlanItemDtos = new ArrayList<DiscountPlanItemDto>();
        List<DiscountPlanItem> discountPlanItems = discountPlanItemService.list(provider);
        if (discountPlanItems != null && !discountPlanItems.isEmpty()) {
            DiscountPlanItemDto dpid=null;
            for (DiscountPlanItem dpi : discountPlanItems) {
                dpid = new DiscountPlanItemDto(dpi);
                discountPlanItemDtos.add(dpid);
            }
        }
        return discountPlanItemDtos;
    }
}
