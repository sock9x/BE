package com.px.tool.domain.kiemhong.service.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.dathang.PhieuDatHang;
import com.px.tool.domain.dathang.PhieuDatHangDetail;
import com.px.tool.domain.dathang.repository.PhieuDatHangDetailRepository;
import com.px.tool.domain.dathang.repository.PhieuDatHangRepository;
import com.px.tool.domain.kiemhong.KiemHong;
import com.px.tool.domain.kiemhong.KiemHongDetail;
import com.px.tool.domain.kiemhong.KiemHongPayLoad;
import com.px.tool.domain.kiemhong.repository.KiemHongDetailRepository;
import com.px.tool.domain.kiemhong.repository.KiemHongRepository;
import com.px.tool.domain.kiemhong.service.KiemHongService;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.repository.UserRepository;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.service.impl.BaseServiceImpl;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.px.tool.domain.user.repository.UserRepository.group_12_PLUS;
import static com.px.tool.infrastructure.utils.DateTimeUtils.nowAsMilliSec;

@Service
public class KiemHongServiceImpl extends BaseServiceImpl implements KiemHongService {
    @Autowired
    private KiemHongRepository kiemHongRepository;

    @Autowired
    private KiemHongDetailRepository kiemHongDetailRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private PhieuDatHangRepository phieuDatHangRepository;

    @Autowired
    private PhieuDatHangDetailRepository phieuDatHangDetailRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VanBanDenRepository vanBanDenRepository;

