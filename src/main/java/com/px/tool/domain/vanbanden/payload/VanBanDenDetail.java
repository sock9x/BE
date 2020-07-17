package com.px.tool.domain.vanbanden.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.infrastructure.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class VanBanDenDetail {
    private Long vbdId;
    private String noiNhan;
    private String noiDung;
    private String soPa;
    private List<Long> cusReceivers;
    private List<String> files;
    private Long requestId;
    private RequestType requestType;

    public static VanBanDenDetail fromEntity(VanBanDen vanBanDen, Map<Long, String> noiNhanById) {
        VanBanDenDetail payload = new VanBanDenDetail();
        payload.vbdId = vanBanDen.getVbdId();
        payload.cusReceivers = CommonUtils.toCollection(vanBanDen.getNoiNhan());
        payload.noiNhan = CommonUtils.toString(payload.cusReceivers, noiNhanById);
        payload.noiDung = vanBanDen.getNoiDung();
        payload.soPa = vanBanDen.getSoPa();
        payload.requestId = vanBanDen.getRequestId();
        payload.requestType = vanBanDen.getRequestType();
        return payload;
    }

    public VanBanDenDetail withFilesName(List<String> listFile) {
        this.files = listFile;
        return this;
    }
}
