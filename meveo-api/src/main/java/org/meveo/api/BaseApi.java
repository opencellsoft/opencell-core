package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Oct 15, 2013
 **/
public abstract class BaseApi {
	 protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected List<String> missingParameters = new ArrayList<String>();

	protected String getMissingParametersExceptionMessage() {

		if (missingParameters == null) {
			missingParameters = new ArrayList<String>();
		}

		StringBuilder sb = new StringBuilder("The following parameters are required ");
		List<String> missingFields = new ArrayList<String>();

		if (missingParameters != null) {
			for (String param : missingParameters) {
				missingFields.add(param);
			}
		}

		if (missingFields.size() > 1) {
			sb.append(org.apache.commons.lang.StringUtils.join(missingFields.toArray(), ", "));
		} else {
			sb.append(missingFields.get(0));
		}
		sb.append(".");

		missingParameters = new ArrayList<String>();

		return sb.toString();
	}

}
