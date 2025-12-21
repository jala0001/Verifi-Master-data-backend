package verifi.verifimasterdatabackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import verifi.verifimasterdatabackend.entity.DataTable;

import java.util.Optional;

@Repository
public interface DataTableRepository extends JpaRepository<DataTable, Long> {

    Optional<DataTable> findByTableName(String tableName);

    boolean existsByTableName(String tableName);
}