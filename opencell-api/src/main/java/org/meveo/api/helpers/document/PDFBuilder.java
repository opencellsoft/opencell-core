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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder class to generate a PDF file and return its path.
 * 
 * Usage example : 
 * 
 *  PDFBuilder pdfBuilder = PDFBuilder.newInstance(documentDir, documentNamePrefix, mainTemplateDoc);
 * 
 *  pdfBuilder.withFormFieds(templateDto.getTemplateFields())
 *                          .withBarcodeFieds(templateDto.getBarCodeFields())
 *                          .withTemplate(templateDoc)
 *                          .buildAndAppendToMainTemplate();
 *                          
 *  String pdfFilePath = pdfBuilder.save();
 *                                    
 * @author Said Ramli
 */
public class PDFBuilder {
    
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(PDFBuilder.class);
    
    /** The target dir path. */
    private final String targetDirPath;
    
    /** The pdf merger utility. */
    private PDFMergerUtility pdfMergerUtility;
    
    /** The document name prefix. */
    private String documentNamePrefix;
    
    /** The map form fields. */
    private Map<String, String> mapFormFields;
    
    /** The current template doc. */
    private PDDocument currentTemplateDoc;
    
    /** The main template doc. */
    private PDDocument mainTemplateDoc;
    
    /** The bar code fields. */
    private List<String> barCodeFields;
   

    /**
     * Instantiates a new PDF builder.
     *
     * @param targetDirPath the target dir path
     * @param documentNamePrefix the contarct name prefix
     * @param mainTemplateDoc the main template doc
     */
    private PDFBuilder(String targetDirPath, String documentNamePrefix, PDDocument mainTemplateDoc) {
        this.targetDirPath = targetDirPath;
        this.mainTemplateDoc = mainTemplateDoc;
        this.documentNamePrefix = documentNamePrefix;
        this.initPdfMergeUtilty();
     }
    
    /**
     * New instance.
     *
     * @param targetDirPath the target dir path
     * @param documentNamePrefix the contarct name prefix
     * @param mainTemplateDoc the main template doc
     * @return the PDF builder
     */
    public static PDFBuilder newInstance(String targetDirPath, String documentNamePrefix, PDDocument mainTemplateDoc) {
        return new PDFBuilder(targetDirPath, documentNamePrefix, mainTemplateDoc);
    }

    /**
     * Inits the pdf merge utilty.
     */
    private void initPdfMergeUtilty() {
        pdfMergerUtility = new PDFMergerUtility();
    }

    /**
     * Append to main template.
     *
     * @return the PDF builder
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private PDFBuilder appendToMainTemplate() throws IOException {
        this.pdfMergerUtility.appendDocument(this.mainTemplateDoc, this.currentTemplateDoc);
        return this;
    }

    /**
     * With form fieds.
     *
     * @param mapFormFields the map form fields
     * @return the PDF builder
     */
    public PDFBuilder withFormFieds(Map<String, String> mapFormFields) {
        this.mapFormFields = mapFormFields;
        return this;
    }
    
    /**
     * With template.
     *
     * @param templateDoc the template doc
     * @return the PDF builder
     */
    public PDFBuilder withTemplate(PDDocument templateDoc) {
        this.currentTemplateDoc = templateDoc;
        return this;
    }

    /**
     * New pdf file path.
     *
     * @return the string
     */
    private String newPdfFilePath() {
        return this.targetDirPath.concat(File.separator).concat(documentNamePrefix).concat(".pdf");
    }
    
    /**
     * Builds the and append to main template.
     *
     * @throws Exception the exception
     * @param flattened to flatten (make it read-only) to document
     */
    public void buildAndAppendToMainTemplate(boolean flattened) throws Exception {
        this.build(flattened);
        this.appendToMainTemplate();
    }
    
    /**
     * Builds the.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @param flattened to flatten (make it read-only) to document
     */
    private void build(boolean flattened) throws IOException  {
        PDDocumentCatalog docCatalog = currentTemplateDoc.getDocumentCatalog();
        PDAcroForm acroForm = docCatalog.getAcroForm();
        if (MapUtils.isNotEmpty(this.mapFormFields)) {
            for (String key : this.mapFormFields.keySet()) {
                setFieldValue(acroForm, key, this.mapFormFields.get(key));
            }
        }
        if(flattened && acroForm != null) {
            acroForm.flatten();
        }
    }

