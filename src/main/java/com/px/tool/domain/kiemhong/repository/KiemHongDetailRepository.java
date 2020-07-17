package com.px.tool.domain.kiemhong.repository;

import com.px.tool.domain.kiemhong.KiemHongDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
public interface KiemHongDetailRepository extends JpaRepository<KiemHongDetail, Long> {
    @Modifying
    @Query("DELETE FROM KiemHongDetail khdt WHERE khdt.khDetailId IN ?1")
    void deleteAllByIds(Collection<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE KiemHongDetail rq SET rq.paId =?1 WHERE rq.khDetailId IN ?2")
    void taoPhuongAn(Long paId, Collection<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE KiemHongDetail rq SET rq.deleted = true WHERE rq.kiemHong.khId = ?1")
    void deleteByKhId(Long khId);
}