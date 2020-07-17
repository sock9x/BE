package com.px.tool.domain.cntp.service.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.CongNhanThanhPhamPayload;
import com.px.tool.domain.cntp.repository.CongNhanThanhPhamRepository;
import com.px.tool.domain.cntp.repository.NoiDungThucHienRepository;
import com.px.tool.domain.cntp.service.CongNhanThanhPhamService;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.service.impl.BaseServiceImpl;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.tool.domain.user.repository.UserRepository.group_12_PLUS;
import static com.px.tool.infrastructure.utils.CommonUtils.collectionAdd;

@Service
public class CongNhanThanhPhamServiceImpl extends BaseServiceImpl implements CongNhanThanhPhamService {
    @Autowired
    private CongNhanThanhPhamRepository congNhanThanhPhamRepository;

    @Autowired
    private NoiDungThucHienRepository noiDungThucHienRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VanBanDenRepository vanBanDenRepository;


    @Override
    @Transactional
    public CongNhanThanhPham saveCongNhanThanhPham(Long userId, CongNhanThanhPhamPayload congNhanThanhPhamPayload) {
        if (Objects.isNull(congNhanThanhPhamPayload.getTpId())) {
            throw new RuntimeException("Thanh Pham phai co id");
        }
        CongNhanThanhPham existedCongNhanThanhPham = congNhanThanhPhamRepository
                .findById(congNhanThanhPhamPayload.getTpId())
                .orElse(null);

        User user = userService.findById(userId);
        congNhanThanhPhamPayload.capNhatChuKy(user);
        CongNhanThanhPham congNhanThanhPham = new CongNhanThanhPham();
        if (congNhanThanhPham.getTpId() == null) {
            congNhanThanhPham.setNgayGui(DateTimeUtils.nowAsMilliSec());
        }
        congNhanThanhPhamPayload.toEntity(congNhanThanhPham);
        congNhanThanhPhamPayload.capNhatNgayThangChuKy(congNhanThanhPham, existedCongNhanThanhPham);
        congNhanThanhPhamPayload.validateXacNhan(user, congNhanThanhPham, existedCongNhanThanhPham);
        // NOTE: tu dong chuyen den TP.KCS
        if (congNhanThanhPhamPayload.allNhanVienKCSAssinged()) {
            Long tpkcsId = userService.findTPKCS().getUserId();
            congNhanThanhPham.setTpkcsId(tpkcsId);
            congNhanThanhPhamPayload.setTpkcsId(tpkcsId);
        }

        cleanOldDetailData(congNhanThanhPhamPayload, existedCongNhanThanhPham);

        if (congNhanThanhPham.getTpkcsXacNhan()) {
            congNhanThanhPham.setStep(1L);
            guiVanBanDen(congNhanThanhPham);
        }
        return congNhanThanhPhamRepository.save(congNhanThanhPham);
    }

    /**
     * Nếu thấy tất cả các nhân viên KCS đã ký xác nhận thì Trưởng
     * phòng KCS tích chọn vào ô Đồng ý và ấn nút Chuyển để
     * chuyển cho các Phân xưởng đã thực hiện, Phòng KCS,
     * Phòng Kế hoạch trong mục Văn bản đến =&gt; Phiếu CNTP
     * được hoàn thành.
     */
    private void guiVanBanDen(CongNhanThanhPham congNhanThanhPham) {
        try {
            List<Long> usersId = CommonUtils.toCollection(congNhanThanhPham.getQuanDocIds());
            usersId.add(10L); // phong KCS
            usersId.add(14L); // phong ke hoach

            VanBanDen vanBanDen = new VanBanDen();
            vanBanDen.setNoiDung("Phiếu CNTP : CNTP-"+congNhanThanhPham.getTpId()+"đã hoàn thành.");
            vanBanDen.setNoiNhan(CommonUtils.toString(usersId));
            vanBanDen.setRequestType(RequestType.CONG_NHAN_THANH_PHAM);
            vanBanDen.setRead(false);
            vanBanDen.setRequestId(congNhanThanhPham.getTpId());
            vanBanDen.setSoPa("CNTP-" + congNhanThanhPham.getTpId());
            logger.info("CNTP đang gửi vbd: \n-So PA: {}\n-userIds:{}", congNhanThanhPham.getSoPA(), vanBanDen.getNoiNhan());
            vanBanDenRepository.save(vanBanDen);
        } catch (Exception e) {
            throw new PXException("[CNTP]: Có lỗi khi gửi văn bản đến.");
        }
    }

    private void cleanOldDetailData(CongNhanThanhPhamPayload requestCNTP, CongNhanThanhPham existedCongNhanThanhPham) {
        if (Objects.isNull(requestCNTP)) {
            return;
        }
        try {
            Collection<Long> deletedIds = requestCNTP.getDeletedIds(existedCongNhanThanhPham);
            if (!CollectionUtils.isEmpty(deletedIds)) {
                noiDungThucHienRepository.deleteAllByIds(deletedIds);
            }
        } catch (Exception e) {
            throw new RuntimeException("Co loi xay ra khi clean noi_dung_thuc_hien");
        }
    }

    @Override
    public CongNhanThanhPhamPayload timCongNhanThanhPham(Long userId, Long id) {
//        Request request = requestService.findById(id);
        CongNhanThanhPham existedCNTP = congNhanThanhPhamRepository
                .findById(id)
                .orElseThrow(() -> new PXException("cntp.not_found"));
        CongNhanThanhPhamPayload payload = CongNhanThanhPhamPayload.fromEntity(existedCNTP);
        User currentUser = userService.findById(userId);

        if (currentUser.isQuanDocPhanXuong()) {
            List<Long> cusIds = new ArrayList<>(5);
            collectionAdd(cusIds, payload.getToTruong1Id(), payload.getToTruong2Id(), payload.getToTruong3Id(), payload.getToTruong4Id(), payload.getToTruong5Id());
            payload.setCusToTruongIds(cusIds);
        } else if (currentUser.isToTruong() && payload.noiDungThucHienFilled()) {
            payload.setCusToTruongIds(payload.getTpkcsId() != null ? Arrays.asList(payload.getTpkcsId()) : Collections.emptyList());
        }

        payload.setRequestId(existedCNTP.getTpId());
        payload.filterPermission(currentUser);

        payload.processSignImgAndFullName(userService.userById());
        return payload;
    }


}
