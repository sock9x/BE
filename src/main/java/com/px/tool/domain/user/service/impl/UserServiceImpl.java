package com.px.tool.domain.user.service.impl;

import com.google.common.collect.Sets;
import com.px.tool.domain.RequestType;
import com.px.tool.domain.cntp.CongNhanThanhPham;
import com.px.tool.domain.cntp.repository.CongNhanThanhPhamRepository;
import com.px.tool.domain.phuongan.service.PhuongAnService;
import com.px.tool.domain.request.NguoiDangXuLy;
import com.px.tool.domain.request.Request;
import com.px.tool.domain.request.payload.NoiNhan;
import com.px.tool.domain.request.payload.PhanXuongPayload;
import com.px.tool.domain.request.payload.ToSXPayload;
import com.px.tool.domain.request.service.RequestService;
import com.px.tool.domain.user.PhongBan;
import com.px.tool.domain.user.Role;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.payload.NoiNhanRequestParams;
import com.px.tool.domain.user.payload.UserPageRequest;
import com.px.tool.domain.user.payload.UserPageResponse;
import com.px.tool.domain.user.payload.UserPayload;
import com.px.tool.domain.user.payload.UserRequest;
import com.px.tool.domain.user.repository.UserRepository;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.logger.PXLogger;
import com.px.tool.infrastructure.service.impl.BaseServiceImpl;
import com.px.tool.infrastructure.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.px.tool.domain.user.repository.UserRepository.group_12;
import static com.px.tool.domain.user.repository.UserRepository.group_14;
import static com.px.tool.domain.user.repository.UserRepository.group_17_25;
import static com.px.tool.domain.user.repository.UserRepository.group_29_40;
import static com.px.tool.domain.user.repository.UserRepository.group_KCS;
import static com.px.tool.domain.user.repository.UserRepository.group_giam_doc;
import static com.px.tool.infrastructure.utils.CommonUtils.collectionAdd;

