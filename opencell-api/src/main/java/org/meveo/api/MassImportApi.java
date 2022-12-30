package org.meveo.api;

import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;
import org.meveo.api.admin.FilesApi;
import org.meveo.api.dto.ImportFileTypeDto;
import org.meveo.api.dto.ImportTypesEnum;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.admin.impl.FileImportForm;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.bi.FlatFile;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ilham Chafik
 *
 */
@Stateless
public class MassImportApi {

    @Inject
    private ParamBeanFactory paramBeanFactory;


    @Inject
    private FilesApi filesApi;

    private final String TEMP_DIR = "imports/temp/";

    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }

    public List<ImportFileTypeDto> uploadAndImport(FileImportForm massImportForm) {
        if (StringUtils.isBlank(massImportForm.getData())) {
            throw new MissingParameterException("fileToImport");
        }

        List<ImportFileTypeDto> fileTypes = new ArrayList<>();
        List<String> filesToImport = new Gson().fromJson( massImportForm.getFiles(), List.class );
        try {
            String tempDir = getProviderRootDir() + File.separator + TEMP_DIR;
            Path path = Paths.get(tempDir);
            Files.createDirectories(path);

            String importTempDir = TEMP_DIR + massImportForm.getFilename();
            FlatFile flatFile = filesApi.uploadFile(massImportForm.getData(), importTempDir, null);

            File[] files = new File(tempDir).listFiles((file, s) -> filesToImport.contains(s));

            fileTypes = detectFileType(files);
            moveFiles(fileTypes);

            FileUtils.deleteDirectory(new File(tempDir));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileTypes;
    }

    private ImportTypesEnum getImportType(String importFilePath) {
        ImportTypesEnum importType = null;
        try {
            String line = Files.readAllLines(Paths.get(importFilePath)).get(1);
            String type = line.split(";")[0].replace("\"", "");
            if(!EnumUtils.isValidEnum(ImportTypesEnum.class, type)) {
                return ImportTypesEnum.UNKNOWN;
            } else {
                return ImportTypesEnum.valueOf(type);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<ImportFileTypeDto> detectFileType(File[] files) {
        ArrayList<ImportFileTypeDto> fileTypeList = new ArrayList<>();
        for (File file : files) {
            if (FilenameUtils.getExtension(file.getName()).equals("csv")) {
                String path = file.getPath();
                ImportTypesEnum importType = getImportType(path);
                ImportFileTypeDto importFileType = new ImportFileTypeDto(file.getName(), importType);
                fileTypeList.add(importFileType);
            }
        }
        return fileTypeList;
    }


    private String moveFiles(List<ImportFileTypeDto> filesType) {
        String tempDir = getProviderRootDir() + File.separator + TEMP_DIR;
        File[] files = new File(tempDir).listFiles();
        for (File file : files) {
            try {
                ImportFileTypeDto fileTypeDto = filesType.stream().filter(fileType -> file.getName().equals(fileType.getFileName()))
                        .findFirst()
                        .orElse(null);

                if(fileTypeDto != null && fileTypeDto.getFileType() != ImportTypesEnum.UNKNOWN) {
                    String toPath = getProviderRootDir() + File.separator
                            + ImportTypesEnum.valueOf(fileTypeDto.getFileType().toString()).path + File.separator + file.getName();
                    Files.createDirectories(Paths.get(toPath));
                    Files.move(Paths.get(file.getPath()), Paths.get(toPath), StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
