package com.elinter.data.migrate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elinter.data.migrate.dao.entity.EquipInverterDaily;
import com.elinter.data.migrate.dao.mapper.EquipInverterDailyMapper;
import com.elinter.data.migrate.service.EquipInverterDailyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class EquipInverterDailyServiceImpl extends ServiceImpl<EquipInverterDailyMapper, EquipInverterDaily> implements EquipInverterDailyService {


    @Resource
    EquipInverterDailyMapper equipInverterDailyMapper;
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(100,200,3, TimeUnit.MINUTES,new LinkedBlockingQueue<>(100));
    @Override
    public void migrate() {

    }
}
