package verifi.verifimasterdatabackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import verifi.verifimasterdatabackend.dto.*;
import verifi.verifimasterdatabackend.entity.DataTable;
import verifi.verifimasterdatabackend.entity.TableColumn;
import verifi.verifimasterdatabackend.repository.DataTableRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataTableService {

    private final DataTableRepository dataTableRepository;
    private final JsonFileService jsonFileService;

    @Transactional
    public TableResponse createTable(CreateTableRequest request) {
        // Tjek om tabelnavn allerede eksisterer
        if (dataTableRepository.existsByTableName(request.getTableName())) {
            throw new RuntimeException("Table with name '" + request.getTableName() + "' already exists");
        }

        // Opret DataTable entity
        DataTable dataTable = new DataTable();
        dataTable.setTableName(request.getTableName());
        dataTable.setDescription(request.getDescription());

        // JSON fil path
        String jsonFilePath = "data/" + request.getTableName() + ".json";
        dataTable.setJsonFilePath(jsonFilePath);

        // Opret kolonner
        List<TableColumn> columns = request.getColumns().stream()
                .map(colDto -> {
                    TableColumn column = new TableColumn();
                    column.setColumnName(colDto.getColumnName());
                    column.setDataType(colDto.getDataType());
                    column.setRequired(colDto.getRequired());
                    column.setColumnOrder(request.getColumns().indexOf(colDto));
                    column.setDataTable(dataTable);
                    return column;
                })
                .collect(Collectors.toList());

        dataTable.setColumns(columns);

        // Gem i database
        DataTable savedTable = dataTableRepository.save(dataTable);

        // Opret tom JSON fil
        jsonFileService.createEmptyJsonFile(jsonFilePath);

        return mapToTableResponse(savedTable);
    }

    @Transactional(readOnly = true)
    public List<TableListResponse> getAllTables() {
        return dataTableRepository.findAll().stream()
                .map(this::mapToTableListResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TableResponse getTableById(Long id) {
        DataTable dataTable = dataTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));
        return mapToTableResponse(dataTable);
    }

    @Transactional
    public void deleteTable(Long id) {
        DataTable dataTable = dataTableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + id));

        // Slet JSON fil
        jsonFileService.deleteJsonFile(dataTable.getJsonFilePath());

        // Slet fra database
        dataTableRepository.delete(dataTable);
    }

    private TableResponse mapToTableResponse(DataTable dataTable) {
        List<ColumnResponse> columnResponses = dataTable.getColumns().stream()
                .map(col -> new ColumnResponse(
                        col.getId(),
                        col.getColumnName(),
                        col.getDataType(),
                        col.getRequired(),
                        col.getColumnOrder()
                ))
                .collect(Collectors.toList());

        return new TableResponse(
                dataTable.getId(),
                dataTable.getTableName(),
                dataTable.getDescription(),
                dataTable.getJsonFilePath(),
                columnResponses,
                dataTable.getCreatedAt(),
                dataTable.getUpdatedAt()
        );
    }

    private TableListResponse mapToTableListResponse(DataTable dataTable) {
        return new TableListResponse(
                dataTable.getId(),
                dataTable.getTableName(),
                dataTable.getDescription(),
                dataTable.getColumns().size(),
                dataTable.getCreatedAt(),
                dataTable.getUpdatedAt()
        );
    }
}