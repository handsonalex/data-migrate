package com.elinter.data.migrate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EquipInverterDailyMapper extends BaseMapper<EquipInverterDaily> {

    @Insert("INSERT IGNORE INTO equip_inverter_daily_new " +
            "( " +
            "SELECT a.id,a.sn,a.pac,a.etoday,a.etotal,a.station_id,a.`date`, " +
            "(IF(MAX(a.update_time) IS NULL, now(), MAX(a.update_time) )) AS update_time " +
            "FROM (SELECT * FROM equip_inverter_daily LIMIT #{start},#{end} ) a " +
            "GROUP BY a.sn,a.date ORDER BY a.update_time DESC) "
            )
    int migrateData(@Param("start") Integer start,@Param("end") Integer end);

    @Select("SELECT COUNT(id) FROM equip_inverter_daily")
    int getCount();
}
