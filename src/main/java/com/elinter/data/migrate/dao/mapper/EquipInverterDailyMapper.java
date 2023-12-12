package com.elinter.data.migrate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EquipInverterDailyMapper extends BaseMapper<EquipInverterDaily> {

    @Insert("INSERT IGNORE INTO equip_inverter_daily_new " +
            "( " +
            "SELECT ANY_VALUE(a.id) as id,a.sn,ANY_VALUE(a.pac) as pac,ANY_VALUE(a.etoday) as etoday," +
            "ANY_VALUE(a.etotal) as etotal,ANY_VALUE(a.station_id) as station_id,a.`date`," +
            "(IF(MAX(a.update_time) IS NULL, now(), MAX(a.update_time) )) as update_time " +
            "FROM (SELECT * FROM equip_inverter_daily where id between #{start} and #{end} ) a " +
            "GROUP BY a.sn,a.date ORDER BY update_time DESC) "
            )
    int migrateData(@Param("start") Long start,@Param("end") Long end);

    @Select("SELECT COUNT(id) FROM equip_inverter_daily")
    int getCount();

    @Select("SELECT COUNT(*)  " +
            "FROM ( " +
            "    SELECT * " +
            "    FROM ( " +
            "        SELECT * " +
            "        FROM equip_inverter_daily " +
            "        LIMIT #{start},#{end} " +
            "    ) a " +
            "    GROUP BY a.sn, a.date " +
            ") b;")
    int getCountByGroup(@Param("start") Integer start,@Param("end") Integer end);

    @Select("SELECT MAX(id) FROM equip_inverter_daily")
    Long selectMaxId();
}
