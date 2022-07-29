package org.meveo.api.catalog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.DatePeriodDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListPricePlanMatrixVersionResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;

@Stateless
public class PricePlanMatrixVersionApi extends BaseCrudApi<PricePlanMatrixVersion, PricePlanMatrixVersionDto> {

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    @Inject
    protected ResourceBundle resourceMessages;

    @Override
    public PricePlanMatrixVersion create(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException, BusinessException {
        return createOrUpdate(pricePlanMatrixVersionDto);
    }

    @Override
    public PricePlanMatrixVersion update(PricePlanMatrixVersionDto dtoData) throws MeveoApiException, BusinessException {
        return createOrUpdate(dtoData);
    }

    public void removePricePlanMatrixVersion(String pricePlanMatrixCode, int currentVersion) {
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        handleMissingParameters();

        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            pricePlanMatrixVersionService.removePriceMatrixVersion(pricePlanMatrixVersion);
        } catch (BusinessException exp) {
            throw new MeveoApiException(exp);
        }
    }

    @Override
    public PricePlanMatrixVersion createOrUpdate(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        String pricePlanMatrixCode = checkPricePlanMatrixVersion(pricePlanMatrixVersionDto);
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionDto.getVersion()==null ? null: pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, pricePlanMatrixVersionDto.getVersion());
        if (pricePlanMatrixVersion == null) {
            pricePlanMatrixVersion = populatePricePlanMatrixVersion(new PricePlanMatrixVersion(), pricePlanMatrixVersionDto, VersionStatusEnum.DRAFT, Calendar.getInstance().getTime());
            pricePlanMatrixVersionService.create(pricePlanMatrixVersion);
        } else {
            throw new MeveoApiException("PricePlanMatrixVersion[code=" + pricePlanMatrixVersionDto.getLabel() + ",version=" + pricePlanMatrixVersionDto.getVersion() + "] already exists");
        }
        pricePlanMatrixService.update(pricePlanMatrixVersion.getPricePlanMatrix());

