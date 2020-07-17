package com.px.tool.controller;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.repository.CongNhanThanhPhamRepository;
import com.px.tool.domain.dathang.repository.PhieuDatHangRepository;
import com.px.tool.domain.kiemhong.repository.KiemHongDetailRepository;
import com.px.tool.domain.kiemhong.repository.KiemHongRepository;
import com.px.tool.domain.phuongan.repository.PhuongAnRepository;
import com.px.tool.domain.request.payload.NoiNhan;
import com.px.tool.domain.request.payload.NotificationPayload;
import com.px.tool.domain.request.payload.PhanXuongPayload;
import com.px.tool.domain.request.payload.ToSXPayload;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.payload.NoiNhanRequestParams;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/req")
public class RequestController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private VanBanDenRepository vanBanDenRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private KiemHongRepository kiemHongRepository;

    @Autowired
    private KiemHongDetailRepository kiemHongDetailRepository;

    @Autowired
    private PhieuDatHangRepository phieuDatHangRepository;

    @Autowired
    private PhuongAnRepository phuongAnRepository;

    @Autowired
    private CongNhanThanhPhamRepository congNhanThanhPhamRepository;

    @GetMapping("/noi-nhan")
    public List<NoiNhan> getListNoiNhan(HttpServletRequest httpServletRequest,
                                        @RequestParam(required = false) Long requestId,
                                        @RequestParam(required = false) Boolean toTruong,
                                        @RequestParam(required = false) Boolean troLyKT,
                                        @RequestParam(required = false) Boolean quanDoc,
                                        //
                                        @RequestParam(required = false) Boolean nguoiDatHang,
                                        //
                                        @RequestParam(required = false) Boolean nguoiLap,
                                        @RequestParam(required = false) Boolean tpVatTu,
                                        @RequestParam(required = false) Boolean tpKeHoach,
                                        @RequestParam(required = false) Boolean tpKTHK,
                                        @RequestParam(required = false) Boolean giamDoc,
                                        //
                                        @RequestParam(required = false) Boolean tpKCS,
                                        @RequestParam(required = false) Boolean nguoiThucHien,
                                        @RequestParam(required = false) Boolean nguoiGiaoViec,

                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) RequestType type


    ) {

        return userService.findNoiNhan(extractUserInfo(httpServletRequest), NoiNhanRequestParams.builder()
                .requestId(requestId)
                .toTruong(toTruong)
                .troLyKT(troLyKT)
                .quanDoc(quanDoc)
                .nguoiDatHang(nguoiDatHang)
                .nguoiLap(nguoiLap)
                .tpVatTu(tpVatTu)
                .tpKeHoach(tpKeHoach)
                .tpKTHK(tpKTHK)
                .giamDoc(giamDoc)
                .tpKCS(tpKCS)
                .nguoiThucHien(nguoiThucHien)
                .nguoiGiaoViec(nguoiGiaoViec)
                .type(type)
                .build());
    }

    @GetMapping("/vbd/noi-nhan")
    public List<NoiNhan> getListNoiNhan() {
        return userService.findVanBanDenNoiNhan();
    }

    @GetMapping("/phan-xuong")
    public List<PhanXuongPayload> getPhanXuong(HttpServletRequest request, @RequestParam(required = false) Long requestId) {
        return userService.findListPhanXuong(extractUserInfo(request), requestId);
    }

    @GetMapping("/to-sx")
    public List<ToSXPayload> getToSanXuat(HttpServletRequest httpServletRequest, @RequestParam(required = false) Long pxId, @RequestParam(required = false) Long tsxId) {
        return userService.findListToSanXuat(extractUserInfo(httpServletRequest), pxId, tsxId);
    }

    @GetMapping("/notification")
    public List<NotificationPayload> getNotification(HttpServletRequest request) {
        return vanBanDenRepository.findNotification(extractUserInfo(request), PageRequest.of(0, 100))
                .stream()
                .map(el -> {
                    NotificationPayload payload = new NotificationPayload();
                    payload.setRequestId(Long.valueOf(el.getVbdId()));
                    payload.setNotiId(Long.valueOf(el.getVbdId()));
                    if (el.getRequestType() == RequestType.KIEM_HONG) {
                        payload.setBody("Bạn đang có 1 yêu cầu kiểm hỏng.");
                    } else if (el.getRequestType() == RequestType.DAT_HANG) {
                        payload.setBody("Bạn đang có 1 yêu cầu đặt hàng.");
                    } else if (el.getRequestType() == RequestType.PHUONG_AN) {
                        payload.setBody("Bạn đang có 1 yêu cầu phương án.");
                    } else if (el.getRequestType() == RequestType.CONG_NHAN_THANH_PHAM) {
                        payload.setBody("Bạn đang có 1 thông yêu cầu CNTP.");
                    }

                    return payload;
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/notification/{id}")
    public void readNoti(@PathVariable Long id) {
        Optional<VanBanDen> vbd = vanBanDenRepository.findById(id);
        if (vbd.isPresent()) {
            vbd.get().setRead(true);
            vanBanDenRepository.save(vbd.get());
        }
    }

    @GetMapping("/nguoi-thuc-hien")
    public List<PhanXuongPayload> getNguoiThucHien(@RequestParam RequestType requestType) {
        return userService.findNguoiThucHien();
    }

    @GetMapping("/cus-noi-nhan")
    public List<NoiNhan> getCusNoiNhan(HttpServletRequest httpServletRequest, @RequestParam RequestType requestType, @RequestParam(required = false) Long requestId) {
        return userService.findCusNoiNhan(extractUserInfo(httpServletRequest), requestType, requestId);
    }

    @GetMapping("/cntp/nguoi-lam")
    public List<NoiNhan> getNguoiLamCNTP() {
        return userService.findNhanVienKCS();
    }

    @DeleteMapping("/del/range")
    public String deleteData(@RequestParam Long fromDate, @RequestParam Long toDate) {
        logger.info("delete data\nstartDate:\t{}\nendDate:\t{}", fromDate, toDate);
        requestService.deleteData(fromDate, toDate);
        return "Data is deleted !!!!";
    }
}
