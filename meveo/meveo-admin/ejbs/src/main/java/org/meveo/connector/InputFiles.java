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
 * @created 22.12.2010
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
				e.printStackTrace();
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
			e.printStackTrace();
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
