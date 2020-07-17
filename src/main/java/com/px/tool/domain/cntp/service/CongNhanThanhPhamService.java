package com.px.tool.domain.cntp.service;

import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.CongNhanThanhPhamPayload;

public interface CongNhanThanhPhamService {

    CongNhanThanhPham saveCongNhanThanhPham(Long userId, CongNhanThanhPhamPayload congNhanThanhPhamPayload);

    CongNhanThanhPhamPayload timCongNhanThanhPham(Long userId, Long id);

}
