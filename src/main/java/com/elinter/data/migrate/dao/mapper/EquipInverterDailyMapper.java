package com.elinter.data.migrate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface EquipInverterDailyMapper extends BaseMapper<EquipInverterDaily> {

    @Insert("INSERT IGNORE INTO equip_inverter_daily (sn,pac,etoday,etotal,station_id,date,update_time) " +
            "( " +
            "SELECT a.sn,ANY_VALUE(a.pac) as pac,ANY_VALUE(a.etoday) as eToday," +
            "ANY_VALUE(a.etotal) as eTotal,ANY_VALUE(a.station_id) as stationId,a.`date`," +
            "ANY_VALUE ( a.update_time ) AS updateTime " +
            "FROM (SELECT DISTINCT sn,pac,etoday,etotal,station_id,date,(IF(update_time IS NULL, now(), update_time)) AS update_time " +
            "FROM equip_inverter_daily_old where id between #{start} and #{end} ORDER BY update_time desc ) a " +
            "GROUP BY a.sn,a.date ORDER BY update_time DESC) "
            )
    int migrateData(@Param("start") Long start,@Param("end") Long end);

    @Select("SELECT COUNT(id) FROM equip_inverter_daily")
    int getCount();


    @Select("SELECT MAX(id) FROM equip_inverter_daily_old")
    Long selectMaxId();
}
