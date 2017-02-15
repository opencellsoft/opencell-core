/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.meveo.model.wf.WFDecisionRule;
import org.meveo.service.wf.WFDecisionRuleService;

@FacesConverter("wfDecisionRuleConverter")
public class WFDecisionRuleConverter implements Converter {

    @Inject
    private WFDecisionRuleService wFDecisionServiceRule;

	@Override
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            Long id = Long.parseLong(value);
            return wFDecisionServiceRule.findById(id);
        } else {
            return null;
        }
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent uic, Object o) {
	    if (o == null) {
            return null;
        }
        return String.valueOf(((WFDecisionRule) o).getId());
	}
}
