package com.px.tool.domain.dathang.repository;

import com.px.tool.domain.dathang.PhieuDatHangDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PhieuDatHangDetailRepository extends JpaRepository<PhieuDatHangDetail, Long> {
    @Modifying
    @Query("DELETE FROM PhieuDatHangDetail pdhd WHERE pdhd.pdhDetailId IN ?1")
    void deleteAllByIds(Collection<Long> ids);
}
