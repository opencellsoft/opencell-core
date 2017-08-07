package org.meveo.api.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.admin.FileDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class FilesApi extends BaseApi {

	private ParamBean paramBean = ParamBean.getInstance();

	private String providerRootDir;

	public FilesApi() {
		providerRootDir = paramBean.getProperty("providers.rootDir", "./opencelldata");
	}

	public String getProviderRootDir() {
		return providerRootDir;
	}

	public List<FileDto> listFiles(String dir) throws BusinessApiException {
		if (!StringUtils.isBlank(dir)) {
			dir = getProviderRootDir() + File.separator + dir;
		} else {
			dir = getProviderRootDir();
		}

		File folder = new File(dir);

		if (folder.isFile()) {
			throw new BusinessApiException("Path " + dir + " is a file.");
		}

		List<FileDto> result = new ArrayList<FileDto>();

		List<File> files = Arrays.asList(folder.listFiles());
		if (files != null) {
			for (File file : files) {
				result.add(new FileDto(file));
			}
		}

		return result;
	}

	public void createDir(String dir) {
		
	}

	public void suppressFile(String file) {
		// TODO Auto-generated method stub

	}

	public void suppressDir(String dir) {
		// TODO Auto-generated method stub

	}

}
