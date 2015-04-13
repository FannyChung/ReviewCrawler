import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class fileTest {

	public static void main(String[] args) {
		WritableWorkbook book = null;
		for (int i = 0; i < 3; i++) {
			OutputStream os = null;
			try {
				os = new FileOutputStream("abc"+i+".xls", true);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				book = Workbook.createWorkbook(os);
			} catch (IOException e) {
				System.err.println("excel表打开失败");
				e.printStackTrace();
			}
			WritableSheet sheet = book.createSheet("eigj" + i, 0);// 设置表单名字和编号
			Label newLabel;
			newLabel = new Label(0, 0, "tea"+i);
			try {
				sheet.addCell(newLabel);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			try {
				book.write();
				book.close();
			} catch (IOException e) {
				System.err.println("excel表写入失败");
				e.printStackTrace();
			} catch (WriteException e) {
				System.err.println("excel表关闭失败");
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				System.err.println("没有创建表单");
			}
		}
	}
}
