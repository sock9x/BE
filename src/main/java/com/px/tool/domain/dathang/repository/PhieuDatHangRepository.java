package com.px.tool.domain.dathang.repository;

import com.px.tool.domain.dathang.PhieuDatHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuDatHangRepository extends JpaRepository<PhieuDatHang, Long> {
    List<PhieuDatHang> findByCreatedBy(Long userId);

    @Query("SELECT pdh FROM PhieuDatHang pdh WHERE pdh.trolyKT =?1")
    List<PhieuDatHang> findListCongViecCuaTLKT(Long userId);
}
