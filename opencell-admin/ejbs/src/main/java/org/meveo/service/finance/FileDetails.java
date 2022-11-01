package org.meveo.service.finance;

public class FileDetails {

    private String fileName;
    private int size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FileDetails(String fileName, int size) {
        this.fileName = fileName;
        this.size = size;
    }
}
