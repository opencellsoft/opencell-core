package org.meveo.apiv2.media;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;


public class MediaFile {
    public enum LevelEnum{
        OFFER_TEMPLATE, PRODUCT, QUOTE
    }

    @FormParam("uploadedFile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] data;

    @FormParam("fileName")
    @PartType(MediaType.TEXT_PLAIN)
    private String fileName;

    @FormParam("level")
    @PartType(MediaType.TEXT_PLAIN)
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
