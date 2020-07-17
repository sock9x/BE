package com.px.tool.domain.vanbanden.payload;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

@Getter
@Setter
public class VanBanDenResponse {
    private Long vbdId;
    private String noiNhan;
    private String noiDung;
    private String soPa;
    private String cusReceivers;

    private Long folder;
    private List<String> files;
    private String ngayGui;

    public static VanBanDenResponse fromEntity(VanBanDen vanBanDen) {
        VanBanDenResponse payload = new VanBanDenResponse();
        payload.vbdId = vanBanDen.getVbdId();
        payload.noiNhan = "noi nhan";
        payload.cusReceivers = "cusReceivers";
        payload.noiDung = vanBanDen.getNoiDung();
        payload.folder = vanBanDen.getFolder();
        payload.soPa = vanBanDen.getSoPa();
        payload.ngayGui = DateTimeUtils.dateLongToString(vanBanDen.getCreatedAt());
        return payload;
    }

    public static VanBanDenResponse fromRequest(VanBanDenRequest request) {
        VanBanDenResponse payload = new VanBanDenResponse();
        payload.vbdId = request.getVbdId();
        payload.noiDung = request.getNoiDung();
        payload.soPa = request.getSoPa();
        return payload;
    }

    public VanBanDenResponse withFilesName(List<String> listFile) {
        this.files = listFile;
        return this;
    }

    public String getNoiDung() {
        return StringUtils.isEmpty(noiDung) ? "" : noiDung;
    }
}

