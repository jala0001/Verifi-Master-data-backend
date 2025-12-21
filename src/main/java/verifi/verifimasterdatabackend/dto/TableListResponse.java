package verifi.verifimasterdatabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableListResponse {

    private Long id;
    private String tableName;
    private String description;
    private Integer columnCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}