package com.px.tool.domain.phuongan.repository;

import com.px.tool.domain.request.NguoiDangXuLy;

public interface PhuongAnRepositoryCustom {
    NguoiDangXuLy findDetail(Long phuongAnId);

}
