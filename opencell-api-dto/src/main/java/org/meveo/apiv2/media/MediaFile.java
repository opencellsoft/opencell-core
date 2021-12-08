package org.meveo.apiv2.media;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;


public class MediaFile {
    public enum LevelEnum{
        OFFER_TEMPLATE, PRODUCT, QUOTE
    }

    @FormParam("uploadedFile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @Schema(description = "The media file content en byte[]")
    private byte[] data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    @Schema(description = "The name of the media file")
    private String fileName;

    @FormParam("level")
    @PartType(MediaType.TEXT_PLAIN)
    @Schema(description = "The level to which the quote is attached (OFFER_TEMPLATE, PRODUCT, QUOTE)")
    private LevelEnum level;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LevelEnum getLevel() {
        return level;
    }

    public void setLevel(LevelEnum level) {
        this.level = level;
    }
}
