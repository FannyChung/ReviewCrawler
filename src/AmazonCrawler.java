//package cn.edu.hfut.dmic.webcollector.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AmazonCrawler extends DeepCrawler {
	private RegexRule regexRule = new RegexRule();
	private BufferedWriter bufferWritter;

	private ArrayList<String> allLinksHashSet = new ArrayList<String>();
	private Vector<Review> reviews = new Vector<Review>();
	private int SLEEP_TIME=10000;

	/*
	 * 构造函数
	 */
	public AmazonCrawler(String crawlPath) {
		super(crawlPath);
		regexRule.addRule("http://www.amazon.cn/product-reviews/.*");
	}

	public Links visitAndGetNextLinks(Page page) {
		String reviewReg = "div[style=margin-left:0.5em;]";
		String textReg = ".reviewText";
		String levelReg = "span[class~=swSprite s_star_.*]";
		String titleReg = "b";
		String timeReg = "nobr";
		String userReg = "span[style = font-weight: bold;]";
		String dateFormat = "yyyy年MM月dd日";

		Document doc = page.getDoc();
		String title = doc.title();
		if (title.matches("Amazon CAPTCHA")) {
			System.out
					.println("访问过于频繁，被拒绝了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			return null;
		}
		Elements ele1 = doc.select(reviewReg);// 评论相关信息的集合
		System.out.println("URL:" + page.getUrl() + " \n标题:" + title);
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (ele1.isEmpty()) {
			System.out
					.println("没找到评论信息对应的元素！------------------------------------");
		} else {
			for (Element element : ele1) {// 每个元素有评论、作者、时间等
				Review review = new Review();
				Elements textElements = element.select(textReg);// 获取评论文本
				String cString = textElements.get(0).text();
//				System.out.println(cString);
				review.setText(cString);

				Elements levelElements = element.select(levelReg);// 获取星级信息
				cString = levelElements.get(0).text();
				/*
				 * TODO
				 */
				int level = cString.charAt(2) - '0';
//				System.out.println(level);
				review.setLevel(level);

				Elements titleEle = element.select(titleReg);
				cString = titleEle.get(0).text(); // 标题
//				System.out.println(cString);
				review.setReTitle(cString);

				Elements timeEle = element.select(timeReg);
				cString = timeEle.get(0).text(); // 获取日期
//				System.out.println(cString);
				SimpleDateFormat s = new SimpleDateFormat(dateFormat);
				Date d = null;
				try {
					long t = s.parse(cString).getTime();
					d = new Date(t);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				review.setTime(d);

				Elements userElements = element.select(userReg);// 获取评论者名称
				cString = userElements.get(0).text();
//				System.out.println(cString);
				review.setUserName(cString);

				reviews.add(review);
			}
			System.out
					.println("处理完评论信息============================================================");
		}

		Links nextLinks = new Links();
		Elements as = doc.select("span.paging>*").select("a[href]");
		if (as.isEmpty()) {
			System.out
					.println("没找到其他评论页面的超链接！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		}else {
			String href = as.last().attr("abs:href");
			System.out.println("Amazon获取-------------------\n" + href);
			if (href.matches(".*link_next.*")) {
				nextLinks.add(href);
				allLinksHashSet.add(href);
			}
		}
		return nextLinks;
	}

	public void addToLinks(String link) {
		allLinksHashSet.add(link);
	}

	public void printText(Vector<Review> reviews) {
		File file = new File("out.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileWriter fileWritter = null;
		try {
			fileWritter = new FileWriter(file.getName(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bufferWritter = new BufferedWriter(fileWritter);

		try {
			for (Review review : reviews) {
				bufferWritter.write(review.getLevel() + review.getText() + '\t'
						+ review.getReTitle() + '\t' + review.getTime() + '\t'
						+ review.getUserName());
				bufferWritter.newLine();
			}
			bufferWritter.newLine();
			bufferWritter.write("总数目："+reviews.size());
			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printAllLinks() {
		printText(reviews);
		for (String string : allLinksHashSet) {
			System.out.println(string);
		}
	}

	public static void main(String[] args) throws Exception {
		AmazonCrawler crawler = new AmazonCrawler("/home/hu/data/az");
		crawler.setThreads(1);
		crawler.addSeed("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
		crawler.addToLinks("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
		Proxys proxys = new Proxys();
		crawler.setProxys(proxys);

		/* 设置是否断点爬取 */
		crawler.setResumable(false);
		crawler.start(5);
		crawler.printAllLinks();
	}
}