package com.px.tool.domain.mucdich.sudung;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "muc_dic_su_dung")
public class MucDichSuDung {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long mdId;

    @Column
    private String ten;

    @Column
    private boolean delete = false;
}
