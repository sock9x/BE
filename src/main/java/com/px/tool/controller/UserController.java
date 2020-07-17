package com.px.tool.controller;

import com.px.tool.domain.request.payload.NoiNhan;
import com.px.tool.domain.user.Folder;
import com.px.tool.domain.user.PhongBan;
import com.px.tool.domain.user.payload.FolderPayload;
import com.px.tool.domain.user.payload.PhongBanPayload;
import com.px.tool.domain.user.payload.RolePayLoad;
import com.px.tool.domain.user.payload.UserDetailResponse;
import com.px.tool.domain.user.payload.UserPageRequest;
import com.px.tool.domain.user.payload.UserPageResponse;
import com.px.tool.domain.user.payload.UserPayload;
import com.px.tool.domain.user.payload.UserRequest;
import com.px.tool.domain.user.repository.FolderRepository;
import com.px.tool.domain.user.repository.PhongBanRepository;
import com.px.tool.domain.user.repository.UserRepository;
import com.px.tool.domain.user.service.UserService;
import com.px.tool.domain.user.service.impl.PhongBanServiceImpl;
import com.px.tool.domain.user.service.impl.RoleServiceImpl;
import com.px.tool.infrastructure.BaseController;
import com.px.tool.infrastructure.exception.PXException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PhongBanRepository phongBanRepository;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private PhongBanServiceImpl phongBanService;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public UserPageResponse findUsers(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return userService.findUsers(new UserPageRequest(page, 50));
    }

    /**
     * Update user Info
     */
    @PostMapping
    public int save(@RequestBody UserRequest user) throws InterruptedException {
        this.userService.updateProfile(user);
        return 1;
    }

    @DeleteMapping("/{id}")
    public Long deleteUser(@PathVariable Long id) {
        return this.userService.delete(id);
    }

    @GetMapping("/pb")
    public List<PhongBanPayload> getListPhongBan() {
        return phongBanRepository.findAll()
                .stream()
                .map(PhongBanPayload::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/info")
    public UserPayload getUserInfo(HttpServletRequest httpServletRequest) {
        Long userId = extractUserInfo(httpServletRequest);
        return UserPayload.fromEntity(userService.findById(userId));
    }

    @GetMapping("/detail")
    public UserDetailResponse findById(@RequestParam Long userId) {
        return UserDetailResponse.fromEntity(userService.findById(userId));
    }

    @PostMapping("/tao-user")
    public void createUser(@RequestBody UserRequest userRequest) {
        userService.taoUser(userRequest);
    }

    @GetMapping("/roles")
    public List<RolePayLoad> findByAllRole() {
        return roleService.findAll()
                .values()
                .stream()
                .map(RolePayLoad::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/phongbans")
    public Collection<PhongBan> findAllPhongBan() {
        return phongBanService.findAll().values();
    }

    @PostMapping("/thu-muc")
    public FolderPayload saveThuMuc(HttpServletRequest request, @RequestBody FolderPayload payload) {
        try {
            Folder entity = payload.toEntity();
            entity.setUser(userService.findById(extractUserInfo(request)));
            folderRepository.save(entity);
            payload.setFolderId(entity.getFolderId());
            return payload;
        } catch (Exception e) {
            throw new PXException("user.folder_save_failed");
        }
    }

    @GetMapping("/list-thu-muc")
    public List<FolderPayload> getDanhSachThuMuc(HttpServletRequest request) {
        return folderRepository
                .findAll(extractUserInfo(request))
                .stream()
                .filter(Objects::nonNull)
                .map(FolderPayload::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/generate-folder")
    public void createFolders(@RequestParam Long userId) {
        if (!userService.userById().containsKey(userId)) {
            throw new PXException("user.not_found");
        }
        IntStream.rangeClosed(1, 15)
                .forEach(el -> folderRepository.insertFolder("Thư mục " + el, userId));
    }

    @GetMapping("/find-to-truong")
    public List<NoiNhan> findToTruong(){
        return userRepository
                .findByGroup(UserRepository.group_17_25)
                .stream()
                .filter(el -> el.getLevel() == 5)
                .map(NoiNhan::fromUserEntity)
                .sorted()
                .collect(Collectors.toList());
    }


}