        return pricePlanMatrixVersion;
    }    
    
    public PricePlanMatrixVersion updatePricePlanMatrixVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        String pricePlanMatrixCode = checkPricePlanMatrixVersion(pricePlanMatrixVersionDto);
        final DatePeriod validity = pricePlanMatrixVersionDto.getValidity();
        if(validity!=null) {
            Date from = validity.getFrom();
            Date to = validity.getTo();
            if(from!=null && to!=null && to.before(from)) {
                throw new InvalidParameterException("Invalid validity period, the end date must be greather than the start date");
            }
        }
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, pricePlanMatrixVersionDto.getVersion());
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())){
            if(validity==null || validity.getTo()==null) {
                throw new InvalidParameterException("ending date must not be null to update a published pricePlanMatrixVersion ");
            } else if(validity.getTo().before(org.meveo.model.shared.DateUtils.setDateToEndOfDay(new Date()))) {
                throw new InvalidParameterException("ending date must be greater than today");
            }
            pricePlanMatrixVersionService.updatePublishedPricePlanMatrixVersion(pricePlanMatrixVersion, validity.getTo());
        } else {
            populatePricePlanMatrixVersion(pricePlanMatrixVersion, pricePlanMatrixVersionDto, pricePlanMatrixVersionDto.getStatusEnum(), pricePlanMatrixVersionDto.getStatusDate());
            try {
                pricePlanMatrixVersionService.updatePricePlanMatrixVersion(pricePlanMatrixVersion);
            } catch(BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        }
        pricePlanMatrixService.update(pricePlanMatrixVersion.getPricePlanMatrix());
        return pricePlanMatrixVersion;
    }

    private String checkPricePlanMatrixVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        Boolean isMatrix = pricePlanMatrixVersionDto.getMatrix();
        String pricePlanMatrixCode = pricePlanMatrixVersionDto.getPricePlanMatrixCode();

        if (StringUtils.isBlank(isMatrix)) {
            missingParameters.add("isMatrix");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
        if (StringUtils.isBlank(isMatrix)) {
            missingParameters.add("isMatrix");
        }
        if (isMatrix!=null && !isMatrix) {
        	int priceFilledParametersCounter = 0;
        	
        	if(!StringUtils.isBlank(pricePlanMatrixVersionDto.getPrice())){
        		priceFilledParametersCounter++;
        	}
        	
        	if(!StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTax())){
        		priceFilledParametersCounter++;
        	}

        	if(!StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTax())){
        		priceFilledParametersCounter++;
        	}
        	
        	if(priceFilledParametersCounter == 0) {
                throw new MeveoApiException("price must be provided for non-grid price version");
        	}
        	
        	if(priceFilledParametersCounter > 1) {
                throw new MeveoApiException("'amountWithoutTax' and 'amountWithTax' are deprecated, please use only property 'price' to provide unit price");
        	}
        	
        	if (!appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTax())) {
                missingParameters.add("amountWithTax");
            }
            if (appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTax())) {
                missingParameters.add("amountWithoutTax");
            }
        }
        
        
        
        
        

        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }

        handleMissingParametersAndValidate(pricePlanMatrixVersionDto);
        return pricePlanMatrixCode;
    }

    private PricePlanMatrixVersion populatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrixVersionDto pricePlanMatrixVersionDto, VersionStatusEnum status, Date statusTime) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        }
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        if(pricePlanMatrixVersion.getId() == null) {
            pricePlanMatrixVersion.setCurrentVersion(pricePlanMatrixVersionService.getLastVersion(pricePlanMatrix) + 1);
        }

        DatePeriod validity = pricePlanMatrixVersionDto.getValidity();
        pricePlanMatrixVersion.setValidity(new DatePeriod(DateUtils.truncateTime(validity.getFrom()), DateUtils.truncateTime(validity.getTo())));
        if (status != null) {
            pricePlanMatrixVersion.setStatus(status);
        }
        if (statusTime != null) {
            pricePlanMatrixVersion.setStatusDate(statusTime);
        }
        pricePlanMatrixVersion.setPrice(pricePlanMatrixVersionDto.getPrice());
        pricePlanMatrixVersion.setAmountWithoutTax(pricePlanMatrixVersionDto.getAmountWithoutTax());
        pricePlanMatrixVersion.setAmountWithTax(pricePlanMatrixVersionDto.getAmountWithTax());
        pricePlanMatrixVersion.setPriceEL(pricePlanMatrixVersionDto.getPriceEL());
        pricePlanMatrixVersion.setMatrix(pricePlanMatrixVersionDto.getMatrix());
        pricePlanMatrixVersion.setLabel(pricePlanMatrixVersionDto.getLabel());
        pricePlanMatrixVersion.setPriority(pricePlanMatrixVersionDto.getPriority());
        pricePlanMatrix.getVersions().add(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public GetPricePlanVersionResponseDto updateProductVersionStatus(String pricePlanMatrixCode, int currentVersion, VersionStatusEnum status) {
        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }            
            PricePlanMatrix pricePlanMatrix = pricePlanMatrixVersion.getPricePlanMatrix();
            if (pricePlanMatrix == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixCode);
            }
            pricePlanMatrix.getVersions().stream().filter(ppmv -> pricePlanMatrixVersion.getId() == null ||  pricePlanMatrixVersion.getId() != ppmv.getId())
                        .forEach(ppmv -> {
                            if(ppmv.getValidity() != null && ppmv.getStatus().equals(VersionStatusEnum.PUBLISHED) && ppmv.getValidity().isCorrespondsToPeriod(pricePlanMatrixVersion.getValidity(), false)) {
                                var formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String from = ppmv.getValidity() != null && ppmv.getValidity().getFrom() != null ? formatter.format(ppmv.getValidity().getFrom()) : "";
                                String to = ppmv.getValidity() != null && ppmv.getValidity().getTo() != null ? formatter.format(ppmv.getValidity().getTo()) : "";
                                throw new MeveoApiException("The current period is overlapping date with [" + from + " - "+ to +"]");
                            }
                        });
            pricePlanMatrixVersionService.updateProductVersionStatus(pricePlanMatrixVersion, status);
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersion);
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }

    public GetPricePlanVersionResponseDto duplicatePricePlanMatrixVersion(String pricePlanMatrixCode, int currentVersion,DatePeriodDto periodDto) {
        try {  
        	 PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
             if (pricePlanMatrixVersion == null) {
                 throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
             }  
             Date from = null;
             Date to =null;
             
             if(periodDto != null) {
            	 from=periodDto.getValidity().getFrom();
            	 to = periodDto.getValidity().getTo();
             }
             
             if(from==null){
                 PricePlanMatrixVersion pricePlanMatrixVersionPublished = pricePlanMatrixVersionService.getLastPublishedVersion(pricePlanMatrixCode);
                 if (pricePlanMatrixVersionPublished != null) {
                     Date endDate=pricePlanMatrixVersion.getValidity().getTo();
                     if(endDate==null) {
                        throw new MeveoApiException(resourceMessages.getString("error.pricePlanMatrixVersion.overlapPeriod"));
                     }
                     from=endDate;
                 } 
             }
             
            DatePeriod validity=new DatePeriod(DateUtils.truncateTime(from), DateUtils.truncateTime(to));
            
            PricePlanMatrix pricePlanMatrix =  pricePlanMatrixVersion.getPricePlanMatrix(); 
            pricePlanMatrix.getVersions()
			.stream()
			.forEach(ppmv -> {
                if(VersionStatusEnum.PUBLISHED.equals(ppmv.getStatus()) && ppmv.getValidity().isCorrespondsToPeriod(validity, false)) {
                    throw new MeveoApiException(resourceMessages.getString("error.pricePlanMatrixVersion.overlapPeriodWithVersion") + ppmv.getCurrentVersion());
	        	}
			 });
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersionService.duplicate(pricePlanMatrixVersion, validity));
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }

    public PricePlanMatrixVersionDto load(Long id) {
        return pricePlanMatrixVersionService.load(id);
    }

    public GetListPricePlanMatrixVersionResponseDto listPricePlanMatrixVersions(PagingAndFiltering pagingAndFiltering) {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }
        String sortBy = DEFAULT_SORT_ORDER_ID;
        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
            sortBy = pagingAndFiltering.getSortBy();
        }
        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, PricePlanMatrixVersion.class);
        Long totalCount = pricePlanMatrixVersionService.count(paginationConfiguration);
        GetListPricePlanMatrixVersionResponseDto result = new GetListPricePlanMatrixVersionResponseDto();
        result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if(totalCount > 0) {
            pricePlanMatrixVersionService.list(paginationConfiguration).stream().forEach(version -> {
                result.getPpmVersions().add(new PricePlanMatrixVersionDto(version));
            });
        }
        return result;
    }
}
