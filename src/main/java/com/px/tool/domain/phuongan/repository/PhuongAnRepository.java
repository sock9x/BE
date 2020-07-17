package com.px.tool.domain.phuongan.repository;

import com.px.tool.domain.phuongan.PhuongAn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PhuongAnRepository extends JpaRepository<PhuongAn, Long>, PhuongAnRepositoryCustom {

    @Modifying
    @Transactional
    @Query(value = "UPDATE phuong_an rq SET rq.tp_id = ?2 WHERE rq.pa_id = ?1", nativeQuery = true)
    void updateCNTP(Long paId, Long cntpId);

    @Query("SELECT pa FROM PhuongAn  pa WHERE pa.cntpReceiverId = ?1 OR pa.phuongAnReceiverId = ?1")
    List<PhuongAn> findByUserId(Long userId);

    @Query("SELECT pa FROM PhuongAn  pa WHERE (pa.createdAt >= ?1 AND pa.createdAt <= ?2) OR (pa.updatedAt >= ?1 AND pa.updatedAt <= ?2)")
    List<PhuongAn> find(Long fromDate, Long toDate);

    @Query("SELECT MAX(pa.paId) FROM PhuongAn  pa")
    Long findPaIDMax();
}
