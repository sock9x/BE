package com.px.tool.controller;

import com.px.tool.domain.mucdich.sudung.MucDichSuDung;
import com.px.tool.domain.mucdich.sudung.repository.MucDichSuDungRepository;
import com.px.tool.infrastructure.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mdsd")
public class MucDichSuDungController extends BaseController {

    @Autowired
    private MucDichSuDungRepository mucDichSuDungRepository;

    @GetMapping
    public List<MucDichSuDung> findList() {
        return mucDichSuDungRepository.findAll();
    }

    @PostMapping
    public MucDichSuDung save(@RequestBody MucDichSuDung mucDichSuDung) {
        return mucDichSuDungRepository.save(mucDichSuDung);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mucDichSuDungRepository.delete(id);
    }
}
