/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.test.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.TreeService;
import com.jeesite.modules.test.entity.XlTest;
import com.jeesite.modules.test.dao.XlTestDao;
import com.jeesite.modules.file.utils.FileUploadUtils;

/**
 * 测试树表Service
 * @author xiongle
 * @version 2019-11-12
 */
@Service
@Transactional(readOnly=true)
public class XlTestService extends TreeService<XlTestDao, XlTest> {
	
	/**
	 * 获取单条数据
	 * @param xlTest
	 * @return
	 */
	@Override
	public XlTest get(XlTest xlTest) {
		return super.get(xlTest);
	}
	
	/**
	 * 查询列表数据
	 * @param xlTest
	 * @return
	 */
	@Override
	public List<XlTest> findList(XlTest xlTest) {
		return super.findList(xlTest);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param xlTest
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(XlTest xlTest) {
		super.save(xlTest);
		// 保存上传图片
		FileUploadUtils.saveFileUpload(xlTest.getId(), "xlTest_image");
		// 保存上传附件
		FileUploadUtils.saveFileUpload(xlTest.getId(), "xlTest_file");
	}
	
	/**
	 * 更新状态
	 * @param xlTest
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(XlTest xlTest) {
		super.updateStatus(xlTest);
	}
	
	/**
	 * 删除数据
	 * @param xlTest
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(XlTest xlTest) {
		super.delete(xlTest);
	}
	
}