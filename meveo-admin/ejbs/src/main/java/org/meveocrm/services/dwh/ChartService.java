package org.meveocrm.services.dwh;

import javax.ejb.Stateless;

import org.meveo.model.dwh.Chart;
import org.meveo.service.base.BusinessService;

@Stateless
public class ChartService<T extends Chart> extends
BusinessService<T> {

}
