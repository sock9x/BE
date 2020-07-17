package com.px.tool.domain.request.repository;

import com.px.tool.domain.request.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>, RequestRepositoryCustom {
    @Query("SELECT rq FROM Request rq WHERE rq.createdBy IN ?1")
    List<Request> findByNguoiGui(Collection<Long> userIds);

    @Query("SELECT rq FROM Request rq WHERE rq.kiemHongReceiverId IN ?1 OR rq.phieuDatHangReceiverId IN ?1")
    Page<Request> findByNguoiNhan(Collection<Long> userIds, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Request rq SET rq.kiemHongReceiverId =?2, rq.phieuDatHangReceiverId = ?3, rq.phuongAnReceiverId = ?4, rq.cntpReceiverId =?5 WHERE rq.requestId = ?1")
    void updateReceiverId(Long requestId, Long kiemHongReceiverId, Long phieuDatHangReceiverId, Long phuongAnReceiverId, Long cntpReceiverId);

    @Query("SELECT rq " +
            "FROM Request rq FETCH ALL PROPERTIES " +
            "WHERE (rq.deleted <> true OR rq.deleted is null) AND rq.createdAt >= :fromDate AND rq.createdAt <= :toDate " +
            "ORDER BY rq.createdAt DESC")
    Page<Request> findPaging(Pageable pageable, @Param("fromDate") Long fromDate, @Param("toDate") Long toDate);

    @Query("SELECT rq " +
            "FROM Request rq FETCH ALL PROPERTIES " +
            "WHERE (rq.deleted <> true OR rq.deleted is null) AND rq.createdAt >= :fromDate AND rq.createdAt <= :toDate " +
            "AND rq.kiemHong.toTruongId = :toTruongId " +
            "ORDER BY rq.createdAt DESC")
    Page<Request> findPaging(Pageable pageable, @Param("toTruongId") Long toTruongId, @Param("fromDate") Long fromDate, @Param("toDate") Long toDate);

    @Modifying
    @Transactional
    @Query("UPDATE Request rq SET rq.ngayGui =?1 WHERE rq.requestId = ?2")
    void updateNgayGui(long nowAsMilliSec, Long requestId);

    @Query("SELECT rq FROM Request rq WHERE (rq.createdAt >= ?1 AND rq.createdAt <= ?2) OR (rq.updatedAt >= ?1 AND rq.updatedAt <= ?2)")
    List<Request> find(Long fromDate, Long toDate);
}