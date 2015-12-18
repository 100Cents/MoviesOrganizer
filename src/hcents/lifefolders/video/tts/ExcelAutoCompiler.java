package hcents.lifefolders.video.tts;

import java.io.*;
import java.util.*;

import org.apache.poi.hssf.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonWriter;

public class ExcelAutoCompiler {

	public static void main(String[] args) {
		
		ArrayList<Movie_v1> moviesArrayList = new ArrayList<Movie_v1>();
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream("Z:\\+Video\\+Movies\\1895-2015.xls"));
			
			HSSFSheet sheet = wb.getSheetAt(0);
			
			int rows = sheet.getPhysicalNumberOfRows();
			
			System.out.println("Numero di righe: " + rows);
			
			for (int r = 1; r < rows; r++) {
				HSSFRow row = sheet.getRow(r);
				if (row == null) {
					System.out.println("Row null: " + r);
					continue;
				}
				
				Movie_v1 movie = new Movie_v1();
				movie.setYear((int)row.getCell(15).getNumericCellValue());
				
				HSSFCell otlc_cell = row.getCell(4);
				if (otlc_cell != null) {
					String otlc = otlc_cell.getStringCellValue();
					otlc = otlc.trim();
					if (!otlc.equals("")) {
						movie.setOtlc(otlc.toLowerCase());
					}
				}
				
				HSSFCell nt_title_cell = row.getCell(5);
				if (nt_title_cell != null) {
					String nt_title = nt_title_cell.getStringCellValue();
					nt_title = nt_title.trim();
					if (!nt_title.equals("")) {
						movie.addTitle(nt_title, "nt");
					}
				}
				
				HSSFCell it_title_cell = row.getCell(6);
				if (it_title_cell != null) {
					String it_title = it_title_cell.getStringCellValue();
					it_title = it_title.trim();
					if (!it_title.equals("")) {
						movie.addTitle(it_title, "it");
					}
				}
				
				HSSFCell en_title_cell = row.getCell(7);
				if (en_title_cell != null) {
					String en_title = en_title_cell.getStringCellValue();
					en_title = en_title.trim();
					if (!en_title.equals("")) {
						movie.addTitle(en_title, "en");
					}
				}
				
				HSSFCell fr_title_cell = row.getCell(8);
				if (fr_title_cell != null) {
					String fr_title = fr_title_cell.getStringCellValue();
					fr_title = fr_title.trim();
					if (!fr_title.equals("")) {
						movie.addTitle(fr_title, "fr");
					}
				}
				
				HSSFCell es_title_cell = row.getCell(9);
				if (es_title_cell != null) {
					String es_title = es_title_cell.getStringCellValue();
					es_title = es_title.trim();
					if (!es_title.equals("")) {
						movie.addTitle(es_title, "es");
					}
				}
				
				HSSFCell pt_title_cell = row.getCell(10);
				if (pt_title_cell != null) {
					String pt_title = pt_title_cell.getStringCellValue();
					pt_title = pt_title.trim();
					if (!pt_title.equals("")) {
						movie.addTitle(pt_title, "pt");
					}
				}
				
				HSSFCell ru_title_cell = row.getCell(11);
				if (ru_title_cell != null) {
					String ru_title = ru_title_cell.getStringCellValue();
					ru_title = ru_title.trim();
					if (!ru_title.equals("")) {
						movie.addTitle(ru_title, "ru");
					}
				}
				
				HSSFCell xx_title_cell = row.getCell(12);
				if (xx_title_cell != null) {
					String xx_title = xx_title_cell.getStringCellValue();
					xx_title = xx_title.trim();
					if (!xx_title.equals("")) {
						movie.addTitle(xx_title, "xx");
					}
				}
				
				HSSFCell a1_title_cell = row.getCell(13);
				if (a1_title_cell != null) {
					String a1_title = a1_title_cell.getStringCellValue();
					a1_title = a1_title.trim();
					if (!a1_title.equals("")) {
						movie.addTitle(a1_title, "a1");
					}
				}
				
				HSSFCell imdb_code_cell = row.getCell(3);
				if (imdb_code_cell != null) {
					String imdb_code = imdb_code_cell.getStringCellValue();
					imdb_code = imdb_code.trim();
					if (!imdb_code.equals("")) {
						movie.setImdbCode(imdb_code);
					}
				}
				
				HSSFCell status_info_cell = row.getCell(0);
				if (status_info_cell != null) {
					String status_info = status_info_cell.getStringCellValue();
					status_info = status_info.trim();
					if (status_info.equals("G")) {
						movie.setInfoStatus(Movie_v1.INFO_STATUS_GOOGLED);
					}
				}
				
				HSSFCell data_info_cell = row.getCell(2);
				if (data_info_cell != null) {
					String data_info = data_info_cell.getStringCellValue();
					data_info = data_info.trim();
					if (data_info.equals("D")) {
						movie.setRealDataStatus(Movie_v1.REAL_DATA_STATUS_DOWNLOADED);
					}
					if (data_info.equals("U")) {
						movie.setRealDataStatus(Movie_v1.REAL_DATA_STATUS_REQUESTED);
					}
				}
				
				
				moviesArrayList.add(movie);
				//System.out.println(movie);
				//String jsonString = movie.getJSON().toString();
				//System.out.println(JsonWriter.formatJson(jsonString));
			}
			
			System.out.println(moviesArrayList.size());
			
			JSONArray json_array = new JSONArray();
			
			for (Movie_v1 movie : moviesArrayList) {
				json_array.put(movie.getJSON());
			}
			
			JSONObject jo = new JSONObject();
			jo.put("movies_list", json_array);
			
			String jsonString = jo.toString();
			System.out.println(JsonWriter.formatJson(jsonString));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
