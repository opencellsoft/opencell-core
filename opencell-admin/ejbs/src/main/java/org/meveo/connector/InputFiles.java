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
package org.meveo.connector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.ejb.Asynchronous;
import javax.xml.bind.JAXBException;

import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.slf4j.Logger;

/**
 * @author anasseh
 * @since 22.12.2010
 * 
 */

public abstract class InputFiles {
	protected Logger log;

	public void handleFiles(String dirIN, String prefix, String ext, String dirOK, String dirKO) throws InterruptedException {
		File dir = new File(dirIN);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<File> files = getFilesToProcess(dir, prefix, ext);
		int numberOfFiles = files.size();
		log.info("InputFiles job " + numberOfFiles + " to import");

		CountDownLatch latch = new CountDownLatch(numberOfFiles);
		for (File file : files) {
			File currentFile = null;
			try {				
				log.info("InputFiles job " + file.getName() + " in progres");
				currentFile = FileUtils.addExtension(file, ".processing");
				importFile(currentFile, file.getName(), latch);
				FileUtils.moveFile(dirOK, currentFile, file.getName());
				log.info("InputFiles job " + file.getName() + " done");

			} catch (Exception e) {
				log.info("InputFiles job " + file.getName() + " failed");
				FileUtils.moveFile(dirKO, currentFile, file.getName());
				log.error("failed to handle files",e);
			}
		}

		// wait until all files finished processing, then return
		latch.await();
	}

	@Asynchronous
	public abstract void importFile(File file, String fileName, CountDownLatch latch) throws JAXBException, Exception;

	public String getProvider(String fileName) {
		try {
			String[] fields = fileName.split("_");
			if (fields.length >= 3) {
				return fields[1];
			}
		} catch (Exception e) {
			log.error("error on get provider ",e);
		}
		return null;
	}

	private List<File> getFilesToProcess(File dir,String prefix, String ext){
		List<File> files = new ArrayList<File>();
		ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
		File[] listFile = dir.listFiles(filtre);
		if(listFile == null){
			return files;			
		}
		for(File file : listFile){
			if(file.isFile()){
				files.add(file);
			}
		}
		return files;
	}
}
