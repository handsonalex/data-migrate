package com.elinter.data.migrate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;

public interface EquipInverterDailyService extends IService<EquipInverterDaily> {

    void migrate();
}
