/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.test;

import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*import com.aliyun.api.gateway.demo.util.HttpUtils;*/
import com.aliyun.api.gateway.demo.util.HttpUtils;
import com.jeesite.modules.test.entity.PoiVo;
import com.jeesite.modules.test.service.PoiDataService;

import org.apache.http.HttpResponse;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.Region;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.jeesite.common.collect.ListUtils;
import com.jeesite.common.idgen.IdGen;
import com.jeesite.common.tests.BaseSpringContextTests;
import com.jeesite.modules.Application;
import com.jeesite.modules.test.entity.TestData;
import com.jeesite.modules.test.entity.TestDataChild;
import com.jeesite.modules.test.service.TestDataService;


/**
 * 多数据源并发测试<br>
 * 1、将 TestDataChildDao 的数据源设置为 ds2<br>
 * 2、将 TestDataChild 的表名设置为 test_data_child2<br>
 * 3、配置 ds2 数据源，并创建 test_data_child2 表
 * @author ThinkGem
 * @version 2019-6-26
 */
@ActiveProfiles("test")
@SpringBootTest(classes=Application.class)
public class MultiDataSourceTest extends BaseSpringContextTests {
	
	@Autowired
	private TestDataService testDataService;
	@Autowired
	private PoiDataService poiDataService;

	
	@Test
	public void testData() throws Exception{
		ExecutorService pool = Executors.newCachedThreadPool();
		CountDownLatch latch = new CountDownLatch(10);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try{
					Thread.sleep(IdGen.randomInt(1000, 3000));
					TestData testData = new TestData();
					testData.setTestDataChildList(ListUtils.newArrayList(
							new TestDataChild(), new TestDataChild(), new TestDataChild()));
					testDataService.save(testData);
					List<TestData> list = testDataService.findList(new TestData());
					System.out.println("size: " + list.size());
					list.forEach(e -> {
						System.out.println("get: " + testDataService.get(e));
					});
				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					latch.countDown();
				}
			}
		};
		for (int i = 0; i < latch.getCount(); i++) {
			pool.execute(runnable);
		}
		latch.await();
		pool.shutdown();
	}

