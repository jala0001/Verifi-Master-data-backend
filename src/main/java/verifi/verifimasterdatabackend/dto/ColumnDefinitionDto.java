package verifi.verifimasterdatabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import verifi.verifimasterdatabackend.enums.DataType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefinitionDto {

    @NotBlank(message = "Column name is required")
    private String columnName;

    @NotNull(message = "Data type is required")
    private DataType dataType;

    @NotNull(message = "Required flag must be specified")
    private Boolean required;
}