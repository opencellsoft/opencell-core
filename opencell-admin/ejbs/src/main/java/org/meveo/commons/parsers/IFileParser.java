package org.meveo.commons.parsers;

import java.io.File;

/**
 * Flat file parser interface
 */
public interface IFileParser {

    public void setDataFile(File file);

    public void setMappingDescriptor(String mappingDescriptor);

    public void setDataName(String dataName);

    public void parsing() throws Exception;

    /**
     * Return a next record read from a file. Any failure to read or parse a record is recorded in RecordContext.rejectReason field.
     * 
     * @return A next record read from a file or NULL if end of file was reached
     */
    public RecordContext getNextRecord();

    public void close();
}