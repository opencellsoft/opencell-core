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
package org.meveo.admin.action.admin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.Document;
import org.meveo.model.crm.Provider;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ConversationScoped
public class CRMConnectorRejectedFileBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /* TODO @DataModel */
    private List<Document> crmConnectorRejectedFiles;

    @Inject
    @ApplicationProvider
    private Provider appProvider;

    private String filename;
    private Date fromDate;
    private Date toDate;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    private FacesContext facesContext;

    // private static String errorPath = null;
    // private static String allertPath = null;
    // private static String tmpPath = null;
    // static {
    // ParamBean param = ParamBean.getInstance();
    // // TODO: set correct default path
    // errorPath = param.getProperty(
    // "connectorCRM.importCustomers.ouputDir.error", "");
    // allertPath = param.getProperty(
    // "connectorCRM.importCustomers.ouputDir.alert", "");
    // tmpPath = param.getProperty("document.tmp.path", "");
    // }

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CRMConnectorRejectedFileBean() {
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes BaseBean.list() method that handles all data model loading. Overriding is needed only to put factory name on
     * it.
     * 
     * @return A list of documents
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    // TODO @Begin(join = true)
    @Produces
    @Named("crmConnectorRejectedFiles")
    public List<Document> list() {
        crmConnectorRejectedFiles = new ArrayList<Document>();
        ParamBean param = paramBeanFactory.getInstance();
        String errorPath = param.getProperty("connectorCRM.importCustomers.ouputDir.error", "");
        String allertPath = param.getProperty("connectorCRM.importCustomers.ouputDir.alert", "");
        loadFiles(errorPath);
        loadFiles(allertPath);
        Collections.sort(crmConnectorRejectedFiles, new Comparator() {

            public int compare(Object o1, Object o2) {
                Document p1 = (Document) o1;
                Document p2 = (Document) o2;
                return p2.getCreateDate().compareTo(p1.getCreateDate());
            }

        });
        return crmConnectorRejectedFiles;
    }

    public void loadFiles(String documentsPath) {
        File path = new File(documentsPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File[] files = path.listFiles(new FileNameDateFilter(this.filename, this.fromDate, this.toDate));
        if (files != null) {
            Document d = null;
            for (File file : files) {
                d = new Document();
                d.setFilename(file.getName());
                log.info("add file #0", file.getName());
                d.setSize(file.length());
                d.setCreateDate(new Date(file.lastModified()));
                d.setAbsolutePath(file.getAbsolutePath());
                crmConnectorRejectedFiles.add(d);
            }
        }
    }

    public synchronized String compress(Document document) {

        log.info("start to compress: #0", document.getAbsolutePath());
        String tmpPath = paramBeanFactory.getInstance().getProperty("document.tmp.path", "");
        File tmp = new File(tmpPath);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }

        File tmpFile = new File(tmpPath + File.separator + UUID.randomUUID().toString());

        try (FileOutputStream fout = new FileOutputStream(tmpFile);
                CheckedOutputStream csum = new CheckedOutputStream(fout, new CRC32());
                GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(csum));
                InputStream in = new FileInputStream(new File(document.getAbsolutePath()));) {

            int sig = 0;
            byte[] buf = new byte[1024];
            while ((sig = in.read(buf, 0, 1024)) != -1)
                out.write(buf, 0, sig);
            // /TODO FIX TO USE BOTH
            File createdFile = new File(document.getAbsolutePath() + ".gzip");
            if (createdFile.exists()) {
                createdFile.delete();
            }
            tmpFile.renameTo(createdFile);
        } catch (Exception e) {
            log.error("Error:#0, when compress file:#1", e, document.getAbsolutePath());
        }

        list();
        log.info("end compress...");
        return null;
    }

    /**
     * @param document document to download
     * @return null
     */
    public String download(Document document) {
        if (document == null) {
            return null;
        }
        
        log.info("start to download...");
        File f = new File(document.getAbsolutePath());
        HttpServletResponse res = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        res.setContentType("application/force-download");
        res.setContentLength(document.getSize().intValue());
        res.addHeader("Content-disposition", "attachment;filename=\"" + document.getFilename() + "\"");
        try (OutputStream out = res.getOutputStream();
            InputStream fin = new FileInputStream(f);) {
            byte[] buf = new byte[1024];
            int sig = 0;
            while ((sig = fin.read(buf, 0, 1024)) != -1) {
                out.write(buf, 0, sig);
            }
            out.flush();
            facesContext.responseComplete();
            log.info("download over!");
        } catch (Exception e) {
            log.error("Error:#0, when dowload file: #1", e, document.getAbsolutePath());
        }
        log.info("downloaded successfully!");
        return null;
    }

    public String delete(Document document) {
        log.info("start delete...");
        File file = new File(document.getAbsolutePath());
        if (file.exists()) {
            file.delete();
        }
        list();
        log.info("end delete...");
        return null;
    }

    public void clean() {
        this.filename = null;
        this.fromDate = null;
        this.toDate = null;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    class FileNameDateFilter implements FilenameFilter {

        private String filename;
        private Date fromDate;
        private Date toDate;

        FileNameDateFilter(String filename, Date fromDate, Date toDate) {
            this.filename = filename;
            this.toDate = toDate;
            this.fromDate = fromDate;
        }

        public boolean accept(File dir, String name) {
            log.info("accept file path #0, name #1.", dir.getPath(), name);
            File file = new File(dir.getAbsoluteFile() + File.separator + name);
            boolean result = true;
            if (name == null) {
                result = false;
            }

            if (appProvider != null && name != null && !name.contains("_" + appProvider.getCode() + "_")) {
                result = false;
            }
            if (this.filename != null && !this.filename.equals("") && name != null && name.indexOf(filename) < 0) {
                result = false;
            }
            if (this.fromDate != null) {
                if (fromDate.after(new Date(file.lastModified()))) {
                    result = false;
                }
            }
            if (this.toDate != null) {
                if (toDate.before(new Date(file.lastModified()))) {
                    result = false;
                }
            }
            return result;
        }
    }
}