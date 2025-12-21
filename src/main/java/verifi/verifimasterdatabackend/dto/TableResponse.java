package verifi.verifimasterdatabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableResponse {

    private Long id;
    private String tableName;
    private String description;
    private String jsonFilePath;
    private List<ColumnResponse> columns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}