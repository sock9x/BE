package com.px.tool.domain.phuongan.repository.impl;

import com.px.tool.domain.phuongan.repository.PhuongAnRepositoryCustom;
import com.px.tool.domain.request.NguoiDangXuLy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PhuongAnRepositoryCustomImpl implements PhuongAnRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public NguoiDangXuLy findDetail(Long phuongAnId) {
        Object[] objects = (Object[]) entityManager.createNativeQuery("SELECT pa.giam_doc_id,pa.nguoi_lap_id, pa.truong_phongkthkid, pa.truong_phong_ke_hoach_id,pa.truong_phong_vat_tu_id " +
                "FROM phuong_an pa " +
                "WHERE " +
                "pa.pa_id = ?1")
                .setParameter(1, phuongAnId)
                .getSingleResult();
        NguoiDangXuLy nguoiDangXuLy = new NguoiDangXuLy();
        nguoiDangXuLy.setGiamDoc(objects[0]);
        nguoiDangXuLy.setNguoiLap(objects[1]);
        nguoiDangXuLy.setTpKTHK(objects[2]);
        nguoiDangXuLy.setTpKeHoach(objects[3]);
        nguoiDangXuLy.setTpVatTu(objects[4]);
        return nguoiDangXuLy;
    }
}
