package com.px.tool.domain.dathang;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.kiemhong.KiemHong;
import com.px.tool.domain.kiemhong.KiemHongDetail;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.model.payload.AbstractPayLoad;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.tool.infrastructure.utils.CommonUtils.collectFullName;
import static com.px.tool.infrastructure.utils.CommonUtils.collectSignImg;

@Getter
@Setter
public class PhieuDatHangPayload extends AbstractPayLoad<PhieuDatHang> {
    private Long requestId;
    private Long pdhId;
    private String tenNhaMay;
    private String tenPhong;
    private String so;
    private String donViYeuCau;
    private String phanXuong;
    private String noiDung;
    private Long noiNhan;
    private String ngayThangNamTPKTHK;
    private String TPKTHK;
    private Boolean tpkthkXacNhan;
    private String ngayThangNamTPVatTu;
    private String TPVatTu;
    private Boolean tpvatTuXacNhan;
    private String ngayThangNamNguoiDatHang;
    private String NguoiDatHang;
    private Boolean nguoiDatHangXacNhan;

    private boolean nguoiDatHangDisable;
    private boolean tpvatTuDisable;
    private boolean tpkthkDisable;


    @JsonProperty("yKienNguoiDatHang")
    private String yKienNguoiDatHang;

    @JsonProperty("yKienTPKTHK")
    private String yKienTPKTHK;

    @JsonProperty("yKienTPVatTu")
    private String yKienTPVatTu;
    private Long chuyen; // id cua user dc nhan
    private List<PhieuDatHangDetailPayload> phieuDatHangDetails = new ArrayList<>();

    // chuky + ten
    private String nguoiDatHangSignImg;
    private String tpvatTuSignImg;
    private String tpkthkSignImg;

    private String nguoiDatHangFullName;
    private String tpvatTuFullName;
    private String tpkthkFullName;

    private Long nguoiDatHangId;
    private Long tpvatTuId;
    private Long tpkthkId;

    // update flow moi
    private Long trolyKT; // cac tro ly nay thuoc 8,9 level = 4

    private List<Long> cusReceivers;
    private String cusNoiDung;
    private RequestType currentStatus;

    public static PhieuDatHangPayload fromEntity(PhieuDatHang phieuDatHang) {
        PhieuDatHangPayload payload = new PhieuDatHangPayload();
        BeanUtils.copyProperties(phieuDatHang, payload);
        payload.setPhieuDatHangDetails(
                phieuDatHang.getPhieuDatHangDetails()
                        .stream()
                        .map(PhieuDatHangDetailPayload::fromEntity)
                        .map(detail -> {
                            if (!StringUtils.isEmpty(detail.getSoPhieuDatHang())) {
                                return detail;
                            }
                            if (!StringUtils.isEmpty(phieuDatHang.getSo())) {
                                detail.setSoPhieuDatHang(phieuDatHang.getSo());
                            }
                            return detail;
                        })
                        .sorted(Comparator.comparingLong(PhieuDatHangDetailPayload::getPdhDetailId))
                        .collect(Collectors.toList())
        );
        payload.setNoiNhan(null);
        payload.ngayThangNamNguoiDatHang = DateTimeUtils.toString(phieuDatHang.getNgayThangNamNguoiDatHang());
        payload.ngayThangNamTPKTHK = DateTimeUtils.toString(phieuDatHang.getNgayThangNamTPKTHK());
        payload.ngayThangNamTPVatTu = DateTimeUtils.toString(phieuDatHang.getNgayThangNamTPVatTu());
        payload.setCusReceivers(CommonUtils.toCollection(phieuDatHang.getCusReceivers()));
        if (Objects.nonNull(phieuDatHang.getRequest())) {
            payload.setCurrentStatus(phieuDatHang.getRequest().getStatus());
        }
        return payload;
    }

