package com.px.tool;

import com.px.tool.domain.kiemhong.repository.KiemHongRepository;
import com.px.tool.domain.user.PhongBan;
import com.px.tool.domain.user.repository.PhongBanRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.stream.IntStream;

public class PhongBanRepositoryTest extends PxApplicationTests {
    @Autowired
    private PhongBanRepository phongBanRepository;

    @Autowired
    private KiemHongRepository kiemHongRepository;

    @Test
    public void create() {
        IntStream.range(0, 28)
                .forEach(el -> {
                    PhongBan phongBan = new PhongBan();
                    phongBan.setGroup(1);
                    phongBan.setName(UUID.randomUUID().toString() + el);
                    this.phongBanRepository.save(phongBan);
                });
    }

}
