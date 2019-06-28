package org.meveo.model;

/**
 * @author Edward P. Legaspi
 **/
public enum ImageMimeTypeEnum {

    JPG("image/jpg", "jpg"), JPEG("image/jpeg", "jpg"), GIF("image/gif", "gif"), PNG("image/png", "png");

    private String type;
    private String extension;

    private ImageMimeTypeEnum(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public static ImageMimeTypeEnum isValidMimeType(String type) {
        for (ImageMimeTypeEnum e : ImageMimeTypeEnum.values()) {
            if (e.getType().equals(type)) {
                return e;
            }
        }

        return null;
    }

}
