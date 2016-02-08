package org.meveocrm.services.dwh;

import javax.ejb.Stateless;

import org.meveo.service.base.BusinessService;
import org.meveocrm.model.dwh.Chart;

@Stateless
public class ChartService<T extends Chart> extends
BusinessService<T> {

}
