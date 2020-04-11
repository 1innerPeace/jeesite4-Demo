/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.test.dao;

import com.jeesite.common.dao.TreeDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.test.entity.XlTest;

/**
 * 测试树表DAO接口
 * @author xiongle
 * @version 2019-11-12
 */
@MyBatisDao
public interface XlTestDao extends TreeDao<XlTest> {
	
}