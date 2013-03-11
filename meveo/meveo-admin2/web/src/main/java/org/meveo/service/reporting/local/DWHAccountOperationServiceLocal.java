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
package org.meveo.service.reporting.local;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.meveo.model.datawarehouse.DWHAccountOperation;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Account Operation Transformation service interface.
 * 
 */
@Local
public interface DWHAccountOperationServiceLocal extends IPersistenceService<DWHAccountOperation> {

    public BigDecimal calculateRecordsBetweenDueMonth(String providerCode,Integer from, Integer to,String category);
    
    public int countRecordsBetweenDueMonth(String providerCode,Integer from, Integer to,String category);

    public BigDecimal totalAmount(String providerCode,String category);

    public int totalCount(String providerCode,String category);

    public List<DWHAccountOperation> getAccountingDetailRecords(String providerCode,Date endDate);

    public List<Object> getAccountingSummaryRecords(String providerCode,Date endDate, int category);
    
    public List<Object> getObjectsForSIMPAC(String providerCode,Date startDate, Date endDate);

	public List<DWHAccountOperation> getAccountingJournalRecords(String providerCode,Date startDate,Date endDate);
}
