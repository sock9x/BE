package com.px.tool.domain.phuongan.repository;

import com.px.tool.domain.phuongan.DinhMucLaoDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DinhMucLaoDongRepository extends JpaRepository<DinhMucLaoDong, Long> {
    @Modifying
    @Query("DELETE FROM DinhMucLaoDong dmld WHERE dmld.dmId IN ?1")
    void deleteAllByIds(Collection<Long> ids);
}
