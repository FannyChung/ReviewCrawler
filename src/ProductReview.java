import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * @author Fanny
 *
 */
public class ProductReview extends DeepCrawler {
	private int MAXNUM;

	private final int SLEEP_TIME = 10000;
	private int productNum = 0;
	private WritableWorkbook book;
	/**
	 * 记录遍历的商品url
	 */
	MyLogger log1 = new MyLogger("producturl");
	/**
	 * 记录已经创建的表单名和编号
	 */
	MyLogger log2 = new MyLogger("sheet");

	/**
	 * @param crawlPath
	 */
	public ProductReview(String crawlPath) {
		super(crawlPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.edu.hfut.dmic.webcollector.fetcher.Visitor#visitAndGetNextLinks(cn
	 * .edu.hfut.dmic.webcollector.model.Page)
	 */
	public Links visitAndGetNextLinks(Page page) {
		String refusedTitle = "Amazon CAPTCHA"; // 访问频繁时，被拒绝出现的网页标题
		String reviewUrlReg = "^http://www.amazon.cn/product-reviews/.*";// 评论网页url满足的正则式
		String productUrlReg = "^http://www.amazon.cn/.*/dp/.*"; // 商品网页url的正则式
		String reviewReg = "a[class=a-link-emphasis a-text-bold]"; // 商品网页中指向评论网页的元素格式
		String nextPos = "span.paging>*"; // 下一页所在的元素格式

		Links nextLinks = new Links();
		org.jsoup.nodes.Document doc = page.getDoc();
		String url = page.getUrl();
		String title = doc.title();
		System.out.println("URL:\t" + url + "\n标题:\t" + title + "\n");
		log1.info(url);

		if (doc.title().matches(refusedTitle)) {
			System.err
					.println("访问过于频繁，被拒绝了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			return null;
		}

		// 延时，避免被反爬虫
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (Pattern.matches(reviewUrlReg, url)) { // 如果是评论页面，加入”下一页“指向的链接
			// 获取最大页数，以设置评论爬虫的深度
			Elements as = doc.select(nextPos);
			if (as.isEmpty()) {
				System.out
						.println("没有其他评论页~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~只有一页评论");
				MAXNUM = 1;
			} else {
				String numString = as.get(as.size() - 2).text();
				MAXNUM = Integer.parseInt(numString);
			}
			System.out.println("获取到页数：" + MAXNUM + '\n');
			log1.info("页数："+MAXNUM);
			System.out
					.println("加入Amazon的种子============================================");

			// 启动爬取评论页的爬虫
			ReviewCrawler crawler1 = new ReviewCrawler(".review");
			crawler1.addSeed(url);
			crawler1.addToLinks(url);
			crawler1.setThreads(1);
			crawler1.setResumable(false); /* 设置是否断点爬取 */
			try {
				crawler1.start(MAXNUM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			crawler1.printAllLinks();
			//打印该商品评论到表格中
//			openFile("ttt"+productNum+".xls");
//			WritableSheet sheet = book.createSheet(title, productNum);// 设置表单名字和编号
//			log2.info("表单" + title + '\t' + productNum);
//
//			try {
//				printReviews(crawler1.getReviews(), sheet);// 将所有的评论打印到表格中
//			} catch (RowsExceededException e) {
//				e.printStackTrace();
//			} catch (WriteException e) {
//				e.printStackTrace();
//			}
//			closeFile();
			productNum++;
		} else if (Pattern.matches(productUrlReg, url)) { // 如果是商品页面，加入”查看所有评论“对应的链接
			Elements as = doc.select(reviewReg);
			if (as.isEmpty()) {
				System.err.println("没找到评论网页！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			} else {
				String hrefString = as.get(0).attr("abs:href");
				System.out.println("获取评论首页-------------------\n" + hrefString);
				nextLinks.add(hrefString);
			}
		}
		return nextLinks;
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

	public WritableWorkbook getBook() {
		return book;
	}

	/**从网站上获取代理，存入到文件中
	 * @param dlWebUrl "http://www.kuaidaili.com/free/inha/" + i
	 * @param filename "proxys.txt"
	 * @throws IOException
	 */
	public void getProxyOnline(String dlWebUrl, String filename)
			throws IOException {
		// String dlWebUrl = "http://www.kuaidaili.com/free/inha/" + i;
		Document doc = null;
		doc = Jsoup.connect(dlWebUrl).timeout(5000).get();
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWritter = new FileWriter(file.getName(), true);// true = append file
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		Element listDiv = doc.getElementById("list");
		Elements trs = listDiv.select("table tbody tr");
		for (final Element tr : trs) {
			String ip = tr.child(0).text();
			int port = Integer.parseInt(tr.child(1).text());
			bufferWritter.write(ip + ":" + port);
			bufferWritter.newLine();
		}
		bufferWritter.close();
	}
	// public static void main(String[] args) throws Exception {
	// ItemRevDemo crawler = new ItemRevDemo("/home/hu/data/az123");
	// crawler.setThreads(1);
	// //
	// crawler.addSeed("http://www.amazon.cn/HUAWEI-%E5%8D%8E%E4%B8%BA-%E8%8D%A3%E8%80%80-3X%E7%95%85%E7%8E%A9%E7%89%88-G750-T01%E7%A7%BB%E5%8A%A83G%E6%89%8B%E6%9C%BA-%E5%8F%8C%E5%8D%A1%E5%8F%8C%E5%BE%85-%E7%9C%9F8%E6%A0%B8%E5%A4%84%E7%90%86%E5%99%A8-2GB-RAM-8GB-ROM-1300%E4%B8%87-500%E4%B8%87%E5%83%8F%E7%B4%A0/dp/B00JO9DCQM/ref=sr_1_3?ie=UTF8&qid=1428729973&sr=8-3&keywords=%E6%89%8B%E6%9C%BA");
	// crawler.addSeed("http://www.amazon.cn/%E6%9C%97%E6%A0%BC%E5%A4%A9%E5%90%AFL80%E6%89%8B%E6%9C%BA-%E5%85%AB%E6%A0%B84G%E6%89%8B%E6%9C%BA-4G%E6%99%BA%E8%83%BD%E6%89%8B%E6%9C%BA-1600%E4%B8%87%E9%AB%98%E6%B8%85%E5%83%8F%E7%B4%A0-%E7%A7%BB%E5%8A%A84G-%E5%8F%8C%E5%8D%A1%E5%8F%8C%E5%BE%852G-RAM-16G-ROM-%E5%A4%A7%E5%86%85%E5%AD%98%E6%99%BA%E8%83%BD%E6%89%8B%E6%9C%BA-5-5%E8%8B%B1%E5%AF%B8%E5%A4%A7%E5%B1%8F-%E5%AE%89%E5%8D%934-3%E7%B3%BB%E7%BB%9F/dp/B00RMUGQ18/ref=sr_1_302?s=wireless&ie=UTF8&qid=1428734065&sr=1-302&keywords=%E6%89%8B%E6%9C%BA");
	// // setPro(crawler);//设置代理,和重试次数
	// /* 设置是否断点爬取 */
	// crawler.setResumable(false);
	// crawler.start(2);//启动爬虫
	// }
}