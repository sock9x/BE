package com.px.tool.domain.user.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.infrastructure.model.payload.AbstractObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class NoiNhanRequestParams extends AbstractObject {
    private Long requestId;
    private Boolean toTruong;
    private Boolean troLyKT;
    private Boolean quanDoc;
    private Boolean nguoiDatHang;
    private Boolean nguoiLap;
    private Boolean tpVatTu;
    private Boolean tpKeHoach;
    private Boolean tpKTHK;
    private Boolean giamDoc;
    private Boolean tpKCS;
    private Boolean nguoiThucHien;
    private Boolean nguoiGiaoViec;
    private Long userId;
    private RequestType type;

    public Boolean getToTruong() {
        return getBol(toTruong);
    }

    public Boolean getTroLyKT() {
        return getBol(troLyKT);
    }

    public Boolean getQuanDoc() {
        return getBol(quanDoc);
    }

    public Boolean getNguoiDatHang() {
        return getBol(nguoiDatHang);
    }

    public Boolean getNguoiLap() {
        return getBol(nguoiLap);
    }

    public Boolean getTpVatTu() {
        return getBol(tpVatTu);
    }

    public Boolean getTpKeHoach() {
        return getBol(tpKeHoach);
    }

    public Boolean getTpKTHK() {
        return getBol(tpKTHK);
    }

    public Boolean getGiamDoc() {
        return getBol(giamDoc);
    }

    public Boolean getTpKCS() {
        return getBol(tpKCS);
    }

    public Boolean getNguoiThucHien() {
        return getBol(nguoiThucHien);
    }

    public Boolean getNguoiGiaoViec() {
        return getBol(nguoiGiaoViec);
    }
}
