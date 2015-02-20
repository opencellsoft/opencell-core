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
package org.meveo.admin.action.admin;

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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard backing bean for {@link User} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class UserBean extends BaseBean<User> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link User} service. Extends {@link PersistenceService}. */
    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private ProviderService providerService;

    @Inject
    private Messages messages;

    private static final Logger log = LoggerFactory.getLogger(UserBean.class);

    private DualListModel<Role> perks;

    private DualListModel<Provider> providerPerks;

    /**
     * Password set by user which is later encoded and set to user before saving to db.
     */
    private String password;

    /**
     * For showing change password panel
     */
    private boolean show = false;

    /**
     * Repeated password to check if it matches another entered password and user did not make a mistake.
     */
    private String repeatedPassword;

    ParamBean param = ParamBean.getInstance();
    private String providerFilePath = param.getProperty("providers.rootDir", "/tmp/meveo/");
    private String selectedFolder;
    private boolean currentDirEmpty;
    private String selectedFileName;
    private String newFilename;
    private String directoryName;
    private ArrayList<File> fileList;
    private UploadedFile file;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        log.debug("saving new user={}", entity.getUserName());
        boolean passwordsDoNotMatch = password != null && !password.equals(repeatedPassword);

        if (passwordsDoNotMatch) {
            messages.error(new BundleKey("messages", "save.passwordsDoNotMatch"));
            return null;
        } else {
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
            if (!StringUtils.isBlank(password)) {
                entity.setLastPasswordModification(new Date());
                entity.setNewPassword(password);
                entity.setPassword(password);
            }

            // Add current provider to a list of user related providers
            if (entity.getProviders().isEmpty()) {
                entity.getProviders().add(currentProvider);
            }

            // Set provider to the first provider from a user related provider list
            entity.setProvider(entity.getProviders().iterator().next());

            return super.saveOrUpdate(killConversation);
        }
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
        return Arrays.asList("provider", "roles", "providers");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("providers", "roles", "provider");
    }

    /**
     * Standard method for custom component with listType="pickList".
     */
    public DualListModel<Role> getDualListModel() {
        if (perks == null) {
            List<Role> perksSource = roleService.list();
            List<Role> perksTarget = new ArrayList<Role>();
            if (getEntity().getRoles() != null) {
                perksTarget.addAll(getEntity().getRoles());
            }
            perksSource.removeAll(perksTarget);
            perks = new DualListModel<Role>(perksSource, perksTarget);
        }
        return perks;
    }

    public void setDualListModel(DualListModel<Role> perks) {
        getEntity().setRoles(new HashSet<Role>((List<Role>) perks.getTarget()));
    }

    public DualListModel<Provider> getProvidersDualListModel() {
        if (providerPerks == null) {
            List<Provider> perksSource = providerService.list();
            List<Provider> perksTarget = new ArrayList<Provider>();
            if (getEntity().getProviders() != null) {
                perksTarget.addAll(getEntity().getProviders());
            }
            perksSource.removeAll(perksTarget);
            providerPerks = new DualListModel<Provider>(perksSource, perksTarget);
        }
        return providerPerks;
    }

    public void setProvidersDualListModel(DualListModel<Provider> providerPerks) {
        getEntity().setProviders(new HashSet<Provider>((List<Provider>) providerPerks.getTarget()));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void change() {
        this.show = !this.show;
    }

    @Override
    protected String getDefaultSort() {
        return "userName";
    }

    public String getFilePath() {
        return providerFilePath + File.separator + getCurrentProvider().getCode();
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
            log.error(e.getMessage());
        }
        return result;
    }

    public String getSelectedFolder() {
        return selectedFolder;
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

        fileList = file.listFiles() == null ? new ArrayList<File>() : new ArrayList<File>(Arrays.asList(file.listFiles()));
        if (this.selectedFolder != null) {
            if (fileList.size() == 0) {
                currentDirEmpty = true;
            }
            File parent = new File("..");
            fileList.add(0, parent);
        }
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
        log.debug("upload file={}", event.getFile());
        // FIXME: use resource bundle
        try {
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());

            messages.info(event.getFile().getFileName() + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
        } catch (IOException e) {
            log.error(e.getMessage());
            messages.error("error while uploading " + event.getFile().getFileName());
        }
    }

    public void upload(ActionEvent event) {
        if (file != null) {
            log.debug("upload file={}", file);
            try {
                copyFile(FilenameUtils.getName(file.getFileName()), file.getInputstream());

                messages.info(file.getFileName() + " is uploaded to " + ((selectedFolder != null) ? selectedFolder : "Home"));
            } catch (IOException e) {
                log.error(e.getMessage());
                messages.error("error while uploading " + file.getFileName());
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
        if (!StringUtils.isBlank(selectedFolder) && currentDirEmpty) {
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
            log.error("Failed saving file. " + e.getMessage());
        }
    }
    
    /**
     * Add additional criteria for searching by provider
     */
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Do not user a check against user.provider as it contains only one value, while user can be linked to various providers
        searchCriteria.put(PersistenceService.SEARCH_SKIP_PROVIDER_CONSTRAINT, true);

        boolean isSuperAdmin = identity.hasPermission("superAdmin", "superAdminManagement");

        if (!isSuperAdmin) {            
            searchCriteria.put("list-providers", currentProvider);            
        }

        return searchCriteria;
    }

}
