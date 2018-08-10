package org.meveo.api.document;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.document.PDFContractRequestDto;
import org.meveo.api.dto.document.PDFTemplateDto;
import org.meveo.api.dto.response.document.PDFContractResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.helpers.document.PDFContractHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by said on 7/9/18.
 */
@Stateless
public class PDFContractApi extends BaseApi {
    
    private static final Logger LOG = LoggerFactory.getLogger(PDFContractApi.class);
    
    public PDFContractResponseDto generatePDFContract(PDFContractRequestDto postData) throws MeveoApiException {

        PDFContractResponseDto restul = new PDFContractResponseDto();

        try {
            LOG.debug("[ Start checking common required & additional params ...");
            List<PDFTemplateDto> listTemplates = postData.getListTemplates();
            if (CollectionUtils.isEmpty(listTemplates)) {
                throw new MeveoApiException("listTemplates cannot be empty !");
            }
            String contractDestinationDir = checkConfiguredOrApiParam(postData.getContractDestinationDir(), "contractDestinationDir");
            postData.setContractDestinationDir(contractDestinationDir);
           
            if (postData.getContarctNamePrefix() == null) {
                postData.setContarctNamePrefix((String) this.customFieldInstanceService.getCFValue(this.appProvider, "contarctNamePrefix"));
            }
            for (PDFTemplateDto templateDto : listTemplates) {
                this.checkTemplateDtoParams(templateDto);
            }
            LOG.debug("End checking common required & additional params  ]");
         
            // generating the PDF contract & returning its file path
            String pdfFilePath = PDFContractHelper.generatePDFContract(postData);
            restul.setPdfFilePath(pdfFilePath);

            // generating the PDF as byte[]
            if (pdfFilePath != null && BooleanUtils.isTrue(postData.getReturnPdf())) {
                restul.setPdfFile(PDFContractHelper.getPdfFileAsBytes(pdfFilePath));
            }
            return restul;
            
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private void checkTemplateDtoParams(PDFTemplateDto templateDto) throws MissingParameterException, MeveoApiException {
        
        String templateName = templateDto.getTemplateName();
        
        if (StringUtils.isEmpty(templateName)) {
            throw new MeveoApiException("templateName is required for each PDF Template !");
        }
        
        String templatePath = checkConfiguredOrApiParam(templateDto.getTemplatePath(), templateName + "_TemplatePath");
        templateDto.setTemplatePath(templatePath);

        List<String> listOfRequiredFields = (List<String>) this.customFieldInstanceService.getCFValue(this.appProvider, templateName + "_RequiredFields");
        if (CollectionUtils.isNotEmpty(listOfRequiredFields)) {
            Map<String, String> templateFields = templateDto.getTemplateFields();
            for (String fieldKey : listOfRequiredFields) {
                if (!templateFields.containsKey(fieldKey)) {
                    this.missingParameters.add(templateName + " -> " + fieldKey);
                }
            }
            handleMissingParameters();
        }
        
        if (templateDto.getBarCodeFields() == null) {
            templateDto.setBarCodeFields((List<String>) this.customFieldInstanceService.getCFValue(this.appProvider, templateName + "_BarCodeFields"));
        }
    }
    
    private String checkConfiguredOrApiParam(String apiValue, String paramName) throws MeveoApiException {
        if(apiValue == null) {
            apiValue = (String) this.customFieldInstanceService.getCFValue(this.appProvider, paramName);
        }
        if (apiValue == null) {
            throw new MeveoApiException(String.format("%s should be configured or sent by the request !", paramName)); 
        }
        return apiValue;
    }

}