    /**
     * Save.
     *
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String save() throws IOException {
        String pdfFilePath  = newPdfFilePath();
        this.mainTemplateDoc.save(pdfFilePath);
        return pdfFilePath;
    }

    /**
     * Sets the field value.
     *
     * @param acroForm the acro form
     * @param key the key
     * @param value the value
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void setFieldValue(PDAcroForm acroForm, String key, String value) throws IOException {
        try {
            PDField pdField = acroForm.getField(key);
            if (pdField != null) {
                if (this.barCodeFields.contains(key)) {
                    this.insertBarCodeImage(value, pdField);
                } else {
                    pdField.setValue(value);
                }
            }
        } catch (Exception e) {
            LOG.error("Error settign field value : field = {}, value = {} , error = {} ", key, value, e.getMessage());
        }
    }
    
    /**
     * Insert bar code image.
     *
     * @param barCode the bar code
     * @param pdField the pd field
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void insertBarCodeImage(String barCode,PDField pdField) throws IOException {
        PDImageXObject pdImageXObject = generateBarCodeImage(barCode, pdField);
        setImageFieldValue(pdField, pdImageXObject);
    }

    /**
     * Sets the image field value.
     *
     * @param pdField the pd field
     * @param pdImageXObject the pd image X object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void setImageFieldValue(PDField pdField, PDImageXObject pdImageXObject) throws IOException {
        
        List<PDAnnotationWidget> listWidget = pdField.getWidgets();
        
        if(pdImageXObject != null && CollectionUtils.isNotEmpty(listWidget)) {
            for (PDAnnotationWidget widget : listWidget) {
                for (int p = 0; p < this.currentTemplateDoc.getNumberOfPages(); p++) {
                    List<PDAnnotation> annotations = this.currentTemplateDoc.getPage(p).getAnnotations();
                    for (PDAnnotation ann : annotations) {
                        if (ann.getCOSObject() == widget.getCOSObject()) {
                            LOG.debug(" setImageFieldValue, field : {} FOUND in page nbr : {} " , pdField.getFullyQualifiedName() , p);
                            PDPage page = this.currentTemplateDoc.getPages().get(p);
                            this.drawImageInFieldRectangle(pdImageXObject, ann.getRectangle(), page); 
                        }
                    }
                }
            }
        }
    }

    /**
     * Draw image in field rectangle.
     *
     * @param pdImageXObject the pd image X object
     * @param fieldRectangle the field rectangle
     * @param page the page
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void drawImageInFieldRectangle(PDImageXObject pdImageXObject, PDRectangle fieldRectangle, PDPage page) throws IOException {
        try (PDPageContentStream contents = new PDPageContentStream(this.currentTemplateDoc, page, PDPageContentStream.AppendMode.APPEND, true)) {

            float height = fieldRectangle.getHeight();
            float width =  fieldRectangle.getWidth(); 
            float x = fieldRectangle.getLowerLeftX();
            float y = fieldRectangle.getLowerLeftY();
            
            //Drawing the image in the PDF document
            contents.drawImage(pdImageXObject, x, y, width, height);
        }
    }
    
    /**
     * Generate bar code image.
     *
     * @param barCode the bar code
     * @param pdField the pd field
     * @return the PD image X object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private PDImageXObject generateBarCodeImage(String barCode,PDField pdField) throws IOException {
        try {
            Code128Bean code128Bean = new Code128Bean();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(150, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code128Bean.generateBarcode(canvas, barCode);
            canvas.finish();

            BufferedImage bufferedImage = canvas.getBufferedImage();
            return JPEGFactory.createFromImage(this.currentTemplateDoc, bufferedImage);

        } catch (IOException e) {
            LOG.error("error generateBarCodeImage : code = {}, value = {} , pdField = {} ", barCode, pdField, e.getMessage());
            throw e;
        }
    }
    
    /**
     * With barcode fieds.
     *
     * @param barCodeFields the bar code fields
     * @return the PDF builder
     */
    public PDFBuilder withBarcodeFieds(List<String> barCodeFields) {
        if (barCodeFields == null) {
            this.barCodeFields = new ArrayList<>();
        } else {
            this.barCodeFields = barCodeFields;
        }
        return this;
    }
}
