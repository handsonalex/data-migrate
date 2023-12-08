package com.elinter.data.migrate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

public interface EquipInverterDailyService extends IService<EquipInverterDaily> {

    CompletableFuture<Integer> migrate(Integer start, Integer size, Date date);
}
