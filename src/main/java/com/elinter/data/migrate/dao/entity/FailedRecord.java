package com.elinter.data.migrate.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author :何嘉骏
 * Description: 插入失败记录
 * Date: 16:10 2023/12/8
 */
@Data
public class FailedRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long start;

    private Long end;

    private Date operateTime;
}