/*

	@Test
	public void excel03() throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		HSSFSheet sheet = workbook.createSheet("test");

		HSSFRow row = sheet.createRow(0);

		HSSFCell cell = row.createCell(0);

		cell.setCellValue("xiongle");

		FileOutputStream out = new FileOutputStream("F:\\桌面\\xiongle.xls");

		workbook.write(out);


	}
	//POI导出数据到excel
	@Test
	public void excel03Demo() throws IOException {

		String month = "F:\\桌面\\xionglele.xls";
		//1.构造数据
		PoiVo poiVo = new PoiVo();
		List<PoiVo> list = poiDataService.findList(poiVo);
		//2.创建工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 单元格的格式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		// 边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		// 自动换行
		cellStyle.setWrapText(true);
		// 行列对齐方式
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
		//HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.cloneStyleFrom(cellStyle);
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		cellTitleStyle.setFont(font);
		//3.构造sheet
		HSSFSheet sheet = workbook.createSheet("xionglele");
		String[] titles={"编号","单行文本","多行文本","下拉框","下拉多选","单选框","复选框"};
		//构建行
		HSSFRow row = sheet.createRow(0);
		//列计数器
		AtomicInteger atomicInteger = new AtomicInteger();
		for (String title: titles) {
			HSSFCell cell = row.createCell(atomicInteger.getAndIncrement());
			cell.setCellValue(title);
		}
		//行计数器
		AtomicInteger atomicInteger2 = new AtomicInteger(1);
		Cell cell = null;
		for (PoiVo poi : list) {
			//构建行
			HSSFRow row1 = sheet.createRow(atomicInteger2.getAndIncrement());

			cell = row1.createCell(0);
			cell.setCellValue(poi.getId());

			cell = row1.createCell(1);
			cell.setCellValue(poi.getTestInput());

			cell = row1.createCell(2);
			cell.setCellValue(poi.getTestTextarea());

			cell = row1.createCell(3);
			cell.setCellValue(poi.getTestSelect());

			cell = row1.createCell(4);
			cell.setCellValue(poi.getTestSelectMultiple());

			cell = row1.createCell(5);
			cell.setCellValue(poi.getTestRadio());

			cell = row1.createCell(6);
			cell.setCellValue(poi.getTestCheckbox());

		}
*/
/*		String fileName = URLEncoder.encode(month+"人员信息.xlsx", "UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("content-disposition", "attachment;filename=" + new
				String(fileName.getBytes("ISO8859-1")));
		response.setHeader("filename", fileName);
*//*

		FileOutputStream out = new FileOutputStream("F:\\桌面\\xiongle.xls");
		workbook.write(out);

	}

	@Test
	public void excel04Demo() throws IOException {

		String month = "F:\\桌面\\xionglele.xls";
		//1.构造数据
		PoiVo poiVo = new PoiVo();
		List<PoiVo> list = poiDataService.findList(poiVo);
		//2.创建工作簿
		HSSFWorkbook workbook = new HSSFWorkbook();
		//3.构造sheet
		HSSFSheet sheet = workbook.createSheet("xionglele");
*/
/*
		// 单元格的格式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		// 边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		// 自动换行
		cellStyle.setWrapText(true);
		// 行列对齐方式
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
		//HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle cellTitleStyle = workbook.createCellStyle();
		cellTitleStyle.cloneStyleFrom(cellStyle);
		HSSFFont font = workbook.createFont();
		font.setBold(true);
		cellTitleStyle.setFont(font);
*//*

		HSSFCellStyle cellStyle = workbook.createCellStyle();

		cellStyle.setFillForegroundColor((short) 13);// 设置背景色
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中

		HSSFFont font2 = workbook.createFont();
		font2.setFontName("仿宋_GB2312");
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
		font2.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font2);//选择需要用到的字体格式

		sheet.setColumnWidth(0, 5000);
        //第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
		cellStyle.setWrapText(true);//设置自动换行
		Region region1 = new Region(0, (short) 0, 0, (short) 6);//参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号


		String[] titles = {"编号", "单行文本", "多行文本", "下拉框", "下拉多选", "单选框", "复选框"};
		//构建行
		HSSFRow row = sheet.createRow(0);
		//列计数器
		AtomicInteger atomicInteger = new AtomicInteger();
		for (String title : titles) {
			HSSFCell cell = row.createCell(atomicInteger.getAndIncrement());
			cell.setCellValue(title);
		}
		//行计数器
		AtomicInteger atomicInteger2 = new AtomicInteger(1);
		Cell cell = null;
		for (PoiVo poi : list) {
			//构建行
			HSSFRow row1 = sheet.createRow(atomicInteger2.getAndIncrement());

			cell = row1.createCell(0);
			cell.setCellValue(poi.getId());

			cell = row1.createCell(1);
			cell.setCellValue(poi.getTestInput());

			cell = row1.createCell(2);
			cell.setCellValue(poi.getTestTextarea());

			cell = row1.createCell(3);
			cell.setCellValue(poi.getTestSelect());

			cell = row1.createCell(4);
			cell.setCellValue(poi.getTestSelectMultiple());

			cell = row1.createCell(5);
			cell.setCellValue(poi.getTestRadio());

			cell = row1.createCell(6);
			cell.setCellValue(poi.getTestCheckbox());

		}
*/
/*		String fileName = URLEncoder.encode(month+"人员信息.xlsx", "UTF-8");
		response.setContentType("application/octet-stream");
		response.setHeader("content-disposition", "attachment;filename=" + new
				String(fileName.getBytes("ISO8859-1")));
		response.setHeader("filename", fileName);
*//*

		FileOutputStream out = new FileOutputStream("F:\\桌面\\xiongle.xls");
		workbook.write(out);

	}

	@Test
	public void test4(){
		int produceTaskSleepTime = 2;
		int productTaskMaxNumber =10;
		ThreadProduct threadProduct = new ThreadProduct(100);

		ThreadPoolExecutor threadPoolExecutor;
		threadPoolExecutor = new ThreadPoolExecutor(2, 50, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(3), new ThreadPoolExecutor.DiscardOldestPolicy());
		for (int i = 0; i <5 ; i++) {
			threadPoolExecutor.execute(threadProduct);
		}

	}


	*/
/**
	 * 短信测试
	 *//*

	@Test
	public void test5() {
		String host = "http://cowsms.market.alicloudapi.com";
		String path = "/intf/smsapi";
		String method = "GET";
		String appcode = "9398dce6f1ca496388313379550bdf7b";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", "18879267295");
		querys.put("msg", "尊敬的用户,您好!");
		querys.put("paras", "188792,2");
		querys.put("sign", "消息通");
		querys.put("tpid", "009");


		try {
			*/
/**
			 * 重要提示如下:
			 * HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 *//*

			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
			System.out.println(response.toString());
			//获取response的body
			//System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	*/
/**
	 * 短信测试
	 *//*

	@Test
	public void testSenMsg6(){
		//appCode 9398dce6f1ca496388313379550bdf7b
		String host = "http://tongzhi.market.alicloudapi.com";
		String path = "/sms/send/template/notify/70";
		String method = "POST";
		String appcode = "9398dce6f1ca496388313379550bdf7b";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		//测试请用默认短信模板,默认模板不可修改,如需自定义短信内容测试或正式发送,请联系旺旺或QQ1246073271进行申请
		querys.put("content", "【摩字】您的账户所申请模板已报备成功，快去发送吧。摩字感谢您的支持！");
		querys.put("mobile", "15270282957");
		Map<String, String> bodys = new HashMap<String, String>();


		try {
			*/
/**
			 * 重要提示如下:
			 * HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 *//*

			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
			System.out.println(response.toString());
			//获取response的body
			//System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

*/






}
