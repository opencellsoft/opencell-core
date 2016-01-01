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
package org.meveo.admin.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.MeveoModule;

/**
 * a help class for meveo module pictures 
 * @author Tyshan(tyshan@manaty.net)
 */

public class ModuleUtil {

	/**
	 * 
	 * @param entity
	 * @param filename
	 * @return
	 */
	public static String getPicturePath(MeveoModule meveoModule){
		return getPicturePath(meveoModule.getProvider().getCode());
	}
	public static String getPicturePath(String provider){
		String picturePath = ParamBean.getInstance().getProperty("providers.rootDir", "/opt/jboss/files/meveo")+File.separator+provider
			+File.separator+"pictures";
		File file=new File(picturePath);
		if(!file.exists()){
			file.mkdirs();
		}
		return picturePath;
	}
	public static byte[] readModulePicture(MeveoModule meveoModule,String filename) throws IOException{
		return readModulePicture(meveoModule.getProvider().getCode(),filename);
	}
	/**
	 * read a module picture and save into byte[]
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static byte[] readModulePicture(String provider,String filename) throws IOException{
		String picturePath=getPicturePath(provider);
		String file=picturePath+File.separator+filename;
		BufferedImage img=ImageIO.read(new File(file));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img,filename.substring(filename.indexOf(".")+1), out);
		return out.toByteArray();
	}
	/**
	 * save a byte[] data of module picture into file
	 * @param filename
	 * @param fileData
	 * @throws Exception
	 */
	public synchronized static void writeModulePicture(String provider,String filename,byte[] fileData) throws Exception{
		String picturePath=getPicturePath(provider);
		String file=picturePath+File.separator+filename;
		ByteArrayInputStream in=new ByteArrayInputStream(fileData);
		BufferedImage img=ImageIO.read(in);
		in.close();
		ImageIO.write(img, filename.substring(filename.indexOf(".")+1), new File(file));
	}
	public synchronized static void removeModulePicture(MeveoModule meveoModule,String filename) throws Exception{
		String picturePath=getPicturePath(meveoModule);
		filename=picturePath+File.separator+filename;
		File file=new File(filename);
		if(file.exists()){
			file.delete();
		}
	}
}
