package com.px.tool.domain.kiemhong;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.model.payload.AbstractPayLoad;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.tool.infrastructure.utils.CommonUtils.collectFullName;
import static com.px.tool.infrastructure.utils.CommonUtils.collectSignImg;
import static org.springframework.util.StringUtils.isEmpty;

@Getter
@Setter
public class KiemHongPayLoad extends AbstractPayLoad<KiemHong> {
    private Long requestId;
    private Long khId;
    private String tenNhaMay;
    private Long phanXuong;
    private Long toSX;
    private String tenVKTBKT; // may bay L39
    private String nguonVao;// SCL TTNH
    private String congDoan; // kiem hong chi tiet
    private String soHieu; // 8843
    private String soXX; // 8373y64
    private String toSo;
    private String soTo;
    private Long noiNhan;
    private String ngayThangNamQuanDoc;
    private String quanDoc;
    private Boolean quanDocXacNhan;

    private String ngayThangNamTroLyKT;
    private String troLyKT;
    private Boolean troLyKTXacNhan;

    private String ngayThangNamToTruong;
    private String toTruong;
    private Boolean toTruongXacNhan;

    private Boolean giamDocXacNhan;
    private String yKienGiamDoc;

    private List<KiemHongDetailPayload> kiemHongDetails = new LinkedList<>();

    // Permission to edit chu ky:
    private boolean quanDocDisable;
    private boolean troLyKTDisable;
    private boolean toTruongDisable;

    @JsonProperty("yKienQuanDoc")
    private String yKienQuanDoc;

    @JsonProperty("yKienToTruong")
    private String yKienToTruong;

    @JsonProperty("yKienTroLyKT")
    private String yKienTroLyKT;

    // chu ky + ten
    private String quanDocSignImg;
    private String troLyKTSignImg;
    private String toTruongSignImg;

    private String quanDocfullName;
    private String troLyfullName;
    private String toTruongfullName;

    private Long quanDocId;
    private Long troLyId;
    private Long toTruongId;

    private List<Long> cusReceivers;
    private String cusNoiDung;
    private RequestType currentStatus;

    public static KiemHongPayLoad fromEntity(KiemHong kiemHong) {
        KiemHongPayLoad payload = new KiemHongPayLoad();
        BeanUtils.copyProperties(kiemHong, payload);
        payload.kiemHongDetails = kiemHong.getKiemHongDetails()
                .stream()
                .map(KiemHongDetailPayload::fromEntity)
                .sorted(Comparator.comparingLong(KiemHongDetailPayload::getKhDetailId))
                .collect(Collectors.toCollection(LinkedList::new));

        if (kiemHong.getRequest() != null) {
            payload.noiNhan = kiemHong.getRequest().getKiemHongReceiverId();
        }
        payload.quanDocDisable = true;
        payload.troLyKTDisable = true;
        payload.toTruongDisable = true;

        payload.phanXuong = getLong(kiemHong.getPhanXuong());
        payload.toSX = getLong(kiemHong.getToSX());
        payload.setNoiNhan(null);

        payload.ngayThangNamQuanDoc = DateTimeUtils.toString(kiemHong.getNgayThangNamQuanDoc());
        payload.ngayThangNamToTruong = DateTimeUtils.toString(kiemHong.getNgayThangNamToTruong());
        payload.ngayThangNamTroLyKT = DateTimeUtils.toString(kiemHong.getNgayThangNamTroLyKT());

        payload.setCusReceivers(CommonUtils.toCollection(kiemHong.getCusReceivers()));
        if (Objects.nonNull(kiemHong.getRequest())) {
            payload.currentStatus = kiemHong.getRequest().getStatus();
        }
        return payload;
    }

    public KiemHongPayLoad andRequestId(Long requestId) {
        this.requestId = requestId;
        return this;
    }

    public KiemHong toEntity(KiemHong kiemHong) {
        if (khId != null && khId <= 0) {
            khId = null;
        }
        BeanUtils.copyProperties(this, kiemHong);
        kiemHong.setKiemHongDetails(
                this.kiemHongDetails
                        .stream()
                        .map(payload -> {
                            KiemHongDetail entity = payload.toEntity();
                            if (kiemHong.getKhId() != null) {
                                entity.setKiemHong(kiemHong);
                            }
                            return entity;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new)));
        kiemHong.setCusReceivers(CommonUtils.toString(this.getCusReceivers()));
        return kiemHong;
    }

    public boolean includedId() {
        return khId != null && khId > 0;
    }

    public Boolean getQuanDocXacNhan() {
        return quanDocXacNhan == null ? false : quanDocXacNhan;
    }

    public Boolean getTroLyKTXacNhan() {
        return troLyKTXacNhan == null ? false : troLyKTXacNhan;
    }

    public Boolean getToTruongXacNhan() {
        return toTruongXacNhan == null ? false : toTruongXacNhan;
    }

    public Boolean getGiamDocXacNhan() {
        return giamDocXacNhan == null ? false : giamDocXacNhan;
    }

    public KiemHongPayLoad filterPermission(User currentUser) {
        troLyKTDisable = true;
        toTruongDisable = true;
        quanDocDisable = true;
        if (currentUser.isTroLyKT()) {
            this.troLyKTDisable = false;
        } else if (currentUser.isToTruong()) {
            toTruongDisable = false;
        } else if (currentUser.isQuanDocPhanXuong()) {
            quanDocDisable = false;
        }
        return this;
    }

    public void capNhatChuKy(User user) {
        if (user.isQuanDocPhanXuong() && quanDocXacNhan) {
            quanDocId = user.getUserId();
        } else if (user.isTroLyKT() && troLyKTXacNhan) {
            troLyId = user.getUserId();
        } else if (user.isToTruong() & toTruongXacNhan) {
            toTruongId = user.getUserId();
        }
    }

