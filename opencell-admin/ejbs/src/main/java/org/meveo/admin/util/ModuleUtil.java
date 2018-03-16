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
package org.meveo.admin.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.meveo.commons.utils.ParamBean;
import org.primefaces.model.CroppedImage;

/**
 * a help class for meveo pictures like provider/media/module/pictures
 * 
 * @author Tyshan(tyshan@manaty.net)
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */

public class ModuleUtil {

    public static String getRootPicturePath(String providerCode) {
        // To be checked carefully
        String path = ParamBean.getInstanceByProvider(providerCode).getChrootDir(providerCode) + File.separator + "media";
        return getPath(path);
    }

    public static String getPicturePath(String providerCode, String group) {
        return getPicturePath(providerCode, group, true);
    }

    public static String getPicturePath(String providerCode, String group, boolean createDir) {
        String path = getRootPicturePath(providerCode) + File.separator + group + File.separator + "pictures";
        return getPath(path, createDir);
    }

    public static String getModulePicturePath(String providerCode) {
        return getPicturePath(providerCode, "module");
    }

    public static String getTmpRootPath(String providerCode) throws IOException {
        String tmpFolder = System.getProperty("java.io.tmpdir");
        if (StringUtils.isBlank(tmpFolder)) {
            tmpFolder = "/tmp";
        }
        return getPath(tmpFolder + File.separator + providerCode);
    }

    private static String getPath(String path) {
        return getPath(path, true);
    }

    private static String getPath(String path, boolean createDir) {
        File file = new File(path);
        if (createDir && !file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static byte[] readPicture(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }
        BufferedImage img = ImageIO.read(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, filename.substring(filename.indexOf(".") + 1), out);
        return out.toByteArray();
    }

    public static void writePicture(String filename, byte[] fileData) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        BufferedImage img = ImageIO.read(in);
        in.close();
        ImageIO.write(img, filename.substring(filename.indexOf(".") + 1), new File(filename));
    }

    /**
     * read a module picture and save into byte[].
     * 
     * @param providerCode provider code
     * @param filename file name
     * @return module picture as bytes
     * @throws IOException IO exception
     */
    public static byte[] readModulePicture(String providerCode, String filename) throws IOException {
        String picturePath = getModulePicturePath(providerCode);
        String file = picturePath + File.separator + filename;
        return readPicture(file);

    }

    /**
     * save a byte[] data of module picture into file.
     * 
     * @param providerCode provider code.
     * @param filename file name
     * @param fileData file data
     * @throws Exception exception
     */
    public static void writeModulePicture(String providerCode, String filename, byte[] fileData) throws Exception {
        String picturePath = getModulePicturePath(providerCode);
        String file = picturePath + File.separator + filename;
        writePicture(file, fileData);
    }

    public static void removePicture(String filename) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void removeModulePicture(String providerCode, String filename) throws Exception {
        String picturePath = getModulePicturePath(providerCode);
        filename = picturePath + File.separator + filename;
        removePicture(filename);
    }

    public static void cropPicture(String filename, CroppedImage croppedImage) throws Exception {
        FileImageOutputStream imageOutput = new FileImageOutputStream(new File(filename));
        imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
        imageOutput.flush();
        imageOutput.close();
    }
}
