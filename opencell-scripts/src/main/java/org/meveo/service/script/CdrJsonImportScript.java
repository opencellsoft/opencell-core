package org.meveo.service.script;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRService;

public class CdrJsonImportScript extends Script {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private final ParamBeanFactory paramBeanFactory = (ParamBeanFactory) getServiceInterface(ParamBeanFactory.class.getSimpleName());
    private final CDRService cdrService = (CDRService) getServiceInterface(CDRService.class.getSimpleName());

    @Override
    public void execute(Map<String, Object> contextMethod) throws BusinessException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        JSONParser parser = new JSONParser();
        JobExecutionResultImpl jobExecutionResult = (JobExecutionResultImpl) contextMethod.get("JobExecutionResult");
        Map<String, String> context = (Map<String, String>) jobExecutionResult.getJobInstance().getCfValues().getValues().get("mapping");
        String pathFile = (String) jobExecutionResult.getJobInstance().getCfValues().getValues().get("pathFile");
        CDR cdr;
        String rootPathFile = getProviderRootDir() + File.separator + pathFile;
        File dir = new File(rootPathFile);

        File[] fileList = dir.listFiles();
	    FileWriter rejectFile = null;
	    FileReader fread = null;
        try {
	        for (File fileInput : fileList) {
                File file = new File(fileInput.getAbsolutePath().replace("input", "reject") + ".rejected");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                rejectFile = new FileWriter(fileInput.getAbsolutePath().replace("input", "reject") + ".rejected");
                fread = new FileReader(fileInput.getAbsolutePath());
                Object obj = parser.parse(fread);
                JSONArray subjects = (JSONArray) obj;
                Iterator iterator = subjects.iterator();
                while (iterator.hasNext()) {
                    cdr = new CDR();
                    JSONObject jsonObject = (JSONObject) iterator.next();

                    Date dateEvent = null;
                    Date dateParam1 = null;
                    Date dateParam2 = null;
                    Date dateParam3 = null;
                    Date dateParam4 = null;
                    Date dateParam5 = null;
                    boolean reject = false;
                    try {
                        dateEvent = StringUtils.isEmpty((String) jsonObject.get(context.get("eventDate"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("eventDate")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("eventDate") + " \n");
                        reject = true;
                    }

                    String ac = (String) jsonObject.get(context.get("accessCode"));
                    BigDecimal quantity = new BigDecimal(String.valueOf(jsonObject.get(context.get("quantity"))));
                    String parameter1 = (String) jsonObject.get(context.get("parameter1"));
                    String parameter2 = (String) jsonObject.get(context.get("parameter2"));
                    String parameter3 = (String) jsonObject.get(context.get("parameter3"));
                    String parameter4 = (String) jsonObject.get(context.get("parameter4"));
                    String parameter5 = (String) jsonObject.get(context.get("parameter5"));
                    String parameter6 = (String) jsonObject.get(context.get("parameter6"));
                    String parameter7 = (String) jsonObject.get(context.get("parameter7"));
                    String parameter8 = (String) jsonObject.get(context.get("parameter8"));
                    String parameter9 = (String) jsonObject.get(context.get("parameter9"));

                    try {
                        dateParam1 = StringUtils.isEmpty((String) jsonObject.get(context.get("dateParam1"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("dateParam1")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("dateParam1") + " \n");
                        reject = true;
                    }
                    try {
                        dateParam2 = StringUtils.isEmpty((String) jsonObject.get(context.get("dateParam2"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("dateParam2")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("dateParam2") + " \n");
                        reject = true;
                    }
                    try {
                        dateParam3 = StringUtils.isEmpty((String) jsonObject.get(context.get("dateParam3"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("dateParam3")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("dateParam3") + " \n");
                        reject = true;
                    }
                    try {
                        dateParam4 = StringUtils.isEmpty((String) jsonObject.get(context.get("dateParam4"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("dateParam4")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("dateParam4") + " \n");
                        reject = true;
                    }
                    try {
                        dateParam5 = StringUtils.isEmpty((String) jsonObject.get(context.get("dateParam5"))) ? null : dateFormat.parse((String) jsonObject.get(context.get("dateParam5")));
                    } catch (ParseException e) {
                        if (!reject)
                            rejectFile.write(jsonObject + " => Incorrect format date for cdr " + context.get("dateParam5") + " \n");
                        reject = true;
                    }
                    BigDecimal decimalParam1 = checkContent(jsonObject.get(context.get("decimalParam1"))) ? null : new BigDecimal(String.valueOf(jsonObject.get(context.get("decimalParam1"))));
                    BigDecimal decimalParam2 = checkContent(jsonObject.get(context.get("decimalParam2"))) ? null : new BigDecimal(String.valueOf(jsonObject.get(context.get("decimalParam2"))));
                    BigDecimal decimalParam3 = checkContent(jsonObject.get(context.get("decimalParam3"))) ? null : new BigDecimal(String.valueOf(jsonObject.get(context.get("decimalParam3"))));
                    BigDecimal decimalParam4 = checkContent(jsonObject.get(context.get("decimalParam4"))) ? null : new BigDecimal(String.valueOf(jsonObject.get(context.get("decimalParam4"))));
                    BigDecimal decimalParam5 = checkContent(jsonObject.get(context.get("decimalParam5"))) ? null : new BigDecimal(String.valueOf(jsonObject.get(context.get("decimalParam5"))));

                    String extraParam = String.valueOf(jsonObject);
                    cdr.setEventDate(dateEvent);
                    cdr.setAccessCode(ac);
                    cdr.setQuantity(quantity);
                    cdr.setParameter1(parameter1);
                    cdr.setParameter2(parameter2);
                    cdr.setParameter3(parameter3);
                    cdr.setParameter4(parameter4);
                    cdr.setParameter5(parameter5);
                    cdr.setParameter6(parameter6);
                    cdr.setParameter7(parameter7);
                    cdr.setParameter8(parameter8);
                    cdr.setParameter9(parameter9);
                    cdr.setDateParam1(dateParam1);
                    cdr.setDateParam2(dateParam2);
                    cdr.setDateParam3(dateParam3);
                    cdr.setDateParam4(dateParam4);
                    cdr.setDateParam5(dateParam5);
                    cdr.setDecimalParam1(decimalParam1);
                    cdr.setDecimalParam2(decimalParam2);
                    cdr.setDecimalParam3(decimalParam3);
                    cdr.setDecimalParam4(decimalParam4);
                    cdr.setDecimalParam5(decimalParam5);
                    cdr.setExtraParameter(extraParam);
                    if (!reject && cdr.getEventDate() != null && cdr.getQuantity() != null && cdr.getAccessCode() != null && cdr.getParameter1() != null)
                        cdrService.create(cdr);
                    else if (!reject) {
                        validateCdr(jsonObject, cdr, context, rejectFile);
                    }
                }
                fileInput.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
			if(rejectFile != null) {
				try {
					rejectFile.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if(fread != null ) {
				try {
					fread.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
        }

    }

    private void validateCdr(JSONObject line, CDR cdr, Map<String, String> context, FileWriter file) throws IOException {
        if (cdr.getEventDate() == null)
            file.write(line + " => " + context.get("eventDate") + " is required\n");
        else if (cdr.getQuantity() == null)
            file.write(line + " => " + context.get("quantity") + " is required\n");
        else if (cdr.getAccessCode() == null)
            file.write(line + " => " + context.get("accessCode") + " is required\n");
        else if (cdr.getParameter1() == null)
            file.write(line + " => " + context.get("parameter1") + " is required\n");

    }
	
	private boolean checkContent(Object element) {
		if(element == null ) return true;
		if(element instanceof  String) {
			if(StringUtils.isEmpty(element.toString()) || "null".equals(element.toString())){
				return true;
			}
		}
		return false;
	}

    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }
}