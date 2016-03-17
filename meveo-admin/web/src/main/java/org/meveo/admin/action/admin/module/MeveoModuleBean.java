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
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.MeasurableQuantity;
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
        root = new DefaultTreeNode("Root");
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
        if (module.getModuleItems() == null) {
            return module;
        }

        List<MeveoModuleItem> itemsToRemove = new ArrayList<MeveoModuleItem>();

        for (MeveoModuleItem item : module.getModuleItems()) {

            // Load an entity related to a module item. If it was not been able to lead (e.g. was deleted), mark it to be deleted and delete
            meveoModuleService.loadModuleItem(item, getCurrentProvider());

            if (item.getItemEntity() == null) {
                itemsToRemove.add(item);
                continue;
            }

            TreeNode classNode = getOrCreateNodeByClass(item.getItemClass());
            new DefaultTreeNode("item", item, classNode);

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

    public void setCustomEntity(CustomEntityTemplate itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
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

    public void setCustomField(CustomFieldTemplate itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public ScriptInstance getScript() {
        return script;
    }

    public void setScript(ScriptInstance itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public JobInstance getJob() {
        return job;
    }

    public void setJob(JobInstance itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public void removeTreeNode(TreeNode node) {
        MeveoModuleItem item = (MeveoModuleItem) node.getData();
        TreeNode parent = node.getParent();
        parent.getChildren().remove(node);
        if (parent.getChildCount() == 0) {
            parent.getParent().getChildren().remove(parent);
        }
        entity.removeItem(item);
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
            filename.replaceAll(" ","_");            
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
    @ActionMethod
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

    public void setMeveoModule(MeveoModule itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public LazyDataModel<MeveoModule> getSubModules() {

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

    public MeasurableQuantity getMeasurableQuantity() {
        return measurableQuantity;
    }

    public void setMeasurableQuantity(MeasurableQuantity itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart itemEntity) {
        if (itemEntity != null) {
            MeveoModuleItem item = new MeveoModuleItem(itemEntity);
            if (!entity.getModuleItems().contains(item)) {
                entity.addModuleItem(item);
                new DefaultTreeNode("item", item, getOrCreateNodeByClass(itemEntity.getClass().getName()));
            }
        }
    }

    private TreeNode getOrCreateNodeByClass(String classname) {
        for (TreeNode node : root.getChildren()) {
            if (classname.equals(node.getType())) {
                return node;
            }
        }

        TreeNode node = new DefaultTreeNode(classname, ReflectionUtils.getHumanClassName(classname), root);
        node.setExpanded(true);
        return node;
    }
}