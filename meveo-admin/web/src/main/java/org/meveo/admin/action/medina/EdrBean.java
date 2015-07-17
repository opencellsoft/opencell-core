package org.meveo.admin.action.medina;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.EdrService;

@Named
@ConversationScoped
public class EdrBean extends BaseBean<EDR> {

	private static final long serialVersionUID = 7833532801870480214L;

	@Inject
	private EdrService edrService;

	public EdrBean() {
		super(EDR.class);
	}

	@Override
	public String getEditViewName() {
		return "edrDetail";
	}

	@Override
	public Map<String, Object> getFilters() {
		filters = super.getFilters();

		filters.put("status", EDRStatusEnum.REJECTED);

		return filters;
	}

	public void updateStatus(EDR selectedEdr) {
		selectedEdr.setStatus(EDRStatusEnum.OPEN);

		getPersistenceService().update(selectedEdr);
	}

	public void massUpdate() {
		if (getSelectedEntities() != null) {
			log.debug("updating {} edrs", getSelectedEntities().size());

			Set<Long> selectedIds = new HashSet<Long>();
			for (EDR edr : getSelectedEntities()) {
				selectedIds.add(edr.getId());
			}

			edrService.massUpdate(EDRStatusEnum.OPEN, selectedIds, currentProvider);

			messages.info(new BundleKey("messages", "update.successful"));
		}
	}

	@Override
	protected IPersistenceService<EDR> getPersistenceService() {
		return edrService;
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
