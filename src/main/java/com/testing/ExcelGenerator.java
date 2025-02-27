package com.testing;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelGenerator {
	
	public static String generateExcelFile(List<TestResult> testResults) {
		String filePath = "TestResults.xlsx";
		
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Test Case Results");
			
			Row headerRow = sheet.createRow(0);
            String[] headers = {"Test Case Name", "Field Type", "Field Name" ,"Input", "Expected Output", "Actual Output", "Result"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }
            
            int rowNum = 1;
            for (TestResult result : testResults) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(result.getTestCaseName());
                row.createCell(1).setCellValue(result.getFieldType());
                row.createCell(3).setCellValue(result.getGivenInput());
                row.createCell(4).setCellValue(result.getExpectedOutput());
                row.createCell(5).setCellValue(result.getActualOutput());
                row.createCell(6).setCellValue(result.getResult());
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (FileOutputStream file = new FileOutputStream(filePath)) {
                workbook.write(file);
            }
            
            return filePath; 
		}
		catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	
	private static CellStyle getHeaderCellStyle(Workbook workBook) {
		CellStyle style = workBook.createCellStyle();
		
		Font font = workBook.createFont();
		font.setBold(true);
		
		style.setFont(font);
		
		return style;
	}
}
