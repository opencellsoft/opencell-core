package org.meveo.admin.exception;

/**
 * @author Edward P. Legaspi
 **/
public class JobDoesNotExistsException extends BusinessException {

	private static final long serialVersionUID = -2859519167962774523L;

	public JobDoesNotExistsException() {

	}

	public JobDoesNotExistsException(String jobName) {
		super("Job with name=" + jobName + " does not exists.");
	}

}
