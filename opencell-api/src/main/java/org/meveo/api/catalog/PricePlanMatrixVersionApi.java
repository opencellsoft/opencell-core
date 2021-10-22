package org.meveo.api.catalog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.cpq.DuplicatePricePlanVersionRequestDto;
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
        Boolean isMatrix = pricePlanMatrixVersionDto.getMatrix();
        int currentVersion = pricePlanMatrixVersionDto.getVersion();
        String pricePlanMatrixCode = pricePlanMatrixVersionDto.getPricePlanMatrixCode();

        if (StringUtils.isBlank(isMatrix)) {
            missingParameters.add("isMatrix");
        }
        if (StringUtils.isBlank(pricePlanMatrixCode)) {
            missingParameters.add("pricePlanMatrixCode");
        }
		if (!isMatrix) {
			if (!appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTax())) {
				missingParameters.add("amountWithTax");
			}
			if (appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTax())) {
				missingParameters.add("amountWithoutTax");
			}
		}
        handleMissingParametersAndValidate(pricePlanMatrixVersionDto);

        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);

        if (pricePlanMatrixVersion == null) {
            pricePlanMatrixVersion = populatePricePlanMatrixVersion(new PricePlanMatrixVersion(), pricePlanMatrixVersionDto, VersionStatusEnum.DRAFT, Calendar.getInstance().getTime());
            pricePlanMatrixVersionService.create(pricePlanMatrixVersion);
        } else {
        	if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())){
        		final DatePeriod validity = pricePlanMatrixVersionDto.getValidity();
				if(validity==null || validity.getTo()==null) {
        			throw new ValidationException("ending date must not be null to update a published pricePlanMatrixVersion ");
        		} else if(validity.getTo().after(new Date())) {
                	throw new InvalidParameterException("ending date must not be greater than today");
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
        }
        pricePlanMatrixService.update(pricePlanMatrixVersion.getPricePlanMatrix());

        return pricePlanMatrixVersion;
    }

    private PricePlanMatrixVersion populatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrixVersionDto pricePlanMatrixVersionDto, VersionStatusEnum status, Date statusTime) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        }
        pricePlanMatrix.getVersions().stream().filter(ppmv -> pricePlanMatrixVersion.getId() == null ||  pricePlanMatrixVersion.getId() != ppmv.getId())
					.forEach(ppmv -> {
			        	if(ppmv.getValidity().isCorrespondsToPeriod(pricePlanMatrixVersionDto.getValidity(), false)) {
			        		var formatter = new SimpleDateFormat("dd/MM/yyyy");
			        		String from = ppmv.getValidity() != null && ppmv.getValidity().getFrom() != null ? formatter.format(ppmv.getValidity().getFrom()) : "";
			        		String to = ppmv.getValidity() != null && ppmv.getValidity().getTo() != null ? formatter.format(ppmv.getValidity().getTo()) : "";
			        		throw new MeveoApiException("The current period is overlapping date with [" + from + " - "+ to +"]");
			        	}
			        });
        		
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        if(pricePlanMatrixVersion.getId() == null) {
            pricePlanMatrixVersion.setCurrentVersion(pricePlanMatrixVersionService.getLastVersion(pricePlanMatrixVersionDto.getPricePlanMatrixCode()) + 1);
        }
        pricePlanMatrixVersion.setValidity(pricePlanMatrixVersionDto.getValidity());
        if (status != null) {
            pricePlanMatrixVersion.setStatus(status);
        }
        if (statusTime != null) {
            pricePlanMatrixVersion.setStatusDate(statusTime);
        }
        pricePlanMatrixVersion.setAmountWithoutTax(pricePlanMatrixVersionDto.getAmountWithoutTax());
        pricePlanMatrixVersion.setAmountWithTax(pricePlanMatrixVersionDto.getAmountWithTax());
        pricePlanMatrixVersion.setAmountWithoutTaxEL(pricePlanMatrixVersionDto.getAmountWithoutTaxEL());
        pricePlanMatrixVersion.setAmountWithTaxEL(pricePlanMatrixVersionDto.getAmountWithTaxEL());
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
            pricePlanMatrixVersionService.updateProductVersionStatus(pricePlanMatrixVersion, status);
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersion);
        } catch (BusinessException e) {
            throw new MeveoApiException(e);
        }
    }

    public GetPricePlanVersionResponseDto duplicatePricePlanMatrixVersion(DuplicatePricePlanVersionRequestDto postData) {
        try {
        	if (StringUtils.isBlank(postData.getPricePlanMatrixCode())) {
                  missingParameters.add("pricePlanMatrixCode");
              }
              if (StringUtils.isBlank(postData.getPricePlanMatrixVersion())) {
                  missingParameters.add("pricePlanMatrixVersion");
              }
              if (!StringUtils.isBlank(postData.getValidity())) {
            	  DatePeriod validity=postData.getValidity();
            	  if(StringUtils.isBlank(validity.getFrom()))
                  missingParameters.add("from");
            	  if(StringUtils.isBlank(validity.getTo()))
                   missingParameters.add("to");
              }
              handleMissingParameters();
        	
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(postData.getPricePlanMatrixCode(), postData.getPricePlanMatrixVersion());
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, postData.getPricePlanMatrixCode(), "pricePlanMatrixCode", "" + postData.getPricePlanMatrixVersion(), "currentVersion");
            }
            PricePlanMatrix pricePlanMatrix =  pricePlanMatrixVersion.getPricePlanMatrix(); 
            pricePlanMatrix.getVersions()
			.stream()
			.filter(ppmv -> pricePlanMatrixVersion.getId() == null ||  pricePlanMatrixVersion.getId() != ppmv.getId())
			.forEach(ppmv -> {
            if(ppmv.getValidity().isCorrespondsToPeriod(postData.getValidity(), false)) {
        		throw new MeveoApiException(resourceMessages.getString("error.pricePlanMatrixVersion.overlapPeriod"));
        	}
			 }); 
            pricePlanMatrixVersion.setValidity(postData.getValidity());
            return new GetPricePlanVersionResponseDto(pricePlanMatrixVersionService.duplicate(pricePlanMatrixVersion, false, null));
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
