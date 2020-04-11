/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.test.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jeesite.common.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.jeesite.common.config.Global;
import com.jeesite.common.collect.ListUtils;
import com.jeesite.common.collect.MapUtils;
import com.jeesite.common.lang.StringUtils;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.modules.sys.utils.UserUtils;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.test.entity.XlTest;
import com.jeesite.modules.test.service.XlTestService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 测试树表Controller
 * @author xiongle
 * @version 2019-11-12
 */
@Controller
@RequestMapping(value = "${adminPath}/test/xlTest")
public class XlTestController extends BaseController {

	@Autowired
	private XlTestService xlTestService;
	
	/**
	 * 获取数据
	 */
	@ModelAttribute
	public XlTest get(String treeCode, boolean isNewRecord) {
		return xlTestService.get(treeCode, isNewRecord);
	}
	
	/**
	 * 查询列表
	 */
	@RequiresPermissions("test:xlTest:view")
	@RequestMapping(value = {"list", ""})
	public String list(XlTest xlTest, Model model) {
		model.addAttribute("xlTest", xlTest);
		return "modules/test/xlTestList";
	}
	
	/**
	 * 查询列表数据
	 */
	@RequiresPermissions("test:xlTest:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public List<XlTest> listData(XlTest xlTest) {
		if (StringUtils.isBlank(xlTest.getParentCode())) {
			xlTest.setParentCode(XlTest.ROOT_CODE);
		}
		if (StringUtils.isNotBlank(xlTest.getTreeName())){
			xlTest.setParentCode(null);
		}
		if (StringUtils.isNotBlank(xlTest.getRemarks())){
			xlTest.setParentCode(null);
		}
		List<XlTest> list = xlTestService.findList(xlTest);
		return list;
	}

	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("test:xlTest:view")
	@RequestMapping(value = "form")
	public String form(XlTest xlTest, Model model) {
		// 创建并初始化下一个节点信息
		xlTest = createNextNode(xlTest);
		model.addAttribute("xlTest", xlTest);
		return "modules/test/xlTestForm";
	}
	
	/**
	 * 创建并初始化下一个节点信息，如：排序号、默认值
	 */
	@RequiresPermissions("test:xlTest:edit")
	@RequestMapping(value = "createNextNode")
	@ResponseBody
	public XlTest createNextNode(XlTest xlTest) {
		if (StringUtils.isNotBlank(xlTest.getParentCode())){
			xlTest.setParent(xlTestService.get(xlTest.getParentCode()));
		}
		if (xlTest.getIsNewRecord()) {
			XlTest where = new XlTest();
			where.setParentCode(xlTest.getParentCode());
			XlTest last = xlTestService.getLastByParentCode(where);
			// 获取到下级最后一个节点
			if (last != null){
				xlTest.setTreeSort(last.getTreeSort() + 30);
				xlTest.setTreeCode(IdGen.nextCode(last.getTreeCode()));
			}else if (xlTest.getParent() != null){
				xlTest.setTreeCode(xlTest.getParent().getTreeCode() + "001");
			}
		}
		// 以下设置表单默认数据
		if (xlTest.getTreeSort() == null){
			xlTest.setTreeSort(XlTest.DEFAULT_TREE_SORT);
		}
		return xlTest;
	}

	/**
	 * 保存数据
	 */
	@RequiresPermissions("test:xlTest:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated XlTest xlTest) {
		xlTestService.save(xlTest);
		return renderResult(Global.TRUE, text("保存数据成功！"));
	}
	
	/**
	 * 停用数据
	 */
	@RequiresPermissions("test:xlTest:edit")
	@RequestMapping(value = "disable")
	@ResponseBody
	public String disable(XlTest xlTest) {
		XlTest where = new XlTest();
		where.setStatus(XlTest.STATUS_NORMAL);
		where.setParentCodes("," + xlTest.getId() + ",");
		long count = xlTestService.findCount(where);
		if (count > 0) {
			return renderResult(Global.FALSE, text("该数据包含未停用的子数据！"));
		}
		xlTest.setStatus(XlTest.STATUS_DISABLE);
		xlTestService.updateStatus(xlTest);
		return renderResult(Global.TRUE, text("停用数据成功"));
	}
	
