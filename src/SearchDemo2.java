import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SearchDemo2 {

	public void search(String homepageString, String searchString) {
		String inputReg="input[type=text]";
		
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(homepageString);
		System.out.println("Page title is:" + driver.getTitle()+" url: "+driver.getCurrentUrl());
		// 找到文本框
		WebElement element = driver.findElement(By.cssSelector(inputReg));
		// 输入搜索关键字
		element.sendKeys(searchString);
		element.submit();
		System.out.println("Page title is:" + driver.getTitle()+" url: "+driver.getCurrentUrl());
		driver.quit();
	}

	public static void main(String[] args) {
		SearchDemo2 search = new SearchDemo2();
		search.search("http://www.amazon.cn/ref=nav_logo", "kindle");
	}
}