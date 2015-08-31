package org.meveo.api.rest;

/**
 * @author Edward P. Legaspi
 **/
public class RestSecurityTest {

	public static void main(String[] args) {
		new RestSecurityTest();
	}

	public RestSecurityTest() {
		MeveoClient restClient = new MeveoClient("http://localhost:8080/meveo",
			    "api/rest/customerAccount/", "meveo.admin", "meveo.admin");
			  restClient.addParam("customerAccountCode", "CA1");

		System.out.println("response=" + restClient.execute());
	}

}
