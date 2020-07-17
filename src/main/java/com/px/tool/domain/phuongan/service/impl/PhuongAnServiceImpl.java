package com.px.tool.domain.phuongan.service.impl;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.NoiDungThucHien;
import com.px.tool.domain.cntp.repository.CongNhanThanhPhamRepository;
import com.px.tool.domain.file.FileStorageService;
import com.px.tool.domain.kiemhong.KiemHong;
import com.px.tool.domain.kiemhong.repository.KiemHongDetailRepository;
import com.px.tool.domain.kiemhong.repository.KiemHongRepository;
import com.px.tool.domain.phuongan.*;
import com.px.tool.domain.phuongan.repository.DinhMucLaoDongRepository;
import com.px.tool.domain.phuongan.repository.DinhMucVatTuRepository;
import com.px.tool.domain.phuongan.repository.PhuongAnRepository;
import com.px.tool.domain.phuongan.service.PhuongAnService;
import com.px.tool.domain.request.NguoiDangXuLy;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PhuongAnServiceImpl implements PhuongAnService {

    @Autowired
    private PhuongAnRepository phuongAnRepository;

    @Autowired
    private DinhMucVatTuRepository dinhMucVatTuRepository;

    @Autowired
    private DinhMucLaoDongRepository dinhMucLaoDongRepository;

    @Autowired
    private CongNhanThanhPhamRepository congNhanThanhPhamRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private KiemHongDetailRepository kiemHongDetailRepository;

    @Autowired
    private VanBanDenRepository vanBanDenRepository;

    @Autowired
    private KiemHongRepository kiemHongRepository;

    @Override
    public PhuongAnPayload findById(Long userId, Long id) {
        PhuongAn pa = phuongAnRepository.findById(id).orElseThrow(() -> new PXException("phuongan.not_found"));
        PhuongAnPayload payload = PhuongAnPayload.fromEntity(pa)
                .filterPermission(userService.findById(userId))
                .withFiles(fileStorageService.listFileNames(RequestType.PHUONG_AN, id));
        payload.setRequestId(pa.getPaId());
        payload.processSignImgAndFullName(userService.userById());
        return payload;
    }

    @Override
    public List<PhuongAn> findByPhongBan(Long userId) {
        return null;
    }

    @Override
    @Transactional
    public PhuongAn save(Long userId, PhuongAnPayload phuongAnPayload) {

        User user = userService.findById(userId);

        if (Objects.isNull(phuongAnPayload.getPaId()) && !user.isTroLyKT()) {
            throw new RuntimeException("Phuong an phai co id");
        }
        PhuongAn existedPhuongAn = new PhuongAn();
        if (Objects.nonNull(phuongAnPayload.getPaId())) {
            existedPhuongAn = phuongAnRepository
                    .findById(phuongAnPayload.getPaId())
                    .orElse(null);
        }

        Long phuongAnReceiverId = Objects.isNull(phuongAnPayload.getNoiNhan()) ? userId : phuongAnPayload.getNoiNhan();

        Long cntpReceiverId = existedPhuongAn.getCntpReceiverId();

        phuongAnPayload.capNhatChuKy(user);

        CongNhanThanhPham thanhPham = existedPhuongAn.getCongNhanThanhPham();
        PhuongAn phuongAn = new PhuongAn();
        Request reEntity = phuongAnPayload.toRequestEntity();

        boolean createNewPA = false;
        if (Objects.isNull(phuongAnPayload.getPaId())) {
            createNewPA = true ;
            Long maxId = phuongAnRepository.findPaIDMax();
            phuongAnPayload.setPaId(maxId == null ? 1l : maxId + 1l);
            reEntity.setPhuongAnReceiverId(phuongAnPayload.getPaId());
            reEntity.setCreatedBy(userId);
        }
        phuongAnPayload.toEntity(phuongAn);
        phuongAnPayload.capNhatNgayThangChuKy(phuongAn, existedPhuongAn);
        if (Objects.nonNull(phuongAnPayload.getNoiNhan())) {
            phuongAnPayload.validateXacNhan(user, phuongAn, existedPhuongAn);
        }

        if (phuongAn.allApproved()) {
            phuongAn.setStep(1L);
            existedPhuongAn.setStatus(RequestType.CONG_NHAN_THANH_PHAM);
            phuongAn.setStatus(RequestType.CONG_NHAN_THANH_PHAM);

            phuongAn.setCongNhanThanhPham(taoCNTP(phuongAn, thanhPham));
            guiVanBanDen(phuongAnPayload, existedPhuongAn);
            // clear receiverId
            cntpReceiverId = phuongAnPayload.getNoiNhan();
            phuongAnReceiverId = null;

        }
        if (Objects.nonNull(phuongAnPayload.getPaId())) {
            cleanOldDetailData(phuongAn, existedPhuongAn);
        }
        phuongAn.setPhuongAnReceiverId(phuongAnReceiverId);
        phuongAn.setCntpReceiverId(cntpReceiverId);

        if (phuongAnReceiverId != null && !phuongAnReceiverId.equals(existedPhuongAn.getPhuongAnReceiverId())) {
            phuongAn.setNgayGui(DateTimeUtils.nowAsMilliSec());
        }
        if (!user.isGiamDoc() && Objects.isNull(phuongAnPayload.getNoiNhan())) {
//            throw new PXException(" Chưa chọn nơi chuyển tiếp");
            phuongAnPayload.setNoiNhan(userId);
        }
        PhuongAn result = phuongAnRepository.save(phuongAn);
        if (createNewPA) {
            reEntity.setPhuongAn(result);
            requestService.save(reEntity);
        }

        return result;
    }

    @Transactional
    public void guiVanBanDen(PhuongAnPayload phuongAnPayload, PhuongAn existedPhuongAn) {
        try {
            Set<Long> userIds = new HashSet<>();
            userIds.add(existedPhuongAn.getTruongPhongVatTuId());
            userIds.add(existedPhuongAn.getTruongPhongKeHoachId());
            userIds.add(existedPhuongAn.getTruongPhongKTHKId());
            userIds.addAll(phuongAnPayload.getCusReceivers());

            VanBanDen vanBanDen = new VanBanDen();
            vanBanDen.setNoiDung(phuongAnPayload.getCusNoiDung());
            vanBanDen.setNoiNhan(CommonUtils.toString(userIds));
            vanBanDen.setRequestType(RequestType.PHUONG_AN);
            vanBanDen.setRead(false);
            vanBanDen.setSoPa(phuongAnPayload.getMaSo());
            vanBanDen.setNoiDung("Phương án số : " + phuongAnPayload.getMaSo() + " đã hoàn thành.");
            vanBanDen.setRequestId(phuongAnPayload.getRequestId());
            vanBanDenRepository.save(vanBanDen);
        } catch (Exception e) {
            throw new PXException("[Phương Án]: Có lỗi khi gửi văn bản đến.");
        }
    }

    @Transactional
    public CongNhanThanhPham taoCNTP(PhuongAn phuongAn, CongNhanThanhPham congNhanThanhPham) {
        if (congNhanThanhPham == null) {
            congNhanThanhPham = new CongNhanThanhPham();
            congNhanThanhPham.setPhuongAn(phuongAn);
        }
        congNhanThanhPham.setTenSanPham(phuongAn.getSanPham());
        congNhanThanhPham.setNoiDung(phuongAn.getNoiDung());
        congNhanThanhPham.setSoPA(phuongAn.getMaSo());
        congNhanThanhPham.setPaId(phuongAn.getPaId());

        if (!CollectionUtils.isEmpty(phuongAn.getDinhMucLaoDongs())) {
            Set<NoiDungThucHien> noiDungThucHiens = new LinkedHashSet<>();
            NoiDungThucHien detail = null;
            for (DinhMucLaoDong dinhMucLaoDong : phuongAn.getDinhMucLaoDongs()) {
                detail = new NoiDungThucHien(dinhMucLaoDong.getNoiDungCongViec());
                detail.setCongNhanThanhPham(congNhanThanhPham);
                noiDungThucHiens.add(detail);
            }
            congNhanThanhPham.setNoiDungThucHiens(noiDungThucHiens);
        }
        congNhanThanhPham.setQuanDocIds(phuongAn.getNguoiThucHien());
        congNhanThanhPham.setNgayGui(DateTimeUtils.nowAsMilliSec());
        return congNhanThanhPhamRepository.save(congNhanThanhPham);
    }

    private void cleanOldDetailData(PhuongAn requestPhuongAn, PhuongAn existedPhuongAn) {
        try {
            if (Objects.isNull(requestPhuongAn) || Objects.isNull(existedPhuongAn)) {
                return;
            }

            Set<Long> requsetDinhMucVatTuIds = requestPhuongAn.getDinhMucVatTus()
                    .stream()
                    .map(DinhMucVatTu::getVtId)
                    .collect(Collectors.toSet());

            Set<Long> vatuIds = existedPhuongAn.getDinhMucVatTus()
                    .stream()
                    .map(DinhMucVatTu::getVtId)
                    .filter(el -> !requsetDinhMucVatTuIds.contains(el))
                    .collect(Collectors.toSet());

            Set<Long> requsetLaoDongIds = requestPhuongAn.getDinhMucLaoDongs()
                    .stream()
                    .map(DinhMucLaoDong::getDmId)
                    .collect(Collectors.toSet());

            Set<Long> laoDongIds = existedPhuongAn.getDinhMucLaoDongs()
                    .stream()
                    .map(DinhMucLaoDong::getDmId)
                    .filter(el -> !requsetLaoDongIds.contains(el))
                    .collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(vatuIds)) {
                dinhMucVatTuRepository.deleteAllByIds(vatuIds);
            }
            if (!CollectionUtils.isEmpty(laoDongIds)) {
                dinhMucLaoDongRepository.deleteAllByIds(laoDongIds);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Co loi xay ra trong qua trinh clean DinhMucVatTu, DinhMucLaoDong");
        }
    }

    @Override
    public NguoiDangXuLy findNguoiDangXuLy(Long requestId) {
        return phuongAnRepository.findDetail(requestId);
    }

    @Override
    @Transactional
    public PhuongAnTaoMoi taoPhuongAnMoi(Long userid, RequestTaoPhuongAnMoi requestTaoPhuongAnMoi) {
        KiemHong kiemHong = kiemHongRepository.findByDetailId(requestTaoPhuongAnMoi.getDetailIds().get(0))
                .orElseThrow(() -> new PXException("kiemHong detail id khong chinh xac"));
        String soPDH = kiemHong.getRequest().getPhieuDatHang().getSo();

        PhuongAn phuongAn = taoPhuongAnMoi(userid, soPDH);
        CongNhanThanhPham cntp = congNhanThanhPhamRepository.save(new CongNhanThanhPham());

        kiemHongDetailRepository.taoPhuongAn(phuongAn.getPaId(), requestTaoPhuongAnMoi.getDetailIds());
        phuongAnRepository.updateCNTP(phuongAn.getPaId(), cntp.getTpId());

        PhuongAnTaoMoi paMoi = new PhuongAnTaoMoi();
        paMoi.setPaId(phuongAn.getPaId());
        return paMoi;
    }

    @Transactional
    public PhuongAn taoPhuongAnMoi(Long userid, String soPDH) {
        PhuongAn pa = new PhuongAn();
        pa.setStep(0L);
        pa.setNguoiLapId(userid);
        pa.setPDH(soPDH == null ? "" : soPDH);
        pa.setNgayGui(DateTimeUtils.nowAsMilliSec());
        return phuongAnRepository.save(pa);
    }

    @Override
    public Map<Long, PhuongAn> groupById(List<Long> paIds) {
        List<PhuongAn> list = phuongAnRepository.findAllById(paIds);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        } else {
            return list
                    .stream()
                    .collect(Collectors.toMap(PhuongAn::getPaId, Function.identity()));
        }

    }

    @Override
    public List<PhuongAn> findByUserId(Long userId) {
        return phuongAnRepository.findByUserId(userId);
    }
}

