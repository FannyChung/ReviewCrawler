import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class ReivewWebDriver {
	HtmlUnitDriver driver = new HtmlUnitDriver();
	private static Vector<Review> reviews = new Vector<Review>(); // ֻ��Ҫ�������ӣ�����ʹ��vector

	/**
	 * ��ȡ��һҳ����
	 * ����������Ϣ
	 * @param thisPage��ǰ����ҳ
	 */
	public void nextPage(String thisPage) {
		String nextp = null;
		String reviewReg = "table[id=productReviews]>tbody>tr>td>div";// ������Ϣ��ѡ�����
		String textReg = ".reviewText"; // �����ı���ѡ�����
		String levelReg = "span";// ���۵ȼ�
		String titleReg = "b"; // ���۱���
		String timeReg = "nobr"; // ����ʱ��
		String dateFormat = "yyyy��MM��dd��"; // ʱ���ʽ
		
		driver.get(thisPage);
		
		// ����������Ϣ
		List<WebElement> ele1 = driver
				.findElementsByCssSelector(reviewReg);
		for (WebElement element : ele1) {// ��ȡ����Ԫ�أ�����review�У�ÿ��Ԫ�������ۡ����ߡ�ʱ���
			Review review = new Review();
			WebElement textElements = element.findElement(By
					.cssSelector(textReg));// ��ȡ�����ı�
			String cString = textElements.getText();
			 System.out.println(cString);
			review.setText(cString);

			WebElement levelElements = element.findElement(By
					.cssSelector(levelReg));// ��ȡ�Ǽ���Ϣ
			cString = levelElements.getText();
			int level = cString.charAt(2) - '0';
			// System.out.println(level);
			review.setLevel(level);

			WebElement titleEle = element.findElement(By.cssSelector(titleReg));
			cString = titleEle.getText(); // ����
			// System.out.println(cString);
			review.setReTitle(cString);

			WebElement timeEle = element.findElement(By.cssSelector(timeReg));
			cString = timeEle.getText(); // ��ȡ����
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

		//��ȡ��һҳ��������
		WebElement element = null;
		try {
			element= driver.findElementByPartialLinkText("��һҳ");
		} catch (Exception e) {
			System.err.println("��ǰ��"+thisPage+"û����һҳ\n");
			return;
		}
		nextp = element.getAttribute("href");
		System.out.println(nextp);
		nextPage(nextp);
	}

	/**
	 * ����Ʒҳ����ת������ҳ��
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

	public static void main(String[] args) {
		Search search = new Search();
		String s=search.search("http://www.amazon.cn/ref=nav_logo", "�ֻ�");//������ҳ����������
		HashSet<ProductUrl> productsStrings=search.getProductPage(s);//��ȡ������õ���������Ʒurl
		ReivewWebDriver nDriver = new ReivewWebDriver();
		
		ProductReview productReview = new ProductReview("��productdriver");
		WritableSheet sheet = null;
		productReview.openFile("t.xls");
		int i=0;
		int productNum=5;//��Ҫ����Ʒ��Ŀ
		for (ProductUrl productUrl : productsStrings) {
			if (i==productNum) {
				break;
			}
			nDriver.nextPage(nDriver.getReviewPage(productUrl.getString()));
			sheet = productReview.getBook().createSheet(
					productUrl.getString(), i);// ���ñ����ֺͱ��
			try {
				productReview.printReviews(reviews, sheet);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			reviews.clear();
			i++;
		}

		productReview.closeFile();
		nDriver.driver.close();
	}
}
