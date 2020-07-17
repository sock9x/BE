package com.px.tool.controller;

import com.px.tool.domain.kiemhong.KiemHongPayLoad;
import com.px.tool.domain.kiemhong.service.KiemHongService;
import com.px.tool.domain.user.PhongBan;
import com.px.tool.domain.user.repository.PhongBanRepository;
import com.px.tool.infrastructure.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/px")
public class KiemHongController extends BaseController {

    @Autowired
    private KiemHongService kiemHongService;

    @Autowired
    private PhongBanRepository phongBanRepository;

    @GetMapping("/ttkh/{id}")
    public KiemHongPayLoad thongTinKiemHong(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = extractUserInfo(httpServletRequest);

        return kiemHongService.findThongTinKiemHong(userId, id);
    }

    /**
     * tao kiem hong thi dong thoi tao 1 cai request
     * cai request nay la cho chung toan bo 1 civong doi cua kiem hong -> dat hang -> pa -> cntp
     * dua  theo target cua phan chuyen (cap 2 ,3 , 4A ) -> xac nhan dc step hien tai cua request dang la gi.
     * <p>
     * having 1 requirement to get all requsets of an department.  -> base on status + department id ( userId -> departmentId)
     * having 1 requirement to get all request of an user -> base on createdBy
     * <p>
     * <p>
     * validate data on backend and show  clear message for them.
     *
     * @return
     */
    @PostMapping("/tkh")
    public KiemHongPayLoad saveKiemHong(HttpServletRequest httpServletRequest, @RequestBody KiemHongPayLoad kiemHongPayLoad) {
        logger.info("save kiem hong, \nrequestId: {}", kiemHongPayLoad.getRequestId());
        Long userId = extractUserInfo(httpServletRequest);
        return kiemHongService.save(userId, kiemHongPayLoad);
    }

    @GetMapping("/pb")
    public List<PhongBan> findPhongBans() {
        return phongBanRepository.findAll();
    }


    /**
     * tu dong get tat ca thong tin kiem hong cua 1 phong ban
     */
    @GetMapping("/lkh")
    public List<KiemHongPayLoad> getListKiemHongTheoPhongBan(SecurityContextHolderAwareRequestWrapper httpServletRequest) {
        Long userId = extractUserInfo(httpServletRequest);
        return kiemHongService.findThongTinKiemHongCuaPhongBan(userId);
    }

}
