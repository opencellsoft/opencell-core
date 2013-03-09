/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.admin.report;

import java.util.List;

import javax.ejb.Local;

/**
 * This interface should be implemented by class which generates XML file with
 * date for report
 * 
 * @author Gediminas Ubartas
 * @created 2010.10.21
 * 
 */
@Local
public interface ReportDBSourceProducer {
    
    /**
     * Generates current XML DS for report
     * 
     * @param objectList
     *            list of objects to include in XML DS
     * @return String of XML DS
     */
    public String generateXmlString(List<Object> objectList);

    /**
     * Returns query String for current report
     */
    public String getQuery();
}
