package verifi.verifimasterdatabackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JsonFileService {

    private final ObjectMapper objectMapper;

    public JsonFileService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void createEmptyJsonFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());

            List<Map<String, Object>> emptyList = new ArrayList<>();
            objectMapper.writeValue(path.toFile(), emptyList);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create JSON file: " + filePath, e);
        }
    }

    public List<Map<String, Object>> readJsonFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }

    public void writeJsonFile(String filePath, List<Map<String, Object>> data) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            objectMapper.writeValue(path.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON file: " + filePath, e);
        }
    }

    public void deleteJsonFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete JSON file: " + filePath, e);
        }
    }
}