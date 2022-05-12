package org.meveo.service.catalog.impl;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.DatePeriod;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.communication.FormatEnum;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.AttributeInstanceService;
import org.meveo.service.cpq.ProductService;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class PricePlanMatrixVersionService extends PersistenceService<PricePlanMatrixVersion>{

    public static final String STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED = "status of the price plan matrix version is %s, it can not be updated nor removed";

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
    private AttributeInstanceService attributeInstanceService;

    @Override
	public void create(PricePlanMatrixVersion entity) throws BusinessException {
        super.create(entity);
        logAction(entity, "CREATE");
    }


	public PricePlanMatrixVersion findByPricePlanAndVersion(String pricePlanMatrixCode, int currentVersion) {

            List<PricePlanMatrixVersion> ppmVersions = this.getEntityManager()
                    .createNamedQuery("PricePlanMatrixVersion.findByPricePlanAndVersionOrderByPmPriority", entityClass)
                    .setParameter("currentVersion", currentVersion)
                    .setParameter("pricePlanMatrixCode", pricePlanMatrixCode.toLowerCase())
                    .getResultList();
            return ppmVersions.isEmpty() ? null : ppmVersions.get(0);
    }
	
	public List<PricePlanMatrixVersion> findFromDateAfterDateAndVersion(PricePlanMatrix pricePlanMatrix, Date date, int version) {
        return this.getEntityManager()
            .createNamedQuery("PricePlanMatrixVersion.findFromDateAfterDateAndVersion", entityClass)
            .setParameter("pricePlanMatrix", pricePlanMatrix)
            .setParameter("date", date)
            .setParameter("version", version)
            .getResultList();
    }
	
	public List<PricePlanMatrixVersion> findToDateAfterDateAndVersion(PricePlanMatrix pricePlanMatrix, Date date, int version) {
        return this.getEntityManager()
            .createNamedQuery("PricePlanMatrixVersion.findToDateAfterDateAndVersion", entityClass)
            .setParameter("pricePlanMatrix", pricePlanMatrix)
            .setParameter("date", date)
            .setParameter("version", version)
            .getResultList();
    }
	
	public List<PricePlanMatrixVersion> findFromDateBeforeDateAndVersion(PricePlanMatrix pricePlanMatrix, Date date, int version) {
        return this.getEntityManager()
            .createNamedQuery("PricePlanMatrixVersion.findFromDateBeforeDateAndVersion", entityClass)
            .setParameter("pricePlanMatrix", pricePlanMatrix)
            .setParameter("date", date)
            .setParameter("version", version)
            .getResultList();
    }

	public void remove(PricePlanMatrixVersion pricePlanMatrixVersion) {
	    List<PricePlanMatrixLine> pricePlanMatrixLines = pricePlanMatrixLineService.findByPricePlanMatrixVersion(pricePlanMatrixVersion);
	    for (PricePlanMatrixLine pricePlanMatrixLine : pricePlanMatrixLines) {
	        pricePlanMatrixLineService.remove(pricePlanMatrixLine);
        }
//	    List<PricePlanMatrixColumn> pricePlanMatrixColumns = pricePlanMatrixColumnService.findByPricePlanMatrixVersion(pricePlanMatrixVersion);
//        for (PricePlanMatrixColumn pricePlanMatrixColumn : pricePlanMatrixColumns) {
//            pricePlanMatrixColumnService.removePricePlanColumn(pricePlanMatrixColumn.getId());
//        }
        super.remove(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion updatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
        Integer version = pricePlanMatrixVersion.getCurrentVersion();

        log.info("updating pricePlanMatrixVersion with pricePlanMatrix code={} and version={}",ppmCode, version);
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and version={}, it must be DRAFT status.", ppmCode, version);
            throw new MeveoApiException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED, pricePlanMatrixVersion.getStatus().toString()));
        }
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }
    
    public PricePlanMatrixVersion updatePublishedPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, Date endingDate) {
        if(endingDate!=null && endingDate.before(org.meveo.model.shared.DateUtils.setDateToEndOfDay(new Date()))) {
        	throw new ValidationException("ending date must be greater than today");
        }
        pricePlanMatrixVersion.getValidity().setTo(endingDate);
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public void removePriceMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the status of version of the price plan matrix is not DRAFT, the current version is {}.Can not be deleted", pricePlanMatrixVersion.getStatus().toString());
            throw new MeveoApiException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED, pricePlanMatrixVersion.getStatus().toString()));
        }
        logAction(pricePlanMatrixVersion, "DELETE");
        this.remove(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion updateProductVersionStatus(PricePlanMatrixVersion pricePlanMatrixVersion, VersionStatusEnum status) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT) && !VersionStatusEnum.CLOSED.equals(status)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and current version={}, it must be DRAFT status.", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getCurrentVersion());
            throw new MeveoApiException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED, pricePlanMatrixVersion.getStatus().toString()));
        }else {
            pricePlanMatrixVersion.setStatus(status);
            pricePlanMatrixVersion.setStatusDate(Calendar.getInstance().getTime());
        }
        return  update(pricePlanMatrixVersion, "CHANGE_STATUS");
    }


	@Transactional(value = TxType.REQUIRED)
    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion, DatePeriod validity, String pricePlanMatrixNewCode) {
    	var columns = new HashSet<>(pricePlanMatrixVersion.getColumns());
    	var lines = new HashSet<>(pricePlanMatrixVersion.getLines());
    	
    	
        PricePlanMatrixVersion duplicate = new PricePlanMatrixVersion(pricePlanMatrixVersion);
        if(validity!=null) {
         duplicate.setValidity(validity);	
        }
            String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
            Integer lastVersion = getLastVersion(ppmCode);
            duplicate.setCurrentVersion(lastVersion + 1);
        
        try {
            this.create(duplicate);
        }catch(BusinessException e) {
            throw new BusinessException(String.format("Can not duplicate the version of product from version product (%d)", duplicate.getId()), e);
        }
        
        var columnsIds = duplicateColumns(duplicate, columns);
        var lineIds = duplicateLines(duplicate, lines);

        duplicatePricePlanMatrixValue(columnsIds, lineIds);
        return duplicate;
    }

    @SuppressWarnings("unchecked")
	public Integer getLastVersion(String ppmCode) {
    	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
                												.setParameter("pricePlanMatrixCode", ppmCode).getResultList();
        return pricesVersions.isEmpty() ? 0 : pricesVersions.get(0).getCurrentVersion();
    }
    
    public PricePlanMatrixVersionDto load(Long id) {
        PricePlanMatrixVersion pricePlanMatrixVersion = findById(id);
        return new PricePlanMatrixVersionDto(pricePlanMatrixVersion);
    }
    
    @SuppressWarnings("unchecked")
	public PricePlanMatrixVersion getLastPublishedVersion(String ppmCode) {
        List<PricePlanMatrixVersion> result=(List<PricePlanMatrixVersion>) this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getLastPublishedVersion")
                    .setParameter("pricePlanMatrixCode", ppmCode)
                    .getResultList();
        
        return result.isEmpty()?null:result.get(0);
    }

    public PricePlanMatrixLine loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, WalletOperation walletOperation) throws NoPricePlanException {
        ChargeInstance chargeInstance = walletOperation.getChargeInstance();
        if (chargeInstance.getServiceInstance() != null) {

        	String serviceCode=chargeInstance.getServiceInstance().getCode();
     	   Set<AttributeValue> attributeValues = chargeInstance.getServiceInstance().getAttributeInstances()
                    .stream()
                    .map(attributeInstance -> attributeInstanceService.getAttributeValue(attributeInstance, walletOperation))
                    .collect(Collectors.toSet());
     	   
     	   
     	   return pricePlanMatrixLineService.loadMatchedLinesForServiceInstance(pricePlanMatrixVersion, attributeValues, serviceCode, walletOperation);
        }

        return null;
    }
    
   

    public PricePlanMatrixLine loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, String productCode, Set<AttributeValue> attributeValues) throws NoPricePlanException {
        return pricePlanMatrixLineService.loadMatchedLinesForServiceInstance(pricePlanMatrixVersion, attributeValues, productCode, null);
    }
    
    @SuppressWarnings("unchecked")
	public PricePlanMatrixVersion getLastPricePlanMatrixtVersion(String ppmCode) {
    	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
                												.setParameter("pricePlanMatrixCode", ppmCode).getResultList();
        return pricesVersions.isEmpty() ? null : pricesVersions.get(0);
    }
    
    
    private Map<Long, PricePlanMatrixColumn> duplicateColumns(PricePlanMatrixVersion entity, Set<PricePlanMatrixColumn> columns ) {
    	var ids = new HashMap<Long, PricePlanMatrixColumn>();
    	if(columns != null && !columns.isEmpty()) {
    		
    		for (PricePlanMatrixColumn ppmc : columns) {
    			ppmc.getPricePlanMatrixValues().size();
        		//pricePlanMatrixColumnService.detach(ppmc);
        		
        		var duplicatePricePlanMatrixColumn = new PricePlanMatrixColumn(ppmc);
        		if(ppmc.getProduct() != null) {
        			var product = productService.findById(ppmc.getProduct().getId());
        			duplicatePricePlanMatrixColumn.setProduct(product);
        		}
//        		duplicatePricePlanMatrixColumn.setCode(pricePlanMatrixColumnService.findDuplicateCode(ppmc));
        		duplicatePricePlanMatrixColumn.setPricePlanMatrixVersion(entity);
        		pricePlanMatrixColumnService.create(duplicatePricePlanMatrixColumn);
        		
        		ids.put(ppmc.getId().longValue(), duplicatePricePlanMatrixColumn);
        		
        		entity.getColumns().add(duplicatePricePlanMatrixColumn);
    		}
    	}
    	return ids;
    }
    
    private Map<Long, PricePlanMatrixLine> duplicateLines(PricePlanMatrixVersion entity, Set<PricePlanMatrixLine> lines) {
    	var ids = new HashMap<Long, PricePlanMatrixLine>();
    	if(lines != null && !lines.isEmpty()) {
    		lines.forEach(ppml -> {
    			ppml.getPricePlanMatrixValues().size();

    			//pricePlanMatrixLineService.detach(ppml);
    			
    			var duplicateLine = new PricePlanMatrixLine(ppml);
    			duplicateLine.setPricePlanMatrixVersion(entity);
    			
    			pricePlanMatrixLineService.create(duplicateLine);
    			
    			ids.put(ppml.getId().longValue(), duplicateLine);
    			
    			entity.getLines().add(duplicateLine);
    		});
    	}
    	return ids;
    }
    
    private void duplicatePricePlanMatrixValue(Map<Long,PricePlanMatrixColumn> columnsId, Map<Long,PricePlanMatrixLine> lineIds) {
    	var pricePlanMatrixValues = new HashSet<PricePlanMatrixValue>();
    	columnsId.forEach((key, value) -> {
    		var ppmv = new HashSet<>(pricePlanMatrixValueService.findByPricePlanMatrixColumn(key));
    		ppmv.forEach(tmpValue -> {
    			var pricePlanMatrixValue = new PricePlanMatrixValue(tmpValue);
    			pricePlanMatrixValue.setPricePlanMatrixColumn(value);
        		pricePlanMatrixValues.add(pricePlanMatrixValue);
    		});
    	});
    	pricePlanMatrixValues.stream()
    			.filter(ppmv -> lineIds.get(ppmv.getPricePlanMatrixLine().getId()) != null)
    			.map(ppmv -> {
    				ppmv.setPricePlanMatrixLine(lineIds.get(ppmv.getPricePlanMatrixLine().getId()));
    				return ppmv;
    			}).forEach(ppmv -> {
    				var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv);
    				pricePlanMatrixValueService.create(pricePlanMatrixValue);
    			});
    	
    	
    	
    	/*if(pricePlanMatrixValues != null && !pricePlanMatrixValues.isEmpty()) {
    		
    		pricePlanMatrixValues.forEach(ppmv -> {
    			
    			pricePlanMatrixValueService.detach(ppmv);
    			
    			var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv);
    			if(pricePlanMatrixColumn != null) {
    				pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumn);
    				pricePlanMatrixColumn.getPricePlanMatrixValues().add(pricePlanMatrixValue);
    			}
    			if(pricePlanMatrixLine != null) {
    				pricePlanMatrixValue.setPricePlanMatrixLine(pricePlanMatrixLine);
    				pricePlanMatrixLine.getPricePlanMatrixValues().add(pricePlanMatrixValue);
    			}
    			pricePlanMatrixValueService.create(pricePlanMatrixValue);
    			
    		});
    	}*/
    }


	/**
	 * @param pricePlanMatrixCode
	 * @param pricePlanMatrixVersion
	 * @return
	 */
	public Map<String, List<Long>> getUsedEntities(String pricePlanMatrixCode, int version) {
		Map<String, List<Long>> result = new TreeMap<String, List<Long>>();
		PricePlanMatrixVersion pricePlanMatrixVersion = findByPricePlanAndVersion(pricePlanMatrixCode, version);
		if(pricePlanMatrixVersion==null) {
			throw new BusinessException("pricePlanMatrix with code '"+pricePlanMatrixCode+"' and version '"+version+"' not found.");
		}
		
		if(pricePlanMatrixVersion.getValidity().getTo()==null || pricePlanMatrixVersion.getValidity().getTo().after(new Date())) {
			String eventCode = pricePlanMatrixVersion.getPricePlanMatrix().getEventCode();
	
			 List<Long> subscriptionsIds = this.getEntityManager()
	                 .createNamedQuery("Subscription.getSubscriptionIdsUsingProduct", Long.class)
	                 .setParameter("eventCode", eventCode)
	                 .getResultList();
			 result.put("subscriptions", subscriptionsIds);
			 
			 List<Long> quotesIds = this.getEntityManager()
	                 .createNamedQuery("CpqQuote.getQuoteIdsUsingCharge", Long.class)
	                 .setParameter("eventCode", eventCode)
	                 .getResultList();
			 result.put("quotes", quotesIds);
			 
			 List<Long> ordersIds = this.getEntityManager()
	                 .createNamedQuery("CommercialOrder.getOrderIdsUsingCharge", Long.class)
	                 .setParameter("eventCode", eventCode)
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
	private PricePlanMatrixVersion update(PricePlanMatrixVersion entity, String auditAction) {
		final PricePlanMatrixVersion ppmv = super.update(entity);
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
		final String origin = ppmv.getPricePlanMatrix().getCode()+"."+ppmv.getCurrentVersion();
		auditLog.setOrigin(origin);
		auditLog.setAction(action); 
		auditLog.setParameters("user "+currentUser.getUserName()+" apply "+action+" on "+DateUtils.formatAsDate(date)+" to the price plan version "+origin+". "+ppmv.getStatusChangeLog());
		auditLogService.create(auditLog);
	}

	public String export(List<Long> ids, FormatEnum fileType) {
		Set<PricePlanMatrixVersion> fetchedPricePlanMatrixVersions = (Set<PricePlanMatrixVersion>) this.getEntityManager()
				.createNamedQuery("PricePlanMatrixVersion.getPricePlanVersionsByIds", entityClass)
				.setParameter("ids", ids)
				.getResultStream().collect(Collectors.toSet());
		if(!fetchedPricePlanMatrixVersions.isEmpty()){
			CSVPricePlanExportManager csvPricePlanExportManager = new CSVPricePlanExportManager();
			return csvPricePlanExportManager.export(fetchedPricePlanMatrixVersions);
		}
		log.info("No PricePlanMatrixVersions was exported.");
		return null;
	}

	class CSVPricePlanExportManager {
		private final String PATH_STRING_FOLDER  = "exports" + File.separator + "priceplan_versions";
		private final String saveDirectory;

		public CSVPricePlanExportManager(){
			saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
		}
		public String export(Set<PricePlanMatrixVersion> pricePlanMatrixVersions){
			if(pricePlanMatrixVersions != null && !pricePlanMatrixVersions.isEmpty()) {
				Set<Path> filePaths = pricePlanMatrixVersions.stream()
						.map(ppv  -> saveAsRecord(buildFileName(ppv), ppv))
						.collect(Collectors.toSet());
				if(filePaths.size() > 1) {
					return archiveFiles(filePaths);
				}
				return filePaths.iterator().next().toString();
			}
			return null;
		}

		private Set<Map<String, Object>> toCSVLineGridRecords(PricePlanMatrixVersion ppv) {
			Set<Map<String, Object>> CSVLineRecords = new HashSet<>();
			ppv.getLines().stream()
				.forEach(line -> {
					Map<String, Object> CSVLineRecord = new HashMap<>();
					CSVLineRecord.put("id", line.getId());
					CSVLineRecord.put("priceWithoutTax[number]", line.getPriceWithoutTax());
					line.getPricePlanMatrixValues().iterator()
							.forEachRemaining(ppmv -> {
								String value = resolveValue(ppmv, ppmv.getPricePlanMatrixColumn().getType());
								String type = resolveAttributeType(ppmv.getPricePlanMatrixColumn().getAttribute().getAttributeType(), (value == null ? "" : value).contains("|"));
								CSVLineRecord.put(ppmv.getPricePlanMatrixColumn().getCode()+"["+ (ColumnTypeEnum.String.equals(type) ? "text" : type) +']',
										value);
								CSVLineRecord.put("description[text]", ppmv.getPricePlanMatrixLine().getDescription());

							});
					CSVLineRecords.add(CSVLineRecord);
				});
			return CSVLineRecords;
		}

		private String resolveAttributeType(AttributeTypeEnum attributeType, boolean isRange) {
			switch (attributeType){
				case DATE:
					return isRange ? "range-date": "date";
				case NUMERIC:
				case INTEGER:
					return isRange ? "range-number": "number";
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
				default:
					return "text";
			}
		}

		private String resolveValue(PricePlanMatrixValue ppmv, ColumnTypeEnum type) {
			switch (type){
				case Long:
					return ppmv.getLongValue() == null ? ppmv.getStringValue() : ppmv.getLongValue().toString();
				case Double:
					return ppmv.getDoubleValue() == null ? ppmv.getStringValue() : ppmv.getDoubleValue().toString();
				case Boolean:
					return ppmv.getBooleanValue() == null ? ppmv.getStringValue() : ppmv.getBooleanValue().toString();
				case Range_Date:
					if(ppmv.getFromDateValue() == null && ppmv.getToDateValue() == null) {
						return "";
					}
					return (DateUtils.formatDateWithPattern(ppmv.getFromDateValue(), "yyyy-MM-dd") + "|" + DateUtils.formatDateWithPattern(ppmv.getToDateValue(), "yyyy-MM-dd")).replaceAll("null", "");
				case Range_Numeric:
					if(ppmv.getFromDoubleValue() == null && ppmv.getToDoubleValue() == null) {
						return "";
					}
					return  (ppmv.getFromDoubleValue() + "|" + ppmv.getToDoubleValue()).replaceAll("null", "");
				default:
					return ppmv.getStringValue();
			}
		}

		private String buildFileName(PricePlanMatrixVersion ppmv) {
			final String fileNameSeparator = "_-_";
			StringBuilder fileName = new StringBuilder();
			fileName.append(ppmv.getId());
			if(ppmv.getPricePlanMatrix() != null && ppmv.getPricePlanMatrix().getChargeTemplate() != null){
				ChargeTemplate chargeTemplate = ppmv.getPricePlanMatrix().getChargeTemplate();
				fileName
					.append(fileNameSeparator + chargeTemplate.getDescription())
					.append(fileNameSeparator + chargeTemplate.getCode());
			}
			fileName.append(fileNameSeparator+ ppmv.getLabel());
			fileName.append(fileNameSeparator);
			if(ppmv.getValidity() != null){
				fileName.append(ppmv.getStatus());
			}
			if(ppmv.getValidity() != null){
				fileName.append(fileNameSeparator);
				if(ppmv.getValidity().getFrom() != null){
					fileName.append(ppmv.getValidity().getFrom().getTime());
				}
				fileName.append(fileNameSeparator);
				if(ppmv.getValidity().getTo() != null){
					fileName.append(ppmv.getValidity().getTo().getTime());
				}
			}
			return File.separator + fileName
				.append(".csv").toString()
				.replaceAll("null","").replaceAll("[/: ]", "-");
		}

		private Path saveAsRecord(String fileName, PricePlanMatrixVersion ppv) {
			Set<Map<String, Object>> records = ppv.isMatrix() ? toCSVLineGridRecords(ppv) : Collections.singleton(toCSVLineRecords(ppv));
			CsvMapper csvMapper = new CsvMapper();
			CsvSchema invoiceCsvSchema = ppv.isMatrix() ? buildGridPricePlanVersionCsvSchema(records) : buildPricePlanVersionCsvSchema();
			csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
			try {
				if(!Files.exists(Path.of(saveDirectory))){
					Files.createDirectories(Path.of(saveDirectory));
				}
				File csvFile = new File(saveDirectory + fileName);
				OutputStream fileOutputStream = new FileOutputStream(csvFile);
				fileOutputStream.write('\ufeef');
				fileOutputStream.write('\ufebb');
				fileOutputStream.write('\ufebf');
				csvMapper.writer(invoiceCsvSchema).writeValues(fileOutputStream).write(records);
				log.info("PricePlanMatrix version is exported in -> " + saveDirectory + fileName);
				return Path.of(saveDirectory, fileName);
			} catch (IOException e) {
				log.error("error exporting PricePlanMatrix version " + fileName);
				throw new RuntimeException("error during file writing : ", e);
			}
		}

		private Map<String, Object> toCSVLineRecords(PricePlanMatrixVersion ppv) {
			Map<String, Object> CSVLineRecords = new HashMap<>();
			CSVLineRecords.put("id", ppv.getId());
			CSVLineRecords.put("label", ppv.getLabel());
			CSVLineRecords.put("amount", ppv.getAmountWithoutTax());
			return CSVLineRecords;
		}

		private CsvSchema buildPricePlanVersionCsvSchema() {
			return CsvSchema.builder()
					.addColumn("id", CsvSchema.ColumnType.STRING)
					.addColumn("label", CsvSchema.ColumnType.STRING)
					.addColumn("amount", CsvSchema.ColumnType.NUMBER_OR_STRING)
					.build().withColumnSeparator(';').withLineSeparator("\n").withoutQuoteChar().withHeader();
		}

		private CsvSchema buildGridPricePlanVersionCsvSchema(Set<Map<String, Object>> records) {
		    
		    Set<String> dynamicColumns = new HashSet<>();
		    if (!records.isEmpty()) {
		        Set<String> columns = records.stream()
						.map(record -> record.keySet())
						.flatMap(Collection::stream)
						.collect(Collectors.toSet());
	            dynamicColumns = columns.stream()
	                    .filter(v -> !v.equals("id") && !v.equals("description[text]") && !v.equals("priceWithoutTax[number]")).collect(Collectors.toSet());
            }
			return CsvSchema.builder()
					.addColumn(new CsvSchema.Column(0, "id"))
					.addColumns(dynamicColumns, CsvSchema.ColumnType.NUMBER_OR_STRING)
					.addColumn("description[text]", CsvSchema.ColumnType.STRING)
					.addColumn("priceWithoutTax[number]", CsvSchema.ColumnType.NUMBER_OR_STRING)
					.build().withColumnSeparator(';').withLineSeparator("\n").withoutQuoteChar().withHeader();
		}

		private String archiveFiles(Set<Path> filesPath){
			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
					.appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
					.appendValue(MONTH_OF_YEAR, 2)
					.appendValue(DAY_OF_MONTH, 2)
					.toFormatter();

			String zipFileName = saveDirectory + File.separator + LocalDate.now().format(formatter) + "_export.zip";
			try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(Path.of(zipFileName)))) {
				filesPath.stream()
						.map(Path::toFile)
						.filter(File::exists)
						.map(file -> {
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
						})
						.forEach(File::delete);
				zs.closeEntry();
				log.info("folder {} was archived", zipFileName);
			} catch (IOException e) {
				log.error("folder {} was archived", zipFileName);
			}
			return zipFileName;
		}
	}
}