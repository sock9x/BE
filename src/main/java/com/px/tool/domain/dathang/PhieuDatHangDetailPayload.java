package com.px.tool.domain.dathang;

import com.px.tool.domain.kiemhong.KiemHongDetail;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class PhieuDatHangDetailPayload extends AbstractObject {
    public Long pdhDetailId;
    private String stt;
    private String tenPhuKien;
    private String tenVatTuKyThuat;
    private String kiMaHieu;
    private String dvt;
    private String sl;
    private Long mucDichSuDung;
    private String phuongPhapKhacPhuc;
    private String soPhieuDatHang;
    private String nguoiThucHien;

    public static PhieuDatHangDetailPayload fromEntity(PhieuDatHangDetail phieuDatHangDetail) {
        PhieuDatHangDetailPayload phieuDatHangDetailPayload = new PhieuDatHangDetailPayload();
        BeanUtils.copyProperties(phieuDatHangDetail, phieuDatHangDetailPayload);
        return phieuDatHangDetailPayload;
    }

    public PhieuDatHangDetail toEntity() {
        PhieuDatHangDetail phieuDatHangDetail = new PhieuDatHangDetail();
//        if (pdhDetailId != null && pdhDetailId <= 0) {
        pdhDetailId = null;
//        }
        BeanUtils.copyProperties(this, phieuDatHangDetail);
        return phieuDatHangDetail;
    }

    public String getMucDicSuDungAsString() {
        return mucDichSuDung == null ? "0" : mucDichSuDung.toString();
    }

    public KiemHongDetail toKiemHongDetailEntity() {
        KiemHongDetail kiemHongDetail = new KiemHongDetail();
        kiemHongDetail.setKhDetailId(null);
//        kiemHongDetail.setTt();
        kiemHongDetail.setTenPhuKien(tenPhuKien);
        kiemHongDetail.setTenLinhKien(tenVatTuKyThuat);
        kiemHongDetail.setKyHieu(kiMaHieu);
        kiemHongDetail.setSl(sl);
        kiemHongDetail.setDvt(dvt);
//        kiemHongDetail.setDangHuHong();
//        kiemHongDetail.setPhuongPhapKhacPhuc();
//        kiemHongDetail.setNguoiKiemHong();

        return kiemHongDetail;
    }
}
