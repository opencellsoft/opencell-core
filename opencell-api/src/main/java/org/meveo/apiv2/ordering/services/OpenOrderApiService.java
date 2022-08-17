package org.meveo.apiv2.ordering.services;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.resource.openOrder.OpenOrderMapper;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.OpenOrderTemplateMapper;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.ThresholdMapper;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTemplateStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.order.OpenOrderService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.order.ThresholdService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.meveo.admin.util.CollectionUtil.isNullOrEmpty;

@Stateless
public class OpenOrderApiService extends PersistenceService<OpenOrder>{

    @Inject
    private ProductService productService;
    @Inject
    private AccountingArticleService accountingArticleService;
    @Inject
    private OpenOrderService openOrderService;
    @Inject
    private ThresholdService thresholdService;
    @Inject
    private TagService tagService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private AuditLogService auditLogService;

    private OpenOrderMapper openOrderMapper = new OpenOrderMapper();

    private ThresholdMapper thresholdMapper = new ThresholdMapper();

    /**
     * Update an open order
     * @param code
     * @param dto
     * @return
     */
    public OpenOrderDto update(String code, OpenOrderDto dto) {

        OpenOrder openOrder = openOrderService.findByCode(code);
        if (null == openOrder) {
            throw new BusinessApiException(String.format("open order with code %s doesn't exist", code));
        }
        checkParameters(openOrder, dto);
        openOrderMapper.fillEntity(openOrder, dto);
        if (null != dto.getTags()) openOrder.getTags().addAll(fetchTags(dto.getTags()));
        openOrder = openOrderService.update(openOrder);
        auditLogService.trackOperation("UPDATE", new Date(), openOrder, openOrder.getCode());
        return openOrderMapper.toResource(openOrder);
    }

    private void checkParameters(OpenOrder openOrder, OpenOrderDto dto) {

        if(dto.getEndOfValidityDate().after(new Date()) || dto.getEndOfValidityDate().after(openOrder.getActivationDate())){
            throw new InvalidParameterException(" The EndOfValidityDate field should not be after currente date or the activation date");
        }
    }

    private List<Tag> fetchTags(List<String> tagsCodes) {
        List<Tag> tags = new ArrayList<>();
        for (String tagCode : tagsCodes) {
            Tag tag = tagService.findByCode(tagCode);
            if (null == tag) {
                throw new BusinessApiException(String.format("Tag with code %s doesn't exist", tagCode));
            }
            tags.add(tag);
        }
        return tags;
    }

    /**
     * Cancel an open order
     * @param code
     * @param openOrderDto
     * @return
     */
    public OpenOrderDto cancel(String code, OpenOrderDto openOrderDto) {
        OpenOrder openOrder = openOrderService.findByCode(code);
        if (null == openOrder) {
            throw new BusinessApiException(String.format("open order with code %s doesn't exist", code));
        }
        openOrder.setStatus(OpenOrderStatusEnum.CANCELED);
        openOrder.setCancelReason(openOrderDto.getCancelReason());
        openOrder = openOrderService.update(openOrder);
        auditLogService.trackOperation("CANCEL", new Date(), openOrder, openOrder.getCode());
        return openOrderMapper.toResource(openOrder);
    }
}