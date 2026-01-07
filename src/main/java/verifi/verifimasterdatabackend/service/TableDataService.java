package verifi.verifimasterdatabackend.service;
import java.util.HashMap;
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

        // Konverter til korrekte datatyper
        Map<String, Object> convertedData = convertToCorrectTypes(dataTable, rowData);

        // Læs eksisterende data
        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());

        // Tilføj ID til ny række
        String rowId = UUID.randomUUID().toString();
        convertedData.put("id", rowId);

        // Tilføj række
        rows.add(convertedData);

        // Gem til JSON fil
        jsonFileService.writeJsonFile(dataTable.getJsonFilePath(), rows);

        return convertedData;
    }

    @Transactional
    public Map<String, Object> updateRow(Long tableId, String rowId, Map<String, Object> rowData) {
        DataTable dataTable = dataTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + tableId));

        // Valider row data
        validateRowData(dataTable, rowData);

        // Konverter til korrekte datatyper
        Map<String, Object> convertedData = convertToCorrectTypes(dataTable, rowData);

        // Læs eksisterende data
        List<Map<String, Object>> rows = jsonFileService.readJsonFile(dataTable.getJsonFilePath());

        // Find og opdater række
        boolean found = false;
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            if (rowId.equals(row.get("id"))) {
                convertedData.put("id", rowId); // Behold ID
                rows.set(i, convertedData);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Row not found with id: " + rowId);
        }

        // Gem til JSON fil
        jsonFileService.writeJsonFile(dataTable.getJsonFilePath(), rows);

        return convertedData;
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

    private Map<String, Object> convertToCorrectTypes(DataTable dataTable, Map<String, Object> rowData) {
        Map<String, Object> convertedData = new HashMap<>();

        for (TableColumn column : dataTable.getColumns()) {
            Object value = rowData.get(column.getColumnName());

            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                convertedData.put(column.getColumnName(), null);
                continue;
            }

            // Konverter baseret på datatype
            switch (column.getDataType()) {
                case INTEGER:
                    convertedData.put(column.getColumnName(), Integer.parseInt(value.toString()));
                    break;
                case DECIMAL:
                    convertedData.put(column.getColumnName(), Double.parseDouble(value.toString()));
                    break;
                case BOOLEAN:
                    convertedData.put(column.getColumnName(), value instanceof Boolean ? value : Boolean.parseBoolean(value.toString()));
                    break;
                case DATE:
                case TEXT:
                default:
                    // Behold som string
                    convertedData.put(column.getColumnName(), value.toString());
                    break;
            }
        }

        return convertedData;
    }

    private void validateDataType(String columnName, Object value, verifi.verifimasterdatabackend.enums.DataType dataType) {
        try {
            String strValue = value.toString().trim();

            switch (dataType) {
                case INTEGER:
                    Integer.parseInt(strValue);
                    break;

                case DECIMAL:
                    double decimalValue = Double.parseDouble(strValue);
                    // Tjek for max 2 decimaler
                    String[] parts = strValue.split("\\.");
                    if (parts.length == 2 && parts[1].length() > 2) {
                        throw new RuntimeException("Decimal value for '" + columnName +
                                "' can have maximum 2 decimal places. Got: " + strValue);
                    }
                    break;

                case DATE:
                    // Valider ISO format (YYYY-MM-DD)
                    if (!strValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        throw new RuntimeException("Invalid date format for '" + columnName +
                                "'. Expected YYYY-MM-DD, got: " + strValue);
                    }
                    break;

                case BOOLEAN:
                    if (!(value instanceof Boolean)) {
                        throw new RuntimeException("Invalid boolean value for '" + columnName + "'");
                    }
                    break;

                case TEXT:
                    // Maksimum 100 tegn
                    if (strValue.length() > 100) {
                        throw new RuntimeException("Text value for '" + columnName +
                                "' cannot exceed 100 characters. Got: " + strValue.length() + " characters");
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid " + dataType + " value for '" + columnName + "': " + value);
        }
    }
}