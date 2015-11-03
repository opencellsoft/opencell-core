package org.meveo.admin.ftp;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.User;
import org.meveo.admin.ftp.event.FileDelete;
import org.meveo.admin.ftp.event.FileDownload;
import org.meveo.admin.ftp.event.FileRename;
import org.meveo.admin.ftp.event.FileUpload;
import org.meveo.model.Auditable;
import org.meveo.model.mediation.ImportedFile;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;

@Stateless
public class MeveoDefaultFtplet extends DefaultFtplet {
	@Inject
	private Logger log;

	@Inject
	@FileUpload
	private Event<ImportedFile> upload;

	@Inject
	@FileDownload
	private Event<ImportedFile> download;

	@Inject
	@FileDelete
	private Event<ImportedFile> delete;

	@Inject
	@FileRename
	private Event<ImportedFile> rename;
	
	@Inject
	private UserService userService;

	@Override
	public FtpletResult onDeleteEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp end delete... ");
		FtpFile file = session.getFileSystemView().getFile(request.getArgument());
		if(!file.doesExist()){
			ImportedFile imported = new ImportedFile(file.getAbsolutePath(), file.getSize());
			User user=session.getUser();
			org.meveo.model.admin.User meveoUser=userService.findByUsername(user.getName());
			imported.setProvider(meveoUser.getProvider());
			imported.setDisabled(false);
			imported.setAuditable(new Auditable());
			imported.getAuditable().setCreator(meveoUser);
			log.debug("trace ftp file {}",imported);
		}
		return super.onDeleteEnd(session, request);
	}


	private ImportedFile getEventFile(FtpSession session, FtpRequest request) throws FtpException {
		FtpFile file = session.getFileSystemView().getFile(request.getArgument());
		log.debug("ftp file {} is existed {}",file.getAbsolutePath(),file.doesExist());
		if(file.doesExist()){
			ImportedFile imported = new ImportedFile(file.getAbsolutePath(), file.getSize(), file.getLastModified());
			User user=session.getUser();
			org.meveo.model.admin.User meveoUser=userService.findByUsername(user.getName());
			imported.setProvider(meveoUser.getProvider());
			imported.setDisabled(false);
			imported.setAuditable(new Auditable());
			imported.getAuditable().setCreator(meveoUser);
			log.debug("trace ftp file {}",imported);
			return imported;
		}
		return null;
	}

	@Override
	public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp end download ...");
		ImportedFile imported = getEventFile(session,request);
		if(imported!=null){
			download.fire(imported);
		}
		return super.onDownloadEnd(session, request);
	}

	@Override
	public FtpletResult onRenameEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		ImportedFile imported = getEventFile(session,request);
		if(imported!=null){
			rename.fire(imported);
		}
		return super.onRenameEnd(session, request);
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		log.debug("ftp end upload... ");
		ImportedFile imported = getEventFile(session,request);
		if(imported!=null){
			upload.fire(imported);
		}
		return super.onUploadEnd(session, request);
	}

}
