package org.meveo.apiv2.media.file.upload;

import org.meveo.api.admin.FilesApi;
import org.meveo.apiv2.media.MediaFile;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUploadResourceImpl implements FileUploadResource {

    @Inject
    private FilesApi filesApi;

    @Override
    public Response uploadFile(MediaFile file) {
        file.getFileName();
        Path saveTo = resolePath(file.getLevel());
        try {
            if(Files.notExists(saveTo)){
                (new File(saveTo.toUri())).mkdirs();
            }
            Path savedFilePath = Path.of(saveTo.toString(), file.getFileName());
            Files.write(savedFilePath, file.getData());
            return Response.ok().entity("{\"actionStatus\":{\"status\":\"SUCCESS\",\"message\":\"media file successfully uploaded\"}," +
                    "\"URL\": \"/opencell/files/media/"+savedFilePath.subpath(3,savedFilePath.getNameCount())+"\"} ").build();
        } catch (IOException e) {
            throw new BadRequestException("there was an issue during file creation!");
        }
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
