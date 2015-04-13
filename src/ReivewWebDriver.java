import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ReivewWebDriver {
	HtmlUnitDriver driver = new HtmlUnitDriver();
	private static Vector<Review> reviews = new Vector<Review>(); // 只需要不断增加，所以使用vector
	private WritableWorkbook book;

	/**
	 * 获取下一页评论
	 * 处理评论信息
	 * @param thisPage当前评论页
	 */
	public void nextPage(String thisPage) {
		String nextp = null;
		String reviewReg = "table[id=productReviews]>tbody>tr>td>div";// 评论信息的选择规则
		String textReg = ".reviewText"; // 评论文本的选择规则
		String levelReg = "span";// 评论等级
		String titleReg = "b"; // 评论标题
		String timeReg = "nobr"; // 评论时间
		String dateFormat = "yyyy年MM月dd日"; // 时间格式
		
		driver.get(thisPage);
		
		// 处理评论信息
		List<WebElement> ele1 = driver
				.findElementsByCssSelector(reviewReg);
		for (WebElement element : ele1) {// 获取评论元素，放入review中，每个元素有评论、作者、时间等
			Review review = new Review();
			WebElement textElements = element.findElement(By
					.cssSelector(textReg));// 获取评论文本
			String cString = textElements.getText();
			 System.out.println(cString);
			review.setText(cString);

			WebElement levelElements = element.findElement(By
					.cssSelector(levelReg));// 获取星级信息
			cString = levelElements.getText();
			int level = cString.charAt(2) - '0';
			// System.out.println(level);
			review.setLevel(level);

			WebElement titleEle = element.findElement(By.cssSelector(titleReg));
			cString = titleEle.getText(); // 标题
			// System.out.println(cString);
			review.setReTitle(cString);

			WebElement timeEle = element.findElement(By.cssSelector(timeReg));
			cString = timeEle.getText(); // 获取日期
			// System.out.println(cString);
			SimpleDateFormat s = new SimpleDateFormat(dateFormat);
			Date d = null;
			try {
				long t = s.parse(cString).getTime();
				d = new Date(t);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			review.setTime(d);

			reviews.add(review);
		}

		//获取下一页评论链接
		WebElement element = null;
		try {
			element= driver.findElementByPartialLinkText("下一页");
		} catch (Exception e) {
			System.err.println("当前："+thisPage+"没有下一页\n");
			return;
		}
		nextp = element.getAttribute("href");
		System.out.println(nextp);
		nextPage(nextp);
	}

	/**
	 * 从商品页面跳转到评论页面
	 * 
	 * @param productPage
	 * @return
	 */
	public String getReviewPage(String productPage) {
		driver.get(productPage);
		System.out.println("Page title is:" + driver.getTitle() + " url: "
				+ driver.getCurrentUrl());
		WebElement element = driver
				.findElementByCssSelector("div[id=revF]>div>a");
		element.click();
		String url = driver.getCurrentUrl();
		System.out.println("Page title is:" + driver.getTitle() + " url: "
				+ url);
		return url;
	}
	/**
	 * 打印所有的评论
	 * 
	 * @param reviews
	 * @param sheet
	 *            xls文件中的一个sheet表
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void printReviews(Vector<Review> reviews, WritableSheet sheet)
			throws RowsExceededException, WriteException {
		int col = 0;
		Label newLabel;
		// newLabel=new Label(0,0,"文本");
		for (Review review : reviews) {
			newLabel = new Label(0, col, review.getText());
			sheet.addCell(newLabel);
			newLabel = new Label(1, col, review.getLevel() + "");
			sheet.addCell(newLabel);
			newLabel = new Label(2, col, review.getReTitle());
			sheet.addCell(newLabel);
			newLabel = new Label(3, col, review.getTime().toString());
			sheet.addCell(newLabel);
			newLabel = new Label(4, col, review.getUserName());
			sheet.addCell(newLabel);
			col++;
			System.out.println("excel--------------------------------" + col);
		}
	}

	/**
	 * 打开excel文件
	 * 
	 * @param fileName
	 */
	public void openFile(String fileName) {
		File file = new File(fileName);
		try {
			book = Workbook.createWorkbook(file);
		} catch (IOException e) {
			System.err.println("excel表打开失败");
			e.printStackTrace();
		}
	}

	/**
	 * 关闭excel文件
	 */
	public void closeFile() {
		try {
			book.write();
			book.close();
		} catch (IOException e) {
			System.err.println("excel表写入失败");
			e.printStackTrace();
		} catch (WriteException e) {
			System.err.println("excel表关闭失败");
			e.printStackTrace();
		}catch (IndexOutOfBoundsException e) {
			System.err.println("没有创建表单");
		}
	}
	/**获取excel表对象
	 * @return
	 */
	public WritableWorkbook getBook() {
		return book;
	}
	public static void main(String[] args) {
		Search search = new Search();
		String s=search.search("http://www.amazon.cn/ref=nav_logo", "手机");//设置主页和搜索内容
		HashSet<ProductUrl> productsStrings=search.getProductPage(s);//获取搜索后得到的所有商品url
		ReivewWebDriver nDriver = new ReivewWebDriver();
		
		WritableSheet sheet = null;
		nDriver.openFile("t.xls");
		int i=0;
		int productNum=5;//需要的商品数目
		for (ProductUrl productUrl : productsStrings) {
			if (i==productNum) {
				break;
			}
			nDriver.nextPage(nDriver.getReviewPage(productUrl.getString()));
			sheet = nDriver.getBook().createSheet(
					productUrl.getString(), i);// 设置表单名字和编号
			try {
				nDriver.printReviews(reviews, sheet);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			reviews.clear();
			i++;
		}

		nDriver.closeFile();
		nDriver.driver.close();
	}
}
