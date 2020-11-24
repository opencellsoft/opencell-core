package com.matooma.script;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.notification.InboundRequest;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.Script;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Add, Update, and remove Offer Service Template API
 * 
 */
public class OfferServiceTemplateApi extends Script {

    private static final long serialVersionUID = -87972504119660872L;

    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    private OfferTemplateService offerTemplateService = (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private ServiceTemplateService serviceTemplateService = (ServiceTemplateService) getServiceInterface(ServiceTemplateService.class.getSimpleName());
    private CustomFieldTemplateService cftService = (CustomFieldTemplateService) getServiceInterface(CustomFieldTemplateService.class.getSimpleName());
    private CustomFieldInstanceService cfiService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());

    /**
     * Entry Point
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute(Map<String, Object> context) throws BusinessException {

        InboundRequest inboundRequest = (InboundRequest) context.get(Script.CONTEXT_ENTITY);

        try {

            String body = inboundRequest.getBody();
            String requestType = inboundRequest.getMethod();
            String responseBody;

            if (POST.equals(requestType)) {
                responseBody = createOfferService(body);
            } else if (PUT.equals(requestType)) {
                responseBody = updateOfferService(body);
            } else if (DELETE.equals(requestType)) {
                responseBody = removeOfferService(body);
            } else {
                throw new BusinessException("Unknown request method");
            }

            // response is successful
            inboundRequest.setResponseContentType("application/json");
            inboundRequest.setResponseBody(responseBody);
            inboundRequest.setResponseStatus(200);

        } catch (Exception e) {
            log.error("Failed to execute OfferServiceTemplateApi script", e);
            inboundRequest.setResponseContentType("application/json");
            JSONObject response = new JSONObject();
            response.put("status", "FAILURE");
            response.put("message", e.getMessage());
            inboundRequest.setResponseBody(response.toJSONString());
            inboundRequest.setResponseStatus(500);
        }

    }

    /**
     * Offer templates add offers services templates.
     * 
     * Incompatible services templates are also updated if the incompatible service list is indicated when creating or modifying the offer service template.
     * 
     * @param body
     * @return
     * @throws ParseException
     * @throws BusinessException
     * @throws MeveoApiException
     */
    @SuppressWarnings("unchecked")
    private String createOfferService(String body) throws ParseException, java.text.ParseException, BusinessException, MeveoApiException {
        JSONParser parser = new JSONParser();
        JSONObject requestBody = (JSONObject) parser.parse(body);

        String offerTemplateCode = (String) requestBody.get("offerTemplateCode");
        String serviceTemplateCode = (String) requestBody.get("serviceTemplateCode");
        Boolean mandatory = (Boolean) requestBody.getOrDefault("mandatory", false);
        Date validFrom = requestBody.containsKey("validFrom") ? parseDate((String) requestBody.get("validFrom")) : null;
        Date validTo = requestBody.containsKey("validTo") ? parseDate((String) requestBody.get("validTo")) : null;
        List<String> incompatibleServices = requestBody.containsKey("incompatibleServiceTemplates") ? (List<String>) requestBody.get("incompatibleServiceTemplates") : null;

        List<String> missingParameters = new ArrayList<>();

        if (StringUtils.isBlank(offerTemplateCode)) {
            missingParameters.add("offerTemplateCode");
        }
        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(missingParameters);
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerTemplateCode, validFrom, validTo);
        if (offerTemplate == null) {
            throw new EntityNotFoundException("OfferTemplate code: " + offerTemplateCode + " not exist");
        }

        OfferServiceTemplate newOST = new OfferServiceTemplate();
        newOST.setMandatory(mandatory);
        newOST.setOfferTemplate(offerTemplate);
        newOST.setServiceTemplate(serviceTemplateService.findByCode(serviceTemplateCode));
        if ((incompatibleServices != null) && !incompatibleServices.isEmpty()) {
            for (String code : incompatibleServices) {
                ServiceTemplate incompatibleService = serviceTemplateService.findByCode(code);
                if (!newOST.getIncompatibleServices().contains(incompatibleService)) {
                    newOST.addIncompatibleServiceTemplate(incompatibleService);
                }
            }
        }

