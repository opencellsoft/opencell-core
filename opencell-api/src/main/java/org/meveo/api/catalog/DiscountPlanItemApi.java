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
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 1, 2016 9:46:32 PM
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
     * @param postData posted data
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public void create(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("discountPlanItemCode");
        }
        if (StringUtils.isBlank(postData.getDiscountPlanCode())) {
            missingParameters.add("discountPlanCode");
        }
		if (postData.getDiscountValue() == null && postData.getDiscountValueEL() == null
				&& postData.getDiscountValueElSpark() == null) {
			missingParameters.add("discountValue, discountValueEL or discountValueELSpark");
		}
		if (postData.getDiscountPlanItemType() == null) {
			missingParameters.add("discountPlanItemType");
		}
		if (postData.getDiscountPlanItemType() != null && postData.getDiscountValue() == null) {
			missingParameters.add("discountValue");
		}

        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode());
        if (discountPlanItem != null) {
            throw new EntityAlreadyExistsException(DiscountPlanItem.class, postData.getCode());
        }
        discountPlanItem = toDiscountPlanItem(postData, null);
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), discountPlanItem, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        discountPlanItemService.create(discountPlanItem);
    }

    /**
     * updates the description of an existing discount plan item.
     * 
     * @param postData posted data to API containing discount plan infos
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void update(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("discountPlanItemCode");
        }
        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode());

        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, postData.getCode());
        }
        discountPlanItem = toDiscountPlanItem(postData, discountPlanItem);
        
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), discountPlanItem, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        discountPlanItemService.update(discountPlanItem);
    }

    /**
     * find a discount plan item by code.
     * 
     * @param discountPlanItemCode discount plan code
     * @return discount plan
     * @throws MeveoApiException meveo api exception.
     */
    public DiscountPlanItemDto find(String discountPlanItemCode) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }

        return new DiscountPlanItemDto(discountPlanItem, entityToDtoConverter.getCustomFieldsDTO(discountPlanItem, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

    /**
     * delete a discount plan item by code.
     * 
     * @param discountPlanItemCode discount plan item code
     * @throws MeveoApiException meveo api exception.
     * @throws BusinessException busines exception.
     */
    public void remove(String discountPlanItemCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }
        discountPlanItemService.remove(discountPlanItem);
    }

    /**
     * create if the the discount plan item is not existed, updates if exists.
     * 
     * @param postData posted data
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.s
     */
    public void createOrUpdate(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }
        if (discountPlanItemService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * retrieves all discount plan item of the user
     * 
     * @return list of disount plan item
     * @throws MeveoApiException meveo api exception.
     */
    public List<DiscountPlanItemDto> list() throws MeveoApiException {
        List<DiscountPlanItemDto> discountPlanItemDtos = new ArrayList<>();
        List<DiscountPlanItem> discountPlanItems = discountPlanItemService.list();
        if (discountPlanItems != null && !discountPlanItems.isEmpty()) {
            DiscountPlanItemDto dpid = null;
            for (DiscountPlanItem dpi : discountPlanItems) {
                dpid = new DiscountPlanItemDto(dpi, entityToDtoConverter.getCustomFieldsDTO(dpi, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                discountPlanItemDtos.add(dpid);
            }
        }
        return discountPlanItemDtos;
    }

    public DiscountPlanItem toDiscountPlanItem(DiscountPlanItemDto source, DiscountPlanItem target) throws MeveoApiException {
        DiscountPlanItem discountPlanItem = target;
        if (discountPlanItem == null) {
            discountPlanItem = new DiscountPlanItem();
            discountPlanItem.setCode(source.getCode());
            if (source.isDisabled() != null) {
                discountPlanItem.setDisabled(source.isDisabled());
            }
        }

        if (!StringUtils.isBlank(source.getDiscountPlanCode())) {
            DiscountPlan discountPlan = discountPlanService.findByCode(source.getDiscountPlanCode());
            if (discountPlan == null) {
                throw new EntityDoesNotExistsException(DiscountPlan.class, source.getDiscountPlanCode());
            }
            if (discountPlanItem.getDiscountPlan() != null && discountPlan != discountPlanItem.getDiscountPlan()) {
                throw new MeveoApiException("Parent discountPlan " + discountPlanItem.getDiscountPlan().getCode() + " of item " + source.getCode()
                        + " NOT match with DTO discountPlan " + source.getDiscountPlanCode());
            }
            discountPlanItem.setDiscountPlan(discountPlan);
        }

        if (!StringUtils.isBlank(source.getInvoiceCategoryCode())) {
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(source.getInvoiceCategoryCode());
            if (invoiceCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceCategory.class, source.getInvoiceCategoryCode());
            }
            discountPlanItem.setInvoiceCategory(invoiceCategory);
        }

        if (!StringUtils.isBlank(source.getInvoiceSubCategoryCode())) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(source.getInvoiceSubCategoryCode());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, source.getInvoiceSubCategoryCode());
            }
            discountPlanItem.setInvoiceSubCategory(invoiceSubCategory);
        }
        if (source.getAccountingCode() != null) {
            discountPlanItem.setAccountingCode(source.getAccountingCode());
        }
        if (source.getExpressionEl() != null) {
            discountPlanItem.setExpressionEl(source.getExpressionEl());
        }
        if (source.getExpressionElSpark() != null) {
            discountPlanItem.setExpressionElSpark(source.getExpressionElSpark());
        }
		if (source.getDiscountValue() != null) {
			discountPlanItem.setDiscountValue(source.getDiscountValue());
		}
		if (source.getDiscountValueEL() != null) {
			discountPlanItem.setDiscountValueEL(source.getDiscountValueEL());
		}
		if (source.getDiscountValueElSpark() != null) {
			discountPlanItem.setDiscountValueElSpark(source.getDiscountValueElSpark());
		}
		if (source.getDiscountPlanItemType() != null) {
			discountPlanItem.setDiscountPlanItemType(source.getDiscountPlanItemType());
		}

        return discountPlanItem;
    }

    /**
     * Enable or disable Discount plan item
     * 
     * @param code Discount plan item code
     * @param enable Should Discount plan item be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(code);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, code);
        }
        if (enable) {
            discountPlanItemService.enable(discountPlanItem);
        } else {
            discountPlanItemService.disable(discountPlanItem);
        }
    }
}