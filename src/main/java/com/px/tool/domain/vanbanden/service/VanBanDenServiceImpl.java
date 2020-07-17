package com.px.tool.domain.vanbanden.service;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.file.FileStorageService;
import com.px.tool.domain.request.payload.NoiNhan;
import com.px.tool.domain.user.repository.UserRepository;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.vanbanden.VanBanDen;
import com.px.tool.domain.vanbanden.payload.VanBanDenDetail;
import com.px.tool.domain.vanbanden.payload.VanBanDenMoveFolder;
import com.px.tool.domain.vanbanden.payload.VanBanDenPageRequest;
import com.px.tool.domain.vanbanden.payload.VanBanDenPageResponse;
import com.px.tool.domain.vanbanden.payload.VanBanDenRequest;
import com.px.tool.domain.vanbanden.payload.VanBanDenResponse;
import com.px.tool.domain.vanbanden.repository.VanBanDenRepository;
import com.px.tool.infrastructure.exception.PXException;
import com.px.tool.infrastructure.service.impl.BaseServiceImpl;
import com.px.tool.infrastructure.utils.CommonUtils;
import com.px.tool.infrastructure.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.px.tool.infrastructure.utils.CommonUtils.toCollection;

@Service
public class VanBanDenServiceImpl extends BaseServiceImpl {
    @Autowired
    private VanBanDenRepository vanBanDenRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Những văn bản đã gửi.
     *
     * @return
     */
    public VanBanDenPageResponse findAll(Long userId, VanBanDenPageRequest vanBanDenPageRequest) {
        Page<VanBanDen> val = vanBanDenRepository.findByCreatedBy(userId, PageRequest.of(vanBanDenPageRequest.getPage(), vanBanDenPageRequest.getSize(), Sort.by(Sort.Order.desc("createdAt"))));
        return toResponse(val, vanBanDenPageRequest);
    }

    /**
     * Những văn bản cua toi.
     *
     * @return
     */
    public VanBanDenPageResponse findInBox(Long userId, VanBanDenPageRequest vanBanDenPageRequest) {
        vanBanDenPageRequest.setUserId(userId);
        return vanBanDenRepository.findByNoiNhan(vanBanDenPageRequest, noiNhanById());
    }

    private VanBanDenPageResponse toResponse(Page<VanBanDen> val, VanBanDenPageRequest request) {
        VanBanDenPageResponse res = new VanBanDenPageResponse(request.getPage(), request.getSize());
        if (val.isEmpty()) {
            return res;
        }
        res.setDetails(
                val.stream()
                        .map(el -> {
                            VanBanDenResponse payload = VanBanDenResponse.fromEntity(el);
                            payload.setNoiNhan(CommonUtils.toString(toCollection(el.getNoiNhan()), noiNhanById()));
                            return payload;
                        })
                        .collect(Collectors.toList())
        );
        return res;
    }

    private Map<Long, String> noiNhanById() {
        Map<Long, String> noiNhanById = new HashMap<>();
        for (NoiNhan noiNhan : userService.findVanBanDenNoiNhan()) {
            noiNhanById.put(noiNhan.getId(), noiNhan.getName());
        }
        return noiNhanById;
    }

    @Transactional
    public VanBanDenResponse save(Long userId, VanBanDenRequest payload) {
        VanBanDen entity = payload.toEntity();
        entity.setCreatedBy(userId);
        return VanBanDenResponse.fromEntity(vanBanDenRepository.save(entity));
    }

    public VanBanDenDetail findById(Long id) {
        return VanBanDenDetail
                .fromEntity(vanBanDenRepository.findById(id).orElseThrow(() -> new PXException("vanbanden_notFound")), noiNhanById())
                .withFilesName(fileStorageService.listFileNames(RequestType.VAN_BAN_DEN, id));
    }

    @Transactional
    public void deleteById(Long id) {
        try {
            vanBanDenRepository.delete(id);
            fileStorageService.deleteByRequestId(id);
        } catch (Exception e) {
            throw new PXException("vanbanden_deleteFailed");
        }
    }

    @Transactional
    public void guiVanBanDen(List<Long> group, RequestType requestType) {
        try {
            List<VanBanDen> contents = userRepository.findByGroup(group).stream()
                    .filter(el -> el.getLevel() == 3)
                    .map(el -> {
                        VanBanDen vanBanDen = new VanBanDen();
                        if (requestType == RequestType.KIEM_HONG) {
                            vanBanDen.setNoiDung("Bạn đang có một yêu cầu Kiểm Hỏng, " + DateTimeUtils.nowAsString());
                        } else if (requestType == RequestType.DAT_HANG) {
                            vanBanDen.setNoiDung("Bạn đang có một yêu cầu Đặt Hàng, " + DateTimeUtils.nowAsString());
                        } else if (requestType == RequestType.PHUONG_AN) {
                            vanBanDen.setNoiDung("Bạn đang có một yêu cầu Phương Án, " + DateTimeUtils.nowAsString());
                        }
                        vanBanDen.setNoiNhan(el.getUserId().toString());
                        vanBanDen.setRequestType(requestType);
                        vanBanDen.setRead(false);
                        return vanBanDen;
                    })
                    .collect(Collectors.toList());
            vanBanDenRepository.saveAll(contents);
        } catch (Exception e) {
            logger.error("[Kiem hong] Can't save Van Ban Den");
        }
    }

    @Transactional
    public void moveToFolder(VanBanDenMoveFolder moveFolder) {
        vanBanDenRepository.moveFolder(moveFolder.getVbdId(), moveFolder.getFolderId());
    }

    @Transactional
    public void hide(Long userId, Long id) {
        Optional<VanBanDen> vbd = vanBanDenRepository.findById(id);
        if (vbd.isPresent()) {
            try {
                vbd.get().setNoiNhan(
                        CommonUtils.toString(
                                toCollection(vbd.get().getNoiNhan())
                                        .stream()
                                        .filter(el -> !el.equals(userId))
                                        .collect(Collectors.toList())
                        )
                );
                vanBanDenRepository.save(vbd.get());
            } catch (Exception e) {
                throw new PXException("vanbanden.hide_error");
            }

        }
    }
}
