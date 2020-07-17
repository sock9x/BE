package com.px.tool.domain.kiemhong.service;

import com.px.tool.domain.kiemhong.KiemHongPayLoad;

import java.util.List;

public interface KiemHongService {
    KiemHongPayLoad findThongTinKiemHong(Long userId, Long id);

    List<KiemHongPayLoad> findThongTinKiemHongCuaPhongBan(Long userId);



    /**
     * Buoc dau tien tao yeu cau kiem hong
     *
     * @param userId
     * @param kiemHongPayLoad
     * @return
     */
    KiemHongPayLoad save(Long userId, KiemHongPayLoad kiemHongPayLoad);

    KiemHongPayLoad capNhatKiemHong(Long userId, KiemHongPayLoad kiemHongPayLoad);

    boolean isExisted(Long id);
}
