package com.px.tool.domain.phuongan;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.model.payload.AbstractPayLoad;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.px.tool.infrastructure.utils.CommonUtils.collectFullName;
import static com.px.tool.infrastructure.utils.CommonUtils.collectSignImg;
import static com.px.tool.infrastructure.utils.CommonUtils.getBigDecimal;

@Getter
@Setter
public class PhuongAnPayload extends AbstractPayLoad<PhuongAn> {
    private Long paId;
    private Long requestId;
    private String tenNhaMay;
    private String ngayThangNamPheDuyet;
    private String maSo;
    private String sanPham;
    private String noiDung;
    private String nguonKinhPhi;
    private String toSo;
    private String soTo;
    private String PDH;    //end

    private String ngayThangNamTPKTHK;
    private String truongPhongKTHK;
    private String ngayThangNamTPKEHOACH;
    private String truongPhongKeHoach;
    private String ngayThangNamtpVatTu;
    private String truongPhongVatTu;
    private String ngayThangNamNguoiLap;
    private String NguoiLap;    // cac field tong cong

    @JsonProperty("tongDMLDDM")
    private BigDecimal tongCongDinhMucLaoDong;
    private BigDecimal tongDMVTKho;
    private BigDecimal tongDMVTMuaNgoai;
    private BigDecimal tienLuong;
    private List<DinhMucLaoDongPayload> dinhMucLaoDongs = new LinkedList<>();
    private List<DinhMucVatTuPayload> dinhMucVatTus = new LinkedList<>();
    private Long noiNhan; // id cua user dc nhan


    private String ngayThangNamGiamDoc;
    private Boolean giamDocXacNhan;
    private Boolean truongPhongKTHKXacNhan;
    private Boolean truongPhongKeHoachXacNhan;
    private Boolean truongPhongVatTuXacNhan;
    private Boolean nguoiLapXacNhan;

    private boolean truongPhongKTHKDisable;
    private boolean truongPhongKeHoachDisable;
    private boolean truongPhongVatTuDisable;
    private boolean nguoiLapDisable;
    private boolean giamDocDisable;

    @JsonProperty("yKienNguoiLap")
    private String yKienNguoiLap;

    @JsonProperty("yKienTruongPhongKTHK")
    private String yKienTruongPhongKTHK;

    @JsonProperty("yKienTruongPhongKeHoach")
    private String yKienTruongPhongKeHoach;

    @JsonProperty("yKienTruongPhongVatTu")
    private String yKienTruongPhongVatTu;

    private List<String> files;
    // chu ky + ten
    private String truongPhongKTHKSignImg;
    private String truongPhongKeHoachSignImg;
    private String truongPhongVatTuSignImg;
    private String nguoiLapSignImg;
    private String giamDocSignImg;

    private String truongPhongKTHKFullName;
    private String truongPhongKeHoachFullName;
    private String truongPhongVatTuFullName;
    private String nguoiLapFullName;
    private String giamDocFullName;

    private Long truongPhongKTHKId;
    private Long truongPhongKeHoachId;
    private Long truongPhongVatTuId;
    private Long nguoiLapId;
    private Long giamDocId;

    private List<Long> cusReceivers;
    private String cusNoiDung;
    private List<Long> nguoiThucHien;

    private boolean disableAll; // 0: van dang xu ly, 1: success_phuong_an
    private boolean dmVatTuDisable = false;
    private boolean dmLaoDOngDisable = false;
    private RequestType currentStatus;


