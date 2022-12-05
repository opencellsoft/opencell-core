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

package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2
 **/
@Named
@ConversationScoped
public class OfferTemplateCategoryListBean extends OfferTemplateCategoryBean {

	private static final long serialVersionUID = 5774070245274693692L;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;
	
	private TreeNode treeCategories;
	private List<OfferTemplateCategory> offerTemplateCategories;
	private TreeNode[] selectedCategories;

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
	
	public void initTreeCategories(List<OfferTemplateCategory> offerTemplateCategories) {
        List<TreeNode> selectedCategories = null;
        this.offerTemplateCategories = offerTemplateCategories;
        if (offerTemplateCategories != null && !offerTemplateCategories.isEmpty()) {
            selectedCategories = new ArrayList<TreeNode>();
        }
        
        TreeNode root = new DefaultTreeNode(new OfferTemplateCategory(), null);
        List<OfferTemplateCategory> roots = offerTemplateCategoryService.findRoots();

        if (CollectionUtils.isNotEmpty(roots)) {
            for (OfferTemplateCategory tree : roots) {
                createTree(tree, root, selectedCategories);
            }
        }
        
        if(selectedCategories  != null) { 
            this.selectedCategories = new TreeNode [selectedCategories.size()];
            this.selectedCategories = selectedCategories.toArray(this.selectedCategories);
        }
        this.treeCategories = root;
    }
	
	private TreeNode createTree(OfferTemplateCategory offerTemplateCategory, TreeNode rootNode, List<TreeNode> selectedCategories) {
        TreeNode newNode = new DefaultTreeNode(offerTemplateCategory, rootNode);
        if (this.offerTemplateCategories != null && !this.offerTemplateCategories.isEmpty()) {
            if(offerTemplateCategories.contains(offerTemplateCategory)) {
                selectedCategories.add(newNode);
                newNode.setSelected(true);
            }
        }
        //newNode.setExpanded(true);
        if (offerTemplateCategory.getChildren() != null) {

            List<OfferTemplateCategory> subTree = offerTemplateCategory.getChildren();
            if (CollectionUtils.isNotEmpty(subTree)) {
                for (OfferTemplateCategory child : subTree) {
                    createTree(child, newNode, selectedCategories);
                }
            }
        }
        return newNode;
    }
	
	public void onNodeCategorySelect(NodeSelectEvent event) {
	    if (this.offerTemplateCategories == null) {
            this.offerTemplateCategories = new ArrayList<>();
        }
	    OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory)event.getTreeNode().getData();
        if(!this.offerTemplateCategories.contains(offerTemplateCategory)){
            this.offerTemplateCategories.add(offerTemplateCategory);
        }
    }
 
	public void onNodeCategoryUnselect(NodeUnselectEvent event) {
        if (this.offerTemplateCategories == null) {
            this.offerTemplateCategories = new ArrayList<>();
        }
        OfferTemplateCategory offerTemplateCategory = (OfferTemplateCategory)event.getTreeNode().getData();
        if(this.offerTemplateCategories.contains(offerTemplateCategory)){
            this.offerTemplateCategories.remove(offerTemplateCategory);
        }
    }
	
	public TreeNode getTreeCategories() {
        return treeCategories;
    }

    public void setTreeCategories(TreeNode treeCategories) {
        this.treeCategories = treeCategories;
    }

    public TreeNode[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(TreeNode[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }
	
	
}
