package verifi.verifimasterdatabackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verifi.verifimasterdatabackend.dto.ColumnDefinitionDto;
import verifi.verifimasterdatabackend.dto.CreateTableRequest;
import verifi.verifimasterdatabackend.dto.TableResponse;
import verifi.verifimasterdatabackend.entity.DataTable;
import verifi.verifimasterdatabackend.enums.DataType;
import verifi.verifimasterdatabackend.repository.DataTableRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTableServiceTest {

    @Mock
    private DataTableRepository dataTableRepository;

    @Mock
    private JsonFileService jsonFileService;

    @InjectMocks
    private DataTableService dataTableService;

    @Test
    void testCreateTableWithColumns() {
        // Arrange
        ColumnDefinitionDto col1 = new ColumnDefinitionDto("Name", DataType.TEXT, true);
        ColumnDefinitionDto col2 = new ColumnDefinitionDto("Age", DataType.INTEGER, false);

        List<ColumnDefinitionDto> columns = Arrays.asList(col1, col2);

        CreateTableRequest request = new CreateTableRequest();
        request.setTableName("Employees");
        request.setDescription("Employee data");
        request.setColumns(columns);

        when(dataTableRepository.existsByTableName("Employees")).thenReturn(false);
        when(dataTableRepository.save(any(DataTable.class))).thenAnswer(invocation -> {
            DataTable table = invocation.getArgument(0);
            table.setId(1L);
            return table;
        });

        // Act
        TableResponse response = dataTableService.createTable(request);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("Employees", response.getTableName());
        assertEquals("Employee data", response.getDescription());
        assertEquals(2, response.getColumns().size(), "Should have 2 columns");
        assertEquals("Name", response.getColumns().get(0).getColumnName());
        assertEquals(DataType.TEXT, response.getColumns().get(0).getDataType());
        assertTrue(response.getColumns().get(0).getRequired());

        verify(jsonFileService, times(1)).createEmptyJsonFile(anyString());
        verify(dataTableRepository, times(1)).save(any(DataTable.class));
    }

    @Test
    void testCreateTableWithDuplicateName() {
        // Arrange
        CreateTableRequest request = new CreateTableRequest();
        request.setTableName("DuplicateTable");
        request.setColumns(Arrays.asList(new ColumnDefinitionDto("Col1", DataType.TEXT, false)));

        when(dataTableRepository.existsByTableName("DuplicateTable")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dataTableService.createTable(request));

        assertTrue(exception.getMessage().contains("already exists"));
        verify(dataTableRepository, never()).save(any());
    }
}