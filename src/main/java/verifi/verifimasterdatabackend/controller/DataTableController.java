package verifi.verifimasterdatabackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import verifi.verifimasterdatabackend.dto.CreateTableRequest;
import verifi.verifimasterdatabackend.dto.TableListResponse;
import verifi.verifimasterdatabackend.dto.TableResponse;
import verifi.verifimasterdatabackend.service.DataTableService;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class DataTableController {

    private final DataTableService dataTableService;

    @PostMapping
    public ResponseEntity<TableResponse> createTable(@Valid @RequestBody CreateTableRequest request) {
        TableResponse response = dataTableService.createTable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TableListResponse>> getAllTables() {
        List<TableListResponse> tables = dataTableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(@PathVariable Long id) {
        TableResponse response = dataTableService.getTableById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        dataTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}