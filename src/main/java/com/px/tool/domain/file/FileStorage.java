package com.px.tool.domain.file;

import com.px.tool.domain.RequestType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file")
@Getter
@Setter
@ToString
public class FileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long fileId;

    @Column
    private String fileName;

    @Column
    private String fileUUId;

    @Column
    private Long requestId;

    @Column
    @Enumerated(EnumType.STRING)
    private RequestType requestType;
}