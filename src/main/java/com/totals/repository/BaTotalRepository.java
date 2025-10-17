package com.totals.repository;

import com.totals.model.BaTotalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BaTotalRepository extends JpaRepository<BaTotalRecord, Long> {
    Optional<BaTotalRecord> findByKenmerk(String kenmerk);
    
    @Modifying
    @Transactional
    @Query("UPDATE BaTotalRecord b SET b.totaal1 = :totaal1, b.totaal2 = :totaal2, b.totaal3 = :totaal3, " +
           "b.totaal4 = :totaal4, b.totaal5 = :totaal5, b.requestTimestamp = :newTimestamp " +
           "WHERE b.kenmerk = :kenmerk AND (b.requestTimestamp IS NULL OR b.requestTimestamp < :newTimestamp)")
    int updateIfNewer(@Param("kenmerk") String kenmerk, 
                     @Param("totaal1") Double totaal1,
                     @Param("totaal2") Double totaal2,
                     @Param("totaal3") Double totaal3,
                     @Param("totaal4") Double totaal4,
                     @Param("totaal5") Double totaal5,
                     @Param("newTimestamp") Long newTimestamp);
}