    @Override
    public void processSignImgAndFullName(Map<Long, User> userById) {
        try {
            if (this.getQuanDocXacNhan()) {
                this.setQuanDocfullName(collectFullName(userById.get(this.getQuanDocId()), ""));
                this.setQuanDocSignImg(collectSignImg(userById.get(this.getQuanDocId()), ""));
            }
            if (this.getTroLyKTXacNhan()) {
                this.setTroLyfullName(collectFullName(userById.get(this.getTroLyId()), ""));
                this.setTroLyKTSignImg(collectSignImg(userById.get(this.getTroLyId()), ""));
            }
            if (this.getToTruongXacNhan()) {
                this.setToTruongfullName(collectFullName(userById.get(this.getToTruongId()), ""));
                this.setToTruongSignImg(collectSignImg(userById.get(this.getToTruongId()), ""));
            }
        } catch (Exception e) {
            PXLogger.error("[KiemHong] Parse chữ ký và full name bị lỗi.");
        }
    }

    @Override
    public Collection<Long> getDeletedIds(KiemHong o) {
        if (Objects.isNull(o)) {
            return Collections.emptyList();
        }
        Set<Long> requestDetailIds = this.getKiemHongDetails()
                .stream()
                .map(el -> el.getKhDetailId())
                .collect(Collectors.toSet());

        return
                o.getKiemHongDetails()
                        .stream()
                        .map(KiemHongDetail::getKhDetailId)
                        .filter(el -> !requestDetailIds.contains(el))
                        .collect(Collectors.toSet());
    }

    @Override
    public void capNhatNgayThangChuKy(KiemHong requestKiemHong, KiemHong existedKiemHong) {
        requestKiemHong.setNgayThangNamToTruong(existedKiemHong.getNgayThangNamToTruong());
        requestKiemHong.setNgayThangNamTroLyKT(existedKiemHong.getNgayThangNamTroLyKT());
        requestKiemHong.setNgayThangNamQuanDoc(existedKiemHong.getNgayThangNamQuanDoc());

        if (requestKiemHong.getToTruongXacNhan() != existedKiemHong.getToTruongXacNhan()) {
            requestKiemHong.setNgayThangNamToTruong(DateTimeUtils.nowAsMilliSec());
        }
        if (requestKiemHong.getQuanDocXacNhan() != existedKiemHong.getQuanDocXacNhan()) {
            requestKiemHong.setNgayThangNamQuanDoc(DateTimeUtils.nowAsMilliSec());
        }
        if (requestKiemHong.getTroLyKTXacNhan() != existedKiemHong.getTroLyKTXacNhan()) {
            requestKiemHong.setNgayThangNamTroLyKT(DateTimeUtils.nowAsMilliSec());
        }
    }

    @Override
    public void validateXacNhan(User user, KiemHong request, KiemHong existed) {
        /**
         * Khi Chuyen thi phai co xac nhan, xac nhan thi phai co chuyen.
         * To truong review thi phai dien day du thong tin trong detail, ngoai tru "phuong phap khac phuc".
         *  => khi chuyển đi thì detail phải đc điền.
         */

        // clear y kien:
        if (user.isToTruong() && toTruongXacNhan) {
            this.yKienToTruong = null;
            request.setYKienToTruong(null);
            if (isEmpty(this.phanXuong) || isEmpty(soHieu)) {
                throw new PXException("kiemhong.missingRequiredFields");
            }
        }
        if (user.isTroLyKT()) {
            if (troLyKTXacNhan) {
                yKienTroLyKT = null;
                request.setYKienTroLyKT(null);
                for (KiemHongDetailPayload kiemHongDetail : this.kiemHongDetails) {
                    if (StringUtils.isEmpty(kiemHongDetail.getPhuongPhapKhacPhuc())) {
                        throw new PXException("kiemhong.ppkp");
                    }
                }
            }
            if (!troLyKTXacNhan && StringUtils.isEmpty(yKienTroLyKT) && Objects.nonNull(noiNhan)) {
                throw new PXException("kiemhong.trolyKT_xacnhan.ykien");
            }

        }
        if (user.isQuanDocPhanXuong()) {
            if (quanDocXacNhan) {
                yKienQuanDoc = null;
                request.setYKienQuanDoc(null);
            }
            if (!quanDocXacNhan && StringUtils.isEmpty(yKienQuanDoc) && Objects.nonNull(noiNhan)) {
                throw new PXException("kiemhong.quandoc_xacnhan.ykien");
            }
        }

        if (!Objects.isNull(this.noiNhan)) {
            for (KiemHongDetailPayload kiemHongDetail : this.kiemHongDetails) {
                if (kiemHongDetail.isInvalidData()) {
                    throw new PXException("kiemhong.data_invalid");
                }
            }
            if (user.isToTruong() && !this.toTruongXacNhan && Objects.isNull(yKienToTruong)) {
                throw new PXException("kiemhong.totruong_xacnhan");
            } else if (user.isTroLyKT() && !this.troLyKTXacNhan && Objects.isNull(yKienTroLyKT)) {
                throw new PXException("kiemhong.trolyKT_xacnhan");
            } else if (user.isQuanDocPhanXuong() && !this.quanDocXacNhan && Objects.isNull(this.yKienQuanDoc)) {
                throw new PXException("kiemhong.quandoc_xacnhan");
            }
        }
    }


    public String getCusNoiDung() {
        return StringUtils.isEmpty(cusNoiDung) ? "" : cusNoiDung;
    }

    public List<Long> getCusReceivers() {
        return cusReceivers == null ? Collections.emptyList() : cusReceivers;
    }

    @Override
    public KiemHongPayLoad andStatus(RequestType status) {
        this.currentStatus = status;
        return this;
    }
}
