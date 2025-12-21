package verifi.verifimasterdatabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDataResponse {
    private TableResponse tableMetadata;
    private List<Map<String, Object>> rows;
}