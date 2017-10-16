package org.meveo.admin.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;

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

    public static void add(String pdfFileName, String text, String imagePath) {
        PdfReader reader = null;
        PdfStamper pdfStamper = null;
        PdfContentByte over = null;
        PdfGState gs = null;
        try {
            byte[] pdfBytes = IOUtils.toByteArray(new FileInputStream(new File(pdfFileName)));

            reader = new PdfReader(pdfBytes);
            pdfStamper = new PdfStamper(reader, new FileOutputStream(pdfFileName));
            Image image = null;
            if (imagePath != null) {
                Image.getInstance(imagePath);
            }

            gs = new PdfGState();
            gs.setFillOpacity(0.5f);
            Document document = new Document(PageSize.A4);
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                if (image != null) {
                    PdfContentByte content = pdfStamper.getUnderContent(i);
                    image.setAbsolutePosition(150f, 750f);
                    content.addImage(image);
                } else {
                    over = pdfStamper.getOverContent(i);
                    over.setGState(gs);
                    over.beginText();
                    over.setTextMatrix(document.top(), document.bottom());
                    over.setFontAndSize(bf, 100);
                    over.setColorFill(Color.GRAY);
                    over.showTextAligned(Element.ALIGN_CENTER, text, document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, 45);
                    over.endText();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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