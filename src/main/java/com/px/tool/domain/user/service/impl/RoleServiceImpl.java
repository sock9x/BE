package com.px.tool.domain.user.service.impl;

import com.px.tool.domain.user.Role;
import com.px.tool.domain.user.repository.RoleRepository;
import com.px.tool.infrastructure.exception.PXException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl {
    @Autowired
    private RoleRepository roleRepository;

    public Map<Long, Role> findAll() {
        return roleRepository.findAll().stream().collect(Collectors.toMap(el -> el.getRoleId(), Function.identity()));
    }

    public Role findById(Long id) {
        if (this.findAll().containsKey(id)) {
            return this.findAll().get(id);
        } else {
            throw new PXException("role.not_found");
        }
    }
}
