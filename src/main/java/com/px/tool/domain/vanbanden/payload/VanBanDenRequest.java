package com.px.tool.domain.vanbanden.payload;

import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.infrastructure.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class VanBanDenRequest {
    private Long vbdId;
    private String soPa;
    private String noiDung;
    private List<String> files;
    private List<Long> cusReceivers;

    public static VanBanDenRequest fromEntity(VanBanDen vanBanDen) {
        VanBanDenRequest payload = new VanBanDenRequest();
        payload.vbdId = vanBanDen.getVbdId();
        payload.noiDung = vanBanDen.getNoiDung();
        return payload;
    }

    public VanBanDen toEntity() {
        VanBanDen vanBanDen = new VanBanDen();
        vanBanDen.setVbdId(this.vbdId);
        vanBanDen.setNoiDung(this.noiDung);
        vanBanDen.setSoPa(this.soPa == null ? "Tự động" : soPa);
        vanBanDen.setNoiNhan(CommonUtils.toString(this.cusReceivers));
        return vanBanDen;
    }

}
