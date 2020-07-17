//package com.px.tool.infrastructure;
//
//import com.px.tool.domain.user.PhongBan;
//import com.px.tool.domain.user.Role;
//import com.px.tool.domain.user.User;
//import com.px.tool.domain.user.repository.PhongBanRepository;
//import com.px.tool.domain.user.repository.RoleRepository;
//import com.px.tool.domain.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@Component
//public class CacheService {
//
//    public static final short CACHE_ROLE = 0;
//    public static final short CACHE_PHONG_BAN = 1;
//    public static final short CACHE_USER = 2;
//    public static final short CACHE_USER_BY_ID = 3;
//    public static final short CACHE_PHAN_XUONG = 4;
//
//    private List<User> users_cache;
//    private Map<Long, User> userById_cache;
//    private Map<Long, Role> roleById_cache;
//
//
//    @Autowired
//    private UserRepository userRepository;
//
//
//
//    @PostConstruct
//    public void initCaching() {
//        this.users_cache = ;
//        this.userById_cache = this.users_cache.stream().collect(Collectors.toMap(el -> el.getUserId(), Function.identity()));
//    }
//
//    public List<User> getUsers_cache() {
//        return users_cache;
//    }
//
//    public Map<Long, User> getUserById_cache() {
//        return userById_cache;
//    }
//
//    public Map<Long, Role> getRoleById_cache() {
//        return roleById_cache;
//    }
//
//    public Map<Long, PhongBan> getPhongBanById_cache() {
//        return phongBanById_cache;
//    }
//
//    public void clearCache(short key) {
//        switch (key) {
//            case CACHE_ROLE:
//                this.roleById_cache.clear();
//                this.roleById_cache = roleRepository.findAll().stream()
//                        .collect(Collectors.toMap(el -> el.getRoleId(), Function.identity()));
//                break;
//            case CACHE_PHONG_BAN:
//                this.phongBanById_cache.clear();
//                this.phongBanById_cache = phongBanRepository.findAll().stream()
//                        .collect(Collectors.toMap(el -> el.getPhongBanId(), Function.identity()));
//                break;
//            case CACHE_USER:
//            case CACHE_USER_BY_ID:
//                this.users_cache.clear();
//                this.users_cache = userRepository.findAll();
//                this.userById_cache.clear();
//                this.userById_cache = this.users_cache.stream()
//                        .collect(Collectors.toMap(el -> el.getUserId(), Function.identity()));
//                break;
//            case CACHE_PHAN_XUONG:
//            default:
//                break;
//        }
//    }
//}