    public static PhuongAnPayload fromEntity(PhuongAn phuongAn) {
        PhuongAnPayload payload = new PhuongAnPayload();
        BeanUtils.copyProperties(phuongAn, payload);
        payload.dinhMucLaoDongs = phuongAn.getDinhMucLaoDongs().stream()
                .map(DinhMucLaoDongPayload::fromEntity)
                .sorted(Comparator.comparingLong(DinhMucLaoDongPayload::getDmId))
                .collect(Collectors.toCollection(LinkedList::new));
        payload.dinhMucVatTus = phuongAn.getDinhMucVatTus().stream()
                .map(DinhMucVatTuPayload::fromEntity)
                .sorted(Comparator.comparingLong(DinhMucVatTuPayload::getVtId))
                .collect(Collectors.toCollection(LinkedList::new));
//        phuongAnPayload.files = Arrays.asList("imgpsh_fullsize.jpeg", "1111111111111111ok.jpg");
        payload.disableAll = phuongAn.getStep() == 1;
        payload.setNoiNhan(null);

        payload.ngayThangNamGiamDoc = DateTimeUtils.toString(phuongAn.getNgayThangNamGiamDoc());
        payload.ngayThangNamtpVatTu = DateTimeUtils.toString(phuongAn.getNgayThangNamtpVatTu());
        payload.ngayThangNamTPKTHK = DateTimeUtils.toString(phuongAn.getNgayThangNamTPKTHK());
        payload.ngayThangNamTPKEHOACH = DateTimeUtils.toString(phuongAn.getNgayThangNamTPKEHOACH());
        payload.ngayThangNamPheDuyet = DateTimeUtils.toString(phuongAn.getNgayThangNamGiamDoc());
        payload.ngayThangNamNguoiLap = DateTimeUtils.toString(phuongAn.getNgayThangNamNguoiLap());

        try {
            payload.setCusReceivers(CommonUtils.toCollection(phuongAn.getCusReceivers()));
            payload.setNguoiThucHien(phuongAn.getNguoiThucHien() == null ? Collections.emptyList() : CommonUtils.toCollection(phuongAn.getNguoiThucHien()));
        } catch (Exception e) {

        }
        payload.tongCongDinhMucLaoDong = phuongAn.getTongCongDinhMucLaoDong();
        payload.setCurrentStatus(phuongAn.getStatus());
        return payload;
    }

