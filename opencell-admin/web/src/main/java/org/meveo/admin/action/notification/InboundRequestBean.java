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

package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.notification.InboundRequest;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.InboundRequestService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link InboundRequest} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
@Named
@ViewScoped
public class InboundRequestBean extends UpdateMapTypeFieldBean<InboundRequest> {

    private static final long serialVersionUID = -6762628879784107169L;

    @Inject
    InboundRequestService inboundRequestService;

    public InboundRequestBean() {
        super(InboundRequest.class);
    }

    CsvReader csvReader = null;
    private UploadedFile file;
    CsvBuilder csv = null;
    private String providerDir;
    private String existingEntitiesCsvFile = null;

    private StrategyImportTypeEnum strategyImportType;

    private static final int FROM_IP = 0;
    private static final int PORT = 1;
    private static final int PORTOCOL = 2;
    private static final int PATH_INFO = 3;
    private static final int CODE = 4;
    private static final int ACTIVE = 5;
    private static final int SCHEME = 6;
    private static final int CONTENT_TYPE = 7;
    private static final int CONTENT_LENGHT = 8;
    private static final int METHOD = 9;
    private static final int AUTHENTIFICATION_TYPE = 10;
    private static final int REQUEST_URI = 11;
    private static final int PARAMETERS = 12;
    private static final int COOCKIES = 13;
    private static final int HEADERS = 14;
    private static final int RESPONSE_CONTENT_TYPE = 15;
    private static final int ENCODING = 16;
    private static final int RESPONSE_COOCKIES = 17;
    private static final int RESPONSE_HEADERS = 18;
    private static final int UPDATE_DATE = 19;

    @Override
    protected IPersistenceService<InboundRequest> getPersistenceService() {
        return inboundRequestService;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("parameters", "coockies", "headers", "responseCoockies", "responseHeaders");
    }

    @Override
    public InboundRequest initEntity() {
        InboundRequest inboundRequest = super.initEntity();

        extractMapTypeFieldFromEntity(inboundRequest.getHeaders(), "headers");
        extractMapTypeFieldFromEntity(inboundRequest.getParameters(), "parameters");
        extractMapTypeFieldFromEntity(inboundRequest.getCoockies(), "coockies");
        extractMapTypeFieldFromEntity(inboundRequest.getResponseCoockies(), "responseCoockies");
        extractMapTypeFieldFromEntity(inboundRequest.getResponseHeaders(), "responseHeaders");

        return inboundRequest;
    }

    public void exportToFile() throws Exception {

        CsvBuilder csv = new CsvBuilder();
        csv.appendValue("From IP");
        csv.appendValue("Port");
        csv.appendValue("Protocol");
        csv.appendValue("Path info");
        csv.appendValue("Code");
        csv.appendValue("Active");
        csv.appendValue("Scheme");
        csv.appendValue("Content type");
        csv.appendValue("Content length");
        csv.appendValue("Method");
        csv.appendValue("Authentication type");
        csv.appendValue("Request URI");
        csv.appendValue("Parametres");
        csv.appendValue("Cookies");
        csv.appendValue("Headers");
        csv.appendValue("Response content type");
        csv.appendValue("Encoding");
        csv.appendValue("Response_Coockies");
        csv.appendValue("Response_Headers");
        csv.appendValue("Update date");

        csv.startNewLine();
        for (InboundRequest inboundRequest : (!filters.isEmpty() && filters.size() > 0) ? getLazyDataModel() : inboundRequestService.list()) {
            csv.appendValue(inboundRequest.getRemoteAddr());
            csv.appendValue(inboundRequest.getRemotePort() + "");
            csv.appendValue(inboundRequest.getProtocol());
            csv.appendValue(inboundRequest.getPathInfo());
            csv.appendValue(inboundRequest.getCode());
            csv.appendValue("true");
            csv.appendValue(inboundRequest.getScheme());
            csv.appendValue(inboundRequest.getContentType());
            csv.appendValue(inboundRequest.getContentLength() + "");
            csv.appendValue(inboundRequest.getMethod());
            csv.appendValue(inboundRequest.getAuthType());
            csv.appendValue(inboundRequest.getRequestURI());

            StringBuffer params = new StringBuffer();
            if (inboundRequest.getParameters() != null) {
                String sep = "";
                for (String key : inboundRequest.getParameters().keySet()) {
                    String valueParams = inboundRequest.getParameters().get(key);
                    params.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueParams.getBytes()));
                    sep = "|";
                }
                csv.appendValue(params.toString());
            }
            StringBuffer coockies = new StringBuffer();