    @Override
    public List<KiemHongPayLoad> findThongTinKiemHongCuaPhongBan(Long userId) {
        return kiemHongRepository.findByCreatedBy(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(KiemHongPayLoad::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public KiemHongPayLoad findThongTinKiemHong(Long userId, Long id) {
        Request request = requestService.findById(id);

        if (request.getKiemHong() == null) {
            throw new RuntimeException("kiemhong.not_found");
        }
        KiemHongPayLoad payload = KiemHongPayLoad
                .fromEntity(request.getKiemHong())
                .andRequestId(request.getRequestId())
                .filterPermission(userService.findById(userId));
        payload.processSignImgAndFullName(userService.userById());
        return payload;
    }

    @Override
    @Transactional
    public KiemHongPayLoad save(Long currentUserId, KiemHongPayLoad kiemHongPayLoad) {

        try {
            if (!kiemHongPayLoad.includedId()) {
                validateTaoKiemHong(currentUserId, kiemHongPayLoad);

                KiemHong kiemHong = new KiemHong();
                kiemHongPayLoad.toEntity(kiemHong);
                kiemHong.setCreatedBy(currentUserId);

                kiemHong.setGiamDocXacNhan(false);
                kiemHong.setQuanDocXacNhan(false);
                kiemHong.setTroLyKTXacNhan(false);


                Request request = new Request();
                if (kiemHong.getToTruongXacNhan()) {
                    kiemHong.setNgayThangNamToTruong(nowAsMilliSec());
                    kiemHong.setToTruongId(currentUserId); // mac dinh tao kiemhong thi current user la to truong
                    kiemHong.setToTruongXacNhan(true);
                    request.setNgayGui(nowAsMilliSec());
                }
                request.setCreatedBy(currentUserId);
                request.setKiemHong(kiemHong);

                request.setPhieuDatHang(new PhieuDatHang());
                request.setStatus(RequestType.KIEM_HONG);
                request.setKiemHongReceiverId(Objects.isNull(kiemHongPayLoad.getNoiNhan()) ? currentUserId : kiemHongPayLoad.getNoiNhan());
                Request savedRequest = this.requestService.save(request);

                KiemHong savedKiemHong = savedRequest.getKiemHong();
                kiemHong.getKiemHongDetails()
                        .forEach(el -> el.setKiemHong(savedKiemHong));
                kiemHongDetailRepository.saveAll(kiemHong.getKiemHongDetails());
                return KiemHongPayLoad
                        .fromEntity(savedRequest.getKiemHong())
                        .andStatus(savedRequest.getStatus())
                        .andRequestId(savedRequest.getRequestId());
            } else {
                return capNhatKiemHong(currentUserId, kiemHongPayLoad);
            }
        } catch (Exception e) {
            if (e instanceof PXException) {
                throw e;
            } else {
                throw new PXException("Co loi trong qua trinh save Kiem Hong" + e.getMessage());
            }
        }

    }

    /**
     * Chỉ tổ trưởng mới có quyền lập  phiếu
     * <p>
     * Khi lập phiếu thì không được có xác nhận.
     */
    private void validateTaoKiemHong(Long currentUserId, KiemHongPayLoad kiemHongPayLoad) {
        User user = userService.findById(currentUserId);
        if (!user.isToTruong()) {
            throw new PXException("Chỉ tổ trưởng mới có quyền lập phiếu");
        }
        if (Objects.nonNull(kiemHongPayLoad.getNoiNhan())) {
            User noiNhan = userService.findById(kiemHongPayLoad.getNoiNhan());
            if (noiNhan.getLevel() == 4 && !kiemHongPayLoad.getToTruongXacNhan()) {
                throw new PXException("Tổ trưởng chưa ký không được chuyển");
            }
        }

    }

    @Override
    @Transactional
    public KiemHongPayLoad capNhatKiemHong(Long userId, KiemHongPayLoad kiemHongPayLoad) {
        KiemHong existedKiemHong = kiemHongRepository
                .findById(kiemHongPayLoad.getKhId())
                .orElseThrow(() -> new PXException("Không tìm thấy kiểm hỏng"));

        User user = userService.findById(userId);
        kiemHongPayLoad.capNhatChuKy(user);
        KiemHong requestKiemHong = new KiemHong();
        kiemHongPayLoad.toEntity(requestKiemHong);
        kiemHongPayLoad.capNhatNgayThangChuKy(requestKiemHong, existedKiemHong);
        kiemHongPayLoad.validateXacNhan(user, requestKiemHong, existedKiemHong);

        cleanKiemHongDetails(kiemHongPayLoad, existedKiemHong);
        Long requestId = existedKiemHong.getRequest().getRequestId();
        PhieuDatHang pdh = existedKiemHong.getRequest().getPhieuDatHang();

        Long kiemHongReceiverId = Objects.isNull(kiemHongPayLoad.getNoiNhan()) ? userId : kiemHongPayLoad.getNoiNhan();
        Long phieuDatHangReceiverId = existedKiemHong.getRequest().getPhieuDatHangReceiverId();
        Long phuongAnReceiverId = existedKiemHong.getRequest().getPhuongAnReceiverId(); // TODO: unused
        Long cntpReceiverId = existedKiemHong.getRequest().getCntpReceiverId();// TODO: unused

        if (requestKiemHong.allApproved()) {
            if (Objects.isNull(kiemHongPayLoad.getNoiNhan())) {
                throw new PXException("noi_nhan.must_choose");
            }
            existedKiemHong.getRequest().setStatus(RequestType.DAT_HANG);
            requestKiemHong.setRequest(existedKiemHong.getRequest());
            // clear back recieverid
            phieuDatHangReceiverId = kiemHongPayLoad.getNoiNhan();
            kiemHongReceiverId = null;
            phuongAnReceiverId = null;
            cntpReceiverId = null;

            createPhieuDatHang(requestKiemHong, pdh);
            guiVanBanDen(existedKiemHong, kiemHongPayLoad);
        }
        if ((kiemHongReceiverId != null && !kiemHongReceiverId.equals(existedKiemHong.getRequest().getKiemHongReceiverId()))
                || (phieuDatHangReceiverId != null && !phieuDatHangReceiverId.equals(existedKiemHong.getRequest().getPhieuDatHangReceiverId()))) {
            requestService.updateNgayGui(DateTimeUtils.nowAsMilliSec(), requestId);
        }
        requestService.updateReceiveId(requestId, kiemHongReceiverId, phieuDatHangReceiverId, phuongAnReceiverId, cntpReceiverId);
        kiemHongRepository.save(requestKiemHong);
        kiemHongPayLoad.setRequestId(requestId);
        return kiemHongPayLoad;
    }

    /**
     * update 05/02
     * Phiếu Kiểm hỏng sẽ được chuyển đến các phòng (Phòng  KTHK, Phòng xe máy đặc chủng, Phòng vật tư - account 8,9,12) và
     * PX đã lập kiểm hỏng, Trợ lý KT đã lập kiểm hỏng, Tổ SX đã lập kiểm hỏng
     */
    @Transactional
    public void guiVanBanDen(KiemHong existedKiemHong, KiemHongPayLoad payload) {
        try {
            List<User> users = userRepository
                    .findByGroup(group_12_PLUS)
                    .stream()
                    .filter(el -> el.getLevel() == 3)
                    .collect(Collectors.toList());
            Set<Long> ids = users.stream().map(User::getUserId).collect(Collectors.toSet());
            ids.addAll(payload.getCusReceivers());
            ids.add(existedKiemHong.getQuanDocId());
            ids.add(existedKiemHong.getTroLyId());
            ids.add(existedKiemHong.getToTruongId());

            VanBanDen vanBanDen = new VanBanDen();
            if (StringUtils.isEmpty(payload.getCusNoiDung())) {
                vanBanDen.setNoiDung("Phiếu kiểm hỏng : PKH-" + payload.getKhId() + " đã hoàn thành.");
            } else {
                vanBanDen.setNoiDung(payload.getCusNoiDung());
            }
            vanBanDen.setNoiNhan(CommonUtils.toString(ids));
            vanBanDen.setRequestType(RequestType.KIEM_HONG);
            vanBanDen.setRead(false);
            vanBanDen.setRequestId(existedKiemHong.getRequest().getRequestId());
            vanBanDen.setSoPa("PKH-" + payload.getKhId());
            vanBanDenRepository.save(vanBanDen);
        } catch (Exception e) {
            throw new PXException("[Kiểm Hỏng]: Có lỗi khi gửi văn bản đến.");
        }
    }

    private void createPhieuDatHang(KiemHong requestKiemHong, PhieuDatHang pdh) {
//        pdh.setSo(requestKiemHong.getSoHieu());
        Map<Long, User> userById = userService.userById();
        pdh.setDonViYeuCau(userById.get(requestKiemHong.getToSX()).getAlias()); // C3 sheet1
        pdh.setPhanXuong(userById.get(requestKiemHong.getPhanXuong()).getAlias()); // C2 sheet1
        pdh.setNoiDung(requestKiemHong.getTenVKTBKT() + " " + requestKiemHong.getSoHieu()); // E2 sheet1 + E1 sheet1
        PhieuDatHang savedPdh = phieuDatHangRepository.save(pdh);

        Set<PhieuDatHangDetail> phieuDatHangDetails = new LinkedHashSet<>(requestKiemHong.getKiemHongDetails().size());
        PhieuDatHangDetail detail = null;
        for (KiemHongDetail kiemHongDetail : requestKiemHong.getKiemHongDetails()) {
            detail = new PhieuDatHangDetail();
            detail.setKiemHongDetailId(kiemHongDetail.getKhDetailId());
            detail.setTenPhuKien(kiemHongDetail.getTenPhuKien());
            detail.setTenVatTuKyThuat(kiemHongDetail.getTenLinhKien());
            detail.setKiMaHieu(kiemHongDetail.getKyHieu());
            detail.setDvt(kiemHongDetail.getDvt());
            detail.setSl(kiemHongDetail.getSl());
            detail.setPhuongPhapKhacPhuc(kiemHongDetail.getPhuongPhapKhacPhuc());
            detail.setPhieuDatHang(savedPdh);
            phieuDatHangDetails.add(detail);
        }
        phieuDatHangDetailRepository.saveAll(phieuDatHangDetails);
    }

    @Override
    public boolean isExisted(Long id) {
        return kiemHongRepository.existsById(id);
    }

    public void cleanKiemHongDetails(KiemHongPayLoad payload, KiemHong existedKiemHong) {
        try {
            if (Objects.isNull(payload)) {
                return;
            }
            Collection<Long> deletedIds = payload.getDeletedIds(existedKiemHong);
            if (CollectionUtils.isEmpty(deletedIds)) {
                return;
            }
            kiemHongDetailRepository.deleteAllByIds(deletedIds);
        } catch (Exception e) {
            throw new PXException("kiemhong.xoa_detail");
        }
    }
}
