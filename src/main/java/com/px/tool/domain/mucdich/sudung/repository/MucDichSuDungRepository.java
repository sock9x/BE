package com.px.tool.domain.mucdich.sudung.repository;

import com.px.tool.domain.mucdich.sudung.MucDichSuDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MucDichSuDungRepository extends JpaRepository<MucDichSuDung, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE  MucDichSuDung  md SET md.delete = true WHERE md.mdId = ?1")
    void delete(Long id);

    @Query("SELECT md FROM MucDichSuDung md WHERE md.delete IS NULL OR md.delete <> true OR md.delete = false")
    List<MucDichSuDung> findAll();
}
