package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.util.MeveoJpaForJobs;

/**
 * @author Edward P. Legaspi
 * @since Oct 15, 2013
 **/
public abstract class BaseApi {

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	protected List<String> missingParameters = new ArrayList<String>();

	protected String getMissingParametersExceptionMessage() {
		StringBuilder sb = new StringBuilder(
				"The following parameters are required ");
		List<String> missingFields = new ArrayList<String>();

		if (missingParameters != null) {
			for (String param : missingParameters) {
				missingFields.add(param);
			}
		}

		if (missingFields.size() > 1) {
			sb.append(org.apache.commons.lang.StringUtils.join(
					missingFields.toArray(), ", "));
		} else {
			sb.append(missingFields.get(0));
		}
		sb.append(".");

		return sb.toString();
	}

}
