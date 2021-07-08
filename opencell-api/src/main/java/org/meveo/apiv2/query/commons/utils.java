package org.meveo.apiv2.query.commons;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class utils {

	public static String convertCsvReportToJson(File input, String separator,boolean hasHeader) {
		try {	  
		  CsvSchema csvSchema = CsvSchema.builder().setUseHeader(hasHeader).setColumnSeparator(separator.charAt(0)).build();
		    CsvMapper csvMapper = new CsvMapper();
		    csvMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		    
		    List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(input).readAll();

		    ObjectMapper mapper = new ObjectMapper();
		    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll);
	      } catch(Exception e) {
	         e.printStackTrace();
	      }
		return null;
	}
}
