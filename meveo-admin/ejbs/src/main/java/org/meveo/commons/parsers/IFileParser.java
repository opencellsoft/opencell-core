package org.meveo.commons.parsers;

import java.io.File;

/**
 * 
 *
 *
 */
public interface IFileParser {

	public void setDataFile(File file);
	public void setMappingDescriptor(String mappingDescriptor);
	public void setDataName(String dataName);
	public void parsing()throws  Exception;
	public boolean hasNext()throws Exception;
	public Object getNextRecord() throws RecordRejectedException;
	public void close();
	//TODO : add more stuff like line context, errors....
}
