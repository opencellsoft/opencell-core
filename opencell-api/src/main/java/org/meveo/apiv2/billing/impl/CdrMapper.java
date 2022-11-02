package org.meveo.apiv2.billing.impl;

import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import org.meveo.apiv2.billing.CdrDtoInput;
import org.meveo.apiv2.billing.ImmutableCdrDtoInput;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.model.rating.CDR;
import org.meveo.service.billing.impl.EdrService;

public class CdrMapper extends ResourceMapper<CdrDtoInput, CDR>{

    private EdrService edrService = (EdrService) getServiceInterface(EdrService.class.getSimpleName());
    
    @Override
    protected CdrDtoInput toResource(CDR entity) {
        return ImmutableCdrDtoInput.builder()
                    .id(entity.getId())
                    .accessCode(entity.getAccessCode())
                    .dateParam1(entity.getDateParam1())
                    .dateParam2(entity.getDateParam2())
                    .dateParam3(entity.getDateParam3())
                    .dateParam4(entity.getDateParam4())
                    .dateParam5(entity.getDateParam5())
                    .eventDate(entity.getEventDate())
                    .parameter1(entity.getParameter1())
                    .parameter2(entity.getParameter2())
                    .parameter3(entity.getParameter3())
                    .parameter4(entity.getParameter4())
                    .parameter5(entity.getParameter5())
                    .parameter6(entity.getParameter6())
                    .parameter7(entity.getParameter7())
                    .parameter8(entity.getParameter8())
                    .parameter9(entity.getParameter9())
                    .decimalParam1(entity.getDecimalParam1())
                    .decimalParam2(entity.getDecimalParam2())
                    .decimalParam3(entity.getDecimalParam3())
                    .decimalParam4(entity.getDecimalParam4())
                    .decimalParam5(entity.getDecimalParam5())
                    .extraParam(entity.getExtraParam())
                    .headerEDRId(entity.getHeaderEDR() != null ? ImmutableResource.builder().id(entity.getHeaderEDR().getId()).build() : null)
                    .quantity(entity.getQuantity())
                    .rejectReason(entity.getRejectReason())
                    .status(entity.getStatus())
                    .build();
    }

    @Override
    protected CDR toEntity(CdrDtoInput resource) {
        CDR cdr = new CDR();
        cdr.setId(resource.getId());
        cdr.setEventDate(resource.getEventDate());
        cdr.setQuantity(resource.getQuantity());
        cdr.setAccessCode(resource.getAccessCode());
        cdr.setStatus(resource.getStatus());
        cdr.setRejectReason(resource.getRejectReason());
       
        cdr.setParameter1(resource.getParameter1());
        cdr.setParameter2(resource.getParameter2());
        cdr.setParameter3(resource.getParameter3());
        cdr.setParameter4(resource.getParameter4());
        cdr.setParameter5(resource.getParameter5());
        cdr.setParameter6(resource.getParameter6());
        cdr.setParameter7(resource.getParameter7());
        cdr.setParameter8(resource.getParameter8());
        cdr.setParameter9(resource.getParameter9());
        
        cdr.setDateParam1(resource.getDateParam1());
        cdr.setDateParam2(resource.getDateParam2());
        cdr.setDateParam3(resource.getDateParam3());
        cdr.setDateParam4(resource.getDateParam4());
        cdr.setDateParam5(resource.getDateParam5());

        cdr.setDecimalParam1(resource.getDecimalParam1());
        cdr.setDecimalParam2(resource.getDecimalParam2());
        cdr.setDecimalParam3(resource.getDecimalParam3());
        cdr.setDecimalParam4(resource.getDecimalParam4());
        cdr.setDecimalParam5(resource.getDecimalParam5());
        cdr.setExtraParameter(resource.getExtraParam());
        
        if(resource.getHeaderEDRId() != null  && resource.getHeaderEDRId().getId() != null) {
               cdr.setHeaderEDR(edrService.findById(resource.getHeaderEDRId().getId()));
        }
        return cdr;
    }

}
