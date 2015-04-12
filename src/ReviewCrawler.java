//package cn.edu.hfut.dmic.webcollector.example;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 爬取评论的爬虫
 * @author Fanny
 *
 */
public class ReviewCrawler extends DeepCrawler {
	private RegexRule regexRule = new RegexRule(); 
	private ArrayList<String> allLinksHashSet = new ArrayList<String>();// 记录所有遍历过的链接
	private Vector<Review> reviews=new Vector<Review>(); // 只需要不断增加，所以使用vector
	private int SLEEP_TIME = 10000;//防止访问过于频繁而设置的睡眠时间

	/**
	 * 构造函数
	 * 
	 * @param crawlPath
	 */
	public ReviewCrawler(String crawlPath) {
		super(crawlPath);
		regexRule.addRule("http://www.amazon.cn/product-reviews/.*");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.hfut.dmic.webcollector.fetcher.Visitor#visitAndGetNextLinks(cn
	 * .edu.hfut.dmic.webcollector.model.Page)
	 */
	public Links visitAndGetNextLinks(Page page) {
		String reviewReg = "div[style=margin-left:0.5em;]";// 评论信息的选择规则
		String textReg = ".reviewText"; // 评论文本的选择规则
		String levelReg = "span[class~=swSprite s_star_.*]";// 评论等级
		String titleReg = "b"; // 评论标题
		String timeReg = "nobr"; // 评论时间
		String userReg = "span[style = font-weight: bold;]";// 评论用户
		String dateFormat = "yyyy年MM月dd日"; // 时间格式
		String otherReviewReg = "span.paging>*"; // 本页指向其他评论页的超链接
		String nextReview = ".*link_next.*"; // 本页评论指向下一页评论的超链接格式
		String refusedTitle = "Amazon CAPTCHA"; // 访问频繁时，被拒绝出现的网页标题

		Document doc = page.getDoc(); // 获取页面DOM
		String title = doc.title(); // 获取网页标题
		if (title.matches(refusedTitle)) {
			System.err
					.println("访问过于频繁，被拒绝了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			return null;
		}
		Elements ele1 = doc.select(reviewReg); // 获取评论相关信息的集合
		System.out.println("URL:" + page.getUrl() + " \n标题:" + title);
		// 延时，避免被反爬虫
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (ele1.isEmpty()) {
			System.err
					.println("没找到评论信息对应的元素！------------------------------------");
		} else {
			for (Element element : ele1) {// 获取评论元素，放入review中，每个元素有评论、作者、时间等
				Review review = new Review();
				Elements textElements = element.select(textReg);// 获取评论文本
				String cString = textElements.get(0).text();
				// System.out.println(cString);
				review.setText(cString);

				Elements levelElements = element.select(levelReg);// 获取星级信息
				cString = levelElements.get(0).text();
				int level = cString.charAt(2) - '0';
				// System.out.println(level);
				review.setLevel(level);

				Elements titleEle = element.select(titleReg);
				cString = titleEle.get(0).text(); // 标题
				// System.out.println(cString);
				review.setReTitle(cString);

				Elements timeEle = element.select(timeReg);
				cString = timeEle.get(0).text(); // 获取日期
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

				Elements userElements = element.select(userReg);// 获取评论者名称
				cString = userElements.get(0).text();
				// System.out.println(cString);
				review.setUserName(cString);

				reviews.add(review);
			}
			System.out
					.println("处理完本页评论信息============================================================");
		}

		Links nextLinks = new Links();
		Elements as = doc.select(otherReviewReg).select("a[href]");
		//获取“下一页”超链接
		if (as.isEmpty()) {
			System.out.println("没找到其他评论页面的超链接！！！！！！！！本评论只有一页");
		} else {
			String href = as.last().attr("abs:href"); // 获取指向其他评论页的超链接的最后一个
			System.out.println("Amazon获取-------------------" + href + '\n');
			if (href.matches(nextReview)) { // 检查是否是“下一页”，如果是，则需要进行爬取
				nextLinks.add(href);
				allLinksHashSet.add(href);
			}
		}
		return nextLinks;
	}

	/**
	 * 把该链接加入到记录的所有链接集合中
	 * 
	 * @param link
	 */
	public void addToLinks(String link) {
		allLinksHashSet.add(link);
	}

	/**
	 * 获取已有的所有评论
	 * 
	 * @return reviews
	 */
	public Vector<Review> getReviews() {
		return reviews;
	}

	/**
	 * 打印记录的所有超链接
	 */
	public void printAllLinks() {
		System.out.println("所有的评论url：");
		for (String string : allLinksHashSet) {
			System.out.println(string);
		}
	}

	// public static void main(String[] args) throws Exception {
	// AmazonCrawler crawler = new AmazonCrawler("/home/hu/data/az");
	// crawler.setThreads(1);
	// crawler.addSeed("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
	// crawler.addToLinks("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
	// Proxys proxys = new Proxys();
	// crawler.setProxys(proxys);
	//
	// /* 设置是否断点爬取 */
	// crawler.setResumable(false);
	// crawler.start(5);
	// crawler.printAllLinks();
	// }
}