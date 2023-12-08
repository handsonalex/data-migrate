package com.elinter.data.migrate.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FailedRecordMapper extends BaseMapper<FailedRecord> {
}
