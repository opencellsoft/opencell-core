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

package org.meveo.admin.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class PdfWaterMark {
	
	private static final Logger log = LoggerFactory.getLogger(PdfWaterMark.class);

    public static void add(String pdfFileName, String text, String imagePath) {
        PdfReader reader = null;
        PdfStamper pdfStamper = null;
        PdfContentByte over = null;
        PdfGState gs = null;
        try {
            byte[] pdfBytes = IOUtils.toByteArray(new FileInputStream(new File(pdfFileName)));

            reader = new PdfReader(pdfBytes);
            pdfStamper = new PdfStamper(reader, new FileOutputStream(pdfFileName));
            if (imagePath != null) {
                Image.getInstance(imagePath);
            }

            gs = new PdfGState();
            gs.setFillOpacity(0.5f);
            Document document = new Document(PageSize.A4);
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                over = pdfStamper.getOverContent(i);
                over.setGState(gs);
                over.beginText();
                over.setTextMatrix(document.top(), document.bottom());
                over.setFontAndSize(bf, 100);
                over.setColorFill(Color.GRAY);
                over.showTextAligned(Element.ALIGN_CENTER, text, document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, 45);
                over.endText();

            }

        } catch (Exception e) {
            log.error("error = {}", e);
        } finally {
            if (over != null) {
                over.closePath();
            }
            if (pdfStamper != null) {
                try {
                    pdfStamper.close();
                } catch (Exception e) {
                }
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}