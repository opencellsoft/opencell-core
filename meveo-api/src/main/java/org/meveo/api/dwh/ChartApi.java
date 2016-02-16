package org.meveo.api.dwh;

import javax.ejb.Stateless;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ChartApi extends BaseApi {

	public void create(ChartDto postData, User currentUser) throws MeveoApiException {

	}

	// TODO Manu - implement RUD + List

}
