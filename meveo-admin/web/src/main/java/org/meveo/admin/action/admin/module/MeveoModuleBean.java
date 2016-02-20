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
package org.meveo.admin.action.admin.module;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.MeveoModuleItem;
import org.meveo.model.admin.ModuleItemTypeEnum;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.notification.GenericNotificationService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

/**
 * Meveo module bean
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 */

@Named
@ViewScoped
public class MeveoModuleBean extends BaseBean<MeveoModule> {

    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link MeveoModule} service. Extends {@link PersistenceService}.
     */
    @Inject
    private MeveoModuleService meveoModuleService;
    @Inject
    private CustomEntityTemplateService customEntityTemplateService;
    @Inject
    private FilterService filterService;
    @Inject
    private ScriptInstanceService scriptInstanceService;
    @Inject
    private JobInstanceService jobInstanceService;
    @Inject
    private GenericNotificationService notificationService;
    @Inject
    private MeasurableQuantityService measurableQuantityService;
    @Inject
    private ChartService<? extends Chart> chartService;

    private CustomEntityTemplate customEntity;
    private CustomFieldTemplate customField;
    private Filter filter;
    private ScriptInstance script;
    private JobInstance job;
    private Notification notification;
    private MeveoModule meveoModule;
    private MeasurableQuantity measurableQuantity;
    private Chart chart;

    private TreeNode root;
    private TreeNode cetnode;
    private TreeNode cftnode;
    private TreeNode filternode;
    private TreeNode scriptnode;
    private TreeNode jobnode;
    private TreeNode notificationnode;
    private TreeNode subModuleNode;
    private TreeNode measurableQuantityNode;
    private TreeNode chartNode;

    protected MeveoInstance meveoInstance;

    private CroppedImage croppedImage;
    private String tmpPicture;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public MeveoModuleBean() {
        super(MeveoModule.class);

    }

    @PostConstruct
    public void init() {
        root = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.title", true));
        cetnode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.customEntities", true), root);
        cetnode.setExpanded(true);
        cftnode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.customFields", true), root);
        cftnode.setExpanded(true);
        filternode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.filters", true), root);
        filternode.setExpanded(true);
        scriptnode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.scriptInstances", true), root);
        scriptnode.setExpanded(true);
        jobnode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.jobInstances", true), root);
        jobnode.setExpanded(true);
        notificationnode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.notifications", true), root);
        notificationnode.setExpanded(true);
        subModuleNode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.subModules", true), root);
        subModuleNode.setExpanded(true);
        measurableQuantityNode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.measurableQuantities", true), root);
        measurableQuantityNode.setExpanded(true);
        chartNode = new DefaultTreeNode(new CustomizedModuleItem("meveoModule.charts", true), root);
        chartNode.setExpanded(true);
    }

    public MeveoInstance getMeveoInstance() {
        return meveoInstance;
    }

    public void setMeveoInstance(MeveoInstance meveoInstance) {
        this.meveoInstance = meveoInstance;
    }

    @Override
    public MeveoModule initEntity() {
        MeveoModule module = super.initEntity();
        if (module.getModuleItems() != null) {
            List<MeveoModuleItem> items = module.getModuleItems();
            Iterator<MeveoModuleItem> itr = items.iterator();
            while (itr.hasNext()) {
                MeveoModuleItem item = itr.next();
                switch (item.getItemType()) {
                case CET:
                    CustomEntityTemplate cet = customEntityTemplateService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (cet != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(cet.getCode(), cet.getDescription(), ModuleItemTypeEnum.CET), cetnode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case CFT:
                    CustomFieldTemplate cft = customFieldTemplateService.findByCodeAndAppliesTo(item.getItemCode(), item.getAppliesTo(), getCurrentProvider());
                    if (cft != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(cft.getCode(), cft.getDescription(), cft.getAppliesTo(), ModuleItemTypeEnum.CFT), cftnode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case FILTER:
                    Filter filter = filterService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (filter != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(filter.getCode(), filter.getDescription(), ModuleItemTypeEnum.FILTER), filternode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case SCRIPT:
                    ScriptInstance script = scriptInstanceService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (script != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(script.getCode(), script.getDescription(), ModuleItemTypeEnum.SCRIPT), scriptnode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case JOBINSTANCE:
                    JobInstance job = jobInstanceService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (job != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(job.getCode(), job.getDescription(), ModuleItemTypeEnum.JOBINSTANCE), jobnode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case NOTIFICATION:
                    Notification notification = notificationService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (notification != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(notification.getCode(), notification.getDescription(), ModuleItemTypeEnum.NOTIFICATION), notificationnode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case SUBMODULE:
                    MeveoModule meveoModule = meveoModuleService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (meveoModule != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(meveoModule.getCode(), meveoModule.getDescription(), ModuleItemTypeEnum.SUBMODULE), subModuleNode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case MEASURABLEQUANTITIES:
                    MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (measurableQuantity != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(measurableQuantity.getCode(), measurableQuantity.getDescription(), ModuleItemTypeEnum.MEASURABLEQUANTITIES),
                            measurableQuantityNode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                case CHART:
                    Chart chart = chartService.findByCode(item.getItemCode(), getCurrentProvider());
                    if (chart != null) {
                        new DefaultTreeNode(new CustomizedModuleItem(chart.getCode(), chart.getDescription(), ModuleItemTypeEnum.CHART), chartNode);
                    } else {
                        item.setMeveoModule(null);
                        itr.remove();
                    }
                    break;
                default:
                }
            }
        }
        return module;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<MeveoModule> getPersistenceService() {
        return meveoModuleService;
    }

    public CustomEntityTemplate getCustomEntity() {
        return customEntity;
    }

    public void setCustomEntity(CustomEntityTemplate customEntity) {
        // this.customEntity = customEntity;
        if (customEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(customEntity.getCode(), ModuleItemTypeEnum.CET);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(customEntity.getCode(), customEntity.getDescription(), ModuleItemTypeEnum.CET), cetnode);
            }

        }
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public CustomFieldTemplate getCustomField() {
        return customField;
    }

    public void setCustomField(CustomFieldTemplate customField) {
        if (customField != null) {
            MeveoModuleItem item = new MeveoModuleItem(customField.getCode(), customField.getAppliesTo(), ModuleItemTypeEnum.CFT);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(customField.getCode(), customField.getDescription(), customField.getAppliesTo(), ModuleItemTypeEnum.CFT), cftnode);
            }
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        if (filter != null) {
            MeveoModuleItem item = new MeveoModuleItem(filter.getCode(), ModuleItemTypeEnum.FILTER);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(filter.getCode(), filter.getDescription(), ModuleItemTypeEnum.FILTER), filternode);
            }
        }
    }

    public ScriptInstance getScript() {
        return script;
    }

    public void setScript(ScriptInstance script) {
        if (script != null) {
            MeveoModuleItem item = new MeveoModuleItem(script.getCode(), ModuleItemTypeEnum.SCRIPT);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(script.getCode(), script.getDescription(), ModuleItemTypeEnum.SCRIPT), scriptnode);
            }
        }
    }

    public JobInstance getJob() {
        return job;
    }

    public void setJob(JobInstance job) {
        if (job != null) {
            MeveoModuleItem item = new MeveoModuleItem(job.getCode(), ModuleItemTypeEnum.JOBINSTANCE);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(job.getCode(), job.getDescription(), ModuleItemTypeEnum.JOBINSTANCE), jobnode);
            }
        }
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        if (notification != null) {
            MeveoModuleItem item = new MeveoModuleItem(notification.getCode(), ModuleItemTypeEnum.NOTIFICATION);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(notification.getCode(), notification.getDescription(), ModuleItemTypeEnum.NOTIFICATION), notificationnode);
            }
        }
    }

    private void removeItemFromEntity(String code, String applyTo, ModuleItemTypeEnum type) {
        for (MeveoModuleItem item : entity.getModuleItems()) {
            if (type == item.getItemType()) {
                if (ModuleItemTypeEnum.CFT.equals(type)) {
                    if (code.equalsIgnoreCase(item.getItemCode()) && applyTo.equalsIgnoreCase(item.getAppliesTo())) {
                        entity.removeItem(item);
                        break;
                    }
                } else if (code.equalsIgnoreCase(item.getItemCode())) {
                    entity.removeItem(item);
                    break;
                }
            }
        }
    }

    private void removeItemFromEntity(String code, ModuleItemTypeEnum type) {
        removeItemFromEntity(code, null, type);
    }

    public void removeTreeNode(CustomizedModuleItem item) {
        if (item != null && item.getType() != null) {
            switch (item.getType()) {
            case CET:
                for (TreeNode node : cetnode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        cetnode.getChildren().remove(node);
                        log.debug("start to remove cet from entity by {}", item.getCode());
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.CET);
                        break;
                    }
                }
                break;
            case CFT:
                for (TreeNode node : cftnode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode()) && item.getAppliesTo().equals(dest.getAppliesTo())) {
                        cftnode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), item.getAppliesTo(), ModuleItemTypeEnum.CFT);
                        break;
                    }
                }
                break;
            case FILTER:
                for (TreeNode node : filternode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        filternode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.FILTER);
                        break;
                    }
                }
                break;
            case JOBINSTANCE:
                for (TreeNode node : jobnode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        jobnode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.JOBINSTANCE);
                        break;
                    }
                }
                break;
            case SCRIPT:
                for (TreeNode node : scriptnode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        scriptnode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.SCRIPT);
                        break;
                    }
                }
                break;
            case NOTIFICATION:
                for (TreeNode node : notificationnode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        notificationnode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.NOTIFICATION);
                        break;
                    }
                }
                break;
            case SUBMODULE:
                for (TreeNode node : subModuleNode.getChildren()) {
                    CustomizedModuleItem dest = (CustomizedModuleItem) node.getData();
                    if (item.getCode().equals(dest.getCode())) {
                        subModuleNode.getChildren().remove(node);
                        removeItemFromEntity(item.getCode(), ModuleItemTypeEnum.SUBMODULE);
                        break;
                    }
                }
                break;
            default:
            }
        }
    }

    public void publishModule() {

        if (meveoInstance != null) {
            log.debug("export module {} to remote instance {}", entity.getCode(), meveoInstance.getCode());
            try {
                meveoModuleService.publishModule2MeveoInstance(entity, meveoInstance, this.currentUser);
                messages.info(new BundleKey("messages", "meveoModule.publishSuccess"), entity.getCode(), meveoInstance.getCode());
            } catch (Exception e) {
                log.error("Error when export module {} to {}", entity.getCode(), meveoInstance, e);
                messages.error(new BundleKey("messages", "meveoModule.publishFailed"), entity.getCode(), meveoInstance.getCode(), (e.getMessage() == null ? e.getClass()
                    .getSimpleName() : e.getMessage()));
            }
        }
    }

    public void cropLogo() {
        try {
            String originFilename = croppedImage.getOriginalFilename();
            String formatname = originFilename.substring(originFilename.lastIndexOf(".") + 1);
            String filename = String.format("%s.%s", entity.getCode(), formatname);
            log.debug("crop module picture to {}", filename);
            String dest = ModuleUtil.getModulePicturePath(entity.getProvider().getCode()) + File.separator + filename;
            ModuleUtil.cropPicture(dest, croppedImage);
            entity.setLogoPicture(filename);
            messages.info(new BundleKey("messages", "meveoModule.cropPictureSuccess"));
        } catch (Exception e) {
            log.error("error when crop a module picture {}, info {}!", croppedImage.getOriginalFilename(), e.getMessage());
            messages.error(new BundleKey("messages", "meveoModule.cropPictureFailed"), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        log.debug("upload file={}", event.getFile().getFileName());
        String originFilename = event.getFile().getFileName();
        int formatPosition = originFilename.lastIndexOf(".");
        String formatname = null;
        if (formatPosition > 0) {
            formatname = originFilename.substring(formatPosition + 1);
        }
        if (!"JPEG".equalsIgnoreCase(formatname) && !"JPG".equalsIgnoreCase(formatname) && !"PNG".equalsIgnoreCase(formatname) && !"GIF".equalsIgnoreCase(formatname)) {
            log.debug("error picture format name for origin file {}!", originFilename);
            return;
        }
        String filename = String.format("%s.%s", getTmpFilePrefix(), formatname);
        this.tmpPicture = filename;
        InputStream in = null;
        try {
            String tmpFolder = ModuleUtil.getTmpRootPath(entity.getProvider().getCode());
            String dest = tmpFolder + File.separator + filename;
            log.debug("output original module picture file to {}", dest);
            in = event.getFile().getInputstream();
            BufferedImage src = ImageIO.read(in);
            ImageIO.write(src, formatname, new File(dest));
            messages.info(new BundleKey("messages", "meveoModule.uploadPictureSuccess"), originFilename);
        } catch (Exception e) {
            log.error("Failed to upload a picture {} for module {}, info {}", filename, entity.getCode(), e.getMessage(), e);
            messages.error(new BundleKey("messages", "meveoModule.uploadPictureFailed"), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public CroppedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(CroppedImage croppedImage) {
        this.croppedImage = croppedImage;
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }

    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return null;
    }

    private void removeModulePicture(String filename) {
        try {
            ModuleUtil.removeModulePicture(entity.getProvider().getCode(), filename);
        } catch (Exception e) {
            log.error("failed to remove module picture {}, info {}", filename, e.getMessage(), e);
        }
    }

    /**
     * clean uploaded picture
     */
    @Override
    public void delete() {
        String source = entity.getLogoPicture();
        super.delete();
        removeModulePicture(source);
    }

    /**
     * clean uploaded pictures for multi delete
     */
    @Override
    public void deleteMany() {
        List<String> files = new ArrayList<String>();
        String source = null;
        for (MeveoModule entity : getSelectedEntities()) {
            source = entity.getLogoPicture();
            files.add(source);
        }
        super.deleteMany();
        for (String file : files) {
            removeModulePicture(file);
        }
    }

    private static String getTmpFilePrefix() {
        return UUID.randomUUID().toString();
    }

    public String getTmpPicture() {
        return tmpPicture;
    }

    public void setTmpPicture(String tmpPicture) {
        this.tmpPicture = tmpPicture;
    }

    public MeveoModule getMeveoModule() {
        return meveoModule;
    }

    public void setMeveoModule(MeveoModule meveoModule) {
        if (meveoModule != null) {
            MeveoModuleItem item = new MeveoModuleItem(meveoModule.getCode(), ModuleItemTypeEnum.SUBMODULE);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(meveoModule.getCode(), meveoModule.getDescription(), ModuleItemTypeEnum.SUBMODULE), subModuleNode);
            }
        }
    }

    public LazyDataModel<MeveoModule> getSubModules() {
        log.debug("getSubModules");

        LazyDataModel<MeveoModule> result = null;
        HashMap<String, Object> filters = new HashMap<String, Object>();

        if (getEntity().isTransient()) {
            result = getLazyDataModel(filters, true);
        } else {
            filters.put("ne id", entity.getId());
            result = getLazyDataModel(filters, true);
        }

        return result;
    }

    public TreeNode getSubModuleNode() {
        return subModuleNode;
    }

    public void setSubModuleNode(TreeNode subModuleNode) {
        this.subModuleNode = subModuleNode;
    }

    public MeasurableQuantity getMeasurableQuantity() {
        return measurableQuantity;
    }

    public void setMeasurableQuantity(MeasurableQuantity measurableQuantity) {
        if (measurableQuantity != null) {
            MeveoModuleItem item = new MeveoModuleItem(measurableQuantity.getCode(), ModuleItemTypeEnum.MEASURABLEQUANTITIES);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(measurableQuantity.getCode(), measurableQuantity.getDescription(), ModuleItemTypeEnum.MEASURABLEQUANTITIES),
                    measurableQuantityNode);
            }
        }
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        if (chart != null) {
            MeveoModuleItem item = new MeveoModuleItem(chart.getCode(), ModuleItemTypeEnum.CHART);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode(new CustomizedModuleItem(chart.getCode(), chart.getDescription(), ModuleItemTypeEnum.CHART), chartNode);
            }
        }
    }

}