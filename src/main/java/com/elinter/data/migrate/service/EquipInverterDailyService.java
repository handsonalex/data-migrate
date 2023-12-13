package com.elinter.data.migrate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import com.elinter.data.migrate.dao.entity.FailedRecord;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

public interface EquipInverterDailyService extends IService<EquipInverterDaily> {

    CompletableFuture<Map<Boolean,FailedRecord>> migrate(Long start, Long end, Date date);

    int simpleMigrate(Long start, Long end, Date date);

    Long selectMaxId();


}
