package com.px.tool.domain.cntp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.user.User;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.model.payload.AbstractPayLoad;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.tool.infrastructure.utils.CommonUtils.collectAlias;
import static com.px.tool.infrastructure.utils.CommonUtils.collectFullName;
import static com.px.tool.infrastructure.utils.CommonUtils.collectSignImg;
import static com.px.tool.infrastructure.utils.CommonUtils.getVal;
import static com.px.tool.infrastructure.utils.DateTimeUtils.nowAsMilliSec;

@Getter
@Setter
public class CongNhanThanhPhamPayload extends AbstractPayLoad<CongNhanThanhPham> {
    private Long tpId; // thanh pham id
    private Long requestId;

    private String tenSanPham;

    private String noiDung;

    @JsonProperty("soPa")
    private String soPA;
    private Long paId;

    @JsonProperty("donViThucHien")
    private String donviThucHien;

    @JsonProperty("donViDatHang")
    private String donviDatHang;

    private String soNghiemThuDuoc;
    private Float dong;
    private Float gioX;
    private Float laoDongTienLuong;

    private String dvt;
    private String to;
    private String soLuong;
    private Long noiNhan;

    private List<NoiDungThucHienPayload> noiDungThucHiens = new ArrayList<>();

    // chu ky + full name
    private Boolean quanDocXacNhan;
    private Boolean quanDocDisable = true;
    private Long quanDocId;
    private String quanDocFullName;
    private String quanDocSignImg;
    private String ykienQuanDoc;
    private String ngayThangNamQuanDoc;

    private Boolean tpkcsXacNhan;
    private Boolean tpkcsDisable = true;
    private Long tpkcsId;
    private String tpkcsFullName;
    private String tpkcsSignImg;

    @JsonProperty("yKienTPKCS")
    private String ykientpkcs;
    private String ngayThangNamTPKCS;

    // danh sach 5 to truong:
    private Boolean toTruong1XacNhan;
    private Boolean toTruong1Disable = true;
    private Long toTruong1Id;
    private String toTruong1SignImg;
    private String toTruong1fullName;
    private String ykienToTruong1;
    private String ngayThangNamToTruong1;
    private String toTruong1Alias;

    private Boolean toTruong2XacNhan;
    private Boolean toTruong2Disable = true;
    private Long toTruong2Id;
    private String toTruong2SignImg;
    private String toTruong2fullName;
    private String ykienToTruong2;
    private String ngayThangNamToTruong2;
    private String toTruong2Alias;

    private Boolean toTruong3XacNhan;
    private Boolean toTruong3Disable = true;
    private Long toTruong3Id;
    private String toTruong3SignImg;
    private String toTruong3fullName;
    private String ykienToTruong3;
    private String ngayThangNamToTruong3;
    private String toTruong3Alias;

    private Boolean toTruong4XacNhan;
    private Boolean toTruong4Disable = true;
    private Long toTruong4Id;
    private String toTruong4SignImg;
    private String toTruong4fullName;
    private String ykienToTruong4;
    private String ngayThangNamToTruong4;
    private String toTruong4Alias;

    private Boolean toTruong5XacNhan;
    private Boolean toTruong5Disable = true;
    private Long toTruong5Id;
    private String toTruong5SignImg;
    private String toTruong5fullName;
    private String ykienToTruong5;
    private String ngayThangNamToTruong5;
    private String toTruong5Alias;

    private List<Long> cusToTruongIds;
    private boolean nghiemThuDisable = true;

