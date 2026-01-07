package verifi.verifimasterdatabackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import verifi.verifimasterdatabackend.entity.DataTable;
import verifi.verifimasterdatabackend.entity.TableColumn;
import verifi.verifimasterdatabackend.enums.DataType;
import verifi.verifimasterdatabackend.repository.DataTableRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableDataServiceTest {

    @Mock
    private DataTableRepository dataTableRepository;

    @Mock
    private JsonFileService jsonFileService;

    @InjectMocks
    private TableDataService tableDataService;

    private DataTable testTable;

    @BeforeEach
    void setUp() {
        testTable = new DataTable();
        testTable.setId(1L);
        testTable.setTableName("TestTable");
        testTable.setJsonFilePath("data/TestTable.json");

        TableColumn col1 = new TableColumn();
        col1.setColumnName("age");
        col1.setDataType(DataType.INTEGER);
        col1.setRequired(true);
        col1.setDataTable(testTable);

        TableColumn col2 = new TableColumn();
        col2.setColumnName("price");
        col2.setDataType(DataType.DECIMAL);
        col2.setRequired(false);
        col2.setDataTable(testTable);

        testTable.setColumns(Arrays.asList(col1, col2));
    }

    @Test
    void testAddRowWithValidIntegerAndDecimal() {
        // Arrange
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("age", "25");
        rowData.put("price", "99.99");

        when(dataTableRepository.findById(1L)).thenReturn(Optional.of(testTable));
        when(jsonFileService.readJsonFile(anyString())).thenReturn(new ArrayList<>());

        // Act
        Map<String, Object> result = tableDataService.addRow(1L, rowData);

        // Assert
        assertNotNull(result);
        assertEquals(25, result.get("age"), "Integer should be stored as number");
        assertEquals(99.99, result.get("price"), "Decimal should be stored as number");
        verify(jsonFileService, times(1)).writeJsonFile(anyString(), anyList());
    }

    @Test
    void testAddRowWithInvalidInteger() {
        // Arrange
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("age", "not-a-number");
        rowData.put("price", "50.00");

        when(dataTableRepository.findById(1L)).thenReturn(Optional.of(testTable));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tableDataService.addRow(1L, rowData));

        assertTrue(exception.getMessage().contains("Invalid INTEGER"));
    }

    @Test
    void testAddRowWithMissingRequiredField() {
        // Arrange
        Map<String, Object> rowData = new HashMap<>();
        rowData.put("price", "50.00");
        // 'age' is required but missing

        when(dataTableRepository.findById(1L)).thenReturn(Optional.of(testTable));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tableDataService.addRow(1L, rowData));

        assertTrue(exception.getMessage().contains("Required field"));
    }
}