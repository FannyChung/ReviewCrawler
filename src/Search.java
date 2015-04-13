import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


/**
 * 模拟搜索
 * @author Fanny
 *
 */
public class Search {

	/**
	 * 进行搜索
	 * @param homepageString 主页的url
	 * @param searchString 要搜索的内容
	 * @return 搜索后的url
	 */
	public String search(String homepageString, String searchString) {
		String inputReg="input[type=text]";						//输入框元素的选择规则
		
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(homepageString);
		System.out.println("Page title is:" + driver.getTitle()+" url: "+driver.getCurrentUrl());
		// 找到文本框
		WebElement element = driver.findElement(By.cssSelector(inputReg));
		// 输入搜索关键字
		element.sendKeys(searchString);
		//提交输入
		element.submit();
		//获取输入后的url
		String curUrl=driver.getCurrentUrl();
		System.out.println("Page title is:" + driver.getTitle()+" url: "+curUrl);
		driver.close();
		return curUrl;
	}
	
	/**
	 * 获取网页中所有指向商品的超链接
	 * @param searchUrl搜索后的网页
	 * @return 商品url的集合
	 */
	public HashSet<ProductUrl> getProductPage(String searchUrl) {
		String productUrlReg="http://www.amazon.cn/.*/dp/.*";			//商品url对应的格式
		
		HashSet<ProductUrl> products=new HashSet<ProductUrl>();
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(searchUrl);
		List<WebElement> link=driver.findElements(By.cssSelector("[href]"));//获取带有超链接的元素
		for (WebElement webElement : link) {
			String href=webElement.getAttribute("href");					//获取超链接对应的String
			if(href.matches(productUrlReg)&&products.add(new ProductUrl(href))){//获取商品url，加入集合中，同一个商品的多个url不重复添加
				System.out.println(webElement.getText()+'\t'+href);
			}
		}
		System.out.println("get product url over=================================================================");
		System.out.println(products.size());
		driver.close();
		return products;
	}
}