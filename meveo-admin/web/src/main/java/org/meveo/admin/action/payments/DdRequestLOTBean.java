/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
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
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link DDRequestLOT} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class DdRequestLOTBean extends BaseBean<DDRequestLOT> {

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

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DdRequestLOTBean() {
		super(DDRequestLOT.class);
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
					getCurrentProvider());
			messages.info(new BundleKey("messages",
					"ddrequestLot.generateFileSuccessful"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages",
					"ddrequestLot.generateFileFailed"));
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
					getCurrentProvider());
			messages.info(new BundleKey("messages",
					"ddrequestLot.doPaymentsSuccessful"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.info(new BundleKey("messages",
					"ddrequestLot.doPaymentsFailed"));
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
					getCurrentProvider());
			messages.info(new BundleKey("messages",
					"ddrequestLot.launchProcessSuccessful"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.info(new BundleKey("messages",
					"ddrequestLot.launchProcessFailed"));
			messages.info(e.getMessage());
		}
		return null;
	}

	@Override
	public String getNewViewName() {
		return "ddrequestLotDetail";
	}

	@Override
	protected String getListViewName() {
		return "ddrequestLots";
	}

	@Override
	public String getEditViewName() {
		return "ddrequestLotDetail";
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

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}
}
