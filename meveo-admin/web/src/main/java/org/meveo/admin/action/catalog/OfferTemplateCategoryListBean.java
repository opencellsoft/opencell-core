package org.meveo.admin.action.catalog;

import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class OfferTemplateCategoryListBean extends OfferTemplateCategoryBean {

	private static final long serialVersionUID = 5774070245274693692L;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Override
	public String getEditViewName() {
		return "offerTemplateCategoryDetail";
	}

	public TreeNode getHierarchy() {
		List<OfferTemplateCategory> result = offerTemplateCategoryService.listAllRootsExceptId(null);

		TreeNode root = new DefaultTreeNode(new OfferTemplateCategory(), null);
		for (OfferTemplateCategory a : result) {
			if (a.getOrderLevel() == 1) {
				TreeNode level1 = new DefaultTreeNode(a, root);
				level1.setExpanded(true);
				if (a.getChildren() != null && a.getChildren().size() > 0) {
					for (OfferTemplateCategory b : a.getChildren()) {
						TreeNode level2 = new DefaultTreeNode(b, level1);
						level2.setExpanded(true);
						if (b.getChildren() != null && b.getChildren().size() > 0) {
							for (OfferTemplateCategory c : b.getChildren()) {
								new DefaultTreeNode(c, level2);
							}
						}
					}
				}
			}
		}

		return root;
	}
}
