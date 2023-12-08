package com.elinter.data.migrate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EquipInverterDailyMapper extends BaseMapper<EquipInverterDaily> {

    int migrateData();
}