    public PhuongAn toEntity(PhuongAn phuongAn) {
        if (paId != null && paId <= 0) {
            paId = null;
        }
        BeanUtils.copyProperties(this, phuongAn);
        phuongAn.setDinhMucLaoDongs(
                dinhMucLaoDongs
                        .stream()
                        .map(payload -> {
                            DinhMucLaoDong entity = payload.toEntity();
                            if (Objects.nonNull(phuongAn.getPaId())) {
                                entity.setPhuongAn(phuongAn);
                            }
                            return entity;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        phuongAn.setDinhMucVatTus(
                dinhMucVatTus.stream()
                        .map(payload -> {
                            DinhMucVatTu entity = payload.toEntity();
                            if (Objects.nonNull(phuongAn.getPaId())) {
                                entity.setPhuongAn(phuongAn);
                            }
                            return entity;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        try {
            phuongAn.setCusReceivers(CommonUtils.toString(this.cusReceivers));
            phuongAn.setNguoiThucHien(CommonUtils.toString(this.nguoiThucHien));
        } catch (Exception e) {

        }
        tongCongDinhMucLaoDong = new BigDecimal(0);
        for (DinhMucLaoDongPayload dinhMucLaoDong : this.dinhMucLaoDongs) {
            tongCongDinhMucLaoDong = tongCongDinhMucLaoDong.add(getBigDecimal(dinhMucLaoDong.getDm()));
        }
        phuongAn.setTongCongDinhMucLaoDong(tongCongDinhMucLaoDong);
        return phuongAn;
    }

    public PhuongAnPayload withFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public PhuongAnPayload filterPermission(User user) {
        truongPhongKTHKDisable = true;
        truongPhongKeHoachDisable = true;
        truongPhongVatTuDisable = true;
        nguoiLapDisable = true;
        giamDocDisable = true;
        if (user.isTruongPhongKTHK()) {
            truongPhongKTHKDisable = false;
        } else if (user.isTruongPhongKeHoach()) {
            truongPhongKeHoachDisable = false;
        } else if (user.isTruongPhongVatTu()) {
            truongPhongVatTuDisable = false;
        } else if (user.isNguoiLapPhieu()) {
            nguoiLapDisable = false;
        } else if (user.getLevel() == 2) {
            giamDocDisable = false;
        }
        if (user.isNhanVienTiepLieu() || user.isTruongPhongVatTu()) {
            dmVatTuDisable = true;
        }
        if (user.isNhanVienDinhMuc()) {
            dmLaoDOngDisable = true;
        }
        return this;
    }

    public void capNhatChuKy(User user) {
        if (user.isNguoiLapPhieu() && Objects.nonNull(nguoiLapXacNhan) && nguoiLapXacNhan) {
            nguoiLapId = user.getUserId();
        }
        if (user.isTruongPhongVatTu() && truongPhongVatTuXacNhan) {
            truongPhongVatTuId = user.getUserId();
        }
        if (user.isTruongPhongKeHoach() && truongPhongKeHoachXacNhan) {
            truongPhongKeHoachId = user.getUserId();
        }
        if (user.isTruongPhongKTHK() && truongPhongKTHKXacNhan) {
            truongPhongKTHKId = user.getUserId();
        }
        if (user.getLevel() == 2 && giamDocXacNhan) {
            giamDocId = user.getUserId();
        }
    }

    public Boolean getGiamDocXacNhan() {
        return giamDocXacNhan == null ? false : giamDocXacNhan;
    }

    public Boolean getTruongPhongKTHKXacNhan() {
        return truongPhongKTHKXacNhan == null ? false : truongPhongKTHKXacNhan;
    }

    public Boolean getTruongPhongKeHoachXacNhan() {
        return truongPhongKeHoachXacNhan == null ? false : truongPhongKeHoachXacNhan;
    }

    public Boolean getTruongPhongVatTuXacNhan() {
        return truongPhongVatTuXacNhan == null ? false : truongPhongVatTuXacNhan;
    }

    public Boolean getNguoiLapXacNhan() {
        return nguoiLapXacNhan == null ? false : nguoiLapXacNhan;
    }

    @Override
    public void processSignImgAndFullName(Map<Long, User> userById) {
        try {
            if (this.getGiamDocXacNhan()) {
                this.setGiamDocSignImg(collectSignImg(userById.get(this.getGiamDocId()), ""));
                this.setGiamDocFullName(collectFullName(userById.get(this.getGiamDocId()), ""));
            }
            if (this.getTruongPhongKTHKXacNhan()) {
                this.setTruongPhongKTHKSignImg(collectSignImg(userById.get(this.getTruongPhongKTHKId()), ""));
                this.setTruongPhongKTHKFullName(collectFullName(userById.get(this.getTruongPhongKTHKId()), ""));
            }
            if (this.getTruongPhongKeHoachXacNhan()) {
                this.setTruongPhongKeHoachSignImg(collectSignImg(userById.get(this.getTruongPhongKeHoachId()), ""));
                this.setTruongPhongKeHoachFullName(collectFullName(userById.get(this.getTruongPhongKeHoachId()), ""));
            }
            if (this.getTruongPhongVatTuXacNhan()) {
                this.setTruongPhongVatTuSignImg(collectSignImg(userById.get(this.getTruongPhongVatTuId()), ""));
                this.setTruongPhongVatTuFullName(collectFullName(userById.get(this.getTruongPhongVatTuId()), ""));
            }
            if (this.getNguoiLapXacNhan()) {
                this.setNguoiLapSignImg(collectSignImg(userById.get(this.getNguoiLapId()), ""));
                this.setNguoiLapFullName(collectFullName(userById.get(this.getNguoiLapId()), ""));
            }
        } catch (Exception e) {
            PXLogger.error("[PHUONG_AN] Parse chữ ký và full name bị lỗi.");
        }

    }

    @Override
    public Collection<Long> getDeletedIds(PhuongAn o) {
        return null;
    }

    // TODO: nghiapt-> chuyen sang kieu long cho field ngaythangnam
    @Override
    public void capNhatNgayThangChuKy(PhuongAn pa, PhuongAn existedPhuongAn) {
        pa.setNgayThangNamtpVatTu(existedPhuongAn.getNgayThangNamtpVatTu());
        pa.setNgayThangNamTPKEHOACH(existedPhuongAn.getNgayThangNamTPKEHOACH());
        pa.setNgayThangNamNguoiLap(existedPhuongAn.getNgayThangNamNguoiLap());
        pa.setNgayThangNamTPKTHK(existedPhuongAn.getNgayThangNamTPKTHK());
        pa.setNgayThangNamGiamDoc(existedPhuongAn.getNgayThangNamGiamDoc());
        pa.setCongNhanThanhPham(existedPhuongAn.getCongNhanThanhPham());

        if (existedPhuongAn.getNguoiLapId() != null) pa.setNguoiLapId(existedPhuongAn.getNguoiLapId());
        if (existedPhuongAn.getTruongPhongKTHKId() != null)
            pa.setTruongPhongKTHKId(existedPhuongAn.getTruongPhongKTHKId());
        if (existedPhuongAn.getTruongPhongKeHoachId() != null)
            pa.setTruongPhongKeHoachId(existedPhuongAn.getTruongPhongKeHoachId());
        if (existedPhuongAn.getTruongPhongVatTuId() != null)
            pa.setTruongPhongVatTuId(existedPhuongAn.getTruongPhongVatTuId());
        if (existedPhuongAn.getGiamDocId() != null) pa.setGiamDocId(existedPhuongAn.getGiamDocId());

        if (pa.getTruongPhongVatTuXacNhan() != existedPhuongAn.getTruongPhongVatTuXacNhan()) {
            pa.setNgayThangNamtpVatTu(DateTimeUtils.nowAsMilliSec());
        }
        if (pa.getTruongPhongKeHoachXacNhan() != existedPhuongAn.getTruongPhongKeHoachXacNhan()) {
            pa.setNgayThangNamTPKEHOACH(DateTimeUtils.nowAsMilliSec());
        }
        if (pa.getNguoiLapXacNhan() != existedPhuongAn.getNguoiLapXacNhan()) {
            pa.setNgayThangNamNguoiLap(DateTimeUtils.nowAsMilliSec());
        }
        if (pa.getTruongPhongKTHKXacNhan() != existedPhuongAn.getTruongPhongKTHKXacNhan()) {
            pa.setNgayThangNamTPKTHK(DateTimeUtils.nowAsMilliSec());
        }
        if (pa.getGiamDocXacNhan() != existedPhuongAn.getGiamDocXacNhan()) {
            pa.setNgayThangNamGiamDoc(DateTimeUtils.nowAsMilliSec());
        }
    }

    @Override
    public void validateXacNhan(User user, PhuongAn request, PhuongAn existed) {
        if (Objects.nonNull(this.noiNhan)) {
            if (user.isNguoiLapPhieu()) { // tl kthk, xmdc
                if (Objects.isNull(cusReceivers) || Objects.isNull(nguoiThucHien)) {
                    throw new PXException("phuongan.vanbanden");
                }
                if (CollectionUtils.isEmpty(dinhMucLaoDongs)) {
                    throw new PXException("phuongan.dmLaoDong");
                }
                for (DinhMucLaoDongPayload dinhMucLaoDong : dinhMucLaoDongs) {
                    if (dinhMucLaoDong.isInvalidData()) {
                        throw new PXException("phuongan.dmLaoDong");
                    }
                }

                // file update: validate them dmVatTu
                if (CollectionUtils.isEmpty(dinhMucVatTus)) {
                    throw new PXException("phuongan.dinhmucvattu.nguoiLap");
                }
                for (DinhMucVatTuPayload mucVatTus : this.dinhMucVatTus) {
                    if (mucVatTus.isInvalidData_NguoiLap()) {
                        throw new PXException("phuongan.dinhmucvattu.nguoiLap");
                    }
                }
            }
            if (user.isNhanVienTiepLieu()) {
                if (CollectionUtils.isEmpty(dinhMucVatTus)) {
                    throw new PXException("phuongan.dinhmucvattu");
                }
                for (DinhMucVatTuPayload mucVatTus : this.dinhMucVatTus) {
                    if (mucVatTus.isInvalidData()) {
                        throw new PXException("phuongan.dinhmucvattu");
                    }
                }
            }
            if (user.isNhanVienVatTu() || user.isTruongPhongVatTu()) {
                if (CollectionUtils.isEmpty(dinhMucVatTus)) {
                    throw new PXException("phuongan.dinhmucvattu");
                }
                for (DinhMucVatTuPayload mucVatTus : this.dinhMucVatTus) {
                    if (mucVatTus.isInvalidData()) {
                        throw new PXException("phuongan.dinhmucvattu");
                    }
                }
            }

//            if(!request.getNguoiLapXacNhan()){
//                throw new PXException("phuongan.dinhmucvattu");
//            }
            // TODO: NghiaPT  nơi nhận mà có data thì "xác nhận " của user hiện tại phải == true
        }
        if (request.allApproved() && CollectionUtils.isEmpty(this.nguoiThucHien)) {
            throw new PXException("phuongan.nguoithuchien_missing");
        }
        if(user.isTroLyKT() && !request.getNguoiLapXacNhan()){
            throw new PXException("Người lập chưa xác nhận");
        }
    }

    @Override
    public PhuongAnPayload andStatus(RequestType status) {
        this.currentStatus = status;
        return this;
    }

    public BigDecimal getTongCongDinhMucLaoDong() {
        return tongCongDinhMucLaoDong == null ? new BigDecimal(0) : tongCongDinhMucLaoDong;
    }

    public BigDecimal getTongDMVTKho() {
        return tongDMVTKho == null ? new BigDecimal(0) : tongDMVTKho;
    }

    public BigDecimal getTongDMVTMuaNgoai() {
        return tongDMVTMuaNgoai == null ? new BigDecimal(0) : tongDMVTMuaNgoai;
    }

    public BigDecimal getTienLuong() {
        return tienLuong == null ? new BigDecimal(0) : tienLuong;
    }

    public Request toRequestEntity() {
        Request request = new Request();
        request.setStatus(RequestType.PHUONG_AN);
        request.setNgayGui(DateTimeUtils.nowAsMilliSec());
        request.setCreatedAt(DateTimeUtils.nowAsMilliSec());
        return request;
    }
}
