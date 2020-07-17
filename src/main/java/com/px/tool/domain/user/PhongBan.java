package com.px.tool.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.px.tool.infrastructure.model.payload.EntityDefault;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "phong_ban")
public class PhongBan extends EntityDefault {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long phongBanId;

    @Column
    private String name;

    @Column
    private Integer group;

    @JsonBackReference
    @OneToMany(mappedBy = "phongBan", cascade = CascadeType.ALL)
    private Set<User> users;
}
