package com.elinter.data.migrate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

public interface EquipInverterDailyService extends IService<EquipInverterDaily> {

    CompletableFuture<Integer> migrate(Long start, Long end,Date date);

    Long selectMaxId();
}
