package com.jeesite.modules.test.service;

import com.jeesite.common.service.CrudService;
import com.jeesite.modules.test.dao.PoiDataDao;
import com.jeesite.modules.test.dao.TestDataDao;
import com.jeesite.modules.test.entity.PoiVo;
import com.jeesite.modules.test.entity.TestData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
public class PoiDataService extends CrudService<PoiDataDao,PoiVo> {

}
