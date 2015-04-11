import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class SearchDemo2 {

	public String search(String homepageString, String searchString) {
		String inputReg="input[type=text]";
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(homepageString);
		System.out.println("Page title is:" + driver.getTitle()+" url: "+driver.getCurrentUrl());
		// 找到文本框
		WebElement element = driver.findElement(By.cssSelector(inputReg));
		// 输入搜索关键字
		element.sendKeys(searchString);
		element.submit();
		String curUrl=driver.getCurrentUrl();
		System.out.println("Page title is:" + driver.getTitle()+" url: "+curUrl);
		driver.close();
		return curUrl;
	}
	
	public HashSet<ProductUrl> getProductPage(String searchUrl) {
		HashSet<ProductUrl> products=new HashSet<ProductUrl>();
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(searchUrl);
		List<WebElement> link=driver.findElements(By.cssSelector("[href]"));
		for (WebElement webElement : link) {
			String href=webElement.getAttribute("href");
			if(href.matches("http://www.amazon.cn/.*/dp/.*")&&products.add(new ProductUrl(href))){
				System.out.println(webElement.getText()+'\t'+href);
			}
		}
		System.out.println("over=================================================================");
		System.out.println(products.size());
		driver.close();
		return products;
	}

	public static void main(String[] args) {
		SearchDemo2 search = new SearchDemo2();
		String s=search.search("http://www.amazon.cn/ref=nav_logo", "手机");
		search.getProductPage(s);
	}
}