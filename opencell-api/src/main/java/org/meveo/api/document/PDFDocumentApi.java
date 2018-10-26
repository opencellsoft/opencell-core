package org.meveo.api.document;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.document.PDFDocumentRequestDto;
import org.meveo.api.dto.document.PDFTemplateDto;
import org.meveo.api.dto.response.document.PDFDocumentResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.helpers.document.PDFDocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by said on 7/9/18.
 */
@Stateless
public class PDFDocumentApi extends BaseApi {
    
    private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentApi.class);
    
    public PDFDocumentResponseDto generatePDF(PDFDocumentRequestDto postData) throws MeveoApiException {

        PDFDocumentResponseDto restul = new PDFDocumentResponseDto();
        
        try {
            LOG.debug("[ Start checking common required & additional params ...");
            List<PDFTemplateDto> listTemplates = postData.getListTemplates();
            if (CollectionUtils.isEmpty(listTemplates)) {
                throw new MeveoApiException("listTemplates cannot be empty !");
            }
           
            if (StringUtils.isEmpty(postData.getDocumentNamePrefix())) {
                this.missingParameters.add("documentNamePrefix");
                this.handleMissingParameters();
            }
            for (PDFTemplateDto templateDto : listTemplates) {
                this.checkTemplateDtoParams(templateDto);
            }
            LOG.debug("End checking common required & additional params  ]");
            
            final String rootPath = this.paramBeanFactory.getChrootDir();
         
            // generating the PDF document & returning its file path
            String absolutePdfFilePath = PDFDocumentHelper.generatePDF(postData, rootPath);
            restul.setPdfFilePath(postData.isAbsolutePaths() ? absolutePdfFilePath : absolutePdfFilePath.substring(rootPath.length()));

            // generating the PDF as byte[]
            if (absolutePdfFilePath != null && postData.isReturnPdf()) {
                restul.setPdfFile(PDFDocumentHelper.getPdfFileAsBytes(absolutePdfFilePath));
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
        if(StringUtils.isEmpty(apiValue)) {
            apiValue = (String) this.customFieldInstanceService.getCFValue(this.appProvider, paramName);
        }
        if (StringUtils.isEmpty(apiValue)) {
            throw new MeveoApiException(String.format("%s should be configured  or sent by the API request !", paramName)); 
        }
        return apiValue;
    }

}
