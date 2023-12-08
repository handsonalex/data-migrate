package com.elinter.data.migrate.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class EquipInverterDaily {

    @TableId
    private Long id;

    private String sn;

    private Integer pac;

    @TableField("etoday")
    private BigDecimal eToday;

    @TableField("etotal")
    private BigDecimal eTotal;

    private Long stationId;

    private String date;

    private Date updateTime;

}
