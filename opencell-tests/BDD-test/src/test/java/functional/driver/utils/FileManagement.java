package functional.driver.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileManagement {
    public static String readEntityDto(String featurePath, String filename) {
        File featureFile = new File("src/test/resources/" + featurePath);
        String jsonPath = featureFile.getParentFile().getAbsolutePath() + "/json/" + filename;

        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(jsonPath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }
}
