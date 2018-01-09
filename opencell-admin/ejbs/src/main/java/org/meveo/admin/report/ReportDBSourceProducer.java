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
package org.meveo.admin.report;

import java.util.List;

import javax.ejb.Local;

/**
 * This interface should be implemented by class which generates XML file with
 * date for report
 * 
 * @author Gediminas Ubartas
 * 
 */
@Local
public interface ReportDBSourceProducer {

    /**
     * Generates current XML DS for report.
     * 
     * @param objectList list of objects to include in XML DS
     * @return String of XML DS
     */
    public String generateXmlString(List<Object> objectList);

    /**
     * Returns query String for current report.
     * @return query.
     */
    public String getQuery();
}
