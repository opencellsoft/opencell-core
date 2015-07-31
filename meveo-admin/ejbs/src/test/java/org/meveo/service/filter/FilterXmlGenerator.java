package org.meveo.service.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.meveo.model.filter.Filter;
import org.meveo.model.filter.FilterSelector;
import org.meveo.model.filter.NativeFilterCondition;
import org.meveo.model.filter.OrderCondition;
import org.meveo.model.filter.Projector;

import com.thoughtworks.xstream.XStream;

/**
 * @author Edward P. Legaspi
 **/
public class FilterXmlGenerator {

	public static void main(String[] args) {
		FilterXmlGenerator fg = new FilterXmlGenerator();
		String result = fg.generate();
		fg.degenerate(result);
	}

	public void degenerate(String input) {
		System.out.println(input);
		try {
			XStream xStream = new XStream();
			Filter filter = (Filter) xStream.fromXML(input);
			System.out.println(filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String generate() {
		Projector projector = new Projector();
		projector.setTargetEntity("org.meveo.model.billing.BillingAccount");
		projector.setDisplayFields(Arrays.asList("id", "code", "status"));
		projector.setExportFields(Arrays.asList("id", "code", "status"));
		projector.setExportFields(Arrays.asList("version"));

		NativeFilterCondition nativeFilterCondition = new NativeFilterCondition();
		nativeFilterCondition.setEl("#{ba.code eq 'BA1'}");
		nativeFilterCondition.setJpql("ba.code='BA1'");

		OrderCondition orderCondition = new OrderCondition();
		orderCondition.setAscending(true);
		orderCondition.setFieldNames(Arrays.asList("id", "code"));

		List<FilterSelector> filterSelectors = new ArrayList<>();

		FilterSelector filterSelector1 = new FilterSelector();
		filterSelector1.setTargetEntity("org.meveo.model.billing.CustomerAccount");
		filterSelector1.setAlias("ca");

		filterSelectors.add(filterSelector1);

		FilterSelector filterSelector2 = new FilterSelector();
		filterSelector2.setTargetEntity("org.meveo.model.billing.CustomerAccount");
		filterSelector2.setAlias("ca");

		filterSelectors.add(filterSelector2);

		Filter filter = new Filter();
		filter.setFilterCondition(nativeFilterCondition);
		filter.setOrderCondition(orderCondition);
		filter.setSecondarySelectors(filterSelectors);
		filter.setPrimarySelector(filterSelector1);

		try {
			XStream xStream = new XStream();
			// xStream.aliasPackage("", "org.meveo.filter");
			return xStream.toXML(filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

}
