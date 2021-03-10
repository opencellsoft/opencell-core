package org.meveo.test.csv;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.meveo.model.catalog.OfferTemplateCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

/**
 * @author Edward P. Legaspi
 **/
public class CsvTest {

	private final String FILE_NAME = "offerTemplateCategory.csv";
	private static final Logger log = LoggerFactory.getLogger(CsvTest.class);

	public static void main(String args[]) {
		try {
			CsvTest app = new CsvTest();
			app.testCsvRead();
			app.testCsvWrite();
		} catch (Exception e) {
			log.error("error = {}", e);
		}
	}

	private void testCsvRead() throws Exception {
		System.out.println("read csv");

		// load file from resource
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(FILE_NAME).getFile());

		// configure the schema we want to read
		CsvSchema schema = CsvSchema.builder().addColumn("parentCategoryCode").addColumn("code").addColumn("name").addColumn("description").build();
		CsvMapper mapper = new CsvMapper();

		// configure the reader on what bean to read and how we want to write
		// that bean
		ObjectReader oReader = mapper.readerFor(OfferTemplateCategory.class).with(schema);

		// read from file
		try (Reader reader = new FileReader(file)) {
			MappingIterator<OfferTemplateCategory> mi = oReader.readValues(reader);
			while (mi.hasNext()) {
				System.out.println(mi.next());
			}
		}
	}

	private void testCsvWrite() throws Exception {
		// initialize our list
		List<OfferTemplateCategory> list = new ArrayList<>();
		list.add(populateOfferCat(1));
		list.add(populateOfferCat(2));
		list.add(populateOfferCat(3));

		// initialize and configure the mapper
		CsvMapper mapper = new CsvMapper();
		// we ignore unknown fields or fields not specified in schema, otherwise
		// writing will fail
		mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

		// initialize the schema
		CsvSchema schema = CsvSchema.builder().addColumn("parentCategoryCode").addColumn("code").addColumn("name").addColumn("description").build();

		// map the bean with our schema for the writer
		ObjectWriter writer = mapper.writerFor(OfferTemplateCategory.class).with(schema);

		File tempFile = new File("c://temp//output.csv");
		// we write the list of objects
		writer.writeValues(tempFile).writeAll(list);
	}

	/**
	 * Initialize an OfferTemplateCategory using index as suffix.
	 * 
	 * @param index
	 * @return
	 */
	private OfferTemplateCategory populateOfferCat(int index) {
		OfferTemplateCategory o1 = new OfferTemplateCategory();
		o1.setParentCategoryCode("PARENT_" + index);
		o1.setCode("CAT_" + index);
		o1.setName("CAT_NAME_" + index);
		o1.setDescription("CAT_DESCRIPTION_" + index);

		return o1;
	}

}
