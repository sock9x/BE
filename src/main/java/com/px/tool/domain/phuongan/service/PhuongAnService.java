package com.px.tool.domain.phuongan.service;

import com.px.tool.domain.phuongan.PhuongAn;
import com.px.tool.domain.phuongan.PhuongAnPayload;
import com.px.tool.domain.phuongan.PhuongAnTaoMoi;
import com.px.tool.domain.phuongan.RequestTaoPhuongAnMoi;
import com.px.tool.domain.request.NguoiDangXuLy;

import java.util.List;
import java.util.Map;

public interface PhuongAnService {
    PhuongAnPayload findById(Long userId, Long id);

    List<PhuongAn> findByPhongBan(Long userId);

    PhuongAn save(Long userId, PhuongAnPayload phuongAnPayload);

    NguoiDangXuLy findNguoiDangXuLy(Long requsetId);

    PhuongAnTaoMoi taoPhuongAnMoi(Long userid, RequestTaoPhuongAnMoi requestTaoPhuongAnMoi);

    Map<Long, PhuongAn> groupById(List<Long> paIds);


    /**
     * Tôi đang được giao những việc gì trong phương án
     */
    List<PhuongAn> findByUserId(Long userId);
}
