package org.meveo.service.script;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRService;

public class CdrCsvImportScript extends GenericMassImportScript {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private static final String RECORD_VARIABLE_NAME = "record";
    private static final String ENTITY = "CDR";
    private static final String ENTITY_NAME = "Cdr";
    private final CDRService cdrService = (CDRService) getServiceInterface(CDRService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
            if (recordMap != null && !recordMap.isEmpty()) {
                CdrActionEnum action = CdrActionEnum.CREATE;

                CDR cdrDto = validateAndGetCdr(action, recordMap);

                if (CdrActionEnum.CREATE.equals(action)) {
                    setCdrValues(recordMap, cdrDto);
                    cdrService.create(cdrDto);

                }
            }
        } catch (Exception exception) {
            throw new BusinessException(exception);
        }
    }

    private CDR validateAndGetCdr(CdrActionEnum action, Map<String, Object> recordMap) {
        CDR cdr = null;

        String ocDate = (String) recordMap.get("OC_CDR_DATE");
        if (ocDate.isEmpty()) {
            throw new ValidationException("OC_CDR_DATE is required");
        }
        String ocQantity = (String) recordMap.get("OC_QANTITY");
        if (ocQantity.isEmpty()) {
            throw new ValidationException("OC_QANTITY is required");
        }
        String ocAp = (String) recordMap.get("OC_AC");
        if (ocAp.isEmpty()) {
            throw new ValidationException("OC_AC is required");
        }
        String ocParametre1 = (String) recordMap.get("OC_PARAMETER_1");
        if (ocParametre1.isEmpty()) {
            throw new ValidationException("OC_PARAMETER_1 is required");
        }

        cdr = new CDR();

        return cdr;
    }

    private void setCdrValues(Map<String, Object> recordMap, CDR cdr) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

        String OC_cdr_date = (String) recordMap.get("OC_CDR_DATE");
        String OC_quantity = (String) recordMap.get("OC_QANTITY");
        String OC_ac = (String) recordMap.get("OC_AC");
        Date cdrDate;
        try {
            cdrDate = StringUtils.isEmpty(OC_cdr_date) ? null : dateFormat.parse(OC_cdr_date);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_CDR_DATE");
        }
        BigDecimal cdrQuantity = OC_quantity.isEmpty() ? null : new BigDecimal(OC_quantity);

        String OC_date_param_1 = (String) recordMap.get("OC_DATE_PARAM_1");
        String OC_date_param_2 = (String) recordMap.get("OC_DATE_PARAM_2");
        String OC_date_param_3 = (String) recordMap.get("OC_DATE_PARAM_3");
        String OC_date_param_4 = (String) recordMap.get("OC_DATE_PARAM_4");
        String OC_date_param_5 = (String) recordMap.get("OC_DATE_PARAM_5");
        Date cdrDateParam1;
        Date cdrDateParam2;
        Date cdrDateParam3;
        Date cdrDateParam4;
        Date cdrDateParam5;
        try {
            cdrDateParam1 = StringUtils.isEmpty(OC_date_param_1) ? null : dateFormat.parse(OC_date_param_1);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_DATE_PARAM_1");
        }
        try {
            cdrDateParam2 = StringUtils.isEmpty(OC_date_param_2) ? null : dateFormat.parse(OC_date_param_2);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_DATE_PARAM_2");
        }
        try {
            cdrDateParam3 = StringUtils.isEmpty(OC_date_param_3) ? null : dateFormat.parse(OC_date_param_3);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_DATE_PARAM_3");
        }
        try {
            cdrDateParam4 = StringUtils.isEmpty(OC_date_param_4) ? null : dateFormat.parse(OC_date_param_4);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_DATE_PARAM_4");
        }
        try {
            cdrDateParam5 = StringUtils.isEmpty(OC_date_param_5) ? null : dateFormat.parse(OC_date_param_5);
        } catch (ParseException e) {
            throw new ValidationException("Incorrect format date for cdr OC_DATE_PARAM_5");
        }

        String OC_decimal_param_1 = (String) recordMap.get("OC_DECIMAL_PARAM_1");
        String OC_decimal_param_2 = (String) recordMap.get("OC_DECIMAL_PARAM_2");
        String OC_decimal_param_3 = (String) recordMap.get("OC_DECIMAL_PARAM_3");
        String OC_decimal_param_4 = (String) recordMap.get("OC_DECIMAL_PARAM_4");
        String OC_decimal_param_5 = (String) recordMap.get("OC_DECIMAL_PARAM_5");

        BigDecimal cdrDecimalParam1 = OC_decimal_param_1.isEmpty() ? null : new BigDecimal(OC_decimal_param_1);
        BigDecimal cdrDecimalParam2 = OC_decimal_param_2.isEmpty() ? null : new BigDecimal(OC_decimal_param_2);
        BigDecimal cdrDecimalParam3 = OC_decimal_param_3.isEmpty() ? null : new BigDecimal(OC_decimal_param_3);
        BigDecimal cdrDecimalParam4 = OC_decimal_param_4.isEmpty() ? null : new BigDecimal(OC_decimal_param_4);
        BigDecimal cdrDecimalParam5 = OC_decimal_param_5.isEmpty() ? null : new BigDecimal(OC_decimal_param_5);

        cdr.setEventDate(cdrDate);
        cdr.setQuantity(cdrQuantity);
        cdr.setAccessCode(OC_ac);

        cdr.setParameter1((String) recordMap.get("OC_PARAMETER_1"));
        cdr.setParameter2((String) recordMap.get("OC_PARAMETER_2"));
        cdr.setParameter3((String) recordMap.get("OC_PARAMETER_3"));
        cdr.setParameter4((String) recordMap.get("OC_PARAMETER_4"));
        cdr.setParameter5((String) recordMap.get("OC_PARAMETER_5"));
        cdr.setParameter6((String) recordMap.get("OC_PARAMETER_6"));
        cdr.setParameter7((String) recordMap.get("OC_PARAMETER_7"));
        cdr.setParameter8((String) recordMap.get("OC_PARAMETER_8"));
        cdr.setParameter9((String) recordMap.get("OC_PARAMETER_9"));

        cdr.setDateParam1(cdrDateParam1);
        cdr.setDateParam2(cdrDateParam2);
        cdr.setDateParam3(cdrDateParam3);
        cdr.setDateParam4(cdrDateParam4);
        cdr.setDateParam5(cdrDateParam5);

        cdr.setDecimalParam1(cdrDecimalParam1);
        cdr.setDecimalParam2(cdrDecimalParam2);
        cdr.setDecimalParam3(cdrDecimalParam3);
        cdr.setDecimalParam4(cdrDecimalParam4);
        cdr.setDecimalParam5(cdrDecimalParam5);

        cdr.setExtraParameter((String) recordMap.get("OC_EXTRA_PARAM"));

    }

    public enum CdrActionEnum {
        CREATE, UPDATE, SUSPEND, RESUME, ACTIVATE, TERMINATE
    }
}