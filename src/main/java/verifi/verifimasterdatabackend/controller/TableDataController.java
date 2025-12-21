package verifi.verifimasterdatabackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import verifi.verifimasterdatabackend.dto.TableDataResponse;
import verifi.verifimasterdatabackend.service.TableDataService;

import java.util.Map;

@RestController
@RequestMapping("/api/tables/{tableId}/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TableDataController {

    private final TableDataService tableDataService;

    @GetMapping
    public ResponseEntity<TableDataResponse> getTableData(@PathVariable Long tableId) {
        TableDataResponse response = tableDataService.getTableData(tableId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addRow(
            @PathVariable Long tableId,
            @RequestBody Map<String, Object> rowData) {
        Map<String, Object> createdRow = tableDataService.addRow(tableId, rowData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRow);
    }

    @PutMapping("/{rowId}")
    public ResponseEntity<Map<String, Object>> updateRow(
            @PathVariable Long tableId,
            @PathVariable String rowId,
            @RequestBody Map<String, Object> rowData) {
        Map<String, Object> updatedRow = tableDataService.updateRow(tableId, rowId, rowData);
        return ResponseEntity.ok(updatedRow);
    }

    @DeleteMapping("/{rowId}")
    public ResponseEntity<Void> deleteRow(
            @PathVariable Long tableId,
            @PathVariable String rowId) {
        tableDataService.deleteRow(tableId, rowId);
        return ResponseEntity.noContent().build();
    }
}