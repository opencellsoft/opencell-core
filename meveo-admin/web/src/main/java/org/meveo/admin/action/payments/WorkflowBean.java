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
package org.meveo.admin.action.payments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.custom.GroupedTransitionRule;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.admin.wf.WorkflowTypeClass;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.wf.TransitionRuleTypeEnum;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.WFTransitionRule;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WFTransitionRuleService;
import org.meveo.service.wf.WorkflowService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Workflow} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class WorkflowBean extends BaseBean<Workflow> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link Workflow} service. Extends {@link PersistenceService}.
     */
    @Inject
    private WorkflowService workflowService;

    @Inject
    private WFTransitionService wFTransitionService;

    @Inject
    private WFTransitionRuleService wFTransitionServiceRule;

    private List<String> wfTransitionRulesName;

    private List<List<WFTransitionRule>> wfTransitionRulesByName = new ArrayList<>();

    private List<GroupedTransitionRule> selectedRules;

    private List<Integer> indexRules;

    // @Produces
    // @Named
    private transient WFTransition wfTransition = new WFTransition();

    private transient WFTransitionRule newWFTransitionRule = new WFTransitionRule();

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WorkflowBean() {
        super(Workflow.class);
    }

    @Override
    public Workflow initEntity() {
        super.initEntity();
     //   PersistenceUtils.initializeAndUnproxy(entity.getActions());
        return entity;
    }

    public WFTransition getWfTransition() {
        return wfTransition;
    }

    public void setWfTransition(WFTransition wfTransition) {
        this.wfTransition = wfTransition;
    }

    public void newWfTransitionInstance() {
        this.wfTransition = new WFTransition();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return "/pages/admin/workflow/workflowDetail?workflowId=" + entity.getId() + "&faces-redirect=true&includeViewParams=true";
    }

    public void saveWfTransition() {

        try {
            List<WFTransitionRule> wfTransitionRules = new ArrayList<>();
            for (GroupedTransitionRule groupedTransitionRule : selectedRules) {
                if (groupedTransitionRule.getValue() != null && groupedTransitionRule.getValue().getModel()) {
                    WFTransitionRule wfTransitionRule = groupedTransitionRule.getValue();
                    newWFTransitionRule.setModel(Boolean.FALSE);
                    newWFTransitionRule.setConditionEl(wfTransitionRule.getConditionEl());
                    newWFTransitionRule.setName(wfTransitionRule.getName());
                    newWFTransitionRule.setType(wfTransitionRule.getType());
                    newWFTransitionRule.setProvider(entity.getProvider());
                    newWFTransitionRule.setDisabled(Boolean.FALSE);

                    if (wfTransitionRule.getType() == TransitionRuleTypeEnum.RANGE) {
                        StringBuffer value = new StringBuffer();
                        if (groupedTransitionRule.getNewValue() != null) {
                            value.append(groupedTransitionRule.getNewValue()).append("|");
                        } else {
                            value.append("|");
                        }
                        if (groupedTransitionRule.getAnotherValue() != null) {
                            value.append(groupedTransitionRule.getAnotherValue());
                        }
                        newWFTransitionRule.setValue(value.toString());
                    } else if (wfTransitionRule.getType() == TransitionRuleTypeEnum.DATE && groupedTransitionRule.getNewDate() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date value = groupedTransitionRule.getNewDate();
                        newWFTransitionRule.setValue(sdf.format(value));
                    } else {
                        newWFTransitionRule.setValue(groupedTransitionRule.getNewValue());
                    }
                    WFTransitionRule existedTransitionRule = wFTransitionServiceRule.getWFTransitionRuleByNameTypeValue(newWFTransitionRule.getName(),
                            newWFTransitionRule.getValue(), newWFTransitionRule.getType(), entity.getProvider());
                    if (existedTransitionRule != null) {
                        messages.error(new BundleKey("messages", "transitionRule.uniqueNameValueType"), new Object[]{newWFTransitionRule.getName(), newWFTransitionRule.getValue(), newWFTransitionRule.getType()});
                        return;
                    }
                    int currentPriority = wFTransitionServiceRule.getMaxPriority(groupedTransitionRule.getName(), newWFTransitionRule.getType(), entity.getProvider());
                    newWFTransitionRule.setPriority(currentPriority + 1);
                    wFTransitionServiceRule.create(newWFTransitionRule, getCurrentUser());
                    wfTransitionRules.add(newWFTransitionRule);
                    newWFTransitionRule = new WFTransitionRule();
                } else if (groupedTransitionRule.getValue() != null){
                    wfTransitionRules.add(groupedTransitionRule.getValue());
                }
            }
            if (wfTransition.getId() != null) {
                WFTransition wfTrs = wFTransitionService.findById(wfTransition.getId());
                wfTrs.setFromStatus(wfTransition.getFromStatus());
                wfTrs.setToStatus(wfTransition.getToStatus());
                wfTrs.setConditionEl(wfTransition.getConditionEl());

                if (wfTransitionRules.size() > 0) {
                    wfTrs.getWfTransitionRules().clear();
                    wfTrs.getWfTransitionRules().addAll(wfTransitionRules);
                }
                wFTransitionService.update(wfTrs, getCurrentUser());
                messages.info(new BundleKey("messages", "update.successful"));
            } else {

                for (WFTransition transition : entity.getTransitions()) {

                    if ((transition.getFromStatus().equals(wfTransition.getFromStatus()))
                            && (transition.getToStatus().equals(wfTransition.getToStatus()))) {
                        throw new BusinessEntityException();
                    }
                }
                if (wfTransitionRules.size() > 0) {
                    wfTransition.getWfTransitionRules().clear();
                    wfTransition.getWfTransitionRules().addAll(wfTransitionRules);
                }
                wfTransition.setWorkflow(entity);
                wFTransitionService.create(wfTransition, getCurrentUser());
                entity.getTransitions().add(wfTransition);
                messages.info(new BundleKey("messages", "save.successful"));
            }
        } catch (BusinessEntityException e) {
            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));

        } catch (Exception e) {
            log.error("failed to save dunning plan transition", e);

            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));
        }

        indexRules.clear();
        indexRules.add(0);
        wfTransitionRulesByName.clear();
        selectedRules.clear();

        wfTransition = new WFTransition();
    }

    public void deleteWfTransition(WFTransition dunningPlanTransition) {
        WFTransition transition = wFTransitionService.findById(dunningPlanTransition.getId()); 
        wFTransitionService.remove(transition);
        entity.getTransitions().remove(dunningPlanTransition);
        indexRules.clear();
        indexRules.add(0);
        wfTransitionRulesByName.clear();
        selectedRules.clear();
        messages.info(new BundleKey("messages", "delete.successful"));
    }

    public void editWfTransition(WFTransition dunningPlanTransition) {
        this.wfTransition = dunningPlanTransition;
        WFTransition wfTransition1 = wFTransitionService.findById(this.wfTransition.getId(), Arrays.asList("provider", "wfTransitionRules"));
        if (wfTransition1 != null && wfTransition1.getWfTransitionRules() != null) {
            Integer index = 0;
            indexRules.clear();
            wfTransitionRulesByName.clear();
            selectedRules.clear();
            for (WFTransitionRule wfTransitionRule : wfTransition1.getWfTransitionRules()) {
                indexRules.add(index);
                GroupedTransitionRule groupedTransitionRule = new GroupedTransitionRule();
                groupedTransitionRule.setName(wfTransitionRule.getName());
                groupedTransitionRule.setValue(wfTransitionRule);
                List<WFTransitionRule> list = wFTransitionServiceRule.getWFTransitionRules(wfTransitionRule.getName(), entity.getProvider());
                wfTransitionRulesByName.add(list);
                selectedRules.add(groupedTransitionRule);
                index++;
            }
        }
    }
    
    /**
     * Autocomplete method for class filter field - search entity type classes with @ObservableEntity annotation
     * 
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getClasses("org.meveo");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for a model package", e);
            return null;
        }

        List<String> classNames = new ArrayList<String>();
        for (Class clazz : classes) {
            if (clazz.isAnnotationPresent(WorkflowTypeClass.class)) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Workflow> getPersistenceService() {
        return workflowService;
    }

    @Produces
    public Workflow getDunningPlan() {
        return entity;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider", "transitions");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("provider");
    }
    
    @SuppressWarnings({ "unchecked" })
	public Map<String, String> getTransitionStatusFromWorkflowType() {
    	try {
			Class<?> clazz = Class.forName(entity.getWfType());
			Object obj = clazz.newInstance();
			Method testMethod = obj.getClass().getMethod("getStatusList");
			List<String> statusList = (List<String>) testMethod.invoke(obj);
			Map<String, String> statusMap = new TreeMap<>();
			for(String s : statusList) {
				statusMap.put(s, s);
			}
			return statusMap;
		} catch (ClassNotFoundException e) {
			log.error("unable to get class " + entity.getWfType(), e);
		} catch (InstantiationException e) {
			log.error("unable to instantiate class " + entity.getWfType(), e);
		} catch (IllegalAccessException e) {
			log.error("can not access constructor of class " + entity.getWfType(), e);
		} catch (NoSuchMethodException e) {
			log.error("unable to find getStatusList method on class " + entity.getWfType(), e);
		} catch (SecurityException e) {
			log.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			log.error("illegal arguments for getStatusList method on class " + entity.getWfType(), e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage(), e);
		}
    	return new TreeMap<>();
    }

    public List<String> getWfTransitionRulesName() {
        if (wfTransitionRulesName == null) {
            List<WFTransitionRule> perksSource = null;
            if (entity != null && entity.getProvider() != null) {
                wfTransitionRulesName = wFTransitionServiceRule.getDistinctNameWFTransitionRules(entity.getProvider());
            } else {
                wfTransitionRulesName = wFTransitionServiceRule.getDistinctNameWFTransitionRules();
            }
        }
        return wfTransitionRulesName;
    }

    public void setWfTransitionRulesName(List<String> wfTransitionRulesName) {
        this.wfTransitionRulesName = wfTransitionRulesName;
    }

    public List<List<WFTransitionRule>> getWfTransitionRulesByName() {
        return wfTransitionRulesByName;
    }

    public void setWfTransitionRulesByName(List<List<WFTransitionRule>> wfTransitionRulesByName) {
        this.wfTransitionRulesByName = wfTransitionRulesByName;
    }

    public List<Integer> getIndexRules() {
        if (indexRules == null) {
            indexRules = new ArrayList<>();
            indexRules.add(0);
        }
        return indexRules;
    }

    public void setIndexRules(List<Integer> indexRules) {
        this.indexRules = indexRules;
    }

    public void changedRuleName(Integer indexRule) {
        List<WFTransitionRule> list = wFTransitionServiceRule.getWFTransitionRules(selectedRules.get(indexRule).getName(), entity.getProvider());
        if (wfTransitionRulesByName.size() > indexRule && wfTransitionRulesByName.get(indexRule) != null) {
            wfTransitionRulesByName.remove(indexRule);
            wfTransitionRulesByName.add(indexRule, list);
        } else {
            wfTransitionRulesByName.add(indexRule, list);
        }
    }

    public void addNewRule() {
        selectedRules.add(new GroupedTransitionRule());
        indexRules.add(indexRules.size());
    }

    public void addWFTransitionRule(Integer indexRule) {

    }

    public List<GroupedTransitionRule> getSelectedRules() {
        if (selectedRules == null) {
            selectedRules = new ArrayList<>();
            selectedRules.add(new GroupedTransitionRule());
        }
        return selectedRules;
    }

    public void setSelectedRules(List<GroupedTransitionRule> selectedRules) {
        this.selectedRules = selectedRules;
    }

    public WFTransitionRule getNewWFTransitionRule() {
        return newWFTransitionRule;
    }

    public void setNewWFTransitionRule(WFTransitionRule newWFTransitionRule) {
        this.newWFTransitionRule = newWFTransitionRule;
    }
}