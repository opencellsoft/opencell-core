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

package org.meveo.api.helpers.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.meveo.api.dto.document.PDFDocumentRequestDto;
import org.meveo.api.dto.document.PDFTemplateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A business delegate class to generate pdf document file. This will prevent the client code from dealing with the implementation or the libs used to achieve the said pdf file
 * generation.
 * 
 * @author Said Ramli
 */
public class PDFDocumentHelper {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(PDFDocumentHelper.class);

    /**
     * Generate PDF document.
     *
     * @param postData the post data
     * @return the string
     * @throws Exception the exception
     */
    public static List<String> generatePDF(PDFDocumentRequestDto postData, String rootDir, String documentDir) throws Exception {

        List<String> listPaths = new ArrayList<String>();
        String documentNamePrefix = StringUtils.defaultIfEmpty(postData.getDocumentNamePrefix(), "doc");

        if (postData.isCombineFiles()) { // combine all created files in one pdf :
            try (PDDocument mainTemplateDoc = new PDDocument()) {
                PDFBuilder pdfBuilder = PDFBuilder.newInstance(documentDir, documentNamePrefix, mainTemplateDoc);
                String pdfFilePath = null;
                // postData.getListTemplates size should be already verified
                for (PDFTemplateDto templateDto : postData.getListTemplates()) {
                    buildPDFFromTemplate(postData, rootDir, templateDto, pdfBuilder);
                }
                pdfFilePath = pdfBuilder.save();
                LOG.debug(" file created : " + pdfFilePath);
                listPaths.add(pdfFilePath);
            } catch (Exception e) {
                LOG.error("error on generatePDF {} ", e.getMessage(), e);
                throw e;
            }
        } else { // create a pdf per template
            
            for (PDFTemplateDto templateDto : postData.getListTemplates()) {
                try (PDDocument mainTemplateDoc = new PDDocument()) {
                    PDFBuilder pdfBuilder = PDFBuilder.newInstance(documentDir, documentNamePrefix.concat("_").concat(templateDto.getTemplateName()), mainTemplateDoc);
                    buildPDFFromTemplate(postData, rootDir, templateDto, pdfBuilder);
                    String pdfFilePath = pdfBuilder.save();
                    LOG.debug(" file created : " + pdfFilePath);
                    listPaths.add(pdfFilePath);
                } catch (Exception e) {
                    LOG.error("error on generatePDF {} ", e.getMessage(), e);
                    throw e;
                }
            }
        }

        return listPaths;
    }

    public static String combineFiles(String documentDir, List<String> listPaths, String documentNamePrefix)  {
        String destinationPath = null;
        try {
            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            destinationPath = documentDir.concat(File.separator).concat(documentNamePrefix).concat(".pdf");
            pdfMerger.setDestinationFileName(destinationPath);
            for (String path: listPaths) {
                pdfMerger.addSource(new File(path)); 
            }
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            return destinationPath;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    private static void buildPDFFromTemplate(PDFDocumentRequestDto postData, String rootDir, PDFTemplateDto templateDto, PDFBuilder pdfBuilder) throws Exception {
        String templatePath = templateDto.getTemplatePath();
        if (!postData.isAbsolutePaths()) {
            templatePath = rootDir + templatePath;
        }
        File templateFile = new File(templatePath);
        try (PDDocument templateDoc = PDDocument.load(templateFile)) {
            pdfBuilder.withFormFieds(templateDto.getTemplateFields()).withBarcodeFieds(templateDto.getBarCodeFields()).withTemplate(templateDoc).buildAndAppendToMainTemplate(
                    postData.isFlattened());
        }
    }

    /**
     * Gets the document directory absolute path.
     *
     * @param postData : API inputs
     * @param rootDir the provider root directory
     * @return the absolute path where pdf file will be generated
     */
    public static String getDocumentDirectoryAbsolutePath(PDFDocumentRequestDto postData, String rootDir) {
        String documentDir = postData.getDocumentDestinationDir();
        if (StringUtils.isEmpty(documentDir)) {
            return rootDir;
        }
        if (!documentDir.startsWith(File.separator)) {
            documentDir = File.separator + documentDir;
        }

        if (!postData.isAbsolutePaths()) {
            if (File.separator.equals(documentDir)) { // to avoid having paths like : aa/bb//cc
                documentDir = rootDir;
            } else {
                documentDir = rootDir + documentDir;
            }
        }
        File documentDirFile = new File(documentDir);
        if (!documentDirFile.exists()) {
            documentDirFile.mkdirs();
        }
        return documentDir;
    }

    /**
     * Gets the pdf file as bytes.
     *
     * @param pdfFilePath the pdf file path
     * @return the pdf file as bytes
     * @throws FileNotFoundException the file not found exception
     */
    public static byte[] getPdfFileAsBytes(String pdfFilePath) throws FileNotFoundException {

        File pdfFile = new File(pdfFilePath);
        if (!pdfFile.exists()) {
            throw new FileNotFoundException("PDF document not found ! pdfFilePath :  " + pdfFilePath);
        }
        try (FileInputStream fileInputStream = new FileInputStream(pdfFile)) {
            long fileSize = pdfFile.length();
            if (fileSize > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("File is too big to put it to buffer in memory.");
            }
            byte[] fileBytes = new byte[(int) fileSize];
            fileInputStream.read(fileBytes);
            return fileBytes;
        } catch (Exception e) {
            LOG.error("Error reading PDF document file {} contents", pdfFilePath, e);
        }
        return null;
    }
}