            if (inboundRequest.getCoockies() != null) {
                String sep = "";
                for (String key : inboundRequest.getCoockies().keySet()) {
                    String valueCookies = inboundRequest.getCoockies().get(key);
                    coockies.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueCookies.getBytes()));
                    sep = "|";
                }
                csv.appendValue(coockies.toString());
            }

            StringBuffer headers = new StringBuffer();
            if (inboundRequest.getHeaders() != null) {
                String sep = "";
                for (String key : inboundRequest.getHeaders().keySet()) {
                    String valueHeaders = inboundRequest.getHeaders().get(key);
                    headers.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueHeaders.getBytes()));
                    sep = "|";
                }
                csv.appendValue(headers.toString());
            }

            csv.appendValue(inboundRequest.getResponseContentType() + "");
            csv.appendValue(inboundRequest.getResponseEncoding() + "");

            StringBuffer responseCoockies = new StringBuffer();
            if (inboundRequest.getResponseCoockies() != null) {
                String sep = "";
                for (String key : inboundRequest.getResponseCoockies().keySet()) {
                    String valueRespCookies = inboundRequest.getResponseCoockies().get(key);
                    responseCoockies.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueRespCookies.getBytes()));
                    sep = "|";
                }
                csv.appendValue(responseCoockies.toString());
            }
            StringBuffer responseHeaders = new StringBuffer();
            if (inboundRequest.getResponseHeaders() != null) {
                String sep = "";
                for (String key : inboundRequest.getResponseHeaders().keySet()) {
                    String valueRespHeaders = inboundRequest.getResponseHeaders().get(key);
                    responseHeaders.append(sep).append(key).append(":").append(Base64.encodeBase64String(valueRespHeaders.getBytes()));
                    sep = "|";
                }
                csv.appendValue(responseHeaders.toString());
            }
            csv.appendValue(DateUtils.setTimeToZero(inboundRequest.getAuditable().getUpdated()) + "");
            csv.startNewLine();
        }
        InputStream inputStream = new ByteArrayInputStream(csv.toString().getBytes());
        csv.download(inputStream, "InboundRequests.csv");
    }

    public void handleFileUpload(FileUploadEvent event) throws Exception {
        try {
            file = event.getFile();
            log.debug("File uploaded " + file.getFileName());
            upload();
            messages.info(new BundleKey("messages", "import.csv.successful"));
        } catch (Exception e) {
            log.error("Failed to handle uploaded file {}", event.getFile().getFileName(), e);
            messages.error(new BundleKey("messages", "import.csv.failed"), e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }

    private void upload() throws IOException, BusinessException {
        if (file == null) {
            return;
        }
        csvReader = new CsvReader(file.getInputstream(), ';', Charset.forName("ISO-8859-1"));
        csvReader.readHeaders();

        ParamBean paramBean = paramBeanFactory.getInstance();
        String existingEntitiesCSV = paramBean.getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
        providerDir = paramBean.getChrootDir(currentUser.getProviderCode());
        File dir = new File(providerDir + File.separator + existingEntitiesCSV);
        dir.mkdirs();
        existingEntitiesCsvFile = dir.getAbsolutePath() + File.separator + "InboundRequests_" + new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date()) + ".csv";
        csv = new CsvBuilder();
        boolean isEntityAlreadyExist = false;
        while (csvReader.readRecord()) {
            String[] values = csvReader.getValues();
            InboundRequest existingEntity = inboundRequestService.findByCode(values[CODE]);
            if (existingEntity != null) {
                checkSelectedStrategy(values, existingEntity, isEntityAlreadyExist);
                isEntityAlreadyExist = true;
            } else {
                InboundRequest inboundRequest = new InboundRequest();
                inboundRequest.setRemoteAddr(values[FROM_IP]);
                inboundRequest.setRemotePort(!StringUtils.isBlank(values[PORT]) ? Integer.parseInt(values[PORT]) : null);
                inboundRequest.setProtocol(values[PORTOCOL]);
                inboundRequest.setPathInfo(values[PATH_INFO]);
                inboundRequest.setCode(values[CODE]);
                inboundRequest.setScheme(values[SCHEME]);
                inboundRequest.setContentType(values[CONTENT_TYPE]);
                inboundRequest.setContentLength(Integer.parseInt(values[CONTENT_LENGHT]));
                inboundRequest.setMethod(values[METHOD]);
                inboundRequest.setAuthType(values[AUTHENTIFICATION_TYPE]);
                inboundRequest.setRequestURI(values[REQUEST_URI]);

                if (values[PARAMETERS] != null && values[PARAMETERS].length() > 0) {
                    String[] mapElements = values[PARAMETERS].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> params = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            params.put(param[0], value);
                        }
                        inboundRequest.setParameters(params);
                    }
                }
                if (values[COOCKIES] != null && values[COOCKIES].length() > 0) {
                    String[] mapElements = values[COOCKIES].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> coockies = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            coockies.put(param[0], value);
                        }
                        inboundRequest.setCoockies(coockies);
                    }
                }
                if (values[HEADERS] != null && values[HEADERS].length() > 0) {
                    String[] mapElements = values[HEADERS].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> headers = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            headers.put(param[0], value);
                        }
                        inboundRequest.setHeaders(headers);
                    }
                }
                inboundRequest.setResponseContentType(values[RESPONSE_CONTENT_TYPE]);
                inboundRequest.setResponseEncoding(values[ENCODING]);

                if (values[RESPONSE_COOCKIES] != null && values[RESPONSE_COOCKIES].length() > 0) {
                    String[] mapElements = values[RESPONSE_COOCKIES].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> responseCoockies = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            responseCoockies.put(param[0], value);
                        }
                        inboundRequest.setResponseCoockies(responseCoockies);
                    }
                }
                if (values[RESPONSE_HEADERS] != null && values[RESPONSE_HEADERS].length() > 0) {
                    String[] mapElements = values[RESPONSE_HEADERS].split("\\|");
                    if (mapElements != null && mapElements.length > 0) {
                        Map<String, String> responseHeaders = new HashMap<String, String>();
                        for (String element : mapElements) {
                            String[] param = element.split(":");
                            String value = new String(Base64.decodeBase64(param[1]));
                            responseHeaders.put(param[0], value);
                        }
                        inboundRequest.setResponseHeaders(responseHeaders);
                    }
                }
                inboundRequestService.create(inboundRequest);
            }
        }
        if (isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
        }
    }

    public void checkSelectedStrategy(String[] values, InboundRequest existingEntity, boolean isEntityAlreadyExist) throws BusinessException {
        if (strategyImportType.equals(StrategyImportTypeEnum.UPDATED)) {
            existingEntity.setRemoteAddr(values[FROM_IP]);
            existingEntity.setRemotePort(!StringUtils.isBlank(values[PORT]) ? Integer.parseInt(values[PORT]) : null);
            existingEntity.setProtocol(values[PORTOCOL]);
            existingEntity.setPathInfo(values[PATH_INFO]);
            existingEntity.setScheme(values[SCHEME]);
            existingEntity.setContentType(values[CONTENT_TYPE]);
            existingEntity.setContentLength(Integer.parseInt(values[CONTENT_LENGHT]));
            existingEntity.setMethod(values[METHOD]);
            existingEntity.setAuthType(values[AUTHENTIFICATION_TYPE]);
            existingEntity.setRequestURI(values[REQUEST_URI]);

            if (values[PARAMETERS] != null && values[PARAMETERS].length() > 0) {
                String[] mapElements = values[PARAMETERS].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> params = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        params.put(param[0], value);
                    }
                    existingEntity.setParameters(params);
                }
            }
            if (values[COOCKIES] != null && values[COOCKIES].length() > 0) {
                String[] mapElements = values[COOCKIES].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> coockies = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        coockies.put(param[0], value);
                    }
                    existingEntity.setCoockies(coockies);
                }
            }
            if (values[HEADERS] != null && values[HEADERS].length() > 0) {
                String[] mapElements = values[HEADERS].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> headers = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        headers.put(param[0], value);
                    }
                    existingEntity.setHeaders(headers);
                }
            }
            existingEntity.setResponseContentType(values[RESPONSE_CONTENT_TYPE]);
            existingEntity.setResponseEncoding(values[ENCODING]);

            if (values[RESPONSE_COOCKIES] != null && values[RESPONSE_COOCKIES].length() > 0) {
                String[] mapElements = values[RESPONSE_COOCKIES].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> responseCoockies = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        responseCoockies.put(param[0], value);
                    }
                    existingEntity.setResponseCoockies(responseCoockies);
                }
            }
            if (values[RESPONSE_HEADERS] != null && values[RESPONSE_HEADERS].length() > 0) {
                String[] mapElements = values[RESPONSE_HEADERS].split("\\|");
                if (mapElements != null && mapElements.length > 0) {
                    Map<String, String> responseHeaders = new HashMap<String, String>();
                    for (String element : mapElements) {
                        String[] param = element.split(":");
                        String value = new String(Base64.decodeBase64(param[1]));
                        responseHeaders.put(param[0], value);
                    }
                    existingEntity.setResponseHeaders(responseHeaders);
                }
            }
            inboundRequestService.update(existingEntity);

        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECTE_IMPORT)) {
            throw new RejectedImportException("notification.rejectImport");
        } else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            if (!isEntityAlreadyExist) {
                csv.appendValue("From IP");
                csv.appendValue("Port");
                csv.appendValue("Protocol");
                csv.appendValue("Path info");
                csv.appendValue("Code");
                csv.appendValue("Active");
                csv.appendValue("Scheme");
                csv.appendValue("Content type");
                csv.appendValue("Content length");
                csv.appendValue("Method");
                csv.appendValue("Authentication type");
                csv.appendValue("Request URI");
                csv.appendValue("Cookies");
                csv.appendValue("Headers");
                csv.appendValue("Parameters");
                csv.appendValue("Response content type");
                csv.appendValue("Encoding");
                csv.appendValue("Cookies");
                csv.appendValue("Headers");
                csv.appendValue("Update date");
            }
            csv.startNewLine();
            csv.appendValue(values[FROM_IP]);
            csv.appendValue(values[PORT]);
            csv.appendValue(values[PORTOCOL]);
            csv.appendValue(values[PATH_INFO]);
            csv.appendValue(values[CODE]);
            csv.appendValue(values[ACTIVE]);
            csv.appendValue(values[SCHEME]);
            csv.appendValue(values[CONTENT_TYPE]);
            csv.appendValue(values[CONTENT_LENGHT]);
            csv.appendValue(values[METHOD]);
            csv.appendValue(values[AUTHENTIFICATION_TYPE]);
            csv.appendValue(values[REQUEST_URI]);
            csv.appendValue(values[RESPONSE_CONTENT_TYPE]);
            csv.appendValue(values[ENCODING]);
            csv.appendValue(values[UPDATE_DATE]);
        }
    }

    public StrategyImportTypeEnum getStrategyImportType() {
        return strategyImportType;
    }

    public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
        this.strategyImportType = strategyImportType;
    }
}