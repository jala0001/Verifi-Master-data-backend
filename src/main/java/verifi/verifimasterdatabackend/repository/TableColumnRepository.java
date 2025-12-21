package verifi.verifimasterdatabackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import verifi.verifimasterdatabackend.entity.TableColumn;

import java.util.List;

@Repository
public interface TableColumnRepository extends JpaRepository<TableColumn, Long> {

    List<TableColumn> findByDataTableIdOrderByColumnOrder(Long dataTableId);
}