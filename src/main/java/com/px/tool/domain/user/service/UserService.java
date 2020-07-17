package com.px.tool.domain.user.service;

import com.px.tool.domain.RequestType;
import com.px.tool.domain.request.payload.NoiNhan;
import com.px.tool.domain.request.payload.PhanXuongPayload;
import com.px.tool.domain.request.payload.ToSXPayload;
import com.px.tool.domain.user.User;
import com.px.tool.domain.user.payload.NoiNhanRequestParams;
import com.px.tool.domain.user.payload.UserPageRequest;
import com.px.tool.domain.user.payload.UserPageResponse;
import com.px.tool.domain.user.payload.UserRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    List<User> findAll();

    UserPageResponse findUsers(UserPageRequest request);

    void taoUser(UserRequest userRequest);

    int updateProfile(UserRequest user);

    Long delete(Long id);

    User findById(Long userId);

    List<User> findByIds(Collection<Long> userIds);

    List<NoiNhan> findNoiNhan(Long userId, NoiNhanRequestParams requestId);

    List<NoiNhan> findVanBanDenNoiNhan();

    List<NoiNhan> findCusNoiNhan(Long currentUserId, RequestType requestType, Long requestId);


    List<PhanXuongPayload> findListPhanXuong(Long userId, Long requestId);

    List<PhanXuongPayload> findNguoiThucHien();

    List<ToSXPayload> findListToSanXuat(Long userId, Long pxId, Long tsxId);

    Map<Long, User> userById();

    List<NoiNhan> findNhanVienKCS();

    User findTPKCS();
}