        if (!offerTemplate.getOfferServiceTemplates().contains(newOST)) {
            offerTemplate.addOfferServiceTemplate(newOST);
        } else {
            int index = offerTemplate.getOfferServiceTemplates().indexOf(newOST);
            if (index >= 0) {
                OfferServiceTemplate oldOST = offerTemplate.getOfferServiceTemplates().get(index);
                oldOST.setMandatory(newOST.isMandatory());
                if ((oldOST.getIncompatibleServices() != null) && !oldOST.getIncompatibleServices().isEmpty()) {
                    for (ServiceTemplate incompST : newOST.getIncompatibleServices()) {
                        if (!oldOST.getIncompatibleServices().contains(incompST)) {
                            oldOST.getIncompatibleServices().add(incompST);
                        }
                    }
                } else {
                    oldOST.setIncompatibleServices(newOST.getIncompatibleServices());
                }
                offerTemplate.getOfferServiceTemplates().set(index, oldOST);
            }
        }

        offerTemplateService.update(offerTemplate);

        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        response.put("status", "success");
        data.put("offerTemplateCode", offerTemplateCode);
        data.put("validFrom", validFrom);
        data.put("validTo", validTo);
        data.put("serviceTemplateCode", serviceTemplateCode);
        data.put("mandatory", mandatory);
        data.put("incompatibleService", incompatibleServices);
        response.put("data", data);
        return response.toString();
    }

    /**
     * Offer templates update offers services templates.
     * 
     * @param body
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     * @throws BusinessException
     * @throws MeveoApiException
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @SuppressWarnings("unchecked")
    private String updateOfferService(String body)
            throws ParseException, java.text.ParseException, BusinessException, MeveoApiException, JsonParseException, JsonMappingException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject requestBody = (JSONObject) parser.parse(body);

        String offerTemplateCode = (String) requestBody.get("offerTemplateCode");
        Date validFrom = requestBody.containsKey("validFrom") ? parseDate((String) requestBody.get("validFrom")) : null;
        Date validTo = requestBody.containsKey("validTo") ? parseDate((String) requestBody.get("validTo")) : null;

        List<String> missingParameters = new ArrayList<>();

        if (StringUtils.isBlank(offerTemplateCode)) {
            missingParameters.add("offerTemplateCode");
        }
        if (!requestBody.containsKey("serviceTemplates")) {
            missingParameters.add("serviceTemplates");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(missingParameters);
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerTemplateCode, validFrom, validTo);
        if (offerTemplate == null) {
            throw new EntityNotFoundException("OfferTemplate code: " + offerTemplateCode + " not exist");
        }

        boolean offerHasChanged = false;
        // Offer name
        if (requestBody.containsKey("name")) {
            String name = (String) requestBody.get("name");
            offerTemplate.setName(name);
            offerHasChanged = true;
        }
        // Offer description
        if (requestBody.containsKey("description")) {
            String description = (String) requestBody.get("description");
            offerTemplate.setDescription(description);
            offerHasChanged = true;
        }

        OfferTemplateDTO offerDTO = new ObjectMapper().readValue(requestBody.toString(), OfferTemplateDTO.class);
        updateCFs(offerTemplate, offerDTO.parameters);

        if (!offerDTO.parameters.isEmpty() && offerHasChanged) {
            offerTemplateService.update(offerTemplate);
        }

        List<JSONObject> serviceTemplates = (List<JSONObject>) requestBody.get("serviceTemplates");

        for (JSONObject serviceTemplateJson : serviceTemplates) {
            ServiceTemplateDTO serviceDTO = new ObjectMapper().readValue(serviceTemplateJson.toString(), ServiceTemplateDTO.class);

            Optional<OfferServiceTemplate> optional = offerTemplate.getOfferServiceTemplates().stream().filter(ost -> ost.getServiceTemplate().getCode().endsWith(serviceDTO.code))
                .findFirst();

            if (!optional.isPresent()) {
                throw new EntityNotFoundException("OfferServiceTemplate of ServiceTemplate code: " + serviceDTO.code + " not exist into OfferTemplate code: " + offerTemplateCode);
            }

            // update OfferServiceTemplate
            if (serviceDTO.mandatory != null) {
                int index = offerTemplate.getOfferServiceTemplates().indexOf(optional.get());
                OfferServiceTemplate oldOST = offerTemplate.getOfferServiceTemplates().get(index);
                oldOST.setMandatory(serviceDTO.mandatory);
                offerTemplate.getOfferServiceTemplates().set(index, oldOST);
                offerTemplateService.update(offerTemplate);
            }

            ServiceTemplate serviceTemplate = optional.get().getServiceTemplate();

            boolean serviceHasChanged = false;
            // update ServiceTemplate
            if (!StringUtils.isBlank(serviceDTO.description)) {
                serviceTemplate.setDescription(serviceDTO.description);
                serviceHasChanged = true;
            }

            // update service template CFs
            updateCFs(serviceTemplate, serviceDTO.parameters);

            if (!serviceDTO.parameters.isEmpty() || serviceHasChanged) {
                serviceTemplateService.update(serviceTemplate);
            }
        }

        JSONObject response = new JSONObject();
        response.put("status", "success");
        response.put("data", requestBody);
        return response.toString();
    }

    /**
     * remover Offer Service Template or detach incompatible services templates.
     * 
     * If an list incompatible service template is not specified in the offer service template, the offer service template is deleted, or the list of incompatible services
     * templates is detached.
     * 
     * @param body
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     * @throws BusinessException
     * @throws MeveoApiException
     */
    @SuppressWarnings("unchecked")
    private String removeOfferService(String body) throws ParseException, java.text.ParseException, BusinessException, MeveoApiException {
        JSONParser parser = new JSONParser();
        JSONObject requestBody = (JSONObject) parser.parse(body);

        String offerTemplateCode = (String) requestBody.get("offerTemplateCode");
        String serviceTemplateCode = (String) requestBody.get("serviceTemplateCode");
        Date validFrom = requestBody.containsKey("validFrom") ? parseDate((String) requestBody.get("validFrom")) : null;
        Date validTo = requestBody.containsKey("validTo") ? parseDate((String) requestBody.get("validTo")) : null;
        List<String> incompatibleServices = requestBody.containsKey("incompatibleServiceTemplates") ? (List<String>) requestBody.get("incompatibleServiceTemplates") : null;

        List<String> missingParameters = new ArrayList<>();

        if (StringUtils.isBlank(offerTemplateCode)) {
            missingParameters.add("offerTemplateCode");
        }
        if (StringUtils.isBlank(serviceTemplateCode)) {
            missingParameters.add("serviceTemplateCode");
        }
        if (!missingParameters.isEmpty()) {
            throw new MissingParameterException(missingParameters);
        }
        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerTemplateCode, validFrom, validTo);

        OfferServiceTemplate newOST = new OfferServiceTemplate();
        newOST.setOfferTemplate(offerTemplate);
        newOST.setServiceTemplate(serviceTemplateService.findByCode(serviceTemplateCode));

        int index = offerTemplate.getOfferServiceTemplates().indexOf(newOST);
        OfferServiceTemplate oldOST = offerTemplate.getOfferServiceTemplates().get(index);
        if (index >= 0) {
            if ((incompatibleServices != null) && !incompatibleServices.isEmpty()) {
                for (String codeService : incompatibleServices) {
                    ServiceTemplate incompST = serviceTemplateService.findByCode(codeService);
                    if (!oldOST.getIncompatibleServices().isEmpty() && (incompST != null) && oldOST.getIncompatibleServices().contains(incompST)) {
                        oldOST.getIncompatibleServices().remove(incompST);
                    }
                }
                offerTemplate.getOfferServiceTemplates().set(index, oldOST);
            } else {
                offerTemplate.getOfferServiceTemplates().remove(newOST);
            }
        } else {
            throw new EntityNotFoundException("OfferServiceTemplate of ServiceTemplate code: " + serviceTemplateCode + " not exist into OfferTemplate code: " + offerTemplateCode);
        }

        offerTemplateService.update(offerTemplate);

        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        response.put("status", "success");
        data.put("offerTemplateCode", offerTemplateCode);
        data.put("validFrom", validFrom);
        data.put("validTo", validTo);
        data.put("serviceTemplateCode", serviceTemplateCode);
        data.put("incompatibleService", incompatibleServices);
        response.put("data", data);
        return response.toString();
    }

    private Date parseDate(String stringDate) throws ParseException, java.text.ParseException {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        return format.parse(stringDate);
    }

    private void updateCFs(ICustomFieldEntity entity, List<ParameterDto> parameters) {
        for (ParameterDto parameterDto : parameters) {
            CustomFieldTemplate cft = cftService.findByCodeAndAppliesTo(parameterDto.code, entity);
            Object value = parameterDto.value;
            if (value instanceof Integer && cft.getFieldType() == CustomFieldTypeEnum.DOUBLE) {
                value = Double.valueOf((Integer) value);
            }
            cfiService.setCFValue(entity, parameterDto.code, value);
        }
    }

    // inner classes
    static class OfferTemplateDTO {

        private String offerTemplateCode;
        private String name;
        private String description;
        private String validFrom;
        private String validTo;
        private List<ParameterDto> parameters;
        private List<ServiceTemplateDTO> serviceTemplates;

        public OfferTemplateDTO() {
            super();
        }

        /**
         * @return the offerTemplateCode
         */
        public String getOfferTemplateCode() {
            return offerTemplateCode;
        }

        /**
         * @param offerTemplateCode the offerTemplateCode to set
         */
        public void setOfferTemplateCode(String offerTemplateCode) {
            this.offerTemplateCode = offerTemplateCode;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the validFrom
         */
        public String getValidFrom() {
            return validFrom;
        }

        /**
         * @param validFrom the validFrom to set
         */
        public void setValidFrom(String validFrom) {
            this.validFrom = validFrom;
        }

        /**
         * @return the validTo
         */
        public String getValidTo() {
            return validTo;
        }

        /**
         * @param validTo the validTo to set
         */
        public void setValidTo(String validTo) {
            this.validTo = validTo;
        }

        /**
         * @return the parameters
         */
        public List<ParameterDto> getParameters() {
            return parameters;
        }

        /**
         * @param parameters the parameters to set
         */
        public void setParameters(List<ParameterDto> parameters) {
            this.parameters = parameters;
        }

        /**
         * @return the serviceTemplates
         */
        public List<ServiceTemplateDTO> getServiceTemplates() {
            return serviceTemplates;
        }

        /**
         * @param serviceTemplates the serviceTemplates to set
         */
        public void setServiceTemplates(List<ServiceTemplateDTO> serviceTemplates) {
            this.serviceTemplates = serviceTemplates;
        }
    }

    static class ServiceTemplateDTO {

        private Boolean mandatory;
        private String code;
        private String description;
        private List<ParameterDto> parameters;

        public ServiceTemplateDTO() {
            super();
        }

        /**
         * @return the mandatory
         */
        public Boolean getMandatory() {
            return mandatory;
        }

        /**
         * @param mandatory the mandatory to set
         */
        public void setMandatory(Boolean mandatory) {
            this.mandatory = mandatory;
        }

        /**
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code the code to set
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the parameters
         */
        public List<ParameterDto> getParameters() {
            return parameters;
        }

        /**
         * @param parameters the parameters to set
         */
        public void setParameters(List<ParameterDto> parameters) {
            this.parameters = parameters;
        }
    }

    static class ParameterDto {

        private String code;
        private Object value;

        public ParameterDto() {
            super();
        }

        /**
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code the code to set
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return the value
         */
        public Object getValue() {
            return value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(Object value) {
            this.value = value;
        }
    }
}