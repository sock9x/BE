package com.px.tool.domain.dathang.service;

import com.px.tool.domain.dathang.PhieuDatHang;
import com.px.tool.domain.dathang.PhieuDatHangPayload;

import java.util.List;

public interface PhieuDatHangService {
    PhieuDatHangPayload findById(Long userId, Long id);

    List<PhieuDatHang> findByPhongBan(Long userId);

    PhieuDatHang save(Long userId, PhieuDatHangPayload phieuDatHangPayload);

    PhieuDatHang save(PhieuDatHang phieuDatHang);

    List<PhieuDatHang> findListCongViecCuaTLKT(Long userId);
}
