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
package org.meveo.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File utilities class.
 * 
 * @author Donatas Remeika
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
public final class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * The Constant DATETIME_FORMAT for file names
     */
    private static final String DATETIME_FORMAT = "dd_MM_yyyy-HHmmss";

    /**
     * No need to create instance.
     */
    private FileUtils() {

    }

    /**
     * Add extension to existing file by renamig it.
     * 
     * @param file File to be renamed.
     * @param extension Extension.
     * @return Renamed File object.
     */
    public static synchronized File addExtension(File file, String extension) {
        if (file.exists()) {
            String name = file.getName();
            File dest = new File(file.getParentFile(), name + extension);
            if (file.renameTo(dest)) {
                return dest;
            }
        }
        return null;
    }

    /**
     * Replaces file extension with new one.
     * 
     * @param file Old file.
     * @param extension New extension.
     * @return New File.
     */
    public static File replaceFileExtension(File file, String extension) {

        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        String newFileName = file.getName() + extension;
        int indexOfExtension = file.getName().lastIndexOf(".");
        if (indexOfExtension >= 1) {
            newFileName = file.getName().substring(0, indexOfExtension) + extension;
        }
        return renameFile(file, newFileName);
    }

    /**
     * 
     * @param file instance of File needs to rename
     * @param newName new file's name
     * @return file
     */
    public static File renameFile(File file, String newName) {
        if (file.exists()) {
            File dest = new File(file.getParentFile(), newName);
            if (file.renameTo(dest)) {
                return dest;
            }
        }
        return null;
    }

    /**
     * Move file. In case a file with the same name exists, create a name with a timestamp
     *
     * @param dest the destination
     * @param file the file
     * @param name the file name
     * @return the file name
     */
    public static String moveFileDontOverwrite(String dest, File file, String name) {
        String destName = name;
        if ((new File(dest + File.separator + name)).exists()) {
            destName += "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT);
        }
        moveFile(dest, file, destName);
        return destName;
    }

    /**
     * Move file to destination directory.
     * 
     * @param destination Absolute path to destination directory.
     * @param file File object to move.
     * @param newFilename New filename for moved file.
     * @return true if operation was successful, false otherwise.
     */
    public static boolean moveFile(String destination, File file, String newFilename) {
        File destinationDir = new File(destination);

        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        if (destinationDir.isDirectory()) {
            return file.renameTo(new File(destination, newFilename != null ? newFilename : file.getName()));
        }

        return false;
    }

    /**
     * Copy file. If destination file name is directory, then create copy of file with same name in that directory. I destination is file, then copy data to file with this name.
     * 
     * @param fromFileName File name that we are copying.
     * @param toFileName File(dir) name where to copy.
     * @throws IOException IO exeption.
     */
    public static void copy(String fromFileName, String toFileName) throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists()) {
            throw new IOException("FileCopy: no such source file: " + fromFileName);
        }
        if (!fromFile.isFile()) {
            throw new IOException("FileCopy: can't copy directory: " + fromFileName);
        }
        if (!fromFile.canRead()) {
            throw new IOException("FileCopy: source file is unreadable: " + fromFileName);
        }

        if (toFile.isDirectory()) {
            toFile = new File(toFile, fromFile.getName());
        }

        if (toFile.exists()) {
            if (!toFile.canWrite()) {
                throw new IOException("FileCopy: destination file is unwriteable: " + toFileName);
            }
        } else {
            String parent = toFile.getParent();
            if (parent == null) {
                parent = System.getProperty("user.dir");
            }
            File dir = new File(parent);
            if (!dir.exists()) {
                throw new IOException("FileCopy: destination directory doesn't exist: " + parent);
            }
            if (dir.isFile()) {
                throw new IOException("FileCopy: destination is not a directory: " + parent);
            }
            if (!dir.canWrite()) {
                throw new IOException("FileCopy: destination directory is unwriteable: " + parent);
            }
        }

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    logger.warn("Failed to close file resource!", e);
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    logger.warn("Failed to close file resource!", e);
                }
            }
        }
    }

    /**
     * Replaces filename extension with new one.
     * 
     * @param filename Old filename.
     * @param extension New extension.
     * @return New Filename.
     */
    public static String replaceFilenameExtension(String filename, String extension) {

        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        int indexOfExtension = filename.lastIndexOf(".");
        if (indexOfExtension < 1) {
            return filename + extension;
        } else {
            filename = filename.substring(0, indexOfExtension) + extension;
            return filename;
        }
    }

    /**
     * Get file format by file name extension.
     * 
     * @param filename File name.
     * @return FileFormat enum.
     */
    public static FileFormat getFileFormatByExtension(String filename) {
        int indexOfExtension = filename.lastIndexOf(".");
        if (indexOfExtension < 1 || indexOfExtension >= filename.length()) {
            return FileFormat.OTHER;
        } else {
            String extension = filename.substring(indexOfExtension + 1);
            return FileFormat.parseFromExtension(extension);
        }

    }

    /**
     * Get the first file from a given directory matching extensions
     * 
     * @param sourceDirectory Directory to search inside.
     * @param extensions list of extensions to match
     * @return First found file
     */
    public static File getFirstFile(String sourceDirectory, final List<String> extensions) {

        File[] files = listFiles(sourceDirectory, extensions);

        if (files == null || files.length == 0) {
            return null;
        }

        for (File file : files) {
            if (file.isFile()) {
                return file;
            }
        }

        return null;
    }

    /**
     * List files matching extensions in a given directory
     * 
     * @param sourceDirectory Directory to inspect
     * @param extensions List of extensions to filter by
     * @return Array of matched files
     */
    public static File[] listFiles(String sourceDirectory, final List<String> extensions) {
        return listFiles(sourceDirectory, extensions, "*");
    }

    /**
     * List files matching extensions and prefix in a given directory
     * 
     * @param sourceDirectory Directory to inspect
     * @param extensions List of extensions to filter by
     * @param prefix Filename prefix to filter by
     * @return Array of matched files
     */
    public static File[] listFiles(String sourceDirectory, final List<String> extensions, final String prefix) {
        File sourceDir = new File(sourceDirectory);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            logger.error(String.format("Wrong source directory: %s", sourceDir.getAbsolutePath()));
            return null;
        }
        File[] files = sourceDir.listFiles(new ImportFileFiltre(prefix, extensions));

        if (files == null || files.length == 0) {
            return null;
        }

        return files;
    }

    /**
     * List files matching extension and prefix in a given directory
     * 
     * @param dir Directory to inspect
     * @param extension File extension to match
     * @param prefix File prefix to match
     * @return Array of matched files
     */
    public static List<File> listFiles(File dir, String extension, String prefix) {
        List<File> files = new ArrayList<File>();
        ImportFileFiltre filtre = new ImportFileFiltre(prefix, extension);
        File[] listFile = dir.listFiles(filtre);

        if (listFile == null) {
            return files;
        }

        for (File file : listFile) {
            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }

    /**
     * Creates directory by name if it does not exist.
     * 
     * @param dirName Directory name. Must be full path.
     */
    public static void createDirectory(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * @param zipFilename zipe file name
     * @param filesToAdd list of files to add
     */
    public static void createZipArchive(String zipFilename, String... filesToAdd) {
        final int BUFFER = 2048;
        try (FileOutputStream dest = new FileOutputStream(zipFilename); ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
            byte[] data = new byte[BUFFER];
            for (int i = 0; i < filesToAdd.length; i++) {
                try (FileInputStream fi = new FileInputStream(filesToAdd[i]); BufferedInputStream origin = new BufferedInputStream(fi, BUFFER)) {
                    ZipEntry entry = new ZipEntry(new File(filesToAdd[i]).getName());
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    FileUtils.closeStream(origin);
                } catch (Exception ex) {
                    logger.error("Error while working with zip archive", ex);
                }
            }
            FileUtils.closeStream(out);
        } catch (Exception e) {
            logger.error("Error while creating zip archive", e);
        }
    }

    /**
     * @param c closable
     * @return true/false
     */
    public static boolean closeStream(Closeable c) {
        try {
            if (c != null) {
                c.close();
                return true;
            } else {
                logger.warn("Stream provided for closing was null");
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while closing output stream", e);
            return false;
        }
    }

    /**
     * @param filename file name
     * @return content of file as string
     * @throws IOException IO exception
     */
    public static String getFileAsString(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuffer fileData = new StringBuffer();
        char[] buf = new char[1024];
        int numRead = 0;
        try {
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
        } finally {
            reader.close();
        }
        return fileData.toString();

    }

    /**
     * unzip files into folder.
     * 
     * @param folder folder name
     * @param in input stream
     * @throws Exception exception
     */
    public static void unzipFile(String folder, InputStream in) throws Exception {
        ZipInputStream zis = null;
        BufferedInputStream bis = null;
        CheckedInputStream cis = null;
        try {
            cis = new CheckedInputStream(in, new CRC32());
            zis = new ZipInputStream(cis);
            bis = new BufferedInputStream(zis);
            ZipEntry entry = null;
            File fileout = null;
            while ((entry = zis.getNextEntry()) != null) {
                fileout = new File(folder + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    if (!fileout.exists()) {
                        fileout.mkdirs();
                    }
                    continue;
                }
                if (!fileout.exists()) {
                    (new File(fileout.getParent())).mkdirs();
                }
                try (OutputStream fos = new FileOutputStream(fileout); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    int b = -1;
                    while ((b = bis.read()) != -1) {
                        bos.write(b);
                    }
                    bos.flush();
                    fos.flush();
                } catch (Exception ex) {
                    throw ex;
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(cis);
        }
    }

    /**
     * Compress a folder with sub folders and its files into byte array.
     * 
     * @param sourceFolder source folder
     * @return zip file as byte array
     * @throws Exception exception.
     */
    public static byte[] createZipFile(String sourceFolder) throws Exception {

        Logger log = LoggerFactory.getLogger(FileUtils.class);
        log.info("Creating zip file for {}", sourceFolder);

        ZipOutputStream zos = null;
        ByteArrayOutputStream baos = null;
        CheckedOutputStream cos = null;
        try {
            baos = new ByteArrayOutputStream();
            cos = new CheckedOutputStream(baos, new CRC32());
            zos = new ZipOutputStream(new BufferedOutputStream(cos));
            File sourceFile = new File(sourceFolder);
            for (File file : sourceFile.listFiles()) {
                addToZipFile(file, zos, null);
            }
            zos.flush();
            zos.close();
            return baos.toByteArray();

        } finally {
            IOUtils.closeQuietly(zos);
            IOUtils.closeQuietly(cos);
            IOUtils.closeQuietly(baos);
        }
    }

    public static void addToZipFile(File source, ZipOutputStream zos, String basedir) throws Exception {

        if (!source.exists()) {
            return;
        }

        if (source.isDirectory()) {
            addDirectoryToZip(source, zos, basedir);
        } else {
            addFileToZip(source, zos, basedir);
        }
    }

    public static void addFileToZip(File source, ZipOutputStream zos, String basedir) throws Exception {
        if (!source.exists()) {
            return;
        }

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(source));
            ZipEntry entry = new ZipEntry(((basedir != null ? (basedir + File.separator) : "") + source.getName()).replaceAll("\\" + File.separator, "/"));
            entry.setTime(source.lastModified());
            zos.putNextEntry(entry);
            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, 1024)) != -1) {
                zos.write(data, 0, count);
            }
            zos.flush();
        } finally {
            if (bis != null) {
                bis.close();
            }

        }
    }

    public static void addZipEntry(ZipOutputStream zipOut, FileInputStream fis, ZipEntry zipEntry) throws IOException {
        zipOut.putNextEntry(zipEntry);
        final byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.closeEntry();
    }

    public static void addDirectoryToZip(File source, ZipOutputStream zos, String basedir) throws Exception {
        if (!source.exists()) {
            return;
        }

        File[] files = source.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                addToZipFile(file, zos, (basedir != null ? (basedir + File.separator) : "") + source.getName());
            }
        } else {
            ZipEntry entry = new ZipEntry(((basedir != null ? (basedir + File.separator) : "") + source.getName() + File.separator).replaceAll("\\" + File.separator, "/"));
            entry.setTime(source.lastModified());
            zos.putNextEntry(entry);
        }
    }

    /**
     * @param relativeRoot relative root path
     * @param dir2zip directory to be zipped
     * @param zos zip output stream
     * @throws IOException inpu/ouput exception.
     */
    public static void addDirToArchive(String relativeRoot, String dir2zip, ZipOutputStream zos) throws IOException {
        File zipDir = new File(dir2zip);
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];
        int bytesIn = 0;

        for (int i = 0; i < dirList.length; i++) {
            File f = new File(zipDir, dirList[i]);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                addDirToArchive(relativeRoot, filePath, zos);
                continue;
            }
            try (FileInputStream fis = new FileInputStream(f)) {
                String relativePath = Paths.get(relativeRoot).relativize(f.toPath()).toString();
                ZipEntry anEntry = new ZipEntry(relativePath);
                zos.putNextEntry(anEntry);

                while ((bytesIn = fis.read(readBuffer)) != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                }
            } catch (IOException ex) {
                throw ex;
            }
        }
    }

    /**
     * @param file file to be archived
     * @throws IOException input/ouput exception
     */
    public static void archiveFile(File file) throws IOException {
        byte[] buffer = new byte[1024];
        try (FileOutputStream fos = new FileOutputStream(file.getParent() + File.separator + FilenameUtils.removeExtension(file.getName()) + ".zip");
                ZipOutputStream zos = new ZipOutputStream(fos);
                FileInputStream in = new FileInputStream(file)) {
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        } catch (IOException ex) {
            throw ex;
        }
    }

    /**
     * Change the extension of a file to the given a new file extension.
     * 
     * @param filename Name of the file
     * @param newExtension New extension
     * @return Filename with renamed extension
     */
    public static String changeExtension(String filename, String newExtension) {
        String name = filename.substring(0, filename.lastIndexOf('.'));
        return name + newExtension;
    }

    /**
     * Encode a file to byte64 string.
     * 
     * @param file File
     * @return byte string representation of the file
     * @throws IOException IO exeption.
     */
    public static String encodeFileToBase64Binary(File file) throws IOException {
        String encodedFile = null;
        try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedFile = org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
        }

        return encodedFile;
    }

    /**
     * Gets a list of files
     *
     * @param sourceDirectory the source directory
     * @param extensions the extensions
     * @param fileNameFilter the file name key
     * @return the files for parsing
     */
    public static File[] listFilesByNameFilter(String sourceDirectory, ArrayList<String> extensions, String fileNameFilter) {

        File sourceDir = new File(sourceDirectory);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            logger.info(String.format("Wrong source directory: %s", sourceDir.getAbsolutePath()));
            return null;
        }

        String fileNameFilterUpper = fileNameFilter != null ? fileNameFilter.toUpperCase() : null;

        File[] files = sourceDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {

                boolean emptyExtensions = true;
                if (extensions != null) {
                    for (String extension : extensions) {
                        if (extension != null) {
                            emptyExtensions = false;
                        }
                    }
                }

                if (emptyExtensions && fileNameFilterUpper == null) {
                    return true;
                }

                String nameUpper = name.toUpperCase();
                if (emptyExtensions && nameUpper.contains(fileNameFilterUpper)) {
                    return true;
                }

                for (String extension : extensions) {
                    if (extension != null && (name.endsWith(extension) || "*".equals(extension)) && (fileNameFilterUpper == null || nameUpper.contains(fileNameFilterUpper))) {
                        return true;
                    }
                }

                return false;
            }

        });

        if (files == null || files.length == 0) {
            return null;
        }

        return files;

    }

    /**
     * Checks if the file param is valid zip
     * 
     * @param file
     * @return isValidZip
     */
    public static boolean isValidZip(final File file) {
        try (ZipFile zipfile = new ZipFile(file);) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Count lines of file '\n'
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static int countLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024;) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }
}