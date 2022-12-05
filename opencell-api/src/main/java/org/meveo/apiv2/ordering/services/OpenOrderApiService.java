package org.meveo.apiv2.ordering.services;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.ordering.resource.oo.OpenOrderDto;
import org.meveo.apiv2.ordering.resource.openorder.OpenOrderMapper;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.ThresholdMapper;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrder;
import org.meveo.model.ordering.OpenOrderStatusEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.order.OpenOrderService;
import org.meveo.service.order.ThresholdService;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class OpenOrderApiService extends PersistenceService<OpenOrder>{

    @Inject
    private OpenOrderService openOrderService;
    @Inject
    private ThresholdService thresholdService;
    @Inject
    private TagService tagService;


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
        if(dto.getThresholds() != null ) {
            thresholdService.deleteThresholdsByOpenOrderId(openOrder.getId());
            List<Threshold> thresholds = new ArrayList<>();
            if(openOrder.getOpenOrderQuote().getThresholds() != null){
                thresholds.addAll(openOrder.getOpenOrderQuote().getThresholds());
            }
            thresholds.addAll(thresholdMapper.toEntities(dto.getThresholds()));
            openOrder.setThresholds(thresholds);
        }
        if (null != dto.getTags()) {
            List<Tag> tags = fetchTags(dto.getTags());
            if (openOrder.getOpenOrderQuote() != null) {
                openOrder.setTags(new ArrayList<>(openOrder.getOpenOrderQuote().getTags()));
            }
            for(Tag tag : tags){
                if (!openOrder.getTags().contains(tag)){
                    openOrder.getTags().add(tag);
                }
            }


        }
        openOrder = openOrderService.update(openOrder);
        auditLogService.trackOperation("UPDATE", new Date(), openOrder, openOrder.getCode());
        return openOrderMapper.toResource(openOrder);
    }

    private void checkParameters(OpenOrder openOrder, OpenOrderDto dto) {

        if(dto.getEndOfValidityDate() != null && (dto.getEndOfValidityDate().before(new Date()) || dto.getEndOfValidityDate().before(openOrder.getActivationDate()))){
            throw new InvalidParameterException(" The EndOfValidityDate field should not be before current date or the activation date");
        }
        if(!(OpenOrderStatusEnum.NEW.equals(openOrder.getStatus()) || OpenOrderStatusEnum.IN_USE.equals(openOrder.getStatus()))){
            throw new BusinessApiException("Could not modify the open order: "+openOrder.getCode()+" current status: "+openOrder.getStatus());
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
        if(!(OpenOrderStatusEnum.NEW.equals(openOrder.getStatus()) || OpenOrderStatusEnum.IN_USE.equals(openOrder.getStatus()))){
            throw new BusinessApiException("Could not cancel the open order: "+openOrder.getCode()+" current status: "+openOrder.getStatus());
        }
        openOrder.setStatus(OpenOrderStatusEnum.CANCELED);
        openOrder.setCancelReason(openOrderDto.getCancelReason());
        openOrder = openOrderService.update(openOrder);
        
        Date operationDate = new Date();
        String operationType = "CANCEL";

        StringBuilder message = new StringBuilder(auditLogService.getDefaultMessage(operationType, operationDate, openOrder, openOrder.getCode(), Collections.emptyList()));
        message.append(" - Reason : ").append(openOrder.getCancelReason());
        
		auditLogService.trackOperation(operationType, operationDate, openOrder, openOrder.getCode(), message.toString());
        return openOrderMapper.toResource(openOrder);
    }
}