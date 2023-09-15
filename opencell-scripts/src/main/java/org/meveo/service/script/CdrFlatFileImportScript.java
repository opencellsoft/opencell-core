package org.meveo.service.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRService;
import org.primefaces.shaded.commons.io.FilenameUtils;

public class CdrFlatFileImportScript extends Script {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    private final transient ParamBeanFactory paramBeanFactory = (ParamBeanFactory) getServiceInterface(ParamBeanFactory.class.getSimpleName());
    private final transient CDRService cdrService = (CDRService) getServiceInterface(CDRService.class.getSimpleName());

    public static boolean elementExisted(Map<String, String> context, String[] header, String[] body, String element) {
        return ArrayUtils.indexOf(header, context.get(element), 0) >= 0 && ArrayUtils.indexOf(header, context.get(element), 0) < body.length;
    }

    @Override
    public void execute(Map<String, Object> contextMethod) throws BusinessException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        JobExecutionResultImpl jobExecutionResult = (JobExecutionResultImpl) contextMethod.get("JobExecutionResult");
        Map<String, String> context = (Map<String, String>) jobExecutionResult.getJobInstance().getCfValues().getValues().get("mapping");
        String pathFile = (String) jobExecutionResult.getJobInstance().getCfValues().getValues().get("pathFile");

        CDR cdr;
        String rootPathFile = getProviderRootDir() + File.separator + pathFile;
        File dir = new File(rootPathFile);

