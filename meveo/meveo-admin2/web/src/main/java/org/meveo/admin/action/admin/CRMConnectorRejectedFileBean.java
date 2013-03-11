/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.Document;
import org.meveo.model.crm.Provider;

@Name("crmConnectorRejectedFileBean")
@Scope(ScopeType.CONVERSATION)
public class CRMConnectorRejectedFileBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Logger
    private Log log;

    @DataModel
    private List<Document> crmConnectorRejectedFiles;

    @In
    private Provider currentProvider;

    private String filename;
    private Date fromDate;
    private Date toDate;

    private static String errorPath = null;
    private static String allertPath = null;
    private static String tmpPath = null;
    static {
        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
        errorPath = param.getProperty("connectorCRM.importCustomers.ouputDir.error");
        allertPath = param.getProperty("connectorCRM.importCustomers.ouputDir.alert");
        tmpPath = param.getProperty("document.tmp.path");
    }

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public CRMConnectorRejectedFileBean() {
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @SuppressWarnings("unchecked")
    @Begin(join = true)
    @Factory("crmConnectorRejectedFiles")
    public void list() {
        crmConnectorRejectedFiles = new ArrayList<Document>();
        loadFiles(errorPath);
        loadFiles(allertPath);
        Collections.sort(crmConnectorRejectedFiles, new Comparator() {

            public int compare(Object o1, Object o2) {
                Document p1 = (Document) o1;
                Document p2 = (Document) o2;
                return p2.getCreateDate().compareTo(p1.getCreateDate());
            }

        });

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
        File tmp = new File(tmpPath);
        if (!tmp.exists()) {
            tmp.mkdirs();
        }

        File tmpFile = new File(tmpPath + File.separator + UUID.randomUUID().toString());

        try {
            FileOutputStream fout = new FileOutputStream(tmpFile);
            CheckedOutputStream csum = new CheckedOutputStream(fout, new CRC32());
            GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(csum));
            InputStream in = new FileInputStream(new File(document.getAbsolutePath()));
            int sig = 0;
            byte[] buf = new byte[1024];
            while ((sig = in.read(buf, 0, 1024)) != -1)
                out.write(buf, 0, sig);
            in.close();
            out.finish();
            out.close();
            csum.close();
            fout.close();
            // /TODO FIX TO USE BOTH
            File createdFile = new File(document.getAbsolutePath() + ".gzip");
            if (createdFile.exists()) {
                createdFile.delete();
            }
            tmpFile.renameTo(createdFile);
        } catch (Exception e) {
            log.error("Error:#0, when compress file:#1", e.getMessage(), document.getAbsolutePath());
        }
        list();
        log.info("end compress...");
        return null;
    }

    public String download(Document document) {
        log.info("start to download...");
        File f = new File(document.getAbsolutePath());

        try {
            javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
            HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
            res.setContentType("application/force-download");
            res.setContentLength(document.getSize().intValue());
            res.addHeader("Content-disposition", "attachment;filename=\"" + document.getFilename() + "\"");

            OutputStream out = res.getOutputStream();
            InputStream fin = new FileInputStream(f);

            byte[] buf = new byte[1024];
            int sig = 0;
            while ((sig = fin.read(buf, 0, 1024)) != -1) {
                out.write(buf, 0, sig);
            }
            fin.close();
            out.flush();
            out.close();
            context.responseComplete();
            log.info("download over!");
        } catch (Exception e) {
            log.error("Error:#0, when dowload file: #1", e.getMessage(), document.getAbsolutePath());
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
            if (currentProvider != null && !name.contains("_" + currentProvider.getCode() + "_")) {
                result = false;
            }
            if (this.filename != null && !this.filename.equals("") && name.indexOf(filename) < 0) {
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