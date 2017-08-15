package org.meveo.api.rest.admin.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.admin.FilesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.admin.GetFilesResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.admin.FilesRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FilesRsImpl extends BaseRs implements FilesRs {

	@Inject
	private FilesApi filesApi;

	@Override
	public GetFilesResponseDto listFiles() {
		GetFilesResponseDto result = new GetFilesResponseDto();

		try {
			result.setFiles(filesApi.listFiles(null));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public GetFilesResponseDto listFiles(String dir) {
		GetFilesResponseDto result = new GetFilesResponseDto();

		try {
			result.setFiles(filesApi.listFiles(dir));
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

	@Override
	public ActionStatus createDir(String dir) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.createDir(dir);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus zipFile(String filePath) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.zipFile(filePath);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus zipDir(String dir) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.zipDir(dir);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}
	
	@Override
	public ActionStatus suppressFile(String filePath) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.suppressFile(filePath);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus suppressDir(String dir) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.suppressDir(dir);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public ActionStatus uploadFile(FileUploadForm form) {
		ActionStatus result = new ActionStatus();

		try {
			filesApi.uploadFile(form.getData(), form.getFilename());
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

}
