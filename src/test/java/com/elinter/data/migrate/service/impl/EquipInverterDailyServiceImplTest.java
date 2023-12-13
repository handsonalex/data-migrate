package com.elinter.data.migrate.service.impl;

import com.elinter.data.migrate.service.EquipInverterDailyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EquipInverterDailyServiceImplTest {


    @Resource
    EquipInverterDailyService service;
    @Test
    void migrate() {
        long start = 10000;
        long end = 20000;
        service.migrate(start ,end, new Date());
    }

    @Test
    void selectMaxId() {
    }
}