    public PhieuDatHang toEntity(PhieuDatHang phieuDatHang) {
        if (pdhId != null && pdhId <= 0) {
            pdhId = null;
        }
        BeanUtils.copyProperties(this, phieuDatHang);
        phieuDatHang.setPhieuDatHangDetails(
                phieuDatHangDetails.stream()
                        .map(payload -> {
                            PhieuDatHangDetail entity = payload.toEntity();
                            if (Objects.nonNull(phieuDatHang.getPdhId())) {
                                entity.setPhieuDatHang(phieuDatHang);
                            }
                            if (!StringUtils.isEmpty(so)) {
                                entity.setSoPhieuDatHang(so);
                            }
                            return entity;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        phieuDatHang.setCusReceivers(CommonUtils.toString(this.getCusReceivers()));
        return phieuDatHang;
    }

    public boolean notIncludeId() {
        return pdhId == null || (pdhId != null && pdhId <= 0);
    }

    public PhieuDatHangPayload filterPermission(User currentUser) {
        nguoiDatHangDisable = true;
        tpkthkDisable = true;
        tpvatTuDisable = true;
        if (currentUser.isNhanVienVatTu()) {
            nguoiDatHangDisable = false;
        } else if (currentUser.isTruongPhongVatTu()) {
            tpvatTuDisable = false;
        } else if (currentUser.isTruongPhongKTHK()) {
            tpkthkDisable = false;
        }
        return this;
    }

    public void capNhatChuKy(User user) {
        if (user.isNhanVienVatTu() && nguoiDatHangXacNhan) {
            this.nguoiDatHangId = user.getUserId();
        }
        if (user.isTruongPhongVatTu() && tpvatTuXacNhan) {
            tpvatTuId = user.getUserId();
            trolyKT = noiNhan;
        }
        if (user.isTruongPhongKTHK() && tpkthkXacNhan) {
            tpkthkId = user.getUserId();
        }
    }

    public Boolean getTpkthkXacNhan() {
        return tpkthkXacNhan == null ? false : tpkthkXacNhan;
    }

    public Boolean getTpvatTuXacNhan() {
        return tpvatTuXacNhan == null ? false : tpvatTuXacNhan;
    }

    public Boolean getNguoiDatHangXacNhan() {
        return nguoiDatHangXacNhan == null ? false : nguoiDatHangXacNhan;
    }

    @Override
    public void processSignImgAndFullName(Map<Long, User> userById) {
        try {
            if (this.getNguoiDatHangXacNhan()) {
                this.setNguoiDatHangFullName(collectFullName(userById.get(this.getNguoiDatHangId()), ""));
                this.setNguoiDatHangSignImg(collectSignImg(userById.get(this.getNguoiDatHangId()), ""));
            }
            if (this.getTpkthkXacNhan()) {
                this.setTpkthkFullName(collectFullName(userById.get(this.getTpkthkId()), ""));
                this.setTpkthkSignImg(collectSignImg(userById.get(this.getTpkthkId()), ""));
            }
            if (this.getTpvatTuXacNhan()) {
                this.setTpvatTuFullName(collectFullName(userById.get(this.getTpvatTuId()), ""));
                this.setTpvatTuSignImg(collectSignImg(userById.get(this.getTpvatTuId()), ""));
            }
        } catch (Exception e) {
            PXLogger.error("[DAT_HANG] Parse chữ ký và full name bị lỗi.");
        }
    }

    @Override
    public Collection<Long> getDeletedIds(PhieuDatHang o) {
        if (Objects.isNull(o)) {
            return Collections.emptyList();
        }

        Set<Long> requestIds = this.getPhieuDatHangDetails()
                .stream()
                .map(el -> el.getPdhDetailId())
                .collect(Collectors.toSet());

        return o.getPhieuDatHangDetails()
                .stream()
                .map(detail -> detail.getPdhDetailId())
                .filter(el -> !requestIds.contains(el))
                .collect(Collectors.toSet());
    }

    @Override
    public void capNhatNgayThangChuKy(PhieuDatHang requestDatHang, PhieuDatHang existedPhieuDatHang) {
        requestDatHang.setNgayThangNamNguoiDatHang(existedPhieuDatHang.getNgayThangNamNguoiDatHang());
        requestDatHang.setNgayThangNamTPVatTu(existedPhieuDatHang.getNgayThangNamTPVatTu());
        requestDatHang.setNgayThangNamTPKTHK(existedPhieuDatHang.getNgayThangNamTPKTHK());
        // cac field khac
//        requestDatHang.setNguoiDatHangId(existedPhieuDatHang.getNguoiDatHangId());
//        requestDatHang.setTpvatTuId(existedPhieuDatHang.getTpvatTuId());
//        requestDatHang.setTpkthkId(existedPhieuDatHang.getTpkthkId());


        if (requestDatHang.getNguoiDatHangXacNhan() != existedPhieuDatHang.getNguoiDatHangXacNhan()) {
            requestDatHang.setNgayThangNamNguoiDatHang(DateTimeUtils.nowAsMilliSec());
        }
        if (requestDatHang.getTpvatTuXacNhan() != existedPhieuDatHang.getTpvatTuXacNhan()) {
            requestDatHang.setNgayThangNamTPVatTu(DateTimeUtils.nowAsMilliSec());
        }
        if (requestDatHang.getTpkthkXacNhan() != existedPhieuDatHang.getTpkthkXacNhan()) {
            requestDatHang.setNgayThangNamTPKTHK(DateTimeUtils.nowAsMilliSec());
        }
    }

    @Override
    public void validateXacNhan(User user, PhieuDatHang request, PhieuDatHang existed) {
        if (user.isTruongPhongKTHK() && tpkthkXacNhan) {
            noiNhan = null; //  step cuoi cung roi nen noi nhan phai empty
        }
        if ((user.isTroLyKT() || user.isTruongPhongKTHK()) && (CollectionUtils.isEmpty(cusReceivers))) {
            throw new PXException("dathang.noi_nhan.empty");
        }
        if (user.isTruongPhongVatTu()) {
            if (!tpvatTuXacNhan && StringUtils.isEmpty(yKienTPVatTu) && Objects.nonNull(noiNhan)) {
                throw new PXException("dathang.tpvattu_xacnhan.ykien");
            }
        }
        if (user.isTruongPhongKTHK()) {
            if (!tpkthkXacNhan && StringUtils.isEmpty(yKienTPKTHK) && Objects.nonNull(noiNhan)) {
                throw new PXException("dathang.tpkthk_xacnhan.ykien");
            }
        }
        // clear noi nhan, vi day la step cuoi cung, ko can phai co noi nhan
        if (user.isTruongPhongKTHK()) {
            noiNhan = null;
        }
    }

    public Request toRequestEntity() {
        Request request = new Request();
        request.setStatus(RequestType.DAT_HANG);
        request.setNgayGui(DateTimeUtils.nowAsMilliSec());
        request.setCreatedAt(DateTimeUtils.nowAsMilliSec());
        return request;
    }

    public KiemHong toKiemHongEntity() {
        KiemHong kiemHong = new KiemHong();
        kiemHong.setTenNhaMay(tenNhaMay);
        kiemHong.setNoiNhan(noiNhan);
        kiemHong.setCusNoiDung(noiDung);
        Set<KiemHongDetail> kiemHongDetails = new LinkedHashSet<>();
        for (PhieuDatHangDetailPayload phieuDatHangDetail : this.phieuDatHangDetails) {
            kiemHongDetails.add(phieuDatHangDetail.toKiemHongDetailEntity());
        }
        kiemHong.setKiemHongDetails(kiemHongDetails);
//        kiemHong.setGiamDocXacNhan();
        // NOTE: tao dat hang thi ko co Kiem_Hong => ko co' report => ko dc set 3 fields xac_nhan = true.
//        kiemHong.setToTruongXacNhan(true);
//        kiemHong.setTroLyKTXacNhan(true);
//        kiemHong.setQuanDocXacNhan(true);
        return kiemHong;
    }

    @Override
    public PhieuDatHangPayload andStatus(RequestType status) {
        this.currentStatus = status;
        return this;
    }
}
