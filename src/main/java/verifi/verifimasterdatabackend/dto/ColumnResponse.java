package verifi.verifimasterdatabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import verifi.verifimasterdatabackend.enums.DataType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnResponse {

    private Long id;
    private String columnName;
    private DataType dataType;
    private Boolean required;
    private Integer columnOrder;
}