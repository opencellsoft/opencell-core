package org.meveo.apiv2.media;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.net.URL;


public class MediaFile {
    public enum LevelEnum{
        OFFER_TEMPLATE, PRODUCT, QUOTE
    }

    @FormParam("uploadedFile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    private String fileName;

    @FormParam("fileUrl")
    @PartType(MediaType.TEXT_PLAIN)
    private URL fileUrl;

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

    public URL getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LevelEnum getLevel() {
        return level;
    }

    public void setLevel(LevelEnum level) {
        this.level = level;
    }
}
