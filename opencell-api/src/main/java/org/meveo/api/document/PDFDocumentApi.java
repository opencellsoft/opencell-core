package org.meveo.api.document;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

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
            if (isEmpty(listTemplates)) {
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
            List<String> listPdfFilePaths = PDFDocumentHelper.generatePDF(postData, rootPath);
            boolean isCombineFiles = postData.isCombineFiles();
            if (isCombineFiles && isNotEmpty(listPdfFilePaths)) {
                restul.setPdfFilePath(postData.isAbsolutePaths() ? listPdfFilePaths.get(0) : this.relativePaths(listPdfFilePaths, rootPath.length()).get(0) ); 
            } else {
                restul.setListPdfFilePaths(postData.isAbsolutePaths() ? listPdfFilePaths : this.relativePaths(listPdfFilePaths, rootPath.length())); 
            }
            if (isNotEmpty(listPdfFilePaths) && postData.isReturnPdf()) {
                // generating the PDF as byte[]
                List<byte[]>  pdfFiles = this.generatePDFsAsBytes(listPdfFilePaths);
                if (isCombineFiles && isNotEmpty(pdfFiles)) {
                    restul.setPdfFile(pdfFiles.get(0));
                } else {
                    restul.setPdfFiles(pdfFiles); 
                }
            }
            return restul;
            
        } catch (Exception e) {
            throw new MeveoApiException(e);
        }

    }

    private List<byte[]> generatePDFsAsBytes(List<String> listPdfFilePaths) throws FileNotFoundException {
        List<byte[]> listPDFsAsBytes = new ArrayList<>();
        for (String path : listPdfFilePaths) {
            if (path != null) {
                listPDFsAsBytes.add(PDFDocumentHelper.getPdfFileAsBytes(path));
            }
        }
        return listPDFsAsBytes;
    }

    private List<String> relativePaths(List<String> listPdfFilePaths, int index) {
        List<String> result = new ArrayList<>();
        for (String path : listPdfFilePaths) {
            result.add(path.substring(index));
        }
        return result;
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
        if (isNotEmpty(listOfRequiredFields)) {
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
