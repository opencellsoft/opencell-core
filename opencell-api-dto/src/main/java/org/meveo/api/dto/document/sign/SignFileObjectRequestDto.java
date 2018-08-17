package org.meveo.api.dto.document.sign;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO Class for a File Object for a Yousign object document.
 */
public class SignFileObjectRequestDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L; 
    
    public SignFileObjectRequestDto () {
    }
    
    public SignFileObjectRequestDto (String file) {
        this.file = file;
    }
     
     /** The position where to add the file object , it should have this pattern %d,%d,%d,%d, */
     private String position;
     
     /** The page where to add the file object */
     private int page;
     
     /** The file id created on Sousign platform */
     private String file;

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

}
