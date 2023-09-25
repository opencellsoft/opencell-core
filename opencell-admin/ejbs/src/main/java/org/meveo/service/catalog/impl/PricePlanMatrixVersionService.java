package org.meveo.service.catalog.impl;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.util.stream.Collectors.toList;
import static org.meveo.model.catalog.ColumnTypeEnum.Range_Date;
import static org.meveo.model.catalog.ColumnTypeEnum.Range_Numeric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsItem;
import org.meveo.commons.utils.ListUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.DatePeriod;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.TradingPricePlanVersion;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.TradingPricePlanMatrixLine;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class PricePlanMatrixVersionService extends PersistenceService<PricePlanMatrixVersion> {

    private static final String STATUS_ERROR_MSG = "status of the price plan matrix version is %s, it can not be updated nor removed";
    
    private static final String COLUMN_SEPARATOR = "\\|";

    private static final String SEPARATOR = "\"";

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;

    @Inject
    private ProductService productService;

    @Inject
    private AuditLogService auditLogService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    
    @Inject
    private TradingPricePlanVersionService tradingPricePlanVersionService;
    
    @Inject
    private TradingCurrencyService tradingCurrencyService;

	@Inject
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService;
    
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void create(PricePlanMatrixVersion entity) throws BusinessException {
        super.create(entity);
        logAction(entity, "CREATE");
    }

    public PricePlanMatrixVersion findByPricePlanAndVersion(String pricePlanMatrixCode, int currentVersion) {

        List<PricePlanMatrixVersion> ppmVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.findByPricePlanAndVersionOrderByPmPriority", entityClass)
            .setParameter("currentVersion", currentVersion).setParameter("pricePlanMatrixCode", pricePlanMatrixCode.toLowerCase()).getResultList();
        return ppmVersions.isEmpty() ? null : ppmVersions.get(0);
    }

    public List<PricePlanMatrixVersion> findEndDates(PricePlanMatrix pricePlanMatrix, Date date) {

        return this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.findEndDates", entityClass).setParameter("pricePlanMatrix", pricePlanMatrix)
            .setParameter("date", date).getResultList();
    }

    public void remove(PricePlanMatrixVersion pricePlanMatrixVersion) {
        List<PricePlanMatrixLine> pricePlanMatrixLines = pricePlanMatrixLineService.findByPricePlanMatrixVersion(pricePlanMatrixVersion);
        for (PricePlanMatrixLine pricePlanMatrixLine : pricePlanMatrixLines) {
            pricePlanMatrixLineService.remove(pricePlanMatrixLine);
        }
        super.remove(pricePlanMatrixVersion);
    }

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void importPricePlanVersion(String importTempDir, ImportPricePlanVersionsItem importItem) throws IOException {

        Date newFrom = DateUtils.truncateTime(importItem.getStartDate());
        Date newTo = DateUtils.truncateTime(importItem.getEndDate());
        String newChargeCode = importItem.getChargeCode();

        validateDates(newFrom, newTo);

        if (StringUtils.isBlank(importItem.getFileName())) {
            throw new BusinessApiException("The file name is mandatory");
        }
        String pathName = importTempDir + File.separator + importItem.getFileName();
        if (!new File(pathName).exists()) {
            throw new BusinessApiException("The file: '" + importItem.getFileName() + "' does not exist");
        }

        if (StringUtils.isBlank(importItem.getChargeCode())) {
            throw new BusinessApiException("The charge code is mandatory");
        }
        
        List<PricePlanMatrix> pricePlanMatrixs = null;
        pricePlanMatrixs = pricePlanMatrixService.listByChargeCode(newChargeCode);
        
        if (pricePlanMatrixs == null || pricePlanMatrixs.size() == 0) {
        	//Get Charge Template Id from file name
        	 String lChargeTemplateId = importItem.getFileName().split("_-_")[1];
             ChargeTemplate lChargeTemplate = chargeTemplateService.findById(Long.parseLong(lChargeTemplateId));
             pricePlanMatrixs = pricePlanMatrixService.listByChargeCode(lChargeTemplate.getCode());
        }
        
        if (pricePlanMatrixs == null || pricePlanMatrixs.size() == 0) {
            throw new BusinessApiException("No PricePlanMatrix related to the charge '" + newChargeCode + "'");
        }
        
        if (pricePlanMatrixs.size() > 1) {
            throw new BusinessApiException("There are several PricePlanMatrix related to this charge");
        }

        Integer lastCurrentVersion = null;
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixs.get(0);

        if (importItem.getStatus() == VersionStatusEnum.PUBLISHED) {
            List<PricePlanMatrixVersion> pvs = findEndDates(pricePlanMatrix, newFrom);

            for (PricePlanMatrixVersion pv : pvs) {
                DatePeriod validity = pv.getValidity();
                List<TradingPricePlanVersion> listTradingPricePlanVersion = tradingPricePlanVersionService.getListTradingPricePlanVersionByPpmvId(pv.getId());
                for (TradingPricePlanVersion cppv : listTradingPricePlanVersion) {
                    tradingPricePlanVersionService.remove(cppv);
                }                
                if(validity == null) {
                    remove(pv);
                } else {
                    Date oldFrom = DateUtils.truncateTime(validity.getFrom());
                    Date oldTo = DateUtils.truncateTime(validity.getTo());

                    if (newFrom.compareTo(oldFrom) <= 0 && ((newTo != null && oldTo != null && newTo.compareTo(oldTo) >= 0) || newTo == null)) {
                        remove(pv);
                    } else if (newFrom.compareTo(oldFrom) > 0 && newTo != null && oldTo != null && newTo.compareTo(oldTo) < 0) {// Scenario 8
                        pv.setValidity(new DatePeriod(oldFrom, newFrom));
                        update(pv);
                        PricePlanMatrixVersion duplicatedPv = duplicate(pv, pricePlanMatrix, new DatePeriod(newTo, oldTo), VersionStatusEnum.PUBLISHED, PriceVersionTypeEnum.FIXED, true);
                        lastCurrentVersion = duplicatedPv.getCurrentVersion();
                    } else {
                        boolean validityHasChanged = false;
                        if (newFrom.compareTo(oldFrom) > 0 && ((oldTo != null && newFrom.compareTo(oldTo) < 0) || oldTo == null)) {
                            validity.setTo(newFrom);
                            validityHasChanged = true;
                        }
                        if (newTo != null && newTo.compareTo(oldFrom) > 0 && validity.getTo() != null && newTo.compareTo(DateUtils.truncateTime(validity.getTo())) < 0) {
                            validity.setFrom(newTo);
                            validityHasChanged = true;
                        }
                        if (validityHasChanged) {
                            update(pv);
                        }
                    }
                }
            }
        }

        try (FileInputStream fs = new FileInputStream(pathName);
                InputStreamReader isr = new InputStreamReader(fs, StandardCharsets.UTF_8);
                LineNumberReader lnr = new LineNumberReader(isr)) {

            String header = eliminateBOM(lnr.readLine());

            PricePlanMatrixVersion newPv = new PricePlanMatrixVersion();
            newPv.setPricePlanMatrix(pricePlanMatrix);
            newPv.setStatus(importItem.getStatus());
            newPv.setStatusDate(new Date());
            newPv.setValidity(new DatePeriod(newFrom, newTo));
            newPv.setCurrentVersion(lastCurrentVersion != null ? ++lastCurrentVersion : getLastVersion(pricePlanMatrix) + 1);

            if ("label;amount".equals(header.substring(0, 12))) {
                String firstLine = lnr.readLine();
                String[] split = firstLine.split(";");
                newPv.setMatrix(false);
                newPv.setLabel(split[0]);
                newPv.setAmountWithoutTax(new BigDecimal(convertToDecimalFormat(split[1])));
                create(newPv);
                for(int i=2; i < split.length; i++) {
                    String[] unitPriceLineSplit = split[i].split(COLUMN_SEPARATOR);
                    TradingPricePlanVersion tppv = new TradingPricePlanVersion();
                    tppv.setTradingPrice(new BigDecimal(convertToDecimalFormat(unitPriceLineSplit[0])));
                    String tradingCurrencyCode = unitPriceLineSplit[1].toUpperCase();
                    TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCode(tradingCurrencyCode); 
                    if(tradingCurrency == null) {
                        throw new MeveoApiException("Trading currency doesn't exist for code : " +  tradingCurrencyCode);
                    }
                    tppv.setTradingCurrency(tradingCurrency);
                    tppv.setRate(new BigDecimal(convertToDecimalFormat(unitPriceLineSplit[2])));
                    tppv.setUseForBillingAccounts("true".equals(unitPriceLineSplit[3].toLowerCase()) ? true : false);
                    tppv.setPricePlanMatrixVersion(newPv);
                    tradingPricePlanVersionService.create(tppv);
                }
            } else if (StringUtils.isNotBlank(header)) {
                // File name pattern: [Price plan version identifier]_-_[Charge name]_-_[Charge code]_-_[Label of the price version]_-_[Status of price version]_-_[start
                // date time stamp]_-_[end date time stamp]
                String[] split = importItem.getFileName().split("_-_");
                newPv.setMatrix(true);
                newPv.setLabel(split.length >= 5 ? split[4] : null);
                create(newPv);
                String data = new StringBuilder(header).append("\n").append(readAllLines(lnr)).toString();
                PricePlanMatrixLinesDto pricePlanMatrixLinesDto = pricePlanMatrixColumnService.createColumnsAndPopulateLinesAndValues(pricePlanMatrix.getCode(), data, newPv);
                pricePlanMatrixLineService.updatePricePlanMatrixLines(newPv, pricePlanMatrixLinesDto);
            }
        }
    }

    private String convertToDecimalFormat(String str) {
        str = str.replace(" ", "");
        int commaPos = str.indexOf(',');
        int dotPos = str.indexOf('.');
        if (commaPos > 0 && dotPos > 0) {
            if (commaPos < dotPos) {
                str = str.replace(",", "");
            } else {
                str = str.replace(".", "");
                str = str.replace(",", ".");
            }
        } else {
            str = str.replace(",", ".");
        }
        return str;
    }

    private void validateDates(Date newFrom, Date newTo) {
        if (newFrom == null) {
            throw new BusinessApiException("The start date name is mandatory");
        }
        if (newFrom != null && newTo != null && newTo.before(newFrom)) {
            throw new BusinessApiException("Invalid validity period, the end date must be greather than the start date");
        }
    }

    private String eliminateBOM(String row) {
        if (StringUtils.isNotBlank(row)) {
            // Get the first character
            String bom = row.substring(0, 1);
            if (!bom.equals("i")) { // i for id;...
                row = row.substring(1);
            }
        }
        return row;
    }

    private String readAllLines(LineNumberReader lnr) throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineRead;
        while ((lineRead = lnr.readLine()) != null) {
            sb.append(lineRead).append("\n");
        }
        return sb.toString();
    }

    public PricePlanMatrixVersion updatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
        Integer version = pricePlanMatrixVersion.getCurrentVersion();

        log.info("updating pricePlanMatrixVersion with pricePlanMatrix code={} and version={}", ppmCode, version);
        if (!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and version={}, it must be DRAFT status.", ppmCode, version);
            throw new MeveoApiException(String.format(STATUS_ERROR_MSG, pricePlanMatrixVersion.getStatus().toString()));
        }
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public PricePlanMatrixVersion updatePublishedPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, Date endingDate) {
        if( pricePlanMatrixVersion.getValidity() == null) {
        	 pricePlanMatrixVersion.setValidity( new DatePeriod() );
        }
        pricePlanMatrixVersion.getValidity().setTo(DateUtils.setTimeToZero(endingDate));
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public void removePriceMatrixVersionOnlyNotClosed(PricePlanMatrixVersion pricePlanMatrixVersion) {
        removePriceMatrixVersionByStatus(pricePlanMatrixVersion, false);
    }

    public void removePriceMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        boolean isPublished = VersionStatusEnum.PUBLISHED.equals(pricePlanMatrixVersion.getStatus());
        removePriceMatrixVersionByStatus(pricePlanMatrixVersion, isPublished);
    }

    public void removePriceMatrixVersionByStatus(PricePlanMatrixVersion pricePlanMatrixVersion, boolean isStatusKo) {
        if (VersionStatusEnum.CLOSED.equals(pricePlanMatrixVersion.getStatus()) || isStatusKo) {
            log.warn("the status of version of the price plan matrix is {}. Can not be deleted", pricePlanMatrixVersion.getStatus().toString());
            throw new MeveoApiException(String.format(STATUS_ERROR_MSG, pricePlanMatrixVersion.getStatus().toString()));
        }
        logAction(pricePlanMatrixVersion, "DELETE");
        this.remove(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion updateProductVersionStatus(PricePlanMatrixVersion pricePlanMatrixVersion, VersionStatusEnum status) {
        if (!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT) && !VersionStatusEnum.CLOSED.equals(status)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and current version={}, it must be DRAFT status.", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),
                pricePlanMatrixVersion.getCurrentVersion());
            throw new MeveoApiException(String.format(STATUS_ERROR_MSG, pricePlanMatrixVersion.getStatus().toString()));
        } else {
            pricePlanMatrixVersion.setStatus(status);
            pricePlanMatrixVersion.setStatusDate(Calendar.getInstance().getTime());
        }
        return update(pricePlanMatrixVersion, "CHANGE_STATUS");
    }

    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrix pricePlanMatrix, DatePeriod validity, PriceVersionTypeEnum priceVersionType, boolean setNewVersion) {
        return duplicate(pricePlanMatrixVersion, pricePlanMatrix, validity, null, priceVersionType, setNewVersion);
    }

    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrix pricePlanMatrix, DatePeriod validity, VersionStatusEnum status, PriceVersionTypeEnum priceVersionType, boolean setNewVersion) {
        return duplicate(pricePlanMatrixVersion, pricePlanMatrix, validity, status, priceVersionType, setNewVersion, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion, PricePlanMatrix pricePlanMatrix, DatePeriod validity, VersionStatusEnum status, PriceVersionTypeEnum priceVersionType, boolean setNewVersion, Integer currentVersion) {
        var columns = new HashSet<>(pricePlanMatrixVersion.getColumns());
        var lines = new HashSet<>(pricePlanMatrixVersion.getLines());

        PricePlanMatrixVersion duplicate = new PricePlanMatrixVersion(pricePlanMatrixVersion);
        duplicate.setPricePlanMatrix(pricePlanMatrix);
        if (validity != null) {
            duplicate.setValidity(validity);
        }
        if (status != null) {
            duplicate.setStatus(status);
        }
        if(priceVersionType != null){
            duplicate.setPriceVersionType(priceVersionType);
        }
        if(currentVersion != null) {
            duplicate.setCurrentVersion(currentVersion);
        } else if(!setNewVersion) {
            Integer lastVersion = getLastVersion(pricePlanMatrix);
            duplicate.setCurrentVersion(lastVersion + 1);
        }else {
            duplicate.setCurrentVersion(1);
        }
        try {
            this.create(duplicate);
        } catch (BusinessException e) {
            throw new BusinessException(String.format("Can not duplicate the version of product from version product (%d)", duplicate.getId()), e);
        }
        Boolean resetValueToPercent = !pricePlanMatrixVersion.getPriceVersionType().equals(priceVersionType);
        var columnsIds = duplicateColumns(duplicate, columns);
        var lineIds = duplicateLines(duplicate, lines, resetValueToPercent);
        duplicatePricePlanMatrixValue(columnsIds, lineIds);

        return duplicate;
    }

    public int getLastVersion(PricePlanMatrix pricePlanMatrix) {
        Integer version = 0;
        try {
            version = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastCurrentVersion", Integer.class).setParameter("pricePlanMatrix", pricePlanMatrix)
                .setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.debug("No lastCurrentVersion for PricePlanMatrixVersion {} found", pricePlanMatrix.getId());
        }
        return version;
    }

    public PricePlanMatrixVersionDto load(Long id) {
        PricePlanMatrixVersion pricePlanMatrixVersion = findById(id);
        return new PricePlanMatrixVersionDto(pricePlanMatrixVersion, true);
    }

    @SuppressWarnings("unchecked")
    public PricePlanMatrixVersion getLastPublishedVersion(String ppmCode) {
        List<PricePlanMatrixVersion> result = (List<PricePlanMatrixVersion>) this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getLastPublishedVersion")
            .setParameter("pricePlanMatrixCode", ppmCode).setFlushMode(FlushModeType.COMMIT).getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @SuppressWarnings("unchecked")
    public PricePlanMatrixVersion getLastPricePlanMatrixtVersion(String ppmCode) {
        List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion").setParameter("pricePlanMatrixCode", ppmCode)
            .getResultList();
        return pricesVersions.isEmpty() ? null : pricesVersions.get(0);
    }

    private Map<Long, PricePlanMatrixColumn> duplicateColumns(PricePlanMatrixVersion entity, Set<PricePlanMatrixColumn> columns) {
        var ids = new HashMap<Long, PricePlanMatrixColumn>();
        if (columns != null && !columns.isEmpty()) {

            for (PricePlanMatrixColumn ppmc : columns) {
                ppmc.getPricePlanMatrixValues().size();
                // pricePlanMatrixColumnService.detach(ppmc);

                var duplicatePricePlanMatrixColumn = new PricePlanMatrixColumn(ppmc);
                if (ppmc.getProduct() != null) {
                    var product = productService.findById(ppmc.getProduct().getId());
                    duplicatePricePlanMatrixColumn.setProduct(product);
                }
//        		duplicatePricePlanMatrixColumn.setCode(pricePlanMatrixColumnService.findDuplicateCode(ppmc));
                duplicatePricePlanMatrixColumn.setPricePlanMatrixVersion(entity);
                pricePlanMatrixColumnService.create(duplicatePricePlanMatrixColumn);

                ids.put(ppmc.getId(), duplicatePricePlanMatrixColumn);

                entity.getColumns().add(duplicatePricePlanMatrixColumn);
            }
        }
        return ids;
    }

    private Map<Long, PricePlanMatrixLine> duplicateLines(PricePlanMatrixVersion entity, Set<PricePlanMatrixLine> lines, Boolean resetValueToPercent) {
        var ids = new HashMap<Long, PricePlanMatrixLine>();
        if (lines != null && !lines.isEmpty()) {
            lines.forEach(ppml -> {
                ppml.getPricePlanMatrixValues().size();

                // pricePlanMatrixLineService.detach(ppml);

                var duplicateLine = new PricePlanMatrixLine(ppml);
                duplicateLine.setPricePlanMatrixVersion(entity);
                if(resetValueToPercent){
                    duplicateLine.setValue(BigDecimal.ZERO);
                }

                ppml.getTradingPricePlanMatrixLines().forEach(trading -> {
                	TradingPricePlanMatrixLine tppml = new TradingPricePlanMatrixLine(trading.getTradingValue(), 
                			trading.getTradingCurrency(), 
                			trading.getRate(), 
                			trading.isUseForBillingAccounts(), 
                			duplicateLine);
                	
                	duplicateLine.getTradingPricePlanMatrixLines().add(tppml);
                });
                
                pricePlanMatrixLineService.create(duplicateLine);

                ids.put(ppml.getId(), duplicateLine);

                entity.getLines().add(duplicateLine);
            });
        }
        return ids;
    }

    private void duplicatePricePlanMatrixValue(Map<Long, PricePlanMatrixColumn> columnsId, Map<Long, PricePlanMatrixLine> lineIds) {
        var pricePlanMatrixValues = new HashSet<PricePlanMatrixValue>();
        columnsId.forEach((key, value) -> {
            var ppmv = new HashSet<>(pricePlanMatrixValueService.findByPricePlanMatrixColumn(key));
            ppmv.forEach(tmpValue -> {
                var pricePlanMatrixValue = new PricePlanMatrixValue(tmpValue);
                pricePlanMatrixValue.setPricePlanMatrixColumn(value);
                pricePlanMatrixValues.add(pricePlanMatrixValue);
            });
        });
        pricePlanMatrixValues.stream().filter(ppmv -> lineIds.get(ppmv.getPricePlanMatrixLine().getId()) != null).map(ppmv -> {
            ppmv.setPricePlanMatrixLine(lineIds.get(ppmv.getPricePlanMatrixLine().getId()));
            return ppmv;
        }).forEach(ppmv -> {
            var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv);
            pricePlanMatrixValueService.create(pricePlanMatrixValue);
        });

        /*
         * if(pricePlanMatrixValues != null && !pricePlanMatrixValues.isEmpty()) {
         *
         * pricePlanMatrixValues.forEach(ppmv -> {
         *
         * pricePlanMatrixValueService.detach(ppmv);
         *
         * var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv); if(pricePlanMatrixColumn != null) { pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumn);
         * pricePlanMatrixColumn.getPricePlanMatrixValues().add(pricePlanMatrixValue); } if(pricePlanMatrixLine != null) {
         * pricePlanMatrixValue.setPricePlanMatrixLine(pricePlanMatrixLine); pricePlanMatrixLine.getPricePlanMatrixValues().add(pricePlanMatrixValue); }
         * pricePlanMatrixValueService.create(pricePlanMatrixValue);
         *
         * }); }
         */
    }

    /**
     * @param pricePlanMatrixCode
     * @return
     */
    public Map<String, List<Long>> getUsedEntities(String pricePlanMatrixCode, int version) {
        Map<String, List<Long>> result = new TreeMap<String, List<Long>>();
        PricePlanMatrixVersion pricePlanMatrixVersion = findByPricePlanAndVersion(pricePlanMatrixCode, version);
        if (pricePlanMatrixVersion == null) {
            throw new BusinessException("pricePlanMatrix with code '" + pricePlanMatrixCode + "' and version '" + version + "' not found.");
        }

        if (pricePlanMatrixVersion.getValidity() == null || pricePlanMatrixVersion.getValidity().getTo() == null || pricePlanMatrixVersion.getValidity().getTo().after(new Date())) {
            List<Long> chargeIds = pricePlanMatrixVersion.getPricePlanMatrix()
                                                            .getChargeTemplates()
                                                            .stream()
                                                            .map(ChargeTemplate::getId)
                                                            .collect(toList());

            List<Long> subscriptionsIds = this.getEntityManager().createNamedQuery("Subscription.getSubscriptionIdsUsingProduct", Long.class).setParameter("chargeIds", chargeIds)
                .getResultList();
            result.put("subscriptions", subscriptionsIds);

            List<Long> quotesIds = this.getEntityManager().createNamedQuery("CpqQuote.getQuoteIdsUsingCharge", Long.class).setParameter("chargeIds", chargeIds).getResultList();
            result.put("quotes", quotesIds);

            List<Long> ordersIds = this.getEntityManager().createNamedQuery("CommercialOrder.getOrderIdsUsingCharge", Long.class).setParameter("chargeIds", chargeIds)
                .getResultList();
            result.put("orders", ordersIds);
        }
        return result;
    }

    @Override
    public PricePlanMatrixVersion update(PricePlanMatrixVersion ppmv) throws BusinessException {
        return update(ppmv, "UPDATE");
    }

    /**
     * @param pricePlanMatrixVersion
     * @param auditAction
     * @return
     */
    private PricePlanMatrixVersion update(PricePlanMatrixVersion pricePlanMatrixVersion, String auditAction) {
        final PricePlanMatrixVersion ppmv = super.update(pricePlanMatrixVersion);
        logAction(ppmv, auditAction);
        return ppmv;
    }

    /**
     * @param ppmv
     * @param action
     */
    private void logAction(PricePlanMatrixVersion ppmv, String action) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActor(currentUser.getFullNameOrUserName());
        final Date date = new Date();
        auditLog.setCreated(date);
        auditLog.setEntity("PricePlanMatrixVersion");
        final String origin = ppmv.getPricePlanMatrix().getCode() + "." + ppmv.getCurrentVersion();
        auditLog.setOrigin(origin);
        auditLog.setAction(action);
        auditLog.setParameters("user " + currentUser.getUserName() + " apply " + action + " on " + DateUtils.formatAsDate(date) + " to the price plan version " + origin + ". "
                + ppmv.getStatusChangeLog());
        auditLogService.create(auditLog);
    }

    public String export(List<Long> ids, String fileType) {
        Set<PricePlanMatrixVersion> fetchedPricePlanMatrixVersions = (Set<PricePlanMatrixVersion>) this.getEntityManager()
            .createNamedQuery("PricePlanMatrixVersion.getPricePlanVersionsByIds", entityClass).setParameter("ids", ids).getResultStream().collect(Collectors.toSet());
        if (!fetchedPricePlanMatrixVersions.isEmpty()) {
            CSVPricePlanExportManager csvPricePlanExportManager = new CSVPricePlanExportManager();
            return csvPricePlanExportManager.export(fetchedPricePlanMatrixVersions, fileType);
        }
        log.info("No PricePlanMatrixVersions was exported.");
        return null;
    }

    public List<PricePlanMatrixVersion> findByPricePlan(PricePlanMatrix pricePlan) {
       return this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.findByPricePlan", entityClass)
                .setParameter("priceplan", pricePlan).getResultList();
    }

    public List<PricePlanMatrixVersion> findByPricePlans(List<PricePlanMatrix> pricePlans) {
        return this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.findByPricePlans", entityClass)
                .setParameter("priceplans", pricePlans).getResultList();
    }

    public List<PricePlanMatrixVersion> findByChargeTemplates(Set<Long> chargeTemplateIds) {
        return this.getEntityManager()
                   .createNamedQuery("PricePlanMatrixVersion.getAllVersionsForChargeTemplates", entityClass)
                   .setParameter("charges", chargeTemplateIds)
                   .getResultList();
    }

    class CSVPricePlanExportManager {
        private final String PATH_STRING_FOLDER = "exports" + File.separator + "priceplan_versions";
        private final String saveDirectory;

        public CSVPricePlanExportManager() {
            saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
        }

        public String export(Set<PricePlanMatrixVersion> pricePlanMatrixVersions, String fileType){
            List<Long> ppmvIds = pricePlanMatrixVersions.stream().map(PricePlanMatrixVersion::getId).collect(toList());            
            List<Map<String, Object>> ppmvMaps = tradingPricePlanVersionService.getPPVWithCPPVByPpmvId(ppmvIds);            
            if (pricePlanMatrixVersions != null && !pricePlanMatrixVersions.isEmpty()) {
                Set<Path> filePaths = pricePlanMatrixVersions.stream().map(ppv -> saveAsRecord(buildFileName(ppv), ppv, fileType, ppmvMaps)).collect(Collectors.toSet());
                if (filePaths.size() > 1) {
                    return archiveFiles(filePaths);
                }
                return filePaths.iterator().next().toString();
            }
            return null;
        }

        private Set<LinkedHashMap<String, Object>> toCSVLineGridRecords(PricePlanMatrixVersion ppv, boolean isCsv) {
            Set<LinkedHashMap<String, Object>> CSVLineRecords = new HashSet<>();
            ppv.getLines().stream().forEach(line -> {
                Map<String, Object> CSVLineRecord = new HashMap<>();

                LinkedHashMap<String, Integer> CSVLineRecordPosition = new LinkedHashMap<>();
                line.getPricePlanMatrixValues().iterator().forEachRemaining(ppmv -> {

                    String value = resolveValue(ppmv, ppmv.getPricePlanMatrixColumn().getType());
                    value = value != null ? isCsv ? SEPARATOR.concat(value).concat(SEPARATOR) : value : "";
                    String type = resolveAttributeType(ppmv.getPricePlanMatrixColumn().getAttribute().getAttributeType(), ppmv.getPricePlanMatrixColumn().getType(), (value == null ? "" : value).contains("|"));
                    CSVLineRecord.put(ppmv.getPricePlanMatrixColumn().getCode() + "[" + (ColumnTypeEnum.String.equals(type) ? "text" : type) + ']', value);

                    CSVLineRecordPosition.put(ppmv.getPricePlanMatrixColumn().getCode() + "[" + (ColumnTypeEnum.String.equals(type) ? "text" : type) + ']',
                        ppmv.getPricePlanMatrixColumn().getPosition());

                });
                
                line.getTradingPricePlanMatrixLines().iterator().forEachRemaining(cppmv -> {
                    String codeCurrency = "";
                    if(cppmv.getTradingCurrency().getCurrency() != null) {
                        codeCurrency = cppmv.getTradingCurrency().getCurrency().getCurrencyCode();
                    }
                    String keyLine = "unitPrice-" + codeCurrency;
                    String valueLine = cppmv.getTradingValue() + "|" + codeCurrency + "|" + cppmv.getRate() + "|" + cppmv.isUseForBillingAccounts();
                    CSVLineRecord.put(keyLine, isCsv ? SEPARATOR.concat(valueLine).concat(SEPARATOR) : valueLine);
                    int sizePosition = CSVLineRecordPosition.size();
                    CSVLineRecordPosition.put(keyLine, sizePosition);
                });

                //Check if any of line contains an EL value, then add new column
                if(!StringUtils.isBlank(line.getValueEL())) {
                    CSVLineRecord.put("description[text]", isCsv ? SEPARATOR.concat(line.getDescription()).concat(SEPARATOR) : line.getDescription());
                    CSVLineRecordPosition.put("description[text]", Integer.MAX_VALUE - 2);
                    CSVLineRecord.put("priceWithoutTax[number]", isCsv ? SEPARATOR.concat(String.valueOf(line.getPriceWithoutTax())).concat(SEPARATOR) : line.getPriceWithoutTax());
                    CSVLineRecordPosition.put("priceWithoutTax[number]", Integer.MAX_VALUE - 1);
                    CSVLineRecord.put("unitPriceEL[text]", isCsv ? SEPARATOR.concat(line.getValueEL()).concat(SEPARATOR) : line.getValueEL());
                    CSVLineRecordPosition.put("unitPriceEL[text]", Integer.MAX_VALUE);
                } else {
                    CSVLineRecord.put("description[text]", isCsv ? SEPARATOR.concat(line.getDescription()).concat(SEPARATOR) : line.getDescription());
                    CSVLineRecordPosition.put("description[text]", Integer.MAX_VALUE - 1);
                    CSVLineRecord.put("priceWithoutTax[number]", isCsv ? SEPARATOR.concat(String.valueOf(line.getPriceWithoutTax())).concat(SEPARATOR) : line.getPriceWithoutTax());
                    CSVLineRecordPosition.put("priceWithoutTax[number]", Integer.MAX_VALUE);
                }

                CSVLineRecords.add(copyToSortedMap(CSVLineRecord, CSVLineRecordPosition));
            });

            return CSVLineRecords;
        }

        private LinkedHashMap<String, Object> copyToSortedMap(Map<String, Object> originMap, Map<String, Integer> mapPosition) {
            LinkedHashMap<String, Object> sortedMap = new LinkedHashMap<>();
            mapPosition.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortedMap.put(x.getKey(), originMap.get(x.getKey())));
            return sortedMap;
        }

        private String resolveAttributeType(AttributeTypeEnum attributeType, ColumnTypeEnum columnType, boolean isRange) {
            switch (attributeType) {
                case DATE:
                    return isRange ? "range-date" : "date";
                case NUMERIC:
                case INTEGER:
                    return isRange ? "range-number" : "number";
                case LIST_TEXT:
                    return "list_of_text_values";
                case LIST_MULTIPLE_TEXT:
                    return "multiple_list_of_text_values";
                case LIST_NUMERIC:
                    return "list_of_numeric_values";
                case LIST_MULTIPLE_NUMERIC:
                    return "multiple_list_of_numeric_values";
                case BOOLEAN:
                    return "boolean";
                case EXPRESSION_LANGUAGE:
                    if (columnType.equals(Range_Numeric)) {
                        return "range-numeric";
                    } else if (columnType.equals(Range_Date)) {
                        return "range-date";
                    }
                default:
                    return "text";
            }
        }

        private String resolveValue(PricePlanMatrixValue ppmv, ColumnTypeEnum type) {
            switch (type) {
            case Long:
                return ppmv.getLongValue() == null ? ppmv.getStringValue() : ppmv.getLongValue().toString();
            case Double:
                return ppmv.getDoubleValue() == null ? ppmv.getStringValue() : ppmv.getDoubleValue().toString();
            case Boolean:
                return ppmv.getBooleanValue() == null ? ppmv.getStringValue() : ppmv.getBooleanValue().toString();
            case Range_Date:
                if (ppmv.getFromDateValue() == null && ppmv.getToDateValue() == null) {
                    return "";
                }
                return (DateUtils.formatDateWithPattern(ppmv.getFromDateValue(), "yyyy-MM-dd") + "|" + DateUtils.formatDateWithPattern(ppmv.getToDateValue(), "yyyy-MM-dd"))
                    .replaceAll("null", "");
            case Range_Numeric:
                if (ppmv.getFromDoubleValue() == null && ppmv.getToDoubleValue() == null) {
                    return "";
                }
                return (ppmv.getFromDoubleValue() + "|" + ppmv.getToDoubleValue()).replaceAll("null", "");
            default:
                return ppmv.getStringValue();
            }
        }

        private String buildFileName(PricePlanMatrixVersion ppmv) {
            final String fileNameSeparator = "_-_";
            StringBuilder fileName = new StringBuilder();
            fileName.append(ppmv.getId());
            if (ppmv.getPricePlanMatrix() != null && !ListUtils.isEmtyCollection(ppmv.getPricePlanMatrix().getChargeTemplates())) {
                if(ppmv.getPricePlanMatrix().getChargeTemplates().size() == 1) {
                    ChargeTemplate chargeTemplate = ppmv.getPricePlanMatrix().getChargeTemplates().iterator().next();
                    fileName.append(fileNameSeparator + chargeTemplate.getId());
                    fileName.append(fileNameSeparator + chargeTemplate.getDescription()).append(fileNameSeparator + chargeTemplate.getCode());
                } else {
                    fileName.append(fileNameSeparator + ppmv.getPricePlanMatrix().getCode());
                }
            }

            fileName.append(fileNameSeparator + ppmv.getLabel());
            fileName.append(fileNameSeparator);
            if (ppmv.getValidity() != null) {
                fileName.append(ppmv.getStatus());
            }
            if (ppmv.getValidity() != null) {
                fileName.append(fileNameSeparator);
                if (ppmv.getValidity().getFrom() != null) {
                    fileName.append(ppmv.getValidity().getFrom().getTime());
                }
                fileName.append(fileNameSeparator);
                if (ppmv.getValidity().getTo() != null) {
                    fileName.append(ppmv.getValidity().getTo().getTime());
                }
            }
            return File.separator + fileName.toString().replaceAll("null", "").replaceAll("[/: ]", "-");
        }

        /**
         * @param file
         * @param CSVLineRecords
         * @param isMatrix
         * @throws IOException
         */
        private void writeExcelFile(File file, Set<LinkedHashMap<String, Object>> csvLineRecords, boolean isMatrix) throws IOException {

            var workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();

            if (isMatrix) {
                buildPriceGridExcel(csvLineRecords, sheet);
            } else {
                buildPricePlanExcel(csvLineRecords, sheet);
            }

            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        }

        private void buildPriceGridExcel(Set<LinkedHashMap<String, Object>> csvLineRecords, XSSFSheet sheet) {
        	List<String> attributeNames = (csvLineRecords.stream().map(LinkedHashMap::keySet).flatMap(Collection::stream).collect(Collectors.toList())).stream().distinct().collect(Collectors.toList());
        	 var header = sheet.createRow(0);
          
             for (String dynamicAttribute : attributeNames) {
                 if(header.getLastCellNum() < 0) {
                     header.createCell(0).setCellValue(dynamicAttribute);
                 } else {
                     header.createCell(header.getLastCellNum()).setCellValue(dynamicAttribute);
                 }
             }
            buildMatrixPlanPriceExcelLines(csvLineRecords,attributeNames, sheet);
        }

        private void buildPricePlanExcel(Set<LinkedHashMap<String, Object>> csvLineRecords, XSSFSheet sheet) {
            buildSimplePricePlanExcelHeader(csvLineRecords, sheet);
            buildSimplePricePlanExcelLines(csvLineRecords, sheet);
        }

        private void buildSimplePricePlanExcelHeader(Set<LinkedHashMap<String, Object>> csvLineRecords, XSSFSheet sheet) {
            var baseRow = sheet.createRow(0);
            for (LinkedHashMap<String, Object> lineRecord : csvLineRecords) {
                int iKey = 0;
                for (String key : lineRecord.keySet()) {
                    baseRow.createCell(++iKey).setCellValue(key);
                }
            }
        }

        private void buildSimplePricePlanExcelLines(Set<LinkedHashMap<String, Object>> CSVLineRecords, XSSFSheet sheet) {
            int lineNumber = 1;
            for (LinkedHashMap<String, Object> lineRecord : CSVLineRecords) {
                XSSFRow row = sheet.createRow(lineNumber++);
                int iKey = 0;
                for (String key : lineRecord.keySet()) {
                    Object cellValue = lineRecord.get(key);
                    row.createCell(++iKey).setCellValue(cellValue != null ? String.valueOf(cellValue) : "");
                }
            }
        }


        private void buildMatrixPlanPriceExcelLines(Set<LinkedHashMap<String, Object>> csvLineRecords,List<String> attributeNames, XSSFSheet sheet) {

            int lineNumber = 1;
            for (LinkedHashMap<String, Object> lineRecord : csvLineRecords) {
                XSSFRow row = sheet.createRow(lineNumber++);
                for (String key : attributeNames) {
                    Object cellValue = lineRecord.get(key);
                    String value = "";

                    if(cellValue != null) {
                        value = String.valueOf(cellValue);
                    }

                    if(row.getLastCellNum() < 0) {
                        row.createCell(0).setCellValue(value);
                    } else {
                        row.createCell(row.getLastCellNum()).setCellValue(value);
                    }
                }
            }
        }

        /**
         * @param fileName
         * @param ppv
         * @param fileType
         * @return
         */
        private Path saveAsRecord(String fileName, PricePlanMatrixVersion ppv, String fileType, List<Map<String, Object>> ppmvMaps) {
            Set<LinkedHashMap<String, Object>> records = ppv.isMatrix() ? toCSVLineGridRecords(ppv, fileType.equals("CSV")) : Collections.singleton(toCSVLineRecords(ppv, ppmvMaps));
            String extensionFile = ".csv";
            try {
                if(fileType.equals("CSV")) {
                    CsvMapper csvMapper = new CsvMapper();
                    CsvSchema invoiceCsvSchema = ppv.isMatrix() ? buildGridPricePlanVersionCsvSchema(records) : buildPricePlanVersionCsvSchema(records);
                    csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
                    if(!Files.exists(Path.of(saveDirectory))){
                        Files.createDirectories(Path.of(saveDirectory));
                    }
                    File csvFile = new File(saveDirectory + fileName + extensionFile);
                    OutputStream fileOutputStream = new FileOutputStream(csvFile);
                    fileOutputStream.write('\ufeef');
                    fileOutputStream.write('\ufebb');
                    fileOutputStream.write('\ufebf');
                    csvMapper.writer(invoiceCsvSchema).writeValues(fileOutputStream).write(records);
                    log.info("PricePlanMatrix version is exported in -> " + saveDirectory + fileName + extensionFile);
                    return Path.of(saveDirectory, fileName + extensionFile);
                }
                if(fileType.equals("EXCEL")) {
                    extensionFile = ".xlsx";
                    File outputExcelFile = new File(saveDirectory + fileName + extensionFile);
                    writeExcelFile(outputExcelFile, records, ppv.isMatrix());
                    return Path.of(saveDirectory + fileName + extensionFile);
                }
            } catch (IOException e) {
                log.error("error exporting PricePlanMatrix version " + fileName + extensionFile);
                throw new RuntimeException("error during file writing : ", e);
            }
            return null;
        }

        private LinkedHashMap<String, Object> toCSVLineRecords(PricePlanMatrixVersion ppv, List<Map<String, Object>> ppmvMaps) {
            LinkedHashMap<String, Object> CSVLineRecords = new LinkedHashMap<>();
            CSVLineRecords.put("label", ppv.getLabel());
            CSVLineRecords.put("amount", ppv.getPrice());            
            for (Map<String, Object> ppmvMap : ppmvMaps)
            {
                Long ppvId = (Long) ppmvMap.get("ppvId");
                Long ppvcId = (Long) ppmvMap.get("ppvcId");
                if (ppvId.equals(ppv.getId()) && ppvcId != null) {
                    BigDecimal ppvCPrice = (BigDecimal) ppmvMap.get("ppvCPrice");
                    BigDecimal rate = (BigDecimal) ppmvMap.get("rate");
                    String cCurrencyCode = (String) ppmvMap.get("cCurrencyCode");
                    Boolean useForBA = (Boolean) ppmvMap.get("useForBA");                    
                    String keyLine = "unitPrice-" + cCurrencyCode;
                    String valueLine = ppvCPrice + "|" + cCurrencyCode + "|" + rate + "|" + useForBA;
                    CSVLineRecords.put(keyLine, valueLine);
                }
            }
            return CSVLineRecords;
        }

        private CsvSchema buildPricePlanVersionCsvSchema(Set<LinkedHashMap<String, Object>> records) {
            List<String> dynamicColumns = new ArrayList();
            if (!records.isEmpty()) {
                dynamicColumns = records.stream().map(record -> record.keySet()).flatMap(Collection::stream).collect(Collectors.toList());
            }

            //Build default columns
            CsvSchema.Builder columns = CsvSchema.builder().addColumns(dynamicColumns, CsvSchema.ColumnType.NUMBER_OR_STRING);

            return columns.build().withColumnSeparator(';').withLineSeparator("\n").withoutQuoteChar().withHeader();
        }

        private CsvSchema buildGridPricePlanVersionCsvSchema(Set<LinkedHashMap<String, Object>> records) {

            List<String> dynamicColumns = new ArrayList();
            if (!records.isEmpty()) {
                List<String> columns = records.stream().map(record -> record.keySet()).flatMap(Collection::stream).collect(Collectors.toList());
                dynamicColumns = columns.stream().distinct().filter(v -> !v.equals("id") && !v.equals("description[text]") && !v.equals("priceWithoutTax[number]"))
                    .collect(Collectors.toList());
            }

            //Build default columns
            CsvSchema.Builder columns = CsvSchema.builder().addColumns(dynamicColumns, CsvSchema.ColumnType.NUMBER_OR_STRING)
                .addColumn("description[text]", CsvSchema.ColumnType.STRING)
                .addColumn("priceWithoutTax[number]", CsvSchema.ColumnType.NUMBER_OR_STRING);

            return columns.build().withColumnSeparator(';').withLineSeparator("\n").withoutQuoteChar().withHeader();
        }

        private String archiveFiles(Set<Path> filesPath) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendValue(MONTH_OF_YEAR, 2).appendValue(DAY_OF_MONTH, 2)
                .toFormatter();

            String zipFileName = saveDirectory + File.separator + LocalDate.now().format(formatter) + "_export.zip";
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(Path.of(zipFileName)))) {
                filesPath.stream().map(Path::toFile).filter(File::exists).map(file -> {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zs.putNextEntry(zipEntry);
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zs.write(bytes, 0, length);
                        }
                        fis.close();
                    } catch (IOException e) {
                        log.error("error archiving PricePlanMatrix version files into " + zipFileName);
                    }
                    return file;
                }).forEach(File::delete);
                zs.closeEntry();
                log.info("folder {} was archived", zipFileName);
            } catch (IOException e) {
                log.error("folder {} was archived", zipFileName);
            }
            return zipFileName;
        }
    }

}

