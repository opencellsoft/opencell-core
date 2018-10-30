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
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
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
        if (postData.getDiscountPlanItemType() == null) {
            missingParameters.add("discountPlanItemType");
        }
		if (postData.getDiscountPlanItemType() != null
				&& postData.getDiscountPlanItemType().equals(DiscountPlanItemTypeEnum.PERCENTAGE)
				&& postData.getPercent() == null) {
			missingParameters.add("percent");
		}
		if (postData.getDiscountPlanItemType() != null
				&& postData.getDiscountPlanItemType().equals(DiscountPlanItemTypeEnum.FIXED)
				&& postData.getDiscountAmount() == null) {
			missingParameters.add("discountAmount");
		}

        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode());
        if (discountPlanItem != null) {
            throw new EntityAlreadyExistsException(DiscountPlanItem.class, postData.getCode());
        }
        discountPlanItem = toDiscountPlanItem(postData, null);
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

        return new DiscountPlanItemDto(discountPlanItem);
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
                dpid = new DiscountPlanItemDto(dpi);
                discountPlanItemDtos.add(dpid);
            }
        }
        return discountPlanItemDtos;
    }

    public DiscountPlanItem toDiscountPlanItem(DiscountPlanItemDto source, DiscountPlanItem target) throws MeveoApiException {
        if (target == null) {
            target = new DiscountPlanItem();
            target.setCode(source.getCode());
            if (source.isDisabled() != null) {
                target.setDisabled(source.isDisabled());
            }
        }

        if (!StringUtils.isBlank(source.getDiscountPlanCode())) {
            DiscountPlan discountPlan = discountPlanService.findByCode(source.getDiscountPlanCode());
            if (discountPlan == null) {
                throw new EntityDoesNotExistsException(DiscountPlan.class, source.getDiscountPlanCode());
            }
            if (target.getDiscountPlan() != null && discountPlan != target.getDiscountPlan()) {
                throw new MeveoApiException("Parent discountPlan " + target.getDiscountPlan().getCode() + " of item " + source.getCode()
                        + " NOT match with DTO discountPlan " + source.getDiscountPlanCode());
            }
            target.setDiscountPlan(discountPlan);
        }

        if (!StringUtils.isBlank(source.getInvoiceCategoryCode())) {
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(source.getInvoiceCategoryCode());
            if (invoiceCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceCategory.class, source.getInvoiceCategoryCode());
            }
            target.setInvoiceCategory(invoiceCategory);
        }

        if (!StringUtils.isBlank(source.getInvoiceSubCategoryCode())) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(source.getInvoiceSubCategoryCode());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, source.getInvoiceSubCategoryCode());
            }
            target.setInvoiceSubCategory(invoiceSubCategory);
        }
        if (source.getPercent() != null) {
            target.setPercent(source.getPercent());
        }
        if (source.getAccountingCode() != null) {
            target.setAccountingCode(source.getAccountingCode());
        }
        if (source.getExpressionEl() != null) {
            target.setExpressionEl(source.getExpressionEl());
        }
        if (source.getDiscountPercentEl() != null) {
            target.setDiscountPercentEl(source.getDiscountPercentEl());
		}
		if (source.getDiscountAmount() != null) {
			target.setDiscountAmount(source.getDiscountAmount());
		}
		if (source.getDiscountPlanItemType() != null) {
			target.setDiscountPlanItemType(source.getDiscountPlanItemType());
		}
		if (source.getStartDate() != null) {
			target.setStartDate(source.getStartDate());
		}
		if (source.getEndDate() != null) {
			target.setEndDate(source.getEndDate());
		}
		if (source.getDefaultDuration() != null) {
			target.setDefaultDuration(source.getDefaultDuration());
		}
		if (source.getDurationUnit() != null) {
			target.setDurationUnit(source.getDurationUnit());
		}

        return target;
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