	/**
	 * 启用数据
	 */
	@RequiresPermissions("test:xlTest:edit")
	@RequestMapping(value = "enable")
	@ResponseBody
	public String enable(XlTest xlTest) {
		xlTest.setStatus(XlTest.STATUS_NORMAL);
		xlTestService.updateStatus(xlTest);
		return renderResult(Global.TRUE, text("启用数据成功"));
	}
	
	/**
	 * 删除数据
	 */
	@RequiresPermissions("test:xlTest:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(XlTest xlTest) {
		xlTestService.delete(xlTest);
		return renderResult(Global.TRUE, text("删除数据成功！"));
	}
	
	/**
	 * 获取树结构数据
	 * @param excludeCode 排除的Code
	 * @param isShowCode 是否显示编码（true or 1：显示在左侧；2：显示在右侧；false or null：不显示）
	 * @return
	 */
	@RequiresPermissions("test:xlTest:view")
	@RequestMapping(value = "treeData")
	@ResponseBody
	public List<Map<String, Object>> treeData(String excludeCode, String isShowCode) {
		List<Map<String, Object>> mapList = ListUtils.newArrayList();
		List<XlTest> list = xlTestService.findList(new XlTest());
		for (int i=0; i<list.size(); i++){
			XlTest e = list.get(i);
			// 过滤非正常的数据
			if (!XlTest.STATUS_NORMAL.equals(e.getStatus())){
				continue;
			}
			// 过滤被排除的编码（包括所有子级）
			if (StringUtils.isNotBlank(excludeCode)){
				if (e.getId().equals(excludeCode)){
					continue;
				}
				if (e.getParentCodes().contains("," + excludeCode + ",")){
					continue;
				}
			}
			Map<String, Object> map = MapUtils.newHashMap();
			map.put("id", e.getId());
			map.put("pId", e.getParentCode());
			map.put("name", StringUtils.getTreeNodeName(isShowCode, e.getTreeCode(), e.getTreeName()));
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 修复表结构相关数据
	 */
	@RequiresPermissions("test:xlTest:edit")
	@RequestMapping(value = "fixTreeData")
	@ResponseBody
	public String fixTreeData(XlTest xlTest){
		if (!UserUtils.getUser().isAdmin()){
			return renderResult(Global.FALSE, "操作失败，只有管理员才能进行修复！");
		}
		xlTestService.fixTreeData();
		return renderResult(Global.TRUE, "数据修复成功");
	}


	/**
	 * 上传文件测试
	 * @param file
	 * @return Object
	 * @throws IOException
	 */
	@RequestMapping(value ="/uploadFileTest",method = RequestMethod.POST)
	@ResponseBody
	public Object uploadFileTest(@RequestParam("uploadFile")MultipartFile file) throws IOException {
		HashMap<Object, Object> result = new HashMap<>();
        if(!file.isEmpty()){
        	//取文件后缀
			String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
		    //取当前时间戳作为文件名
			long time = System.currentTimeMillis();
			String fileName = time + type;
			//时间戳转换成具体时间
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formatTime = format.format(new Date(time * 1000L));
			System.out.println("当前时间戳："+time);
			System.out.println("当前时间："+formatTime);
			//设置存放位置
			String path="F:\\javaSpace\\workSpace\\jeesite4-Demo\\web\\src\\main\\java\\com\\jeesite\\modules\\test\\web\\"+fileName;
			//创建文件
			File destFile = new File(path);
			try{
				//复制临时文件到指定目录下
				FileUtils.copyInputStreamToFile(file.getInputStream(),destFile);
			}catch (IOException e){
				e.printStackTrace();
			}
			result.put("message","upload success!");
		}else{
			result.put("message","upload failed!");
		}
		return result;
	}

	
}