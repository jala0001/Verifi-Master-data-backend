package verifi.verifimasterdatabackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import verifi.verifimasterdatabackend.dto.TableDataResponse;
import verifi.verifimasterdatabackend.dto.TableResponse;
import verifi.verifimasterdatabackend.entity.DataTable;
import verifi.verifimasterdatabackend.entity.TableColumn;
import verifi.verifimasterdatabackend.repository.DataTableRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TableDataService {

    private final DataTableRepository dataTableRepository;
    private final JsonFileService jsonFileService;
    private final DataTableService dataTableService;

    @Transactional(readOnly = true)
    public TableDataResponse getTableData(Long tableId) {
        DataTable dataTable = dataTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));

        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());
        TableResponse tableMetadata = dataTableService.getTableById(tableId);

        return new TableDataResponse(tableMetadata, rows);
    }

    @Transactional
    public Map<String, Object> addRow(Long tableId, Map<String, Object> rowData) {
        DataTable dataTable = dataTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));

        // Valider row data
        validateRowData(dataTable, rowData);

        // Læs eksisterende data
        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());

        // Tilføj ID til ny række
        String rowId = UUID.randomUUID().toString();
        rowData.put("id", rowId);

        // Tilføj række
        rows.add(rowData);

        // Gem til JSON fil
        jsonFileService.writeJsonFile(dataTable.getJsonFilePath(), rows);

        return rowData;
    }

    @Transactional
    public Map<String, Object> updateRow(Long tableId, String rowId, Map<String, Object> rowData) {
        DataTable dataTable = dataTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));

        // Valider row data
        validateRowData(dataTable, rowData);

        // Læs eksisterende data
        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());

        // Find og opdater række
        boolean found = false;
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            if (rowId.equals(row.get("id"))) {
                rowData.put("id", rowId); // Behold ID
                rows.set(i, rowData);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Row not found with id: " + rowId);
        }

        // Gem til JSON fil
        jsonFileService.writeJsonFile(dataTable.getJsonFilePath(), rows);

        return rowData;
    }

    @Transactional
    public void deleteRow(Long tableId, String rowId) {
        DataTable dataTable = dataTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));

        // Læs eksisterende data
        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());

        // Fjern række
        boolean removed = rows.removeIf(row -> rowId.equals(row.get("id")));

        if (!removed) {
            throw new RuntimeException("Row not found with id: " + rowId);
        }

        // Gem til JSON fil
        jsonFileService.writeJsonFile(dataTable.getJsonFilePath(), rows);
    }

    private void validateRowData(DataTable dataTable, Map<String, Object> rowData) {
        for (TableColumn column : dataTable.getColumns()) {
            Object value = rowData.get(column.getColumnName());

            // Tjek required felter
            if (column.getRequired() && (value == null || value.toString().trim().isEmpty())) {
                throw new RuntimeException("Required field '" + column.getColumnName() + "' is missing or empty");
            }

            // Valider datatype hvis værdi er til stede
            if (value != null && !value.toString().trim().isEmpty()) {
                validateDataType(column.getColumnName(), value, column.getDataType());
            }
        }
    }

    private void validateDataType(String columnName, Object value, verifi.verifimasterdatabackend.enums.DataType dataType) {
        try {
            switch (dataType) {
                case INTEGER:
                    Integer.parseInt(value.toString());
                    break;
                case DECIMAL:
                    Double.parseDouble(value.toString());
                    break;
                case DATE:
                    // Simpel dato validering (YYYY-MM-DD format)
                    if (!value.toString().matches("\\d{4}-\\d{2}-\\d{2}")) {
                        throw new RuntimeException("Invalid date format for '" + columnName + "'. Expected YYYY-MM-DD");
                    }
                    break;
                case BOOLEAN:
                    if (!(value instanceof Boolean)) {
                        throw new RuntimeException("Invalid boolean value for '" + columnName + "'");
                    }
                    break;
                case TEXT:
                    // Text accepterer alt
                    break;
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid " + dataType + " value for '" + columnName + "': " + value);
        }
    }
}