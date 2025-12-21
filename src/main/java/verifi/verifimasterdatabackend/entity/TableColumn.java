package verifi.verifimasterdatabackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import verifi.verifimasterdatabackend.enums.DataType;

@Entity
@Table(name = "table_columns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableColumn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String columnName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataType dataType;

    @Column(nullable = false)
    private Boolean required = false;

    @Column(nullable = false)
    private Integer columnOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_table_id", nullable = false)
    private DataTable dataTable;
}