/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.wf.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.wf.WorkflowType;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;

@WorkflowTypeClass
public class DunningWF extends WorkflowType<CustomerAccount> {
	
	public DunningWF(){
		super();
	}
	
	public DunningWF(CustomerAccount ca){
		super(ca);
	}
	

    @Override
    public List<String> getStatusList() {
        // return Arrays.asList(Arrays.stream(DunningLevelEnum.values()).map(Enum::name).toArray(String[]::new));
        List<String> values = new ArrayList<String>();
        for (DunningLevelEnum dunningLevelEnum : DunningLevelEnum.values()) {
            values.add(dunningLevelEnum.name());
        }
        return values;
    }

    @Override
    public void changeStatus(String newStatus) throws BusinessException {
        entity.setDunningLevel(DunningLevelEnum.valueOf(newStatus));
    }

    @Override
    public String getActualStatus() {
        return entity.getDunningLevel().name();
    }
}