    public static CongNhanThanhPhamPayload fromEntity(CongNhanThanhPham congNhanThanhPham) {
        CongNhanThanhPhamPayload congNhanThanhPhamPayload = new CongNhanThanhPhamPayload();
        BeanUtils.copyProperties(congNhanThanhPham, congNhanThanhPhamPayload);
        congNhanThanhPhamPayload.setNoiDungThucHiens(
                congNhanThanhPham.getNoiDungThucHiens()
                        .stream()
                        .map(NoiDungThucHienPayload::fromEntity)
                        .sorted(Comparator.comparing(NoiDungThucHienPayload::getNoiDungId))
                        .collect(Collectors.toCollection(() -> new ArrayList<>(congNhanThanhPham.getNoiDungThucHiens().size())))
        );
        try {
            congNhanThanhPhamPayload.soPA = "PA-" + congNhanThanhPham.getPhuongAn().getPaId();
        } catch (Exception e) {
            congNhanThanhPhamPayload.soPA = "[Trống]";
        }
        congNhanThanhPhamPayload.setCusToTruongIds(Collections.emptyList());
        congNhanThanhPhamPayload.setNoiNhan(null);
        congNhanThanhPhamPayload.setNgayThangNamTPKCS(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamTPKCS()));
//        congNhanThanhPhamPayload.setNgayThangNamQuanDoc(DateTimeUtils.toString(congNhanThanhPham.getngay)); // Quan doc khong ky, nen khong co ngay thang nam.
        congNhanThanhPhamPayload.setNgayThangNamToTruong1(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamToTruong1()));
        congNhanThanhPhamPayload.setNgayThangNamToTruong2(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamToTruong2()));
        congNhanThanhPhamPayload.setNgayThangNamToTruong3(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamToTruong3()));
        congNhanThanhPhamPayload.setNgayThangNamToTruong4(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamToTruong4()));
        congNhanThanhPhamPayload.setNgayThangNamToTruong5(DateTimeUtils.toString(congNhanThanhPham.getNgayThangNamToTruong5()));
        congNhanThanhPhamPayload.setPaId(congNhanThanhPham.getPaId());
        return congNhanThanhPhamPayload;
    }

    public CongNhanThanhPham toEntity(CongNhanThanhPham congNhanThanhPham) {
        if (tpId != null && tpId <= 0) {
            tpId = null;
        }
        BeanUtils.copyProperties(this, congNhanThanhPham);
        congNhanThanhPham.setNoiDungThucHiens(
                this.getNoiDungThucHiens()
                        .stream()
                        .map(detail -> {
                            NoiDungThucHien entity = detail.toEntity();
                            if (Objects.nonNull(congNhanThanhPham.getTpId())) {
                                entity.setCongNhanThanhPham(congNhanThanhPham);
                            }
                            return entity;
                        })
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        congNhanThanhPham.setGioX(this.getGioX());
        congNhanThanhPham.setLaoDongTienLuong(this.getLaoDongTienLuong());
        congNhanThanhPham.setDong(this.getGioX() * this.getLaoDongTienLuong());
        return congNhanThanhPham;
    }

    public void filterPermission(User user) {
        if (user.isQuanDocPhanXuong()) {
            quanDocDisable = false;
        }
        if (user.isTruongPhongKCS()) {
            tpkcsDisable = false;
        }
        if (user.getUserId().equals(toTruong1Id)) {
            toTruong1Disable = false;
            nghiemThuDisable = false;
        }
        if (user.getUserId().equals(toTruong2Id)) {
            toTruong2Disable = false;
            nghiemThuDisable = false;
        }
        if (user.getUserId().equals(toTruong3Id)) {
            toTruong3Disable = false;
            nghiemThuDisable = false;
        }
        if (user.getUserId().equals(toTruong4Id)) {
            toTruong4Disable = false;
            nghiemThuDisable = false;
        }
        if (user.getUserId().equals(toTruong5Id)) {
            toTruong5Disable = false;
            nghiemThuDisable = false;
        }
    }

    public void capNhatChuKy(User user) {
        if (user.isTruongPhongKCS() && tpkcsXacNhan) {
            tpkcsId = user.getUserId();
        }
        if (user.isQuanDocPhanXuong() && quanDocXacNhan) {
            quanDocId = user.getUserId();
        }
    }

    public Boolean getTpkcsXacNhan() {
        return tpkcsXacNhan == null ? false : tpkcsXacNhan;
    }

    public Boolean getQuanDocXacNhan() {
        return quanDocXacNhan == null ? false : quanDocXacNhan;
    }

    public Boolean getToTruong1XacNhan() {
        return toTruong1XacNhan == null ? false : toTruong1XacNhan;
    }

    public Boolean getToTruong2XacNhan() {
        return toTruong2XacNhan == null ? false : toTruong2XacNhan;
    }

    public Boolean getToTruong3XacNhan() {
        return toTruong3XacNhan == null ? false : toTruong3XacNhan;
    }

    public Boolean getToTruong4XacNhan() {
        return toTruong4XacNhan == null ? false : toTruong4XacNhan;
    }

    public Boolean getToTruong5XacNhan() {
        return toTruong5XacNhan == null ? false : toTruong5XacNhan;
    }


    @Override
    public void processSignImgAndFullName(Map<Long, User> userById) {
        try {
            if (this.getTpkcsXacNhan()) {
                this.setTpkcsFullName(userById.get(this.getTpkcsId()).getFullName());
                this.setTpkcsSignImg(userById.get(this.getTpkcsId()).getSignImg());
            }
            toTruong1fullName = collectFullName(userById.get(toTruong1Id), "");
            toTruong2fullName = collectFullName(userById.get(toTruong2Id), "");
            toTruong3fullName = collectFullName(userById.get(toTruong3Id), "");
            toTruong4fullName = collectFullName(userById.get(toTruong4Id), "");
            toTruong5fullName = collectFullName(userById.get(toTruong5Id), "");

            toTruong1Alias = collectAlias(userById.get(toTruong1Id), "");
            toTruong2Alias = collectAlias(userById.get(toTruong2Id), "");
            toTruong3Alias = collectAlias(userById.get(toTruong3Id), "");
            toTruong4Alias = collectAlias(userById.get(toTruong4Id), "");
            toTruong5Alias = collectAlias(userById.get(toTruong5Id), "");

            if (getQuanDocXacNhan()) {
                quanDocFullName = collectFullName(userById.get(quanDocId), "");
                quanDocSignImg = collectSignImg(userById.get(quanDocId), "");
            }
            if (getToTruong1XacNhan()) {
                toTruong1SignImg = collectSignImg(userById.get(toTruong1Id), "");
            }
            if (getToTruong2XacNhan()) {
                toTruong2SignImg = collectSignImg(userById.get(toTruong2Id), "");
            }

            if (getToTruong3XacNhan()) {
                toTruong3SignImg = collectSignImg(userById.get(toTruong3Id), "");
            }
            if (getToTruong4XacNhan()) {
                toTruong4SignImg = collectSignImg(userById.get(toTruong4Id), "");
            }
            if (getToTruong5XacNhan()) {
                toTruong5SignImg = collectSignImg(userById.get(toTruong5Id), "");
            }

            for(NoiDungThucHienPayload nd : noiDungThucHiens){
                nd.setSignImg(collectSignImg(userById.get(nd.getNghiemThu()), ""));
            }
        } catch (Exception e) {
            PXLogger.error("[CNTP] Parse chữ ký và full name bị lỗi.");
        }
    }

    @Override
    public Collection<Long> getDeletedIds(CongNhanThanhPham o) {
        if (Objects.isNull(o)) {
            return Collections.emptyList();
        }
        Set<Long> requestIds = this.getNoiDungThucHiens()
                .stream()
                .map(el -> el.getNoiDungId())
                .collect(Collectors.toSet());

        return o.getNoiDungThucHiens()
                .stream()
                .map(el -> el.getNoiDungId())
                .filter(el -> !requestIds.contains(el))
                .collect(Collectors.toSet());
    }

    @Override
    public void capNhatNgayThangChuKy(CongNhanThanhPham cntp, CongNhanThanhPham existed) {
        cntp.setNgayThangNamTPKCS(existed.getNgayThangNamTPKCS());
        cntp.setTpkcsId(existed.getTpkcsId());
        cntp.setQuanDocIds(existed.getQuanDocIds());

        cntp.setNgayThangNamToTruong1(existed.getNgayThangNamToTruong1());
        cntp.setNgayThangNamToTruong2(existed.getNgayThangNamToTruong2());
        cntp.setNgayThangNamToTruong3(existed.getNgayThangNamToTruong3());
        cntp.setNgayThangNamToTruong4(existed.getNgayThangNamToTruong4());
        cntp.setNgayThangNamToTruong5(existed.getNgayThangNamToTruong5());


        if (cntp.getTpkcsXacNhan() && !existed.getTpkcsXacNhan()) {
            cntp.setNgayThangNamTPKCS(nowAsMilliSec());
        }
        if (Objects.nonNull(toTruong1Id) && cntp.getToTruong1XacNhan() && (cntp.getToTruong1XacNhan() != existed.getToTruong1XacNhan())) {
            cntp.setNgayThangNamToTruong1(nowAsMilliSec());
        }
        if (Objects.nonNull(toTruong2Id) && cntp.getToTruong2XacNhan() && (cntp.getToTruong2XacNhan() != existed.getToTruong2XacNhan())) {
            cntp.setNgayThangNamToTruong2(nowAsMilliSec());
        }
        if (Objects.nonNull(toTruong3Id) && cntp.getToTruong3XacNhan() && (cntp.getToTruong3XacNhan() != existed.getToTruong3XacNhan())) {
            cntp.setNgayThangNamToTruong3(nowAsMilliSec());
        }
        if (Objects.nonNull(toTruong4Id) && cntp.getToTruong4XacNhan() && (cntp.getToTruong4XacNhan() != existed.getToTruong4XacNhan())) {
            cntp.setNgayThangNamToTruong4(nowAsMilliSec());
        }
        if (Objects.nonNull(toTruong5Id) && cntp.getToTruong5XacNhan() && (cntp.getToTruong5XacNhan() != existed.getToTruong5XacNhan())) {
            cntp.setNgayThangNamToTruong5(nowAsMilliSec());
        }
    }

    @Override
    public void validateXacNhan(User user, CongNhanThanhPham request, CongNhanThanhPham existed) {
        if (user.isQuanDocPhanXuong()) {
            if (cusToTruongIds.size() > 5) {
                throw new PXException("cntp.totruong_max5");
            }
            toTruong1Id = getVal(cusToTruongIds, 0);
            toTruong2Id = getVal(cusToTruongIds, 1);
            toTruong3Id = getVal(cusToTruongIds, 2);
            toTruong4Id = getVal(cusToTruongIds, 3);
            toTruong5Id = getVal(cusToTruongIds, 4);
        } else if (user.isToTruong()) {
            // to truong se ko chuyen di dau nua, do to truong chi dinh nhanvienKcs o muc noi dung thuc hien roi. list_cus_id = empty.
            // NOTE: khong nhac den viec validate.
//            if (CollectionUtils.isNotEmpty(cusToTruongIds)) {
//                for (NoiDungThucHienPayload noiDungThucHien : noiDungThucHiens) {
//                    if (noiDungThucHien.isInvalidData()) {
//                        throw new PXException("cntp.noi_dung_thuc_hien");
//                    }
//                }
//            }
        } else if (user.isTruongPhongKCS()) {

        }

        request.setToTruong1Id(toTruong1Id);
        request.setToTruong2Id(toTruong2Id);
        request.setToTruong3Id(toTruong3Id);
        request.setToTruong4Id(toTruong4Id);
        request.setToTruong5Id(toTruong5Id);

        // update: tpkcs khong dong y thi gui ve cac nhan vien kcs
        if (user.isTruongPhongKCS() && !this.tpkcsXacNhan && (CollectionUtils.isEmpty(cusToTruongIds) || StringUtils.isEmpty(this.ykientpkcs))) {
            throw new PXException("cntp.kcs.thieu_thong_tin");
        }
        if (user.isTruongPhongKCS() && !this.tpkcsXacNhan && !CollectionUtils.isEmpty(cusToTruongIds)) {
            request.getNoiDungThucHiens().forEach(el -> {
                if (cusToTruongIds.contains(el.getNghiemThu())) {
                    el.setXacNhan(false);
                }
            });
        }
    }

    @JsonIgnore
    public boolean allNhanVienKCSAssinged() {
        for (NoiDungThucHienPayload noiDungThucHien : this.noiDungThucHiens) {
            if (noiDungThucHien.getNghiemThu() == null || noiDungThucHien.getNghiemThu() <= 0L) {
                return false;
            }
        }
        return true;
    }

    public boolean noiDungThucHienFilled() {
        for (NoiDungThucHienPayload noiDungThucHien : noiDungThucHiens) {
            if (noiDungThucHien.isInvalidData()) {
                return false;
            }
        }
        return true;
    }

    public Float getLaoDongTienLuong() {
        return laoDongTienLuong == null ? 0f : laoDongTienLuong;
    }

    public Float getDong() {
        return dong == null ? 0f : dong;
    }

    public Float getGioX() {
        return gioX == null ? 0f : gioX;
    }

    @Override
    public CongNhanThanhPhamPayload andStatus(RequestType status) {
        return this;
    }


}
