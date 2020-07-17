package com.px.tool.domain.user.repository;

import com.px.tool.domain.user.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {

    @Query(value = "SELECT f.* FROM folder f WHERE f.user_id = ?1", nativeQuery = true)
    List<Folder> findAll(Long userId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO folder (name, user_id) VALUES (?1, ?2)", nativeQuery = true)
    void insertFolder(String name, Long userId);
}
