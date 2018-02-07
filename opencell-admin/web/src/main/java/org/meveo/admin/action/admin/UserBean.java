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
package org.meveo.admin.action.admin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.DetailedSecuredEntity;
import org.meveo.model.admin.SecuredEntity;
import org.meveo.model.admin.User;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.hierarchy.impl.UserHierarchyLevelService;
import org.meveo.service.security.SecuredBusinessEntityService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link User} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class UserBean extends CustomFieldBean<User> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link User} service. Extends {@link PersistenceService}. */
    @Inject
    protected UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserHierarchyLevelService userHierarchyLevelService;

    @Inject
    private SecuredBusinessEntityService securedBusinessEntityService;

    @Inject
    @Any
    private Instance<AccountBean<?>> accountBeans;

    @Inject
    @Named
    private SellerBean sellerBean;

    private DualListModel<Role> rolesDM;

    private TreeNode userGroupRootNode;

    private TreeNode userGroupSelectedNode;

    private ParamBean param = ParamBean.getInstance();
    private String providerFilePath = param.getProperty("providers.rootDir", "./opencelldata/");
    private String selectedFolder;
    private boolean currentDirEmpty;
    private String selectedFileName;
    private String newFilename;
    private String directoryName;
    private List<File> fileList;
    private UploadedFile file;
    private String securedEntityType;
    private Map<String, String> securedEntityTypes;
    private Map<String, BaseBean<? extends BusinessEntity>> accountBeanMap;
    private BusinessEntity selectedEntity;
    private BaseBean<?> selectedAccountBean;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    private boolean autoUnzipped;
    
    final private String ZIP_FILE_EXTENSION = ".zip";

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UserBean() {
        super(User.class);
    }

    @PostConstruct
    public void init() {
        if (conversation.isTransient()) {
            conversation.begin();
            createMissingDirectories();
            setSelectedFolder(null);
        }
        initSelectionOptions();
    }

    @Override
    public User initEntity() {
        super.initEntity();

        if (entity.getName() == null) {
            entity.setName(new Name());
        }

        return entity;
    }

    public TreeNode getUserGroupRootNode() {
        if (userGroupRootNode == null) {
            userGroupRootNode = new DefaultTreeNode("Root", null);
            List<UserHierarchyLevel> roots;
            roots = userHierarchyLevelService.findRoots();
            UserHierarchyLevel userHierarchyLevel = getEntity().getUserLevel();
            if (CollectionUtils.isNotEmpty(roots)) {
                Collections.sort(roots);
                for (UserHierarchyLevel userGroupTree : roots) {
                    createTree(userGroupTree, userGroupRootNode, userHierarchyLevel);
                }
            }
        }
        return userGroupRootNode;
    }

    public void setUserGroupRootNode(TreeNode rootNode) {
        this.userGroupRootNode = rootNode;
    }

    public TreeNode getUserGroupSelectedNode() {
        return userGroupSelectedNode;
    }

    public void setUserGroupSelectedNode(TreeNode selectedNode) {
        this.userGroupSelectedNode = selectedNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        log.debug("saving new user={}", entity.getUserName());

        if (getObjectId() != null) {
            if (userService.isUsernameExists(entity.getUserName(), entity.getId())) {
                messages.error(new BundleKey("messages", "exception.UsernameAlreadyExistsException"));
                return null;
            }
        } else {
            if (userService.isUsernameExists(entity.getUserName())) {
                messages.error(new BundleKey("messages", "exception.UsernameAlreadyExistsException"));
                return null;
            }
        }

        if (this.getUserGroupSelectedNode() != null) {
            UserHierarchyLevel userHierarchyLevel = (UserHierarchyLevel) this.getUserGroupSelectedNode().getData();
            getEntity().setUserLevel(userHierarchyLevel);
        }

        getEntity().getRoles().clear();
        getEntity().getRoles().addAll(roleService.refreshOrRetrieve(rolesDM.getTarget()));

        return super.saveOrUpdate(killConversation);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<User> getPersistenceService() {
        return userService;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("roles", "userLevel");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("roles", "userLevel");
    }

    /**
     * Standard method for custom component with listType="pickList".
     */
    public DualListModel<Role> getDualListModel() {
        if (rolesDM == null) {
            List<Role> perksSource = null;
            perksSource = roleService.list();
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getRoles() != null) {
                perksTarget.addAll(getEntity().getRoles());
            }
            perksSource.removeAll(perksTarget);
            rolesDM = new DualListModel<Role>(perksSource, perksTarget);
        }
        return rolesDM;
    }

    public void setDualListModel(DualListModel<Role> rolesDM) {
        this.rolesDM = rolesDM;
    }

    @Override
    protected String getDefaultSort() {
        return null;// "userName";
    }

    public String getFilePath() {
        return providerFilePath + File.separator + appProvider.getCode();
    }

    private String getFilePath(String name) {
        String result = getFilePath() + File.separator + name;
        if (selectedFolder != null) {
            result = getFilePath() + File.separator + selectedFolder + File.separator + name;
        }
        return result;
    }

    public void createMissingDirectories() {
        // log.info("Creating required dirs in "+getFilePath());
        String importDir = getFilePath() + File.separator + "imports" + File.separator + "customers" + File.separator;
        String customerDirIN = importDir + "input";
        String customerDirOUT = importDir + "output";
        String customerDirERR = importDir + "errors";
        String customerDirWARN = importDir + "warnings";
        String customerDirKO = importDir + "reject";
        importDir = getFilePath() + File.separator + "imports" + File.separator + "accounts" + File.separator;
        String accountDirIN = importDir + "input";
        String accountDirOUT = importDir + "output";
        String accountDirERR = importDir + "errors";
        String accountDirWARN = importDir + "warnings";
        String accountDirKO = importDir + "reject";
        importDir = getFilePath() + File.separator + "imports" + File.separator + "subscriptions" + File.separator;
        String subDirIN = importDir + "input";
        String subDirOUT = importDir + "output";
        String subDirERR = importDir + "errors";
        String subDirWARN = importDir + "warnings";
        String subDirKO = importDir + "reject";
        importDir = getFilePath() + File.separator + "imports" + File.separator + "catalog" + File.separator;
        String catDirIN = importDir + "input";
        String catDirOUT = importDir + "output";
        String catDirKO = importDir + "reject";
        importDir = getFilePath() + File.separator + "imports" + File.separator + "metering" + File.separator;
        String meterDirIN = importDir + "input";
        String meterDirOUT = importDir + "output";
        String meterDirKO = importDir + "reject";
        String invoicePdfDir = getFilePath() + File.separator + "invoices" + File.separator + "pdf";
        String invoiceXmlDir = getFilePath() + File.separator + "invoices" + File.separator + "xml";
        String jasperDir = getFilePath() + File.separator + "jasper";
        List<String> filePaths = Arrays.asList("", customerDirIN, customerDirOUT, customerDirERR, customerDirWARN, customerDirKO, accountDirIN, accountDirOUT, accountDirERR,
            accountDirWARN, accountDirKO, subDirIN, subDirOUT, subDirERR, subDirWARN, catDirIN, catDirOUT, catDirKO, subDirKO, meterDirIN, meterDirOUT, meterDirKO, invoicePdfDir,
            invoiceXmlDir, jasperDir);
        for (String custDirs : filePaths) {
            File subDir = new File(custDirs);
            if (!subDir.exists()) {
                subDir.mkdirs();
            }
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
        log.info("set file to" + file.getFileName());
    }

    public void deleteSelectedFile() {
        String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
        log.info("delete file" + folder + File.separator + selectedFileName);
        File file = new File(folder + File.separator + selectedFileName);
        if (file.exists()) {
            file.delete();
        }
        this.selectedFileName = null;
        buildFileList();
    }

    public StreamedContent getSelectedFile() {
        StreamedContent result = null;
        try {
            String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
            result = new DefaultStreamedContent(new FileInputStream(new File(folder + File.separator + selectedFileName)), null, selectedFileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            log.error("error generated while getting seleceted file", e);
        }
        return result;
    }

    public String getSelectedFolder() {
        return selectedFolder;
    }

    public boolean hasSelectedFolder() {
        return !StringUtils.isBlank(selectedFolder);
    }

    public void setSelectedFolder(String selectedFolder) {
        setSelectedFileName(null);
        if (selectedFolder == null) {
            log.debug("setSelectedFolder to null");
            this.selectedFolder = null;
        } else if ("..".equals(selectedFolder)) {
            if (this.selectedFolder.lastIndexOf(File.separator) > 0) {
                log.debug("setSelectedFolder to parent " + this.selectedFolder + " -> " + this.selectedFolder.substring(0, this.selectedFolder.lastIndexOf(File.separator)));
                this.selectedFolder = this.selectedFolder.substring(0, this.selectedFolder.lastIndexOf(File.separator));
            } else {
                this.selectedFolder = null;
            }
        } else {
            log.debug("setSelectedFolder " + selectedFolder);
            if (this.selectedFolder == null) {
                this.selectedFolder = File.separator + selectedFolder;
            } else {
                this.selectedFolder += File.separator + selectedFolder;
            }
        }
        buildFileList();
    }

    private void buildFileList() {
        String folder = getFilePath() + File.separator + (this.selectedFolder == null ? "" : this.selectedFolder);
        File file = new File(folder);
        log.debug("getFileList " + folder);

        File[] files = file.listFiles();

        fileList = files == null ? new ArrayList<File>() : new ArrayList<File>(Arrays.asList(files));
        currentDirEmpty = !StringUtils.isBlank(this.selectedFolder) && fileList.size() == 0;
    }

    public String getFileType(String fileName) {
        if (fileName != null && fileName.endsWith(".zip")) {
            return "zip";
        }
        return "text";
    }

    public String getLastModified(File file) {
        return sdf.format(new Date(file.lastModified()));
    }

    public String getSelectedFileName() {
        return selectedFileName;
    }

    public void setSelectedFileName(String selectedFileName) {
        log.debug("setSelectedFileName " + selectedFileName);
        this.selectedFileName = selectedFileName;
    }

    public String getNewFilename() {
        return newFilename;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public boolean isCurrentDirEmpty() {
        return currentDirEmpty;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        String filename = file.getFileName();
        log.debug("upload file={},autoUnziped {}", filename, autoUnzipped);
        // FIXME: use resource bundle
        try {
            InputStream fileInputStream = file.getInputstream();
            if (this.isAutoUnzipped()) {
                if (!filename.endsWith(ZIP_FILE_EXTENSION)) {
                    messages.info(filename + " isn't a valid zip file!");
                    copyFile(filename, fileInputStream);
                } else {
                    copyUnZippedFile(fileInputStream);
                }
            } else {
                copyFile(filename, fileInputStream);
            }
            messages.info(filename + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
        } catch (IOException e) {
            log.error("Failed to upload a file {}", filename, e);
            messages.error("Error while uploading " + filename);
        }
    }

    public void upload(ActionEvent event) {
        if (file != null) {
            log.debug("upload file={}", file);
            try {
                copyFile(FilenameUtils.getName(file.getFileName()), file.getInputstream());

                messages.info(file.getFileName() + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
            } catch (IOException e) {
                log.error("Failed to upload a file {}", file.getFileName(), e);
                messages.error("Error while uploading " + file.getFileName());
            }
        } else {
            log.info("upload file is null");

        }
    }

    public void createDirectory() {
        if (!StringUtils.isBlank(directoryName)) {
            String filePath = getFilePath(directoryName);
            File newDir = new File(filePath);
            if (!newDir.exists()) {
                if (newDir.mkdir()) {
                    buildFileList();
                    directoryName = "";
                }
            }
        }
    }

    public void deleteDirectory() {
        log.debug("deleteDirectory:" + selectedFolder);
        if (currentDirEmpty) {
            String filePath = getFilePath("");
            File currentDir = new File(filePath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                if (currentDir.delete()) {
                    setSelectedFolder("..");
                    createMissingDirectories();
                    buildFileList();
                }
            }
        }
    }

    public void renameFile() {
        if (!StringUtils.isBlank(selectedFileName) && !StringUtils.isBlank(newFilename)) {
            String filePath = getFilePath(selectedFileName);
            String newFilePath = getFilePath(newFilename);
            File currentFile = new File(filePath);
            File newFile = new File(newFilePath);
            if (currentFile.exists() && currentFile.isFile() && !newFile.exists()) {
                if (currentFile.renameTo(newFile)) {
                    buildFileList();
                    selectedFileName = newFilename;
                    newFilename = "";
                }
            }
        }
    }

    public StreamedContent getDownloadZipFile() {
        String filename = selectedFolder == null ? "meveo-fileexplore" : selectedFolder.substring(selectedFolder.lastIndexOf(File.separator) + 1);
        String sourceFolder = getFilePath() + (selectedFolder == null ? "" : selectedFolder);
        try {
            byte[] filedata = FileUtils.createZipFile(sourceFolder);
            InputStream is = new ByteArrayInputStream(filedata);
            return new DefaultStreamedContent(is, "application/octet-stream", filename + ".zip");
        } catch (Exception e) {
            log.debug("Failed to zip a file", e);
        }
        return null;
    }

    private void copyUnZippedFile(InputStream in) {
        try {
            String folder = getFilePath("");
            FileUtils.unzipFile(folder, in);
            buildFileList();
        } catch (Exception e) {
            log.debug("error when upload zip file for new UI", e);
        }
    }

    public void copyFile(String fileName, InputStream in) {
        try {

            // write the inputStream to a FileOutputStream
            String filePath = getFilePath(fileName);
            OutputStream out = new FileOutputStream(new File(filePath));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            in.close();
            out.flush();
            out.close();

            log.debug("New file created!");
            buildFileList();
        } catch (IOException e) {
            log.error("Failed saving file. ", e);
        }
    }

    // Recursive function to create tree with node checked if selected
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private TreeNode createTree(HierarchyLevel hierarchyLevel, TreeNode rootNode, UserHierarchyLevel selectedHierarchyLevel) {
        TreeNode newNode = new DefaultTreeNode(hierarchyLevel, rootNode);
        List<UserHierarchyLevel> subTree = new ArrayList<UserHierarchyLevel>(hierarchyLevel.getChildLevels());
        newNode.setExpanded(true);
        if (selectedHierarchyLevel != null && selectedHierarchyLevel.getId().equals(hierarchyLevel.getId())) {
            newNode.setSelected(true);
        }
        if (CollectionUtils.isNotEmpty(subTree)) {
            Collections.sort(subTree);
            for (HierarchyLevel userGroupTree : subTree) {
                createTree(userGroupTree, newNode, selectedHierarchyLevel);
            }
        }
        return newNode;
    }

    public boolean isAutoUnzipped() {
        return autoUnzipped;
    }

    public void setAutoUnzipped(boolean autoUnzipped) {
        this.autoUnzipped = autoUnzipped;
    }

    public String getSecuredEntityType() {
        return this.securedEntityType;
    }

    public void setSecuredEntityType(String securedEntityType) {
        this.securedEntityType = securedEntityType;
    }

    public BusinessEntity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(BusinessEntity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public BaseBean<?> getSelectedAccountBean() {
        return selectedAccountBean;
    }

    public void setSelectedAccountBean(BaseBean<?> selectedAccountBean) {
        this.selectedAccountBean = selectedAccountBean;
    }

    public Map<String, String> getSecuredEntityTypes() {
        return this.securedEntityTypes;
    }

    public void setSecuredEntityTypes(Map<String, String> securedEntityTypes) {
        this.securedEntityTypes = securedEntityTypes;
    }

    public List<DetailedSecuredEntity> getSelectedSecuredEntities() {
        List<DetailedSecuredEntity> detailedSecuredEntities = new ArrayList<>();
        DetailedSecuredEntity detailedSecuredEntity = null;
        BusinessEntity businessEntity = null;
        if (entity != null && entity.getSecuredEntities() != null) {
            for (SecuredEntity securedEntity : entity.getSecuredEntities()) {
                detailedSecuredEntity = new DetailedSecuredEntity(securedEntity);
                businessEntity = securedBusinessEntityService.getEntityByCode(securedEntity.getEntityClass(), securedEntity.getCode());
                detailedSecuredEntity.setDescription(businessEntity.getDescription());
                detailedSecuredEntities.add(detailedSecuredEntity);
            }
        }
        return detailedSecuredEntities;
    }

    /**
     * This will allow the chosen secured entity to be removed from the user's securedEntities list.
     * 
     * @param selectedSecuredEntity The chosen securedEntity
     * @throws BusinessException
     */
    @ActionMethod
    public void deleteSecuredEntity(SecuredEntity selectedSecuredEntity) throws BusinessException {
        for (SecuredEntity securedEntity : entity.getSecuredEntities()) {
            if (securedEntity.equals(selectedSecuredEntity)) {
                entity.getSecuredEntities().remove(selectedSecuredEntity);
                break;
            }
        }
        super.saveOrUpdate(false);
    }

    /**
     * This will set the correct account bean based on the selected type(Seller, Customer, etc.)
     */
    public void updateSelectedAccountBean() {
        if (!StringUtils.isBlank(getSecuredEntityType())) {
            setSelectedAccountBean(accountBeanMap.get(getSecuredEntityType()));
        }
    }

    /**
     * This will add the selected business entity to the user's securedEntities list.
     * 
     * @param event
     * @throws BusinessException
     */
    @ActionMethod
    public void saveSecuredEntity(SelectEvent event) throws BusinessException {
        log.debug("saveSecuredEntity: {}", this.selectedEntity);
        if (this.selectedEntity != null) {
            List<SecuredEntity> securedEntities = getEntity().getSecuredEntities();
            for (SecuredEntity securedEntity : securedEntities) {
                if (securedEntity.equals(this.selectedEntity)) {
                    messages.info(new BundleKey("messages", "commons.uniqueField.code"));
                    return;
                }
            }
            getEntity().getSecuredEntities().add(new SecuredEntity(this.selectedEntity));
            super.saveOrUpdate(false);
        }
    }

    /**
     * This will initialize the dropdown values for selecting the entity types (Seller, Customer, etc) and the map of managed beans associated to each entity type.
     */
    private void initSelectionOptions() {
        log.debug("initSelectionOptions...");
        log.debug("this.securedEntityTypes: {}", this.securedEntityTypes);
        log.debug("this.accountBeanMap.", this.accountBeanMap);

        if (accountBeanMap == null || accountBeanMap.isEmpty()) {
            accountBeanMap = new HashMap<>();
            securedEntityTypes = new HashMap<>();
            String key = ReflectionUtils.getHumanClassName(sellerBean.getClazz().getSimpleName());
            String value = ReflectionUtils.getCleanClassName(sellerBean.getClazz().getName());
            securedEntityTypes.put(key, value);
            accountBeanMap.put(value, sellerBean);
            for (AccountBean<?> accountBean : accountBeans) {
                key = ReflectionUtils.getHumanClassName(accountBean.getClazz().getSimpleName());
                value = ReflectionUtils.getCleanClassName(accountBean.getClazz().getName());
                securedEntityTypes.put(key, value);
                accountBeanMap.put(value, accountBean);
            }
        }
        log.debug("this.securedEntityTypes: {}", this.securedEntityTypes);
        log.debug("this.accountBeanMap: {}", this.accountBeanMap);
        log.debug("initSelectionOptions done.");
    }
}