package org.meveo.api.catalog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.DatePeriodDto;
import org.meveo.api.dto.catalog.TradingPricePlanInputDto;
import org.meveo.api.dto.catalog.TradingPricePlanVersionDto;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetListPricePlanMatrixVersionResponseDto;
import org.meveo.api.dto.response.catalog.GetPricePlanVersionResponseDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.TradingPricePlanMatrixLine;
import org.meveo.model.catalog.TradingPricePlanVersion;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.cpq.enums.ContractStatusEnum;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.catalog.impl.TradingPricePlanMatrixLineService;
import org.meveo.service.catalog.impl.TradingPricePlanVersionService;
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
    @Inject
    private TradingCurrencyService tradingCurrencyService;
    @Inject
    private TradingPricePlanVersionService tradingPricePlanVersionService;
    @Inject
    private TradingPricePlanMatrixLineService tradingPricePlanMatrixLineService;

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
            PricePlanMatrix ppm = pricePlanMatrixService.findByCode(pricePlanMatrixCode);
            if (ppm != null && ppm.getContractItems()!=null && ppm.getContractItems().size() > 0) {
                Contract contract = ppm.getContractItems().get(0).getContract();
                if (ContractStatusEnum.DRAFT.toString().equals(contract.getStatus())) {
                    pricePlanMatrixVersionService.removePriceMatrixVersionOnlyNotClosed(pricePlanMatrixVersion);
                }
                else {
                    throw new MeveoApiException(String.format("status of the contrat is not Draft , it can not be updated nor removed the price Plan Matrix Version"));
                }
            }
            else{
                //pour les PV non lié au Contract
                pricePlanMatrixVersionService.removePriceMatrixVersion(pricePlanMatrixVersion);
            }
        } catch (BusinessException exp) {
            throw new MeveoApiException(exp);
        }
    }

    @Override
    public PricePlanMatrixVersion createOrUpdate(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException, BusinessException {
        String pricePlanMatrixCode = checkPricePlanMatrixVersion(pricePlanMatrixVersionDto);
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionDto.getVersion()==null ? null: pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, pricePlanMatrixVersionDto.getVersion());
        if (pricePlanMatrixVersion == null) {
            final DatePeriod validity = pricePlanMatrixVersionDto.getValidity();
        	if(validity!=null) {
                Date from = validity.getFrom();
                Date to = validity.getTo();
                if(from!=null && to!=null && !to.after(from)) {
                    throw new InvalidParameterException("Invalid validity period, the end date must be greather than the start date");
                }
            }
            pricePlanMatrixVersion = populatePricePlanMatrixVersion(new PricePlanMatrixVersion(), pricePlanMatrixVersionDto, VersionStatusEnum.DRAFT, Calendar.getInstance().getTime());
            pricePlanMatrixVersionService.create(pricePlanMatrixVersion);
        } else {
            throw new MeveoApiException("PricePlanMatrixVersion[code=" + pricePlanMatrixVersionDto.getLabel() + ",version=" + pricePlanMatrixVersionDto.getVersion() + "] already exists");
        }
        pricePlanMatrixService.update(pricePlanMatrixVersion.getPricePlanMatrix());

        return pricePlanMatrixVersion;
    }    
    
    public PricePlanMatrixVersion updatePricePlanMatrixVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException {
        String pricePlanMatrixCode = checkPricePlanMatrixVersion(pricePlanMatrixVersionDto);
        final DatePeriod validity = pricePlanMatrixVersionDto.getValidity();
        PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, pricePlanMatrixVersionDto.getVersion());
        if(validity!=null) {
            Date from = validity.getFrom();
            Date to = validity.getTo();
            if(from!=null && to!=null && !to.after(from)) {
                throw new InvalidParameterException("Invalid validity period, the end date must be greather than the start date");
            }


            if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())) {
                List<PricePlanMatrixVersion> versions = pricePlanMatrixVersionService.findByChargeTemplates(pricePlanMatrixVersion.getPricePlanMatrix().getChargeTemplates()
                                                                                                                           .stream()
                                                                                                                           .map(ChargeTemplate::getId)
                                                                                                                           .collect(Collectors.toSet()));
                versions.stream()
                        .filter(ppmv -> !pricePlanMatrixVersion.getId().equals(ppmv.getId()) && VersionStatusEnum.PUBLISHED.equals(ppmv.getStatus()))
                        .forEach(ppmv -> {
                            if(ppmv.getValidity() != null && ppmv.getValidity().isCorrespondsToPeriod(pricePlanMatrixVersionDto.getValidity(), false)) {
                                var formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String sourceFrom = pricePlanMatrixVersion.getValidity().getFrom() != null ? formatter.format(pricePlanMatrixVersion.getValidity().getFrom()) : "";
                                String sourceTo = pricePlanMatrixVersion.getValidity().getTo() != null ? formatter.format(pricePlanMatrixVersion.getValidity().getTo()) : "";
                                String eFrom = ppmv.getValidity().getFrom() != null ? formatter.format(ppmv.getValidity().getFrom()) : "";
                                String eTo = ppmv.getValidity().getTo() != null ? formatter.format(ppmv.getValidity().getTo()) : "";
                                // throw new MeveoApiException("The current period is overlapping date with [" + eFrom + " - "+ eTo +"]");
                                ChargeTemplate chargeTemplate = ppmv.getPricePlanMatrix()
                                                                                      .getChargeTemplates()
                                                                                      .iterator()
                                                                                      .next();
                                throw new MeveoApiException(String.format("Validity overlapping is forbidden for PUBLISHED price versions linked to the same charge. " +
                                        "Price version [id=%s,label=%s,validity.from=%s,validity.to=%s] " +
                                        "would overlap with price version [id=%s,label=%s,validity.from=%s,validity.to=%s] " +
                                        "in charge [id=%s,code=%s,description=%s]",
                                        pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getLabel(), sourceFrom, sourceTo,
                                        ppmv.getId(), ppmv.getLabel(), eFrom, eTo,
                                        chargeTemplate.getId(), chargeTemplate.getCode(), chargeTemplate.getDescription()));

                            }
                });
            }
        }
        if(VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus())){
        	if(validity != null && validity.getTo() != null) {
        		pricePlanMatrixVersionService.updatePublishedPricePlanMatrixVersion(pricePlanMatrixVersion, validity.getTo());
        	}
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

    private String checkPricePlanMatrixVersion(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) throws MeveoApiException {
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
        	if(StringUtils.isBlank(pricePlanMatrixVersionDto.getPrice())) {
        	    if(StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTax()) && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTax())) {
        	        throw new MeveoApiException("price must be provided for non-grid price version");
        	    }

                if (StringUtils.isNotBlank(pricePlanMatrixVersionDto.getAmountWithoutTax()) && StringUtils.isNotBlank(pricePlanMatrixVersionDto.getAmountWithTax())) {
                    throw new MeveoApiException("'amountWithoutTax' and 'amountWithTax' are deprecated, please use only property 'price' to provide unit price");
                }
        	    
        	    if (!appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTax())) {
                    missingParameters.add("amountWithTax");
                }
                if (appProvider.isEntreprise() && StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTax())) {
                    missingParameters.add("amountWithoutTax");
                }
        	} else {
        	    if (StringUtils.isNotBlank(pricePlanMatrixVersionDto.getAmountWithoutTax()) || StringUtils.isNotBlank(pricePlanMatrixVersionDto.getAmountWithTax())) {
        	        throw new MeveoApiException("'amountWithoutTax' and 'amountWithTax' are deprecated, please use only property 'price' to provide unit price");
                }
        	}
        }
        if (!StringUtils.isBlank(pricePlanMatrixVersionDto.getPriceEL()) && (!StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTaxEL())
                || !StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTaxEL()))) {
            log.error("'amountWithoutTaxEL' and 'amountWithTaxEL' are deprecated, please use only property 'priceEL' to provide unit price");
            throw new InvalidParameterException("'amountWithoutTaxEL' and 'amountWithTaxEL' are deprecated, please use only property 'priceEL' to provide unit price");
        }
        if (StringUtils.isBlank(pricePlanMatrixVersionDto.getPriceEL()) && (!StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithoutTaxEL())
                || !StringUtils.isBlank(pricePlanMatrixVersionDto.getAmountWithTaxEL()))) {
            pricePlanMatrixVersionDto.setPriceEL(pricePlanMatrixVersionDto.getAmountWithTaxEL());
            if(StringUtils.isBlank(pricePlanMatrixVersionDto.getPriceEL())){
                pricePlanMatrixVersionDto.setPriceEL(pricePlanMatrixVersionDto.getAmountWithoutTaxEL());
            }
        }
        checkPricePlanMatrixVersionValidityPerContract(pricePlanMatrixVersionDto);
        handleMissingParametersAndValidate(pricePlanMatrixVersionDto);
        return pricePlanMatrixCode;
    }

    private void checkPricePlanMatrixVersionValidityPerContract(PricePlanMatrixVersionDto pricePlanMatrixVersionDto) {
        PricePlanMatrixVersion pricePlanMatrixVersion = null;
        if (pricePlanMatrixVersionDto.getVersion() != null) {
            pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixVersionDto.getPricePlanMatrixCode(), pricePlanMatrixVersionDto.getVersion());
        }
        if (pricePlanMatrixVersion == null) {
            return;
        }
    }

    private PricePlanMatrixVersion populatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrixVersionDto pricePlanMatrixVersionDto, VersionStatusEnum status, Date statusTime) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        if (pricePlanMatrix == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixVersionDto.getPricePlanMatrixCode());
        }

        // TODO #ARE,
        if (!ListUtils.isEmtyCollection(pricePlanMatrix.getChargeTemplates()) && PriceVersionTypeEnum.PERCENTAGE.equals(pricePlanMatrixVersionDto.getPriceVersionType())){
            log.error("The priceVersionType property should not be percentage"); // TODO #ARE, The price plan is linked to the charge: "+pricePlanMatrix.getChargeTemplate().getCode());
            throw new MeveoApiException("The priceVersionType property should not be percentage"); // TODO #ARE, The price plan is linked to the charge: "+pricePlanMatrix.getChargeTemplate().getCode());
        }
        Boolean isMatrix = pricePlanMatrixVersionDto.getMatrix() != null && pricePlanMatrixVersionDto.getMatrix();
        if (!isMatrix && PriceVersionTypeEnum.PERCENTAGE.equals(pricePlanMatrixVersionDto.getPriceVersionType())){
            log.error("The priceVersionType property should not be percentage, The isMatrix property is false");
            throw new MeveoApiException("The priceVersionType property should not be percentage, The isMatrix property is false");
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
        // Update Price with amountWithoutTax if price still null
        if(pricePlanMatrixVersion.getPrice() == null) {
        	pricePlanMatrixVersion.setAmountWithoutTax(pricePlanMatrixVersionDto.getAmountWithoutTax());
        }
        // Update Price with amountWithTax if price still null after update with amountWithoutTax
        if(pricePlanMatrixVersion.getPrice() == null) {
        	pricePlanMatrixVersion.setAmountWithTax(pricePlanMatrixVersionDto.getAmountWithTax());
        }
        pricePlanMatrixVersion.setPriceEL(pricePlanMatrixVersionDto.getPriceEL());
        pricePlanMatrixVersion.setMatrix(pricePlanMatrixVersionDto.getMatrix());
        pricePlanMatrixVersion.setLabel(pricePlanMatrixVersionDto.getLabel());
        pricePlanMatrixVersion.setPriority(pricePlanMatrixVersionDto.getPriority());
        pricePlanMatrix.getVersions().add(pricePlanMatrixVersion);
        if(!StringUtils.isBlank(pricePlanMatrixVersionDto.getPriceVersionType())) {
            pricePlanMatrixVersion.setPriceVersionType(pricePlanMatrixVersionDto.getPriceVersionType());
        }
        return pricePlanMatrixVersion;
    }

    public GetPricePlanVersionResponseDto updateProductVersionStatus(String pricePlanMatrixCode, int currentVersion, VersionStatusEnum status) {
        try {
            PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findByPricePlanAndVersion(pricePlanMatrixCode, currentVersion);
            if (pricePlanMatrixVersion == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixCode, "pricePlanMatrixCode", "" + currentVersion, "currentVersion");
            }
            PricePlanMatrix pricePlanMatrix = PersistenceUtils.initializeAndUnproxy(pricePlanMatrixVersion.getPricePlanMatrix());
            if (pricePlanMatrix == null) {
                throw new EntityDoesNotExistsException(PricePlanMatrix.class, pricePlanMatrixCode);
            }
            // The validity overlapping check will be only between charges templates otherwise it's not mandatory example :
            // duplication the price plan matrix in the framework agreements
            if (!pricePlanMatrix.getChargeTemplates().isEmpty()) {
                List<PricePlanMatrixVersion> versions = pricePlanMatrixVersionService.findByChargeTemplates(pricePlanMatrix.getChargeTemplates()
                        .stream()
                        .map(ChargeTemplate::getId)
                        .collect(Collectors.toSet()));
                versions.stream()
                        .filter(ppmv -> !pricePlanMatrixVersion.getId().equals(ppmv.getId()) && VersionStatusEnum.PUBLISHED.equals(ppmv.getStatus()))
                        .forEach(ppmv -> {
                            if(ppmv.getValidity() != null&& ppmv.getValidity().isCorrespondsToPeriod(pricePlanMatrixVersion.getValidity(), false)) {
                                var formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String sourceFrom = pricePlanMatrixVersion.getValidity().getFrom() != null ? formatter.format(pricePlanMatrixVersion.getValidity().getFrom()) : "";
                                String sourceTo = pricePlanMatrixVersion.getValidity().getTo() != null ? formatter.format(pricePlanMatrixVersion.getValidity().getTo()) : "";
                                String eFrom = ppmv.getValidity().getFrom() != null ? formatter.format(ppmv.getValidity().getFrom()) : "";
                                String eTo = ppmv.getValidity().getTo() != null ? formatter.format(ppmv.getValidity().getTo()) : "";
                                // throw new MeveoApiException("The current period is overlapping date with [" + eFrom + " - "+ eTo +"]");
                                ChargeTemplate chargeTemplate = pricePlanMatrixVersion.getPricePlanMatrix()
                                        .getChargeTemplates()
                                        .iterator()
                                        .next();
                                throw new MeveoApiException(String.format("Validity overlapping is forbidden for PUBLISHED price versions linked to the same charge. " +
                                                "Price version [id=%s,label=%s,validity.from=%s,validity.to=%s] " +
                                                "would overlap with price version [id=%s,label=%s,validity.from=%s,validity.to=%s] " +
                                                "in charge [id=%s,code=%s,description=%s]",
                                        pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getLabel(), sourceFrom, sourceTo,
                                        ppmv.getId(), ppmv.getLabel(), eFrom, eTo,
                                        chargeTemplate.getId(), chargeTemplate.getCode(), chargeTemplate.getDescription()));
                            }
                        });
            }
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
                     Date endDate= Optional.ofNullable(pricePlanMatrixVersion.getValidity()).map(DatePeriod::getTo).orElse(null);
                     if(endDate==null) {
                        throw new MeveoApiException(resourceMessages.getString("error.pricePlanMatrixVersion.overlapPeriod"));
                     }
                     from=endDate;
                 } 
             }
             
             DatePeriod validity=new DatePeriod(DateUtils.truncateTime(from), DateUtils.truncateTime(to));
             return new GetPricePlanVersionResponseDto(pricePlanMatrixVersionService.duplicate(pricePlanMatrixVersion, pricePlanMatrixVersion.getPricePlanMatrix(), validity, pricePlanMatrixVersion.getPriceVersionType(), false));
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
                result.getPpmVersions().add(new PricePlanMatrixVersionDto(version, true));
            });
        }
        return result;
    }


    public void removeAllTradingPricePlanLinesByVersion(Long pricePlanMatrixVersionId, String tradingCurrencyCode) {
        PricePlanMatrixVersion planMatrixVersion = pricePlanMatrixVersionService.findById(pricePlanMatrixVersionId);
        if(planMatrixVersion == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanMatrixVersionId);
        }
        if(tradingCurrencyCode == null) {
            throw new MissingParameterException("tradingCurrenyCode");
        }
        planMatrixVersion.getLines().forEach(ppml -> {
            ppml.getTradingPricePlanMatrixLines().removeIf(cppml -> cppml.getTradingCurrency() != null && cppml.getTradingCurrency().getCurrency() != null && cppml.getTradingCurrency().getCurrency().getCurrencyCode().equals(tradingCurrencyCode));
        });
    }

    @Transactional
    public TradingPricePlanVersion createTradingPricePlanVersion(TradingPricePlanVersionDto dtoData) throws MeveoApiException, BusinessException {
        checkMandatoryFields(dtoData);

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCodeOrId(dtoData.getTradingCurrency().getCode(), dtoData.getTradingCurrency().getId());
        if(tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, dtoData.getTradingCurrency().getCode(), "code", dtoData.getTradingCurrency().getId()+"", "id");
        }

        if(appProvider.getCurrency() != null && appProvider.getCurrency().getCurrencyCode().equals(dtoData.getTradingCurrency().getCode())) {
            throw new org.meveo.admin.exception.InvalidParameterException("Trading PPV currency couldn't be the same as functional currency");
        }

        PricePlanMatrixVersion ppmv = pricePlanMatrixVersionService.findById(dtoData.getPricePlanMatrixVersionId());
        if(ppmv == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixVersionId());
        }

        TradingPricePlanVersion existingCPPV = tradingPricePlanVersionService.findByPricePlanVersionAndCurrency(ppmv, tradingCurrency);
        if(existingCPPV != null) {
            throw new BusinessException("Trading price plan version already exist for Price Plan " + ppmv.getId() + " and currency " + tradingCurrency.getCurrencyCode());
        }

        if(ppmv.getPrice()!= null && dtoData.getRate() != null && dtoData.getTradingPrice() != null && dtoData.getRate().multiply(ppmv.getPrice()).compareTo(dtoData.getTradingPrice()) != 0) {
        	throw new BusinessException("The trading price is inconsistent with the price plan version price and the rate");
        }

        TradingPricePlanVersion entity = new TradingPricePlanVersion();
        entity.setPricePlanMatrixVersion(ppmv);
        entity.setTradingCurrency(tradingCurrency);
        entity.setRate(dtoData.getRate());
        entity.setTradingPrice(dtoData.getTradingPrice());
        entity.setUseForBillingAccounts(dtoData.isUseForBillingAccounts());

        tradingPricePlanVersionService.create(entity);

        return entity;
    }

    public TradingPricePlanVersion updateTradingPricePlanVersion(Long cppvId, TradingPricePlanVersionDto dtoData) throws MeveoApiException, BusinessException {
        checkMandatoryFields(dtoData);

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCodeOrId(dtoData.getTradingCurrency().getCode(), dtoData.getTradingCurrency().getId());
        if(tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, dtoData.getTradingCurrency().getCode(), "code", dtoData.getTradingCurrency().getId()+"", "id");
        }

        if(appProvider.getCurrency() != null && appProvider.getCurrency().getCurrencyCode().equals(dtoData.getTradingCurrency().getCode())) {
            throw new InvalidParameterException("Trading PPV currency couldn't be the same as functional currency");
        }

        PricePlanMatrixVersion ppmv = pricePlanMatrixVersionService.findById(dtoData.getPricePlanMatrixVersionId());
        if(ppmv == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixVersionId());
        }

        log.info(" ##### {}", ppmv.getStatus());
        if(VersionStatusEnum.PUBLISHED.equals(ppmv.getStatus())) {
            throw new BusinessApiException("Trading price plan cannot be update for published price plan matrix version");
        }

        if(ppmv.getPrice()!= null && dtoData.getRate() != null && dtoData.getTradingPrice() != null && dtoData.getRate().multiply(ppmv.getPrice()).compareTo(dtoData.getTradingPrice()) != 0) {
        	throw new BusinessException("The trading price is inconsistent with the price plan version price and the rate");
        }

        TradingPricePlanVersion cppv = ppmv.getTradingPricePlanVersions()
                .stream()
                .filter(e -> e.getId().equals(cppvId))
                .findFirst()
                .orElseThrow(() -> new EntityDoesNotExistsException("Trading PPV " + cppvId + " not found for price plan version " + ppmv.getId()));

        // Check if another cppv exists for the new currency
        TradingPricePlanVersion existingCPPV = tradingPricePlanVersionService.findByPricePlanVersionAndCurrency(ppmv, tradingCurrency);
		if(!cppv.getTradingCurrency().getCurrencyCode().equals(tradingCurrency.getCurrencyCode())
                && existingCPPV != null && existingCPPV.getId().equals(cppv.getId())) {
            throw new BusinessException("Trading price plan version already exist for Price Plan " + ppmv.getId() + " and currecy " + tradingCurrency.getCurrencyCode());
        }

        cppv.setRate(dtoData.getRate());
        cppv.setTradingPrice(dtoData.getTradingPrice());
        cppv.setUseForBillingAccounts(dtoData.isUseForBillingAccounts());
        cppv.setTradingCurrency(tradingCurrency);

        tradingPricePlanVersionService.update(cppv);

        return cppv;
    }

    private void checkMandatoryFields(TradingPricePlanVersionDto dtoData) {
        if(dtoData.getTradingCurrency() == null || (StringUtils.isBlank(dtoData.getTradingCurrency().getCode()) && dtoData.getTradingCurrency().getId() == null)) {
            missingParameters.add("tradingCurrency");
        }
        if(StringUtils.isBlank(dtoData.getPricePlanMatrixVersionId())) {
            missingParameters.add("pricePlanMatrixVersionId");
        }
        handleMissingParameters();
    }

    public void deleteTradingPricePlanVersion(Long tppvId) {
		TradingPricePlanVersion entity = tradingPricePlanVersionService.findById(tppvId);
		if(entity == null) {
			throw new EntityDoesNotExistsException(TradingPricePlanVersion.class, tppvId);
		}
		if(VersionStatusEnum.PUBLISHED.equals(entity.getPricePlanMatrixVersion().getStatus())) {
			throw new BusinessException("Cannot delete trading price plan version for published price plan version");
		}
		tradingPricePlanVersionService.remove(entity);
	}

    public void disableAllTradingPricePlan(TradingPricePlanInputDto tradingPricePlanInputDto) {
    	enableOrDisableTradingPrice(tradingPricePlanInputDto, false);
		
	}

	public void enableAllTradingPricePlan(TradingPricePlanInputDto tradingPricePlanInputDto) {
		enableOrDisableTradingPrice(tradingPricePlanInputDto, true);
	}
	/**
	 * 
	 * @param tradingPricePlanInputDto
	 * @param enable true trading price will be enable otherwise disabled
	 */
	private void enableOrDisableTradingPrice(TradingPricePlanInputDto tradingPricePlanInputDto, boolean enable) {
		if(tradingPricePlanInputDto.getPricePlanMatrixVersionId() == null) {
			missingParameters.add("pricePlanMatrixVersionId");
		}
		if(tradingPricePlanInputDto.getTradingCurrency() == null || (tradingPricePlanInputDto.getTradingCurrency().getId() == null && StringUtils.isBlank(tradingPricePlanInputDto.getTradingCurrency().getCode()))) {
			missingParameters.add("tradingCurrency");
		}
		handleMissingParameters();
		PricePlanMatrixVersion pricePlanMatrixVersion = pricePlanMatrixVersionService.findById(tradingPricePlanInputDto.getPricePlanMatrixVersionId());
		if(pricePlanMatrixVersion == null) {
			throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, tradingPricePlanInputDto.getPricePlanMatrixVersionId());
		}
		TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCodeOrId(tradingPricePlanInputDto.getTradingCurrency().getCode(), tradingPricePlanInputDto.getTradingCurrency().getId());
		if(tradingCurrency == null) {
			throw new EntityDoesNotExistsException(TradingCurrency.class, "(" + tradingPricePlanInputDto.getTradingCurrency().getCode() + "," + tradingPricePlanInputDto.getTradingCurrency().getId() + ")");
		}
		Set<Long> idsToUpdate = pricePlanMatrixVersion.getLines().stream()
								.map(PricePlanMatrixLine::getTradingPricePlanMatrixLines).flatMap(Set::stream)
								.filter(trading -> trading.getTradingCurrency().getId() == tradingCurrency.getId())
								.map(TradingPricePlanMatrixLine::getId)
								.collect(Collectors.toSet());
		tradingPricePlanMatrixLineService.disableOrEnableAllTradingPricePlanMatrixLine(idsToUpdate, enable);
	}

	public void enableTradingVersionPricePlan(Long tradingPricePlanVersionId) {
		enableDisableTradingVersionPrice(tradingPricePlanVersionId, true);
	}

	public void disableTradingVersionPricePlan(Long tradingPricePlanVersionId) {
		enableDisableTradingVersionPrice(tradingPricePlanVersionId, false);
	}
	
	private void enableDisableTradingVersionPrice(Long tradingPricePlanVersionId, boolean enable) {
		TradingPricePlanVersion tradingPricePlanVersion = tradingPricePlanVersionService.findById(tradingPricePlanVersionId);
		if(tradingPricePlanVersion == null) {
			throw new EntityDoesNotExistsException(TradingPricePlanVersion.class, tradingPricePlanVersionId);
		}
		tradingPricePlanVersion.setUseForBillingAccounts(enable);
	}

    public void calculateTradingPricePlanMatrixLine(TradingPricePlanVersionDto dtoData) throws MeveoApiException, BusinessException {
        checkMandatoryFields(dtoData);

        PricePlanMatrixVersion ppmv = pricePlanMatrixVersionService.findById(dtoData.getPricePlanMatrixVersionId());
        if (ppmv == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dtoData.getPricePlanMatrixVersionId());
        }
        
        if (VersionStatusEnum.PUBLISHED.equals(ppmv.getStatus())) {
            throw new BusinessException("Trading price plan line cannot be calculated for published price plan matrix version");
        }
        
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCodeOrId(dtoData.getTradingCurrency().getCode(), dtoData.getTradingCurrency().getId());
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, dtoData.getTradingCurrency().getCode(), "code", dtoData.getTradingCurrency().getId()+"", "id");
        }

        if (appProvider.getCurrency() != null && appProvider.getCurrency().getCurrencyCode().equals(dtoData.getTradingCurrency().getCode())) {
            throw new InvalidParameterException("Trading currency couldn't be the same as functional currency");
        }
        
        List<TradingPricePlanMatrixLine> tradingLines = tradingPricePlanMatrixLineService.getByPricePlanMatrixVersionAndCurrency(ppmv, tradingCurrency);
        
        if (tradingLines.isEmpty()) {
        	ppmv.getLines().forEach( line -> {
        		TradingPricePlanMatrixLine tradingLine =  new TradingPricePlanMatrixLine((dtoData.getRate() == null) ? null : dtoData.getRate().multiply(line.getValue()), tradingCurrency, dtoData.getRate(), dtoData.isUseForBillingAccounts(), line);
                tradingPricePlanMatrixLineService.create(tradingLine);	
        	});
        } else {
        	tradingLines.forEach( tradingLine -> {
        		tradingLine.setRate(dtoData.getRate());
        		tradingLine.setUseForBillingAccounts(dtoData.isUseForBillingAccounts());
        		tradingLine.setTradingValue((dtoData.getRate() == null) ? null : dtoData.getRate().multiply(tradingLine.getPricePlanMatrixLine().getValue()));
        	}); 
        }

    }
}
