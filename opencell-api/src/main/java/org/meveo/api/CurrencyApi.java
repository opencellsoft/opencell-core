/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api;

import static java.math.BigDecimal.ONE;
import static org.meveo.model.shared.DateUtils.setTimeToZero;
import static org.meveo.service.admin.impl.TradingCurrencyService.getCurrencySymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import org.apache.commons.io.FilenameUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.admin.FilesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CurrenciesDto;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.billing.ExchangeRateDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.admin.impl.FileUploadForm;
import org.meveo.api.rest.exception.NotFoundException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.ExchangeRate;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.ExchangeRateService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 * 
 **/
@Stateless
public class CurrencyApi extends BaseApi {

    private static final String EXCHANGE_RATE_DIR = "imports/exchangeRate/";
    private static final String LINE = "Line ";
    private static final String CFO_ROLE = "CFO";

    @Inject
    private CurrencyService currencyService;

    @Inject
    private TradingCurrencyService tradingCurrencyService;

    @Inject
    private ProviderService providerService;

    @Inject
    private ExchangeRateService exchangeRateService;

    @Inject
    private ResourceBundle resourceMessages;
    
    @Inject
    private AuditLogService auditLogService;

    @Inject
    private FilesApi filesApi;
    
    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }
    
    public CurrenciesDto list() {
        CurrenciesDto result = new CurrenciesDto();

        List<TradingCurrency> currencies = tradingCurrencyService.list(GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration());
        if (currencies != null) {
            for (TradingCurrency country : currencies) {
                result.getCurrency().add(new CurrencyDto(country));
            }
        }

        return result;
    }

    public CurrencyDto create(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            String generatedCode = getGenericCode(Currency.class.getName());
            if (generatedCode != null) {
                postData.setCode(generatedCode);
            } else {
                missingParameters.add("code");
            }
        }

        handleMissingParameters();

        if (tradingCurrencyService.findByTradingCurrencyCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(TradingCurrency.class, postData.getCode());
        }

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            // create
            currency = new Currency();
            currency.setCurrencyCode(postData.getCode());
            currency.setDescriptionEn(postData.getDescription());
            currency.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
            currencyService.create(currency);
        }

        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setCurrency(currency);
        tradingCurrency.setCurrencyCode(postData.getCode());
        tradingCurrency.setPrDescription(postData.getDescription());
        tradingCurrency.setActive(true);
        tradingCurrency.setPrCurrencyToThis(postData.getPrCurrencyToThis());
        tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces());
        if (postData.isDisabled() != null) {
            tradingCurrency.setDisabled(postData.isDisabled());
        }
        tradingCurrencyService.create(tradingCurrency);
        return new CurrencyDto(tradingCurrency);
    }

    public CurrencyDto find(String code) throws MissingParameterException, EntityDoesNotExistsException {
        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);

        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        }
        return new CurrencyDto(tradingCurrency);
    }

    public void remove(String code) throws BusinessException, MissingParameterException, EntityDoesNotExistsException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, code);
        }

        tradingCurrencyService.remove(tradingCurrency);
    }

    public CurrencyDto update(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCode());
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, postData.getCode());
        }

        Currency currency = currencyService.findByCode(postData.getCode());

        if (currency == null) {
            throw new EntityDoesNotExistsException(Currency.class, postData.getCode());
        }
        currency.setDescriptionEn(postData.getDescription());
        currency.setDescriptionI18n(convertMultiLanguageToMapOfValues(postData.getLanguageDescriptions(), null));
        currency = currencyService.update(currency);

        tradingCurrency.setCurrency(currency);
        tradingCurrency.setPrDescription(postData.getDescription());
        tradingCurrency.setPrCurrencyToThis(postData.getPrCurrencyToThis());
        tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
        tradingCurrency.setDecimalPlaces(postData.getDecimalPlaces() == null ? 2 : postData.getDecimalPlaces());

        tradingCurrencyService.update(tradingCurrency);
        return new CurrencyDto(currency);
    }

    public CurrencyDto createOrUpdate(CurrencyDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(postData.getCode());
        if (tradingCurrency == null) {
            return create(postData);
        }
        else {
            return update(postData);
        }
    }

    public void findOrCreate(String currencyCode) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(currencyCode)) {
            return;
        }
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currencyCode);
        if (tradingCurrency == null) {
            Currency currency = currencyService.findByCode(currencyCode);
            if (currency == null) {
                throw new EntityDoesNotExistsException(Currency.class, currencyCode);
            }
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setCurrency(currency);
            tradingCurrency.setPrDescription(currency.getDescriptionEn());
            tradingCurrencyService.create(tradingCurrency);
        }
    }

    /**
     * Enable or disable Trading currency
     * 
     * @param code Currency code
     * @param enable Should Trading currency be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(code);
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, code);
        }
        List<ExchangeRate> listExchangeRate = tradingCurrency.getExchangeRates();
        if (enable) {
            tradingCurrencyService.enable(tradingCurrency);
            for (ExchangeRate oneExchangeRate : listExchangeRate) {
                exchangeRateService.enable(oneExchangeRate);
            }
        } else {
            tradingCurrencyService.disable(tradingCurrency);
            for (ExchangeRate oneExchangeRate : listExchangeRate) {
                exchangeRateService.disable(oneExchangeRate);
            }
        }
    }

    public ActionStatus addFunctionalCurrency(CurrencyDto postData) {
        if (postData.getCode() == null) {
            throw new MissingParameterException("code of the currency is mandatory");
        }
        Currency currency = currencyService.findByCode(postData.getCode());
        if (currency == null) {
            throw new NotFoundException(new ActionStatus(ActionStatusEnum.FAIL, "currency not found"));
        }

        Provider provider = providerService.getProviderNoCache();
        provider.setCurrency(currency);
        provider.setMulticurrencyFlag(true);
        provider.setFunctionalCurrencyFlag(true);
        providerService.update(provider);
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currency.getCurrencyCode());
        if(tradingCurrency == null) {
            tradingCurrency = new TradingCurrency();
            tradingCurrency.setCurrencyCode(currency.getCurrencyCode());
            tradingCurrency.setPrDescription(currency.getDescription());
            tradingCurrency.setSymbol(getCurrencySymbol(postData.getCode()));
            tradingCurrency.setDecimalPlaces(2);
            tradingCurrency.setCurrentRate(ONE);
            tradingCurrency.setCurrentRateFromDate(new Date());
            tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
            tradingCurrencyService.create(tradingCurrency);
        } else {
            tradingCurrencyService.updateFunctionalCurrency(tradingCurrency);
        }

        return new ActionStatus(ActionStatusEnum.SUCCESS, "Success");
    }

    public Long addExchangeRate(org.meveo.api.dto.ExchangeRateDto postData) throws MeveoApiException {
        if (postData.getTradingCurrency() == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.tradingCurrency.mandatory"));
        }        
        
        TradingCurrency tradingCurrency = tradingCurrencyService.findById(postData.getTradingCurrency().getId());        
        if (tradingCurrency == null) {
            throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.valide.tradingCurrency"));
        }
        
        ExchangeRate exchangeRate = exchangeRateService.createCurrentRateWithPostData(postData, tradingCurrency);

        auditLogCreateExchangeRate(exchangeRate, "API");

        return exchangeRate.getId();
    }

    public void updateExchangeRate(Long id, ExchangeRateDto postData) {
        updateExchangeRate(id, postData, "API");
    }

    private void updateExchangeRate(Long id, ExchangeRateDto postData, String source) {

        ExchangeRate exchangeRate = exchangeRateService.findById(id);
        if (exchangeRate == null) {
            throw new EntityDoesNotExistsException(ExchangeRate.class, id);
        }
        
        BigDecimal fromRate = exchangeRate.getExchangeRate();
        BigDecimal toRate = postData.getExchangeRate();
        
        Date fromDate = exchangeRate.getFromDate();
        Date toDate = postData.getFromDate();
        
        checkExchangeRateDto(postData, exchangeRate);

        // Check if fromDate = new Date()
        if (postData.getFromDate().compareTo(setTimeToZero(new Date())) == 0) {
            exchangeRate.setCurrentRate(true);
            // set isCurrentRate to false for all other ExchangeRate of the same TradingCurrency
            TradingCurrency tradingCurrency = tradingCurrencyService.findById(exchangeRate.getTradingCurrency().getId(), Arrays.asList("exchangeRates"));
            for (ExchangeRate er : tradingCurrency.getExchangeRates()) {
                if (!er.getId().equals(id)) {
                    er.setCurrentRate(false);
                    exchangeRateService.update(er);
                }
            }

            // Update tradingCurrency fields
            tradingCurrency.setCurrentRate(postData.getExchangeRate());
            tradingCurrency.setCurrentRateFromDate(postData.getFromDate());
            tradingCurrency.setCurrentRateUpdater(currentUser.getUserName());
            tradingCurrencyService.update(tradingCurrency);
        }
        exchangeRate.setFromDate(postData.getFromDate());
        exchangeRate.setExchangeRate(postData.getExchangeRate());
        exchangeRateService.update(exchangeRate);
        auditLogUpdateExchangeRate(exchangeRate, fromRate, toRate, fromDate, toDate, source);
    }

	private void checkExchangeRateDto(ExchangeRateDto postData, ExchangeRate exchangeRate) {
		if (postData.getExchangeRate() != null) {            
            if (postData.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
                throw new MeveoApiException(resourceMessages.getString("error.exchangeRate.exchangeRate.incorrect"));
            }
            
            if (postData.getExchangeRate().compareTo(new BigDecimal("9999999999")) > 0) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
            
            BigDecimal fracExchangeRate = postData.getExchangeRate().subtract(new BigDecimal(postData.getExchangeRate().toBigInteger()));
            if (fracExchangeRate.toString().length() > 8) {
                throw new MeveoApiException(resourceMessages.getString("The exchange rate decimals must be limited to 6 digits and the fractional part to 9,999,999,999"));
            }
        }

        // We can modify only the future rates
        // AEL Update 09/01/2023 : Since we dont have a CFO role mapping between opencell_portal and opencell_web
        //                         We trust Portal restrition made in https://opencellsoft.atlassian.net/browse/INTRD-6451
        /*if (exchangeRate.getFromDate().compareTo(setTimeToZero(new Date())) <= 0 && !currentUser.hasRole(CFO_ROLE)) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.future"));
        }*/

        if (postData.getFromDate() == null) {
            throw new MissingParameterException(resourceMessages.getString("error.exchangeRate.fromDate.empty"));
        }

        // Check if a user choose a date that is already taken
        ExchangeRate exchangeRateFromDate = exchangeRateService.findByFromDate(postData.getFromDate(),exchangeRate.getTradingCurrency().getId());
        if (exchangeRateFromDate != null && !exchangeRateFromDate.getId().equals(exchangeRate.getId())) {
            throw new BusinessApiException(resourceMessages.getString("error.exchangeRate.fromDate.isAlreadyTaken"));
        }

        // Commented related to the same reason of comment line 421 "AEL Update 09/01/2023"
        // BTW, this dubplicated check shall be removed
        // User cannot set a rate in a paste date
        /*if (postData.getFromDate().before(setTimeToZero(new Date()))) {
            throw new BusinessApiException(resourceMessages.getString("The date must not be in the past"));
        }*/
	}
    
    private void auditLogUpdateExchangeRate(ExchangeRate exchangeRate,
            BigDecimal fromRate, BigDecimal toRate,
            Date fromDate, Date toDate, String source) {

        DecimalFormat rateFormatter = new DecimalFormat("#0.######");
        DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

        boolean ratesAreChanged = !(Objects.equals(fromRate, toRate) || (fromRate != null && toRate != null && fromRate.compareTo(toRate) == 0));
        boolean datesAreChanged = DateUtils.truncateTime(fromDate).compareTo(DateUtils.truncateTime(toDate)) != 0;

        StringBuilder parameters = new StringBuilder("User ").append(auditLogService.getActor()).append(" has changed ");
        if (ratesAreChanged) {
            parameters.append("the Exchange rate ");
            parameters.append("for ").append(exchangeRate.getTradingCurrency().getCurrencyCode()).append(" ");
            parameters.append("from ").append((fromRate == null)? null : rateFormatter.format(fromRate)).append(" to ").append((toRate == null)? null : rateFormatter.format(toRate));
        }

        if (datesAreChanged) {
            if (ratesAreChanged) {
                parameters.append(" and ");
            }
            parameters.append("From date ").append(dateFormatter.format(fromDate)).append(" to ").append(dateFormatter.format(toDate));
        }
        if (ratesAreChanged || datesAreChanged) {            
            auditLogService.trackOperation("UPDATE", new Date(), exchangeRate, source, parameters.toString());
        }
    }

    private void auditLogCreateExchangeRate(ExchangeRate exchangeRate, String source) {

        String message = new StringBuilder("User ")
                .append(auditLogService.getActor())
                .append(" has created the Exchange rate for ")
                .append(exchangeRate.getTradingCurrency().getCurrencyCode())
                .append(" with rate  ")
                .append(exchangeRate.getExchangeRate()).toString();
            auditLogService.trackOperation("CREATE", new Date(), exchangeRate, source, message);

    }
    
    public void removeExchangeRateById(Long id) {
        exchangeRateService.delete(id);
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<String> importExchangeRate(FileUploadForm exchangeRateForm) {
        
        if (StringUtils.isBlank(exchangeRateForm.getData())) {
            throw new MissingParameterException("filename");
        }

        Set<String> notMatchedCurrencySombols = new HashSet<>();
        List<String> errorMessages = new ArrayList<>();
        List<String> warningMessages = new ArrayList<>();

        warningMessages.add("No error, all rates are created or updated by importing.");
        
        String exchangeRateDir = getProviderRootDir() + File.separator + EXCHANGE_RATE_DIR;
        Path path = Paths.get(exchangeRateDir);
        try {
			Files.createDirectories(path);
		} catch (IOException e) {
            throw new BusinessApiException("error during file creation.");
		}
        
        String exchangeRateFileDir = EXCHANGE_RATE_DIR + exchangeRateForm.getFilename();
        filesApi.uploadFile(exchangeRateForm.getData(), exchangeRateFileDir, null);
        
        String pathName = exchangeRateDir + exchangeRateForm.getFilename();
        File file = new File(pathName);
        
        if (!file.exists()) {
            throw new BusinessApiException("The file: '" + exchangeRateDir + exchangeRateForm.getFilename() + "' does not exist");
        }else if(!FilenameUtils.getExtension(file.getName()).equals("csv") && !FilenameUtils.getExtension(file.getName()).equals("xlsx")){
          throw new BadRequestException("Only file extensions .csv and .xlsx are allowed.");
        }
        
        try (FileInputStream fs = new FileInputStream(pathName);
                InputStreamReader isr = new InputStreamReader(fs, StandardCharsets.UTF_8);
                LineNumberReader lnr = new LineNumberReader(isr)) {

            String header = lnr.readLine();
            
            if(!"Currency Code,Date,Exchange Rate".equals(header)) {
            	errorMessages.add("Cannot find [Value=Currency Code,Date,Exchange Rate] in the file header, the format is incorrect");
            }

            String lineRead;
            while ((lineRead = lnr.readLine()) != null) {
                String[] split = lineRead.split(",");

                if(split.length != 3) {
                    throw new BusinessApiException("The format is not valide for the line : " + lineRead);
                } else {
                	treateLine(split, errorMessages, warningMessages, notMatchedCurrencySombols, lnr);
                }
            }

        } catch (Exception e) {
        	throw new BusinessApiException(e.getMessage());
        }
        
    	if(errorMessages.isEmpty() && !notMatchedCurrencySombols.isEmpty()) {
    		warningMessages.add(resourceMessages.getString("error.importExchangeRate.valide.tradingCurrency") + ": "+ notMatchedCurrencySombols.toString());
    	}
    	
        if(!errorMessages.isEmpty()) {
            throw new BusinessApiException(errorMessages.toString().replace("[","").replace("]",""));
        }
        
        return warningMessages;
    }
    
    private void treateLine(String[] split, List<String> errorMessages, List<String> warningMessages, Set<String> notMatchedCurrencySombols, LineNumberReader lnr) {
        String currencyCodeExchangerate = split[0];
        String dateExchangerate = split[1];
        String rateExchangerate = split[2];
        
        findAndCheckValues(errorMessages, currencyCodeExchangerate, dateExchangerate, rateExchangerate);

    	TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(currencyCodeExchangerate);
    	if(tradingCurrency == null) {
    		notMatchedCurrencySombols.add(currencyCodeExchangerate);
    	} else if(errorMessages.isEmpty()){
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        Date convertedDateExchangerate = new Date();
			try {
				convertedDateExchangerate = sdf.parse(dateExchangerate);
			} catch (ParseException e) {
	        	errorMessages.add(resourceMessages.getString("error.importExchangeRate.valide.dateFormat"));
			}

                // User cannot set a rate in a paste date
    	        
                if (convertedDateExchangerate.before(DateUtils.setTimeToZero(new Date()))) {
                	warningMessages.set(0, "No error. ");
                	warningMessages.add(LINE+ lnr.getLineNumber() +" (" + dateExchangerate + ") is ignored.");
                }else{
        		//update rate si date in future
    	        ExchangeRate exchangeRate = exchangeRateService.findByFromDate(convertedDateExchangerate, tradingCurrency.getId());
    	        if(exchangeRate != null) {
    	        	if (exchangeRate.getExchangeRate().compareTo(new BigDecimal(rateExchangerate)) != 0){
    	        		ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
    	        		
    	        		exchangeRateDto.setExchangeRate(new BigDecimal(rateExchangerate));
    	        		exchangeRateDto.setFromDate(convertedDateExchangerate);
    	        		
    	        		updateExchangeRate(exchangeRate.getId(), exchangeRateDto, "IMPORT");
    	        		
    	        		warningMessages.add(LINE+ lnr.getLineNumber() +" is updated, is rounded to " + rateExchangerate);
    	        	}
    	        }else {
    	        	ExchangeRate newExchangeRate = exchangeRateService.createCurrentRateWithImpotFile(convertedDateExchangerate, new BigDecimal(rateExchangerate), tradingCurrency);
    	            auditLogCreateExchangeRate(newExchangeRate, "IMPORT");
    	            
	        		warningMessages.add(LINE+ lnr.getLineNumber() +" is created");
    	        }
            }   
    	}
    }

	private void findAndCheckValues(List<String> errorMessages, String currencyCodeExchangerate,
			String dateExchangerate, String rateExchangerate) {
		Currency currency = currencyService.findByCode(currencyCodeExchangerate);
        if(currency == null) {
        	errorMessages.add(resourceMessages.getString("error.importExchangeRate.valide.iso.currency"));
        }

        if(!isValidDateFormat(dateExchangerate)) {
        	errorMessages.add(resourceMessages.getString("error.importExchangeRate.valide.dateFormat"));
        }
        
        if(!isValidRateDecimal(rateExchangerate)) {
        	errorMessages.add(resourceMessages.getString("error.importExchangeRate.valide.rateFormat"));
        }
	}
    
    private boolean isValidDateFormat(String date) {
        boolean valid = false;
        try {
            LocalDate.parse(date,
            		DateTimeFormatter.ofPattern("uuuu-M-dd")
                    	.withResolverStyle(ResolverStyle.SMART)
            );
            
            valid = true;

        } catch (DateTimeParseException e) {
        	valid = false;
        }
        return valid;
    }
    
    private boolean isValidRateDecimal(String rate) {
        boolean valid = false;
        try{
            new BigDecimal(rate);
            valid = true;
        }
        catch(NumberFormatException e){
        	valid = false;
        }
        return valid;
    }

}