import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SearchDemo2 {

	public void search(String homepageString, String searchString) {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.get(homepageString);
		System.out.println("Page title is:" + driver.getTitle());
		// 找到文本框
		WebElement element = driver.findElement(By.cssSelector("input[type=text]"));
		// 输入搜索关键字
		element.sendKeys(searchString);
		element.submit();
		System.out.println("Page title is:" + driver.getTitle());
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.getTitle().toLowerCase().startsWith(searchString);
			}
		});
		System.out.println("Page title is:" + driver.getTitle());
		driver.quit();
	}

	public static void main(String[] args) {
		SearchDemo2 search = new SearchDemo2();
		search.search("http://www.amazon.cn/ref=nav_logo", "kindle");
	}
}
