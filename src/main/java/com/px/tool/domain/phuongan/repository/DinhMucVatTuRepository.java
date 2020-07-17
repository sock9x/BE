package com.px.tool.domain.phuongan.repository;

import com.px.tool.domain.phuongan.DinhMucVatTu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DinhMucVatTuRepository extends JpaRepository<DinhMucVatTu, Long> {
    @Modifying
    @Query("DELETE FROM DinhMucVatTu dmvt WHERE dmvt.vtId IN ?1")
    void deleteAllByIds(Collection<Long> ids);
}
