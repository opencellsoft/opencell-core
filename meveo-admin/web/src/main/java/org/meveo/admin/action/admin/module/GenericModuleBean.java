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
package org.meveo.admin.action.admin.module;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.action.catalog.ScriptInstanceBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ModuleUtil;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.module.MeveoModuleApi;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.module.MeveoModule;
import org.meveo.model.module.MeveoModuleItem;
import org.meveo.service.admin.impl.MeveoModuleService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.view.ServiceBasedLazyDataModel;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

/**
 * Meveo module bean
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * 
 */

public abstract class GenericModuleBean<T extends MeveoModule> extends BaseBean<T> {

    private static final long serialVersionUID = 8332852624069548417L;

    @Inject
    protected MeveoModuleService meveoModuleService;

    @Inject
    private MeveoModuleApi moduleApi;

    @Inject
    @ViewBean
    protected ScriptInstanceBean scriptInstanceBean;

    @Inject
    private ElasticClient elasticClient;

    private BusinessEntity moduleItemEntity;

    private TreeNode root;

    protected MeveoInstance meveoInstance;

    private CroppedImage croppedImage;
    private String tmpPicture;

    public GenericModuleBean() {
    }

    public GenericModuleBean(Class<T> clazz) {
        super(clazz);
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
    public T initEntity() {
        T module = super.initEntity();

        // If module is in being developed, show module items from meveoModule.moduleItems()
        if (!module.isDownloaded()) {
            if (module.getModuleItems() == null) {
                return module;
            }

            List<MeveoModuleItem> itemsToRemove = new ArrayList<MeveoModuleItem>();

            for (MeveoModuleItem item : module.getModuleItems()) {

                // Load an entity related to a module item. If it was not been able to load (e.g. was deleted), mark it to be deleted and delete
                meveoModuleService.loadModuleItem(item, getCurrentProvider());

                if (item.getItemEntity() == null) {
                    itemsToRemove.add(item);
                    continue;
                }

                TreeNode classNode = getOrCreateNodeByClass(item.getItemClass());
                new DefaultTreeNode("item", item, classNode);

            }
            // If module was downloaded, show module items from meveoModule.moduleSource
        } else {
            try {
                MeveoModuleDto dto = MeveoModuleService.moduleSourceToDto(module);

                if (dto.getModuleItems() == null) {
                    return module;
                }

                for (BaseDto itemDto : dto.getModuleItems()) {
                    TreeNode classNode = getOrCreateNodeByClass(itemDto.getClass().getName());
                    new DefaultTreeNode("item", itemDto, classNode);
                }

            } catch (Exception e) {
                log.error("Failed to load module source {}", module.getCode(), e);
                // throw new BusinessException("Failed to load module source", e);
            }

        }

        return module;
    }

    public BusinessEntity getModuleItemEntity() {
        return moduleItemEntity;
    }

    public void setModuleItemEntity(BusinessEntity itemEntity) {
        if (itemEntity != null && !entity.equals(itemEntity)) {
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

    // public LazyDataModel<T> getSubModules() {
    //
    // LazyDataModel<T> result = null;
    // HashMap<String, Object> filters = new HashMap<String, Object>();
    //
    // if (getEntity().isTransient()) {
    // result = getLazyDataModel(filters, true);
    // } else {
    // filters.put("ne id", entity.getId());
    // result = getLazyDataModel(filters, true);
    // }
    //
    // return result;
    // }

    public LazyDataModel<MeveoModule> getSubModules() {
        HashMap<String, Object> filters = new HashMap<String, Object>();

        if (!getEntity().isTransient()) {
            filters.put("ne id", entity.getId());
        }

        final Map<String, Object> finalFilters = filters;

        LazyDataModel<MeveoModule> meveoModuleDataModel = new ServiceBasedLazyDataModel<MeveoModule>() {

            private static final long serialVersionUID = -8167681362884293170L;

            @Override
            protected IPersistenceService<MeveoModule> getPersistenceServiceImpl() {
                return meveoModuleService;
            }

            @Override
            protected Map<String, Object> getSearchCriteria() {

                // Omit empty or null values
                Map<String, Object> cleanFilters = new HashMap<String, Object>();

                for (Map.Entry<String, Object> filterEntry : finalFilters.entrySet()) {
                    if (filterEntry.getValue() == null) {
                        continue;
                    }
                    if (filterEntry.getValue() instanceof String) {
                        if (StringUtils.isBlank((String) filterEntry.getValue())) {
                            continue;
                        }
                    }
                    cleanFilters.put(filterEntry.getKey(), filterEntry.getValue());
                }

                // cleanFilters.put(PersistenceService.SEARCH_CURRENT_USER,
                // getCurrentUser());
                cleanFilters.put(PersistenceService.SEARCH_CURRENT_PROVIDER, getCurrentProvider());
                return GenericModuleBean.this.supplementSearchCriteria(cleanFilters);
            }

            @Override
            protected String getDefaultSortImpl() {
                return getDefaultSort();
            }

            @Override
            protected SortOrder getDefaultSortOrderImpl() {
                return getDefaultSortOrder();
            }

            @Override
            protected List<String> getListFieldsToFetchImpl() {
                return getListFieldsToFetch();
            }

            @Override
            protected ElasticClient getElasticClientImpl() {
                return elasticClient;
            }

            @Override
            public User getCurrentUser() {
                return GenericModuleBean.this.getCurrentUser();
            }
        };

        return meveoModuleDataModel;
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
            filename.replaceAll(" ", "_");
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

        MeveoModule moduleDuplicate = meveoModuleService.findByCode(entity.getCode(), getCurrentProvider());
        if (moduleDuplicate != null && !moduleDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "commons.uniqueField.code"), entity.getCode());
            return null;
        }

        boolean isNew = entity.isTransient();

        super.saveOrUpdate(killConversation);

        if (isNew) {
            return getEditViewName();
        } else {
            return back();
        }
    }

    private void removeModulePicture(String filename) {
        if (filename == null) {
            return;
        }
        try {
            ModuleUtil.removeModulePicture(entity.getProvider().getCode(), filename);
        } catch (Exception e) {
            log.error("failed to remove module picture {}, info {}", filename, e.getMessage(), e);
        }
    }

    /**
     * clean uploaded picture
     */
    @ActionMethod
    @Override
    public void delete() {

        String source = entity.getLogoPicture();
        super.delete();
        if (source != null) {
            removeModulePicture(source);
        }
    }

    /**
     * clean uploaded pictures for multi delete
     */
    @ActionMethod
    @Override
    public void deleteMany() {
        List<String> files = new ArrayList<String>();
        String source = null;
        for (MeveoModule entity : getSelectedEntities()) {
            source = entity.getLogoPicture();
            if (source != null) {
                files.add(source);
            }
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

    private TreeNode getOrCreateNodeByClass(String classname) {

        classname = classname.replaceAll("Dto", "");
        classname = classname.replaceAll("DTO", "");
        for (TreeNode node : root.getChildren()) {
            if (classname.equals(node.getType())) {
                return node;
            }
        }

        TreeNode node = new DefaultTreeNode(classname, ReflectionUtils.getHumanClassName(classname), root);
        node.setExpanded(true);
        return node;
    }

    public void refreshScript() {
        entity.setScript(scriptInstanceBean.getEntity());
    }

    /**
     * Prepare to show a popup to view or edit script
     */
    public void viewEditScript() {
        if (entity.getScript() != null) {
            scriptInstanceBean.initEntity(entity.getScript().getId());
        } else {
            scriptInstanceBean.newEntity();
        }
        scriptInstanceBean.setBackViewSave(this.getEditViewName());
    }

    /**
     * Prepare to show a popup to enter new script
     */
    public void newScript() {
        scriptInstanceBean.newEntity();
        scriptInstanceBean.setBackViewSave(this.getEditViewName());
    }

    @SuppressWarnings("unchecked")
    public void install() {
        entity = (T) install(entity);
    }

    public MeveoModule install(MeveoModule module) {
        try {

            if (!module.isDownloaded()) {
                return module;

            } else if (module.isInstalled()) {
                messages.warn(new BundleKey("messages", "meveoModule.installedAlready"));
                return module;
            }

            MeveoModuleDto moduleDto = MeveoModuleService.moduleSourceToDto(module);

            module = moduleApi.install(moduleDto, currentUser);
            messages.info(new BundleKey("messages", "meveoModule.installSuccess"), moduleDto.getCode());

        } catch (Exception e) {
            log.error("Failed to install meveo module {} ", module.getCode(), e);
            messages.error(new BundleKey("messages", "meveoModule.installFailed"), module.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }

        return module;
    }

    @SuppressWarnings("unchecked")
    public void uninstall() {
        try {

            if (!entity.isDownloaded()) {
                return;

            } else if (!entity.isInstalled()) {
                messages.warn(new BundleKey("messages", "meveoModule.notInstalled"));
                return;
            }

            entity = (T) meveoModuleService.uninstall(entity, getCurrentUser());
            messages.info(new BundleKey("messages", "meveoModule.uninstallSuccess"), entity.getCode());

        } catch (Exception e) {
            log.error("Failed to uninstall meveo module {} ", entity.getCode(), e);
            messages.error(new BundleKey("messages", "meveoModule.uninstallFailed"), entity.getCode(), (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
    }
}