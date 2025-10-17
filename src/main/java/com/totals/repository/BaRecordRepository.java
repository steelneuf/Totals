package com.totals.repository;

import com.totals.dto.BaTotalsResponse;
import com.totals.model.BaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BaRecordRepository extends JpaRepository<BaRecord, Long> {

    List<BaRecord> findByKenmerk(String kenmerk);
    
    long countByKenmerk(String kenmerk);

    @Query(value = "SELECT " +
                   "    kenmerk, " +
                   "    SUM(cijfer1) as totaal1, " +
                   "    SUM(cijfer2) as totaal2, " +
                   "    SUM(cijfer3) as totaal3, " +
                   "    SUM(cijfer4) as totaal4, " +
                   "    SUM(cijfer5) as totaal5, " +
                   "    SUM(cijfer6) as totaal6, " +
                   "    COUNT(*) as recordCount " +
                   "FROM ba_records " +
                   "WHERE kenmerk = :kenmerk " +
                   "GROUP BY kenmerk", 
           nativeQuery = true)
    Optional<BaTotalsResponse> calculateTotalsByKenmerk(@Param("kenmerk") String kenmerk);
}
