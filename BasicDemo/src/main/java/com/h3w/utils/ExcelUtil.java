package com.h3w.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 * @author <a href="mailto:xietianjiao@h3w.com.cn">xietj</a>
 * @version 1.0 2017年8月9日 下午1:36:23
 */
public class ExcelUtil {
	private HSSFWorkbook wb=null;
	private HSSFSheet sheet=null;
	public ExcelUtil(HSSFWorkbook wb, HSSFSheet sheet){
		this.wb = wb;
		this.sheet = sheet;
	}
	/**
	 * 生成表格标题
	 * @param wb
	 * @param sheet
	 * @param headString
	 * @param colSum
	 */
	public void createNormalHead( String headString, int colSum) {

		HSSFRow row = sheet.createRow(0);

		// 设置第一行
		HSSFCell cell = row.createCell(0);
		row.setHeightInPoints(50);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(headString));

		// 指定合并区域
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colSum));

		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("方正小标宋简体");
		font.setFontHeightInPoints((short) 18);
		cellStyle.setFont(font);

		cell.setCellStyle(cellStyle);
	}
	public void createRangeCellSimple(String content, int r0,int r1,int c0,int c1,HSSFCellStyle cellStyle,boolean isBoder) {
		HSSFRow row = sheet.getRow(r0);
		if(row == null){
			row = sheet.createRow(r0);

			row.setHeightInPoints(20);
		}

		// 设置第一行
		HSSFCell cell = row.createCell(c0);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));

		// 指定合并区域
		CellRangeAddress cra = new CellRangeAddress(r0, r1, c0, c1);
		sheet.addMergedRegion(cra);

		if(isBoder){
			RegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
			RegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
			RegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
			RegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
		}

		
		//自适应宽
		sheet.autoSizeColumn(c0, true);
		if(cellStyle!=null)
			cell.setCellStyle(cellStyle);
	}

	public void createRangeCell(String content, int r0,int r1,int c0,int c1) {
		HSSFRow row = sheet.getRow(r0);
		if(row == null){
			row = sheet.createRow(r0);

			row.setHeightInPoints(20);
		}

		// 设置第一行
		HSSFCell cell = row.createCell(c0);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));

		// 指定合并区域
		CellRangeAddress cra = new CellRangeAddress(r0, r1, c0, c1);
		sheet.addMergedRegion(cra);
		RegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
		RegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
		RegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);
		RegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,cra,sheet,wb);

		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		//自适应宽
		sheet.autoSizeColumn(c0, true);

		cell.setCellStyle(cellStyle);
	}
	
	public void createRangeCellNoboder(String content, int r0,int r1,int c0,int c1) {
		HSSFRow row = sheet.getRow(r0);
		if(row == null){
			row = sheet.createRow(r0);

			row.setHeightInPoints(20);
		}

		// 设置第一行
		HSSFCell cell = row.createCell(c0);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));

		// 指定合并区域
		CellRangeAddress cra = new CellRangeAddress(r0, r1, c0, c1);
		sheet.addMergedRegion(cra);

		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		//自适应宽
		sheet.autoSizeColumn(c0, true);

		cell.setCellStyle(cellStyle);
	}
	public void createNormalCell(String content, int r,int c) {
		HSSFRow row = sheet.getRow(r);
		if(row == null){
			row = sheet.createRow(r);

			row.setHeightInPoints(20);
		}

		// 设置行
		HSSFCell cell = row.createCell(c);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));


		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框

		cell.setCellStyle(cellStyle);
		//自适应宽
		sheet.autoSizeColumn(c, true);
	}
	

	public void createNormalCellNoboder(String content, int r,int c) {
		HSSFRow row = sheet.getRow(r);
		if(row == null){
			row = sheet.createRow(r);

			row.setHeightInPoints(20);
		}

		// 设置行
		HSSFCell cell = row.createCell(c);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));


		HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);

		cell.setCellStyle(cellStyle);
		//自适应宽
		sheet.autoSizeColumn(c, true);
	}

	public void createSimpleCell(String content, int r,int c,HSSFCellStyle cellStyle){
		HSSFRow row = sheet.getRow(r);
		if(row == null){
			row = sheet.createRow(r);

			row.setHeightInPoints(20);
		}

		// 设置行
		HSSFCell cell = row.createCell(c);

		// 定义单元格为字符串类型
		cell.setCellType(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(new HSSFRichTextString(content));

		if(cellStyle!=null)cell.setCellStyle(cellStyle);
			
		/*HSSFCellStyle cellStyle = wb.createCellStyle();

		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐
		cellStyle.setWrapText(true);// 指定单元格自动换行

		// 设置单元格字体
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		cellStyle.setFont(font);

		cell.setCellStyle(cellStyle);*/
		//自适应宽
		sheet.autoSizeColumn(c, true);
	}

	public static void main(String arg[]){

	}
}
