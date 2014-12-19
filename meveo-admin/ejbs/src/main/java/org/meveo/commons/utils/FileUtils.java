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
package org.meveo.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File utilities class.
 * 
 * @author Donatas Remeika
 * @created Mar 4, 2009
 */
public final class FileUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(FileUtils.class);

	/**
	 * No need to create instance.
	 */
	private FileUtils() {

	}

	/**
	 * Add extension to existing file by renamig it.
	 * 
	 * @param file
	 *            File to be renamed.
	 * @param extension
	 *            Extension.
	 * @return Renamed File object.
	 */
	public static File addExtension(File file, String extension) {
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
	 * @param file
	 *            Old file.
	 * @param extension
	 *            New extension.
	 * @return New File.
	 */
	public static File replaceFileExtension(File file, String extension) {

		if (!extension.startsWith(".")) {
			extension = "." + extension;
		}
		String newFileName = file.getName() + extension;
		int indexOfExtension = file.getName().lastIndexOf(".");
		if (indexOfExtension >= 1) {
			newFileName = file.getName().substring(0, indexOfExtension)
					+ extension;
		}
		return renameFile(file, newFileName);
	}

	/**
	 * TODO
	 * 
	 * @param file
	 * @param newName
	 * @return
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
	 * Move file to destination directory.
	 * 
	 * @param destionation
	 *            Absolute path to destination directory.
	 * @param file
	 *            File object to move.
	 * @param newFilename
	 *            New filename for moved file.
	 * @return true if operation was successful, false otherwise.
	 */
	public static boolean moveFile(String destination, File file,
			String newFilename) {
		File destinationDir = new File(destination);

		if (!destinationDir.exists()) {
			destinationDir.mkdirs();
		}

		if (destinationDir.isDirectory()) {
			return file.renameTo(new File(destination,
					newFilename != null ? newFilename : file.getName()));
		}

		return false;
	}

	/**
	 * Copy file. If destination file name is directory, then create copy of
	 * file with same name in that directory. I destination is file, then copy
	 * data to file with this name.
	 * 
	 * @param fromFileName
	 *            File name that we are copying.
	 * @param toFileName
	 *            File(dir) name where to copy.
	 * @throws IOException
	 */
	public static void copy(String fromFileName, String toFileName)
			throws IOException {
		File fromFile = new File(fromFileName);
		File toFile = new File(toFileName);

		if (!fromFile.exists())
			throw new IOException("FileCopy: no such source file: "
					+ fromFileName);
		if (!fromFile.isFile())
			throw new IOException("FileCopy: can't copy directory: "
					+ fromFileName);
		if (!fromFile.canRead())
			throw new IOException("FileCopy: source file is unreadable: "
					+ fromFileName);

		if (toFile.isDirectory())
			toFile = new File(toFile, fromFile.getName());

		if (toFile.exists()) {
			if (!toFile.canWrite()) {
				throw new IOException(
						"FileCopy: destination file is unwriteable: "
								+ toFileName);
			}
		} else {
			String parent = toFile.getParent();
			if (parent == null)
				parent = System.getProperty("user.dir");
			File dir = new File(parent);
			if (!dir.exists())
				throw new IOException(
						"FileCopy: destination directory doesn't exist: "
								+ parent);
			if (dir.isFile())
				throw new IOException(
						"FileCopy: destination is not a directory: " + parent);
			if (!dir.canWrite())
				throw new IOException(
						"FileCopy: destination directory is unwriteable: "
								+ parent);
		}

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead);
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					logger.warn("Failed to close file resource!", e);
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					logger.warn("Failed to close file resource!", e);
				}
		}
	}

	/**
	 * Replaces filename extension with new one.
	 * 
	 * @param filename
	 *            Old filename.
	 * @param extension
	 *            New extension.
	 * @return New Filename.
	 */
	public static String replaceFilenameExtension(String filename,
			String extension) {

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
	 * @param filename
	 *            File name.
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
	 * Get File representation ready for parsing.
	 * 
	 * @param sourceDirectory
	 *            Directory to search inside.
	 * @return File object.
	 */
	public static File getFileForParsing(String sourceDirectory,
			final List<String> extensions) {
		File sourceDir = new File(sourceDirectory);
		if (!sourceDir.exists() || !sourceDir.isDirectory()) {
			logger.info(String.format("Wrong source directory: %s",
					sourceDir.getAbsolutePath()));
			return null;
		}
		File[] files = sourceDir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (extensions == null) {
					return true;
				}
				for (String extension : extensions) {
					if (name.endsWith(extension)) {
						return true;
					}
				}
				return false;
			}

		});
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
	 * Creates directory by name if it does not exist.
	 * 
	 * @param dirName
	 *            Directory name. Must be full path.
	 */
	public static void createDirectory(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * @param zipFilename
	 * @param filesToAdd
	 */
	public static void createZipArchive(String zipFilename,
			String... filesToAdd) {
		int BUFFER = 2048;
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(zipFilename);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			byte data[] = new byte[BUFFER];
			for (int i = 0; i < filesToAdd.length; i++) {
				FileInputStream fi = new FileInputStream(filesToAdd[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				ZipEntry entry = new ZipEntry(new File(filesToAdd[i]).getName());
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				FileUtils.closeStream(origin);
			}
			FileUtils.closeStream(out);
		} catch (Exception e) {
			logger.error("Error while creating zip archive", e);
		} finally {

		}
	}

	/**
	 * @param c
	 * @return
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
}