package org.meveo.admin.ftp;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.User;
import org.meveo.admin.ftp.event.FileDelete;
import org.meveo.admin.ftp.event.FileDownload;
import org.meveo.admin.ftp.event.FileRename;
import org.meveo.admin.ftp.event.FileUpload;
import org.meveo.model.mediation.ActionEnum;
import org.meveo.model.mediation.MeveoFtpFile;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

@Stateless
public class MeveoDefaultFtplet extends DefaultFtplet {
	@Inject
	private Logger log;

	@Inject
	@FileUpload
	private Event<MeveoFtpFile> upload;

	@Inject
	@FileDownload
	private Event<MeveoFtpFile> download;

	@Inject
	@FileDelete
	private Event<MeveoFtpFile> delete;

	@Inject
	@FileRename
	private Event<MeveoFtpFile> rename;

	@Inject
	private UserService userService;

	@Override
	public FtpletResult onDeleteStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp start delete... ");
		MeveoFtpFile file = getEventFile(session, request,ActionEnum.DELETE);
		if (file != null) {
			delete.fire(file);
		}
		return super.onDownloadStart(session, request);
	}
	@Override
	public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp end download ...");
		MeveoFtpFile file = getEventFile(session, request,ActionEnum.DOWNLOAD);
		if (file != null) {
			download.fire(file);
		}
		return super.onDownloadEnd(session, request);
	}

	@Override
	public FtpletResult onRenameEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		MeveoFtpFile file = getEventFile(session, request,ActionEnum.RENAME);
		if (file != null) {
			rename.fire(file);
		}
		return super.onRenameEnd(session, request);
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp end upload... ");
		MeveoFtpFile file = getEventFile(session, request,ActionEnum.UPLOAD);
		if (file != null) {
			upload.fire(file);
		}
		return super.onUploadEnd(session, request);
	}
	private MeveoFtpFile getEventFile(FtpSession session, FtpRequest request, ActionEnum action) throws FtpException {
		org.apache.ftpserver.ftplet.FtpFile ftp = session.getFileSystemView().getFile(request.getArgument());
		log.debug("ftp file {} is existed {}", ftp.getAbsolutePath(), ftp.doesExist());
		if (ftp.doesExist()) {
			MeveoFtpFile file = new MeveoFtpFile(ftp.getAbsolutePath(), ftp.getSize(), ftp.getLastModified());
			User user = session.getUser();
			file.setAction(action);
			org.meveo.model.admin.User meveoUser = userService.findByUsername(user.getName());
			log.debug("trace ftp file {}", file);
			return file;
		}
		return null;
	}
}
