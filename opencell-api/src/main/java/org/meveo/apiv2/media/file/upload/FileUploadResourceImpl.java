package org.meveo.apiv2.media.file.upload;

import org.meveo.admin.storage.StorageFactory;
import org.meveo.api.admin.FilesApi;
import org.meveo.apiv2.media.MediaFile;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUploadResourceImpl implements FileUploadResource {

    @Inject
    private FilesApi filesApi;

    @Override
    public Response uploadFile(MediaFile file) {
        Path saveTo = resolePath(file.getLevel());
        try {
            if(Files.notExists(saveTo)){
                (new File(saveTo.toUri())).mkdirs();
            }
            Path savedFilePath = Path.of(saveTo.toString(), URLDecoder.decode(file.getFileName(), StandardCharsets.UTF_8));
            byte[] data = file.getData() != null ? file.getData() : downloadFile(file.getFileUrl());
            StorageFactory.write(savedFilePath, data);
            return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"media file successfully uploaded\"}," +
                    "\"URL\": \"/opencell/files/"+savedFilePath.subpath(3,savedFilePath.getNameCount())+"\"} ").build();
        } catch (IOException e) {
            throw new BadRequestException("there was an issue during file creation : " + e.getCause());
        }
    }

    private byte[] downloadFile(URL fileUrl) throws IOException {
        if(fileUrl == null){
            throw new BadRequestException("there was an issue during file creation : no file or URL was provided");
        }
        return fileUrl.openStream().readAllBytes();
    }

    private Path resolePath(MediaFile.LevelEnum level) {
        if(level == null){
            return  Path.of(filesApi.getProviderRootDir() + File.separator + "media" + File.separator);
        }
        switch (level) {
            case OFFER_TEMPLATE:
                return Path.of(filesApi.getProviderRootDir() + File.separator + "media" + File.separator + "offerTemplate" + File.separator);
            case PRODUCT:
                return  Path.of(filesApi.getProviderRootDir() + File.separator + "media" + File.separator + "product" + File.separator);
            case QUOTE:
                return  Path.of(filesApi.getProviderRootDir() + File.separator + "media" + File.separator + "quote" + File.separator);
            default:
                return  Path.of(filesApi.getProviderRootDir() + File.separator + "media" + File.separator);
        }
    }
}
