package org.meveo.admin.ftp;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

public class FtpTest {

	private static FileSystemOptions getSftpOptions(boolean isSftp) throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		if (isSftp) {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
		}
		return opts;
	}

	public static void main(String[] args) throws Exception {
		String ftpAddress = "ftp://billing:billing@ec2-54-172-112-179.compute-1.amazonaws.com:21/";
		FileSystemManager manager=null;  
		System.out.println("start ftp connector...");
		try {
			FileSystemOptions opts = new FileSystemOptions();
			FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, false);
			FtpFileSystemConfigBuilder.getInstance( ).setUserDirIsRoot(opts,true);
			manager = VFS.getManager();
			FileObject fileObject = manager.resolveFile(ftpAddress,opts);
			System.out.println("children of " + fileObject.getName().getURI());
			
//			FileObject[] fileObjects = fileObject.getChildren();
			FileObject[] fileObjects=fileObject.findFiles(new FileSelector() {
				@Override
				public boolean traverseDescendents(FileSelectInfo info) throws Exception {
					return true;
				}
				@Override
				public boolean includeFile(FileSelectInfo info) throws Exception {
					return true;
				}
			});

			System.out.println(fileObjects == null ? 0 : fileObjects.length);
			if (fileObjects != null) {
				for (FileObject o : fileObjects) {
					System.out.println("o###" + o);
					if (o.getType() == FileType.FILE) {
						String fileName = o.getName().getBaseName();
						FileContent c = o.getContent();
						long lastModified = c.getLastModifiedTime();
						long size = c.getSize();
						System.out
								.println("read file " + fileName + " ,size=" + size + " ,lastModified=" + lastModified);
					} else {
						System.out.println("folder " + o.getName().getBaseName());
					}
				}
			} else {
				System.out.println("null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			((StandardFileSystemManager)manager).close();
		}
		System.out.println("end!");
	}
}