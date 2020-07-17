package com.px.tool.domain.cntp.repository;

import com.px.tool.domain.cntp.NoiDungThucHien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface NoiDungThucHienRepository extends JpaRepository<NoiDungThucHien, Long> {
    @Modifying
    @Query("DELETE FROM NoiDungThucHien pdhd WHERE pdhd.noiDungId IN ?1")
    void deleteAllByIds(Collection<Long> ids);
}