        File[] fileList = dir.listFiles();
        try {
            for (File fileInput : fileList) {
                if (!FilenameUtils.getExtension(fileInput.getName()).equals("csv")) {
                    continue;
                }
                File file = new File(fileInput.getAbsolutePath().replace("input", "reject") + ".rejected");
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter rejectFile = new FileWriter(file);
                FileReader fread = new FileReader(fileInput.getAbsolutePath());
                BufferedReader br = new BufferedReader(fread);
                String line = "";
                String splitBy = ";";
	            String[] header = null;
	            String headerTmp = br.readLine();
	            if(headerTmp.contains(",")){
		            splitBy = ",";
				}
	            header = headerTmp.split(splitBy);
                while ((line = br.readLine()) != null) {
                    try {
                        cdr = new CDR();
                        String[] body = line.split(splitBy);
                        Date dateEvent = null;
                        Date dateParam1 = null;
                        Date dateParam2 = null;
                        Date dateParam3 = null;
                        Date dateParam4 = null;
                        Date dateParam5 = null;
                        boolean reject = false;
                        try {
                            dateEvent = elementExisted(context, header, body, "eventDate") && !body[ArrayUtils.indexOf(header, context.get("eventDate"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("eventDate"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("eventDate"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("eventDate") + " \n");
                            reject = true;
                        }
                        String ac = elementExisted(context, header, body, "accessCode") && !body[ArrayUtils.indexOf(header, context.get("accessCode"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("accessCode"))]
                                : null;
                        BigDecimal quantity = elementExisted(context, header, body, "quantity") && !body[ArrayUtils.indexOf(header, context.get("quantity"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("quantity"))])
                                : null;
                        String parameter1 = elementExisted(context, header, body, "parameter1") && !body[ArrayUtils.indexOf(header, context.get("parameter1"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter1"))]
                                : null;
                        String parameter2 = elementExisted(context, header, body, "parameter2") && !body[ArrayUtils.indexOf(header, context.get("parameter2"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter2"))]
                                : null;
                        String parameter3 = elementExisted(context, header, body, "parameter3") && !body[ArrayUtils.indexOf(header, context.get("parameter3"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter3"))]
                                : null;
                        String parameter4 = elementExisted(context, header, body, "parameter4") && !body[ArrayUtils.indexOf(header, context.get("parameter4"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter4"))]
                                : null;
                        String parameter5 = elementExisted(context, header, body, "parameter5") && !body[ArrayUtils.indexOf(header, context.get("parameter5"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter5"))]
                                : null;
                        String parameter6 = elementExisted(context, header, body, "parameter6") && !body[ArrayUtils.indexOf(header, context.get("parameter6"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter6"))]
                                : null;
                        String parameter7 = elementExisted(context, header, body, "parameter7") && !body[ArrayUtils.indexOf(header, context.get("parameter7"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter7"))]
                                : null;
                        String parameter8 = elementExisted(context, header, body, "parameter8") && !body[ArrayUtils.indexOf(header, context.get("parameter8"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter8"))]
                                : null;
                        String parameter9 = elementExisted(context, header, body, "parameter9") && !body[ArrayUtils.indexOf(header, context.get("parameter9"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("parameter9"))]
                                : null;
                        String extraParameter = elementExisted(context, header, body, "extraParam") && !body[ArrayUtils.indexOf(header, context.get("extraParam"))].isEmpty()
                                ? body[ArrayUtils.indexOf(header, context.get("extraParam"))]
                                : null;
                        try {
                            dateParam1 = elementExisted(context, header, body, "dateParam1") && !body[ArrayUtils.indexOf(header, context.get("dateParam1"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("dateParam1"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("dateParam1"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("dateParam1") + " \n");
                            reject = true;
                        }
                        try {
                            dateParam2 = elementExisted(context, header, body, "dateParam2") && !body[ArrayUtils.indexOf(header, context.get("dateParam2"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("dateParam2"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("dateParam2"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("dateParam2") + " \n");
                            reject = true;
                        }
                        try {
                            dateParam3 = elementExisted(context, header, body, "dateParam3") && !body[ArrayUtils.indexOf(header, context.get("dateParam3"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("dateParam3"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("dateParam3"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("dateParam3") + " \n");
                            reject = true;
                        }
                        try {
                            dateParam4 = elementExisted(context, header, body, "dateParam4") && !body[ArrayUtils.indexOf(header, context.get("dateParam4"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("dateParam4"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("dateParam4"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("dateParam4") + " \n");
                            reject = true;
                        }
                        try {
                            dateParam5 = elementExisted(context, header, body, "dateParam5") && !body[ArrayUtils.indexOf(header, context.get("dateParam5"))].isEmpty()
                                    ? StringUtils.isEmpty(body[ArrayUtils.indexOf(header, context.get("dateParam5"))]) ? null : dateFormat.parse(body[ArrayUtils.indexOf(header, context.get("dateParam5"))])
                                    : null;
                        } catch (ParseException e) {
                            if (!reject)
                                rejectFile.write(line + " => Incorrect format date for cdr " + context.get("dateParam5") + " \n");
                            reject = true;
                        }

                        BigDecimal decimalParam1 = elementExisted(context, header, body, "decimalParam1") && !body[ArrayUtils.indexOf(header, context.get("decimalParam1"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("decimalParam1"))])
                                : null;
                        BigDecimal decimalParam2 = elementExisted(context, header, body, "decimalParam2") && !body[ArrayUtils.indexOf(header, context.get("decimalParam2"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("decimalParam2"))])
                                : null;
                        BigDecimal decimalParam3 = elementExisted(context, header, body, "decimalParam3") && !body[ArrayUtils.indexOf(header, context.get("decimalParam3"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("decimalParam3"))])
                                : null;
                        BigDecimal decimalParam4 = elementExisted(context, header, body, "decimalParam4") && !body[ArrayUtils.indexOf(header, context.get("decimalParam4"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("decimalParam4"))])
                                : null;
                        BigDecimal decimalParam5 = elementExisted(context, header, body, "decimalParam5") && !body[ArrayUtils.indexOf(header, context.get("decimalParam5"))].isEmpty()
                                ? new BigDecimal(body[ArrayUtils.indexOf(header, context.get("decimalParam5"))])
                                : null;

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
                        cdr.setExtraParameter(extraParameter);
                        if (!reject && cdr.getEventDate() != null && cdr.getQuantity() != null && cdr.getAccessCode() != null && cdr.getParameter1() != null)
                            cdrService.create(cdr);
                        else if (!reject) {
                            validateCdr(line, cdr, context, rejectFile);
                        }
                    } catch (Exception e) {
                        rejectFile.write(line + " => " + e.getMessage() + " \n");
                    }
                }
                rejectFile.close();
                br.close();
                fread.close();
                String toPath = getProviderRootDir() + File.separator + "imports/cdr/flatFile/archive" + File.separator + fileInput.getName();
                Files.createDirectories(Paths.get(toPath));
                Files.move(Paths.get(fileInput.getPath()), Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
				if(file.length() == 0) {
					Files.delete(file.toPath());
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void validateCdr(String line, CDR cdr, Map<String, String> context, FileWriter file) throws IOException {
        if (cdr.getEventDate() == null)
            file.write(line + " => " + context.get("eventDate") + " is required\n");
        else if (cdr.getQuantity() == null)
            file.write(line + " => " + context.get("quantity") + " is required\n");
        else if (cdr.getAccessCode() == null)
            file.write(line + " => " + context.get("accessCode") + " is required\n");
        else if (cdr.getParameter1() == null)
            file.write(line + " => " + context.get("parameter1") + " is required\n");

    }

    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }

}