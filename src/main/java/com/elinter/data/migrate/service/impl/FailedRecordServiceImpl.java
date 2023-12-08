package com.elinter.data.migrate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elinter.data.migrate.dao.entity.FailedRecord;
import com.elinter.data.migrate.dao.mapper.FailedRecordMapper;
import com.elinter.data.migrate.service.FailedRecordService;
import org.springframework.stereotype.Service;

@Service
public class FailedRecordServiceImpl extends ServiceImpl<FailedRecordMapper, FailedRecord> implements FailedRecordService {
}
