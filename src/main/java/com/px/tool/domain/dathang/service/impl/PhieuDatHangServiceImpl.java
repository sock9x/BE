package com.px.tool.domain.dathang.service.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.dathang.PhieuDatHang;
import com.px.tool.domain.dathang.PhieuDatHangPayload;
import com.px.tool.domain.dathang.repository.PhieuDatHangDetailRepository;
import com.px.tool.domain.dathang.repository.PhieuDatHangRepository;
import com.px.tool.domain.dathang.service.PhieuDatHangService;
import com.px.tool.domain.kiemhong.KiemHong;
import com.px.tool.domain.kiemhong.repository.KiemHongDetailRepository;
import com.px.tool.domain.kiemhong.repository.KiemHongRepository;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.service.impl.BaseServiceImpl;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class PhieuDatHangServiceImpl extends BaseServiceImpl implements PhieuDatHangService {
    @Autowired
    private PhieuDatHangRepository phieuDatHangRepository;

    @Autowired
    private PhieuDatHangDetailRepository phieuDatHangDetailRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private VanBanDenRepository vanBanDenRepository;

    @Autowired
    private KiemHongDetailRepository kiemHongDetailRepository;

    @Override
    public List<PhieuDatHang> findByPhongBan(Long userId) {
        return phieuDatHangRepository.findByCreatedBy(userId);
    }

    @Override
    public PhieuDatHangPayload findById(Long userId, Long id) {
        Request request = requestService.findById(id);

        PhieuDatHangPayload payload = PhieuDatHangPayload
                .fromEntity(request.getPhieuDatHang())
                .filterPermission(userService.findById(userId));
        payload.setRequestId(request.getRequestId());
        payload.processSignImgAndFullName(userService.userById());
        return payload;
    }

    @Override
    @Transactional
    public PhieuDatHang save(Long userId, PhieuDatHangPayload phieuDatHangPayload) {
        if (phieuDatHangPayload.notIncludeId()) {
            // TODO: new update truong hop khong co id thi van cho tao phieu dat hang, => copy sang kiem hong
            //
            return createData(userId, phieuDatHangPayload);
        }
        PhieuDatHang existedPhieuDatHang = phieuDatHangRepository
                .findById(phieuDatHangPayload.getPdhId())
                .orElse(null);

        checkSaveDatHang(userService.findById(userId), phieuDatHangPayload);

        Long kiemHongReceiverId = existedPhieuDatHang.getRequest().getKiemHongReceiverId();
        Long phieuDatHangReceiverId = Objects.isNull(phieuDatHangPayload.getNoiNhan()) ? userId : phieuDatHangPayload.getNoiNhan();
        Long phuongAnReceiverId = existedPhieuDatHang.getRequest().getPhuongAnReceiverId();
        Long cntpReceiverId = existedPhieuDatHang.getRequest().getCntpReceiverId();

        User user = userService.findById(userId);

        phieuDatHangPayload.capNhatChuKy(user);
        Long requestId = existedPhieuDatHang.getRequest().getRequestId();
        PhieuDatHang phieuDatHang = new PhieuDatHang();
        phieuDatHangPayload.toEntity(phieuDatHang);
        phieuDatHangPayload.capNhatNgayThangChuKy(phieuDatHang, existedPhieuDatHang);
        phieuDatHangPayload.validateXacNhan(user, phieuDatHang, existedPhieuDatHang);
        if (phieuDatHang.allApproved()) {
            existedPhieuDatHang.getRequest().setStatus(RequestType.PHUONG_AN); // phieu dat_hang da success
            guiVanBanDen(phieuDatHangPayload);
            // clear back recieverid
            phuongAnReceiverId = null;
            phieuDatHangReceiverId = phieuDatHangPayload.getNoiNhan();
            kiemHongReceiverId = null;
            cntpReceiverId = null;
        }
        cleanOldDetailData(phieuDatHangPayload, existedPhieuDatHang);
        if (phieuDatHangReceiverId != null && !phieuDatHangReceiverId.equals(existedPhieuDatHang.getRequest().getPhieuDatHangReceiverId())) {
            requestService.updateNgayGui(DateTimeUtils.nowAsMilliSec(), requestId);
        }
        requestService.updateReceiveId(requestId, kiemHongReceiverId, phieuDatHangReceiverId, phuongAnReceiverId, cntpReceiverId);
        phieuDatHangRepository.save(phieuDatHang);

        return phieuDatHang;
    }

    /**
     * tao phieu request va kiem hong de sycn data
     *
     * @return
     */
    @Transactional
    public PhieuDatHang createData(Long userId, PhieuDatHangPayload phieuDatHangPayload) {
        Request reEntity = phieuDatHangPayload.toRequestEntity();
        reEntity.setPhieuDatHangReceiverId(userId);
        reEntity.setCreatedBy(userId);

        KiemHong kiemHong = phieuDatHangPayload.toKiemHongEntity();
        PhieuDatHang phieuDatHang = new PhieuDatHang();
        phieuDatHangPayload.toEntity(phieuDatHang);
        phieuDatHang.setNguoiDatHangId(userId);
        if (Objects.isNull(phieuDatHang.getNoiNhan())) {
            phieuDatHang.setNoiNhan(userId);
            if (phieuDatHang.getNguoiDatHangXacNhan()) {
                throw new PXException("Nơi nhận phải được chọn.");
            }
        }
        if (phieuDatHang.getNguoiDatHangXacNhan()) {
            phieuDatHang.setNgayThangNamNguoiDatHang(DateTimeUtils.nowAsMilliSec());
        }

        User user = userService.findById(userId);
        phieuDatHangPayload.validateXacNhan(user, phieuDatHang, null);
        reEntity.setKiemHong(kiemHong);
        reEntity.setPhieuDatHang(phieuDatHang);

        Request savedRequest = requestService.save(reEntity);
        long requestId = savedRequest.getRequestId();

        KiemHong savedKiemHong = savedRequest.getKiemHong();
        kiemHong.getKiemHongDetails()
                .forEach(el -> el.setKiemHong(savedKiemHong));
        kiemHongDetailRepository.saveAll(kiemHong.getKiemHongDetails());

        requestService.updateNgayGui(DateTimeUtils.nowAsMilliSec(), requestId);
        requestService.updateReceiveId(requestId, -1L, phieuDatHang.getNoiNhan(), -1L, -1L);

        phieuDatHang.getPhieuDatHangDetails()
                .forEach(el -> el.setPhieuDatHang(savedRequest.getPhieuDatHang()));
        phieuDatHangDetailRepository.saveAll(phieuDatHang.getPhieuDatHangDetails());
        logger.info("[PHuong_an] Generate request , kiem hong success\nRequestId: {}\nKiemHongId:{}", savedRequest.getRequestId(), savedRequest.getKiemHong().getKhId());
        return phieuDatHang;
    }

    @Transactional
    public void guiVanBanDen(PhieuDatHangPayload payload) {
        try {
            VanBanDen vanBanDen = new VanBanDen();
            if (StringUtils.isEmpty(payload.getCusNoiDung())) {
                vanBanDen.setNoiDung("Bạn đang có một yêu cầu Đặt Hàng ");
            } else {
                vanBanDen.setNoiDung(payload.getCusNoiDung());
            }
            vanBanDen.setNoiNhan(CommonUtils.toString(payload.getCusReceivers()));
            vanBanDen.setRequestType(RequestType.DAT_HANG);
            vanBanDen.setRead(false);
            vanBanDen.setSoPa(payload.getSo());
            vanBanDen.setRequestId(payload.getRequestId());
            vanBanDenRepository.save(vanBanDen);
        } catch (Exception e) {
            throw new PXException("[Đặt Hàng]: Có lỗi khi gửi văn bản đến.");
        }
    }


    private void cleanOldDetailData(PhieuDatHangPayload requestPhieuDatHang, PhieuDatHang existedPhieuDatHang) {
        try {
            PXLogger.info("Dang clean da ta cu cua phieu dat hang");
            Collection<Long> deleteIds = requestPhieuDatHang.getDeletedIds(existedPhieuDatHang);
            if (!CollectionUtils.isEmpty(deleteIds)) {
                phieuDatHangDetailRepository.deleteAllByIds(deleteIds);
            }
        } catch (Exception e) {
            throw new PXException("dathang.clean_error");
        }
    }

    @Override
    public PhieuDatHang save(PhieuDatHang phieuDatHang) {
        return this.phieuDatHangRepository.save(phieuDatHang);
    }

    @Override
    public List<PhieuDatHang> findListCongViecCuaTLKT(Long userId) {
        return phieuDatHangRepository.findListCongViecCuaTLKT(userId);
    }

    public void checkSaveDatHang(User user, PhieuDatHangPayload phieuDatHangPayload) {

//        if (phieuDatHangPayload.getNoiNhan() == null && !user.isTruongPhongKTHK()) {
//            throw new PXException("Chưa chọn nơi chuyển tiếp");
//        }
        if (!phieuDatHangPayload.getNguoiDatHangXacNhan()) {
            throw new PXException("Người đặt hàng chưa xác nhận");
        }
    }
}
