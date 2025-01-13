package cn.px.service.log.impl;

import cn.px.dbs.dao.log.LogOperateDao;
import cn.px.dbs.dao.log.mapper.LogOperateMapper;
import cn.px.dbs.entity.log.TbLogOperate;
import cn.px.power.module.base.model.OperateLogDto;
import cn.px.power.module.common.service.impl.BaseServiceImpl;
import cn.px.power.module.common.utils.BeanUtil;
import cn.px.service.log.OperateLogService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 品讯科技
 * @date 2024-08
 */
@Service
@AllArgsConstructor
public class OperateLogServiceImpl extends BaseServiceImpl<LogOperateMapper, TbLogOperate> implements OperateLogService {

    private LogOperateDao operateDao;

    @Override
    public void saveOperateLog(OperateLogDto operateLog) {
        TbLogOperate logOperate = BeanUtil.copyProperties(operateLog, TbLogOperate.class);
        operateDao.save(logOperate);
    }
}