@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private RequestService requestService;

    @Autowired
    private PhuongAnService phuongAnService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private PhongBanServiceImpl phongBanService;

    @Autowired
    private CongNhanThanhPhamRepository congNhanThanhPhamRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + username));
    }

    @Override
    public List<User> findAll() {
        PXLogger.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public UserPageResponse findUsers(UserPageRequest request) {
        Page<User> page = userRepository.findAll(request.toPageRequest());

        UserPageResponse usersPage = new UserPageResponse(request.getPage(), request.getSize());

        usersPage.setDetails(page.stream()
                .map(UserPayload::fromEntityNoImg) // each user -> userPayload to view on paging/sorting
                .collect(Collectors.toCollection(() -> new ArrayList<>((int) page.getTotalElements())))
        );
        usersPage.setTotal(page.getTotalElements());
        return usersPage;
    }

    @Override
    @Transactional
    public int updateProfile(UserRequest user) {
        User currentUser = userRepository.findById(user.getUserId()).get();

        if (StringUtils.isEmpty(user.getFullName())) {
            user.setFullName(currentUser.getFullName());
        }
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(currentUser.getPassword());
        }
        if (StringUtils.isEmpty(user.getImgBase64())) {
            userRepository.updateProfile(user.getFullName(), user.getPassword(), user.getUserId());
            return 1;
        }
        userRepository.updateProfile(user.getImgBase64(), user.getFullName(), user.getPassword(), user.getUserId());
        return 1;
    }

    @Override
    @Transactional
    public Long delete(Long id) {
        try {
            User user = userRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setDeleted(true);
            userRepository.save(user);
            return id;
        } catch (RuntimeException e) {
            throw new RuntimeException("Delete user failed.");
        }
    }

    @Override
    public User findById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new PXException("Không tìm thấy User với Id = " + userId));
    }

    @Override
    public List<User> findByIds(Collection<Long> userIds) {
        return userRepository.findByIds(userIds);
    }

    @Override
    public List<NoiNhan> findNoiNhan(Long userId, NoiNhanRequestParams requestParams) {
        User currentUser = findById(userId);
        List<User> pbs = new ArrayList<>();
        if (Objects.isNull(requestParams.getRequestId()) &&
                requestParams.getType() != RequestType.DAT_HANG &&
                requestParams.getType() != RequestType.PHUONG_AN) {
            pbs = userRepository.findByGroup(group_29_40); // case nay cho kiem hong :)), chua check dc cac case khac nhu the nao
        } else {
            if (requestParams.getType() == RequestType.PHUONG_AN) {
                filterTheoPhuongAn(requestParams, currentUser, pbs);
            } else if (requestParams.getType() == RequestType.CONG_NHAN_THANH_PHAM) {
                filterTheoCNTP(requestParams, currentUser, pbs);
            } else {
                Request existedRequest = null;
                try {
                    if (requestParams.getRequestId() != null) {
                        existedRequest = requestService.findById(requestParams.getRequestId());
                    }
                } catch (Exception e) {
                    logger.error("Request not found with id: {}", requestParams.getRequestId());
                }
                if (existedRequest != null && existedRequest.getStatus() == RequestType.KIEM_HONG) {
                    filterTheoKiemHong(requestParams, currentUser, pbs);
                } else if (requestParams.getType() == RequestType.DAT_HANG ||
                        (existedRequest != null && existedRequest.getStatus() == RequestType.DAT_HANG)) {
                    filterTheoDatHang(requestParams, currentUser, pbs);
                }
            }
        }
        if (Objects.isNull(pbs)) {
            return Collections.emptyList();
        }

        return pbs.stream()
                .map(NoiNhan::fromUserEntity)
                .sorted(Comparator.comparingLong(NoiNhan::getId))
                .collect(Collectors.toList());
    }

    private void filterTheoCNTP(NoiNhanRequestParams requestParams, User currentUser, List<User> users) {
        Stream<User> pbs = Stream.empty();
        CongNhanThanhPham congNhanThanhPham = congNhanThanhPhamRepository
                .findById(requestParams.getRequestId())
                .orElseThrow(() -> new PXException("cntp.not_found"));

        if (currentUser.isQuanDocPhanXuong()) {
            // chuyen cho cac to truong
            List<Long> cusIds = new ArrayList<>(5);
            collectionAdd(cusIds, congNhanThanhPham.getToTruong1Id(), congNhanThanhPham.getToTruong2Id(), congNhanThanhPham.getToTruong3Id(), congNhanThanhPham.getToTruong4Id(), congNhanThanhPham.getToTruong5Id());
            if (CollectionUtils.isEmpty(cusIds)) {
                // to truong cua px hien tai
                pbs = userRepository
                        .findByGroup(Arrays.asList(currentUser.getUserId()))
                        .stream()
                        .filter(el -> el.getLevel() == 5);

            } else {
                pbs = Stream.of(
                        // to truong da setup + to truong to hien tai
                        userRepository.findByIds(cusIds).stream(),
                        userRepository.findByGroup(Arrays.asList(currentUser.getUserId())).stream().filter(el -> el.getLevel() == 5)
//                        userRepository.findByGroup(group_KCS).stream().filter(el -> el.getLevel() == 3)
                )
                        .flatMap(el -> el);
            }
        } else if (currentUser.isToTruong()) {
            // chuyen cho cac nhan vien kcs => ma cac nhan vien kcs da dc fill o tren => cho nay empty
            pbs = Stream.empty();
        } else if (currentUser.isNhanVienKCS()) {
            // khong chuyen di dau ca, vi khi all nhanvienKSC ok thi tu chuyen len truong phong Kcs
            pbs = Stream.empty();
        } else if (currentUser.isTruongPhongKCS()) {
            // TP.KCS khong dong y thi chuyen ve cac nhan vien KCS
            if (!requestParams.getTpKCS()) {
                CongNhanThanhPham cntp = congNhanThanhPhamRepository
                        .findById(requestParams.getRequestId())
                        .orElseThrow(() -> new PXException("Cong nhan thanh pham not found"));
                Set<Long> userIds = cntp.getNoiDungThucHiens()
                        .stream()
                        .map(el -> el.getNghiemThu())
                        .collect(Collectors.toSet());

                pbs = userRepository.findByIds(userIds).stream();
            } else {
                // step cuoi cung la TPKCS nen khong can chuyen di dau ca.
                pbs = Stream.empty();
            }
        }

        if (pbs != null) {
            users.addAll(pbs.collect(Collectors.toList()));
        }
    }

    private void filterTheoPhuongAn(NoiNhanRequestParams requestParams, User currentUser, List<User> users) {
        Stream<User> pbs = Stream.empty();

        NguoiDangXuLy nguoiDangXuLy = new NguoiDangXuLy();
        if(Objects.nonNull(requestParams.getRequestId())){
            nguoiDangXuLy = phuongAnService.findNguoiDangXuLy(requestParams.getRequestId());
        }else{
            requestParams.setNguoiLap(true);
        }

        if (currentUser.isNguoiLapPhieu()) {
            if (requestParams.getNguoiLap()) {
                pbs = userRepository.findByGroup(group_29_40).stream().filter(el -> el.getLevel() == 3);
            }
        } else if (currentUser.isTruongPhongKTHK()) {   // chuyen 50d : NV_TIEP_LIEU
            if (requestParams.getTpKTHK()) {
                pbs = userRepository.findByIds(Arrays.asList(54L)).stream().filter(el -> el.getLevel() == 4);
            } else {
                // TODO: chuyen ve nguoi lap phieu
                pbs = toUserStream(nguoiDangXuLy.getNguoiLap());
            }
        } else if (currentUser.isNhanVienTiepLieu()) {
            pbs = userRepository.findByGroup(group_12).stream().filter(el -> el.getLevel() == 3);
        } else if (currentUser.isTruongPhongVatTu()) {
            if (requestParams.getTpVatTu()) {
                // nhan vien dinh muc (phong ke hoach)
                pbs = userRepository.findByGroup(group_14).stream().filter(el -> el.getLevel() == 4);
            } else {
                // TODO chuyen nhan vien tiep lieu = tpkthk
                pbs = userRepository.findByIds(Arrays.asList(nguoiDangXuLy.getTpKTHK(), 54L)).stream();
            }
        } else if (currentUser.isNhanVienDinhMuc()) { // chuyen truong phong ke hoach
            pbs = userRepository.findByGroup(group_14).stream().filter(el -> el.getLevel() == 3);
        } else if (currentUser.isTruongPhongKeHoach()) {
            if (requestParams.getTpKeHoach()) {
                pbs = userRepository.findByGroup(group_giam_doc).stream(); // chuyen len giam doc
            } else {
                // TODO:chuyen nhan vien dinh muc
                pbs = userRepository.findByGroup(group_14).stream().filter(el -> el.getLevel() == 4).limit(1);
            }
        } else if (currentUser.getLevel() == 2) { // chuyen den Nguoi thuc hien (nguoi thuc hien la cac PX)
            // TODO: neu giam doc khong dong y, chuyen ve TPKTHK/XMDC, tp.Vat tu, tp.ke hoach:
            if (!requestParams.getGiamDoc()) {
                pbs = userRepository.findByIds(Arrays.asList(nguoiDangXuLy.getTpKTHK(), nguoiDangXuLy.getTpVatTu(), nguoiDangXuLy.getTpKeHoach())).stream();
            } // NOTE: KHi hoàn thành phương án thì sử dụng data ở mục: các đơn vị thực hiện. Đến đây thì tạo p.a success
        }
        if (pbs != null) {
            users.addAll(pbs.collect(Collectors.toList()));
        }
    }

    Stream<User> toUserStream(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return Stream.of(userOpt.get());
        } else {
            return Stream.empty();
        }
    }

    private void filterTheoDatHang(NoiNhanRequestParams requestParams, User currentUser, List<User> users) {
        Stream<User> pbs = Stream.empty();
        if (currentUser.isNhanVienVatTu()) {
            if (!requestParams.getNguoiDatHang()) {
                pbs = Stream.empty();
            } else {
                pbs = userRepository.findByGroup(group_12).stream();
            }
        } else if (currentUser.isTruongPhongVatTu()) {
            if (!requestParams.getTpVatTu()) {
                pbs = Stream.of(userById().get(requestService.findById(requestParams.getRequestId()).getPhieuDatHang().getNguoiDatHangId()));
            } else {
                Request request = requestService.findById(requestParams.getRequestId());
                if (request.getKiemHong().getTroLyId() == null) {
                    pbs = userRepository.findByIds(Arrays.asList(8L, 9L))
                            .stream()
                            .filter(el -> el.getLevel() == 3);
                } else {
                    // NOTE: trả về luôn cho trợ lý đã approve phiếu kiểm hỏng.
                    pbs = Stream.of(userById().get(request.getKiemHong().getTroLyId()));
                }
            }
        } else if (currentUser.isTroLyKT()) {
            pbs = userRepository.findByGroup(Arrays.asList(currentUser.getPhongBan().getPhongBanId()))
                    .stream()
                    .filter(el -> el.getLevel() == 3);
        } else if (currentUser.isTruongPhongKTHK()) {
            //TODO: neu truong phong kthk dong y thi tao van ban den + PA
            if (!requestParams.getTpKTHK()) {
                // NOTE: trả về luôn cho trợ lý đã approve phiếu kiểm hỏng + TP.Vat tu
                Request request = requestService.findById(requestParams.getRequestId());

                pbs = Stream.of(
                        userById().get(request.getKiemHong().getTroLyId()),
                        userById().get(request.getPhieuDatHang().getTpvatTuId()))
                        .filter(Objects::nonNull);
            } else {
                pbs = Stream.empty();
            }
        }
        if (pbs != null) {
            users.addAll(pbs.collect(Collectors.toList()));
        }
    }

    private void filterTheoKiemHong(NoiNhanRequestParams requestParams, User currentUser, List<User> users) {
        Stream<User> pbs = Stream.empty();
        if (currentUser.isToTruong()) {
            pbs = userRepository.findByGroup(group_29_40)
                    .stream()
                    .filter(el -> el.getLevel() == 4);
        } else if (currentUser.isTroLyKT()) {
            if (requestParams.getTroLyKT()) {
                pbs = Stream.of(userById().get(requestService.findById(requestParams.getRequestId()).getKiemHong().getPhanXuong()));
            } else {
                pbs = Stream.of(userById().get(requestService.findById(requestParams.getRequestId()).getKiemHong().getToTruongId()));
            }
        } else if (currentUser.isQuanDocPhanXuong()) {
            if (requestParams.getQuanDoc()) {
                pbs = userRepository.findByGroup(group_12)
                        .stream()
                        .filter(el -> el.getLevel() == 4);
            } else {
                pbs = Stream.of(userById().get(requestService.findById(requestParams.getRequestId()).getKiemHong().getTroLyId()));
            }
        }
        if (pbs != null) {
            users.addAll(pbs.collect(Collectors.toList()));
        }
    }

    @Override
    public List<NoiNhan> findVanBanDenNoiNhan() {
        return userRepository.findAll()
                .stream()
                .map(NoiNhan::fromUserEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoiNhan> findCusNoiNhan(Long currentUserId, RequestType requestType, Long requestId) {
        User user = findById(currentUserId);
        if (Objects.isNull(user)) {
            return Collections.emptyList();
        }
        if (requestType == RequestType.KIEM_HONG) {
            Long phongBanId = null;
            if (user.isToTruong()) {
                phongBanId = user.getPhongBan().getPhongBanId();
            } else if (requestId != null) {
                // truong hop PKH da duoc tao, va current user khong phai la to truong
                Request request = requestService.findById(requestId);
                User toTruong = null;
                if (request != null && request.getKiemHong() != null && request.getKiemHong().getToTruongId() != null) {
                    toTruong = findById(request.getKiemHong().getToTruongId());
                }
                if (toTruong == null) {
                    throw new PXException("kiemhong.cusNoiNhan.totruong");
                }
                phongBanId = toTruong.getPhongBan().getPhongBanId();
            }

            Long finalPhongBanId = phongBanId;
            return userRepository.findAll()
                    .stream()
                    .filter(el ->
                            !el.isAdmin() && (
                                    el.getPhongBan().getPhongBanId().equals(finalPhongBanId) // PX + to trong PX
                                            || el.isTruongPhongVatTu() // Vat.Tu
                                            || el.getPhongBan().getGroup().equals(8) // KTHK
                                            || el.getPhongBan().getGroup().equals(9)) // XMDC
                    )
                    .map(NoiNhan::fromUserEntity)
                    .sorted()
                    .collect(Collectors.toList());
        } else if (requestType == RequestType.DAT_HANG) {
            return userRepository.findAll()
                    .stream()
                    .filter(el ->
                            !el.isAdmin() && (
                                    el.isQuanDocPhanXuong() // PX
                                            || el.isTruongPhongVatTu() // Vat.Tu
                                            || el.getPhongBan().getGroup().equals(8) // KTHK
                                            || el.getPhongBan().getGroup().equals(9)) // XMDC

                    )
                    .map(NoiNhan::fromUserEntity)
                    .collect(Collectors.toList());
        } else if (requestType == RequestType.PHUONG_AN) {
            return userRepository.findAll()
                    .stream()
                    .filter(el -> // all users thuoc cap 2, cap 3
                            !el.isAdmin() && (el.getLevel() == 2 || el.getLevel() == 3 ||
                                    (el.getLevel() == 4 && (el.getPhongBan().getGroup().equals(8) // KTHK
                                            || el.getPhongBan().getGroup().equals(9))))) // XMDC))))
                    .map(NoiNhan::fromUserEntity)
                    .collect(Collectors.toList());

        } else {
            return findVanBanDenNoiNhan();
        }
    }

    @Override
    public List<PhanXuongPayload> findListPhanXuong(Long userId, Long requestId) {
        if (Objects.nonNull(requestId)) {
            try {
                Request request = requestService.findById(requestId);
                return Arrays.asList(PhanXuongPayload.fromUserEntity(userRepository.findById(request.getKiemHong().getPhanXuong()).get()));
            } catch (Exception e) {
                return PhanXuongPayload.emptyList;
            }
        }
        try {
            return userRepository.findByGroup(Arrays.asList(findById(userId).getPhongBan().getPhongBanId()))
                    .stream()
                    .filter(el -> el.getLevel() == 3)
                    .map(PhanXuongPayload::fromUserEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return PhanXuongPayload.emptyList;
        }
    }

    @Override
    public List<PhanXuongPayload> findNguoiThucHien() {
        return userRepository.findByGroup(group_17_25)
                .stream()
                .filter(el -> el.getLevel() == 3)
                .map(PhanXuongPayload::fromUserEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ToSXPayload> findListToSanXuat(Long userId, Long pxId, Long tsxId) {
        if (Objects.nonNull(tsxId)) {
            try {
                return Arrays.asList(ToSXPayload.fromUserEntity(userRepository.findById(tsxId).get()));
            } catch (Exception e) {
                Collections.emptyList();
            }
        }
        return userRepository.findByGroup(Arrays.asList(pxId))
                .stream()
                .filter(el -> el.getLevel() == 5 && el.getUserId().equals(userId))
                .map(ToSXPayload::fromUserEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, User> userById() {
        return userRepository.findAll().stream().collect(Collectors.toMap(el -> el.getUserId(), Function.identity()));
    }

    @Override
    @Transactional
    public void taoUser(UserRequest user) {
        if (Objects.isNull(user.getLevel()) || Objects.isNull(user.getPhanXuong())) {
            throw new PXException("user.admin.updateUserDetail");
        }
        Role role = roleService.findById(Long.valueOf(user.getLevel()));
        PhongBan phongBan = phongBanService.findById(user.getPhanXuong());
        User entity = user.toUserEntity();


        if (StringUtils.isEmpty(user.getAlias())) {
            entity.setAlias(phongBan.getName());
        } else {
            entity.setAlias(user.getAlias());
        }

        entity.setAuthorities(Sets.newHashSet(role));
        entity.setPhongBan(phongBan);

        if (user.getUserId() != null) {
            entity = userRepository.findById(user.getUserId()).get();
            if (!StringUtils.isEmpty(user.getEmail())) {
                entity.setEmail(user.getEmail());
            }
            if (!StringUtils.isEmpty(user.getFullName())) {
                entity.setFullName(user.getFullName());
            }
            if (!StringUtils.isEmpty(user.getImgBase64())) {
                entity.setSignImg(user.getImgBase64());
            }
            if (!StringUtils.isEmpty(user.getAlias())) {
                entity.setAlias(user.getAlias());
            }
            entity.setAuthorities(Sets.newHashSet(role));
            entity.setPhongBan(phongBan);

            if (!StringUtils.isEmpty(user.getPassword())) {
                entity.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }
        logger.info("Saving user with info: {}", user);
        userRepository.save(entity);
    }

    @Override
    public List<NoiNhan> findNhanVienKCS() {
        return userRepository.findByGroup(group_KCS).stream()
                .filter(el -> el.getLevel() == 4)
                .map(NoiNhan::fromUserEntity)
                .collect(Collectors.toList());
    }

    @Override
    public User findTPKCS() {
        return userRepository.findByGroup(group_KCS)
                .stream()
                .filter(el -> el.isTruongPhongKCS())
                .findFirst()
                .orElse(null);
    }
}
