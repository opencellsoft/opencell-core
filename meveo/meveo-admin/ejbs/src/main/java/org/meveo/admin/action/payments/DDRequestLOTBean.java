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
package org.meveo.admin.action.payments;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.DDRequestLOTService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.service.payments.impl.RecordedInvoiceService;

/**
 * Standard backing bean for {@link DDRequestLOT} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Tyshan(tyshan@manaty.net)
 */
@Named
@ConversationScoped
public class DDRequestLOTBean extends BaseBean<DDRequestLOT> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link DDRequestLOT} service. Extends {@link PersistenceService}
	 * .
	 */
	@Inject
	private DDRequestLOTService ddrequestLOTService;

	@Inject
	private DDRequestLotOpService ddrequestLotOpService;

	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	/**
	 * startDueDate parameter for ddRequest batch
	 */
	private Date startDueDate;
	/**
	 * endDueDate parameter for ddRequest batch
	 */
	private Date endDueDate;

	@Inject
	@CurrentProvider
	private Provider currentProvider;
	
	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DDRequestLOTBean() {
		super(DDRequestLOT.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("ddrequestLOT")
	public DDRequestLOT init() {
		return initEntity();
	}


	/**
	 * Regenerate file from entity DDRequestLOT
	 * 
	 * @return
	 */
	public String generateFile() {
		try {
			DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
			ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.FILE);
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
			ddrequestLotOp.setDdrequestLOT(entity);
			ddrequestLotOpService.create(ddrequestLotOp, getCurrentUser(),
					currentProvider);
			messages.info(new BundleKey("messages", "ddrequestLot.generateFileSuccessful"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.error(new BundleKey("messages", "ddrequestLot.generateFileFailed"));
		}

		return null;
	}

	/**
	 * Do payment for eatch invoice included in DDRequest File
	 * 
	 * @return
	 */
	public String doPayments() {
		try {
			DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
			ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.PAYMENT);
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
			ddrequestLotOp.setDdrequestLOT(entity);
			ddrequestLotOpService.create(ddrequestLotOp, getCurrentUser(),
			    currentProvider);
			messages.info(new BundleKey("messages", "ddrequestLot.doPaymentsSuccessful"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.info(new BundleKey("messages", "ddrequestLot.doPaymentsFailed"));
		}

		return null;
	}

	/**
	 * Launch DDRequestLOT process
	 * 
	 * @return
	 */
	public String launchProcess() {
		try {
			DDRequestLotOp ddrequestLotOp = new DDRequestLotOp();
			ddrequestLotOp.setFromDueDate(getStartDueDate());
			ddrequestLotOp.setToDueDate(getEndDueDate());
			ddrequestLotOp.setStatus(DDRequestOpStatusEnum.WAIT);
			ddrequestLotOp.setDdrequestOp(DDRequestOpEnum.CREATE);
			ddrequestLotOpService.create(ddrequestLotOp, getCurrentUser(),
			    currentProvider);
			messages.info(new BundleKey("messages", "ddrequestLot.launchProcessSuccessful"));
		} catch (Exception e) {
			e.printStackTrace();
			messages.info(new BundleKey("messages", "ddrequestLot.launchProcessFailed"));
			messages.info(e.getMessage());
		}
		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<DDRequestLOT> getPersistenceService() {
		return ddrequestLOTService;
	}

	/**
	 * @param startDueDate
	 *            the startDueDate to set
	 */
	public void setStartDueDate(Date startDueDate) {
		this.startDueDate = startDueDate;
	}

	/**
	 * @return the startDueDate
	 */
	public Date getStartDueDate() {
		return startDueDate;
	}

	/**
	 * @param endDueDate
	 *            the endDueDate to set
	 */
	public void setEndDueDate(Date endDueDate) {
		this.endDueDate = endDueDate;
	}

	/**
	 * @return the endDueDate
	 */
	public Date getEndDueDate() {
		return endDueDate;
	}

	@Override
	public String back() {
		return "/pages/payments/ddrequestLot/ddrequestLots.xhtml";
	}

	public PaginationDataModel<RecordedInvoice> getInvoices() {
		PaginationDataModel<RecordedInvoice> invoices = new PaginationDataModel<RecordedInvoice>(
				recordedInvoiceService);
		Map<String, Object> filters2 = new HashMap<String, Object>();
		filters2.put("ddRequestLOT", entity);
		invoices.addFilters(filters2);
		invoices.addFetchFields(getListFieldsToFetch());
		invoices.forceRefresh();
		return invoices;
	}
}
