package com.china.fortune.thirdTools;

import com.china.fortune.global.Log;
import com.china.fortune.json.JSONArray;
import com.china.fortune.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ExcelHelper {
	static public byte[] jsonToExcel(JSONArray list, String[] lsKeys) {
		if (list != null) {
			try {
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet();
				for (int i = 0; i < list.length(); i++) {
					XSSFRow row = sheet.createRow(i);
					JSONObject item = list.optJSONObject(i);
					for (int j = 0; j < lsKeys.length; j++) {
						XSSFCell cell = row.createCell(j);
						String sData = item.optString(lsKeys[j]);
						cell.setCellValue(sData);
					}
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				workbook.write(out);
				out.close();
				workbook.close();
				return out.toByteArray();
			} catch (Exception e) {
			}
		}
		return null;
	}

	static public ArrayList<ArrayList<String>> readExcel(XSSFSheet sheet, int iRowStart, int iRowEnd, int iColStart, int iColEnd) {
		ArrayList<ArrayList<String>> lslsData = new ArrayList<ArrayList<String>>();
		int iLastRow = sheet.getLastRowNum();
		if (iRowEnd < 0 || iRowEnd > iLastRow) {
			iRowEnd = iLastRow;
		}
		for (int iRow = iRowStart; iRow <= iRowEnd; iRow++) {
			ArrayList<String> lsData = new ArrayList<String>();
			XSSFRow row = sheet.getRow(iRow);
			for (int iCol = iColStart; iCol <= iColEnd; iCol++) {
				Log.logClass(iRow + ":" + iCol);
				XSSFCell cellAH = row.getCell(iCol);
				lsData.add(cellAH.toString());
			}
			lslsData.add(lsData);
		}
		return lslsData;
	}

	static public void writeRow(XSSFSheet sheet, int iRow, int startCol, ArrayList<Object> lsData) {
		XSSFRow row = sheet.getRow(iRow);
		for (int i = 0; i < lsData.size(); i++) {
			XSSFCell cell = row.getCell(startCol + i);
			Object sData = lsData.get(i);
			Class<?> cls = sData.getClass();
			if (cls == int.class || cls == Integer.class) {
				cell.setCellValue((Integer)sData);
			} else if (cls == double.class || cls == Double.class) {
				cell.setCellValue((Double)sData);
			} else if (cls == String.class) {
				cell.setCellValue((String)sData);
			}
		}
	}
}
