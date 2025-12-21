package verifi.verifimasterdatabackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableRequest {

    @NotBlank(message = "Table name is required")
    @Size(min = 1, max = 100, message = "Table name must be between 1 and 100 characters")
    private String tableName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotEmpty(message = "At least one column is required")
    @Valid
    private List<ColumnDefinitionDto> columns;
}