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
 * ��ȡ���۵�����
 * @author Fanny
 *
 */
public class ReviewCrawler extends DeepCrawler {
	private RegexRule regexRule = new RegexRule(); 
	private ArrayList<String> allLinksHashSet = new ArrayList<String>();// ��¼���б�����������
	private Vector<Review> reviews=new Vector<Review>(); // ֻ��Ҫ�������ӣ�����ʹ��vector
	private int SLEEP_TIME = 10000;//��ֹ���ʹ���Ƶ�������õ�˯��ʱ��

	/**
	 * ���캯��
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
		String reviewReg = "div[style=margin-left:0.5em;]";// ������Ϣ��ѡ�����
		String textReg = ".reviewText"; // �����ı���ѡ�����
		String levelReg = "span[class~=swSprite s_star_.*]";// ���۵ȼ�
		String titleReg = "b"; // ���۱���
		String timeReg = "nobr"; // ����ʱ��
		String userReg = "span[style = font-weight: bold;]";// �����û�
		String dateFormat = "yyyy��MM��dd��"; // ʱ���ʽ
		String otherReviewReg = "span.paging>*"; // ��ҳָ����������ҳ�ĳ�����
		String nextReview = ".*link_next.*"; // ��ҳ����ָ����һҳ���۵ĳ����Ӹ�ʽ
		String refusedTitle = "Amazon CAPTCHA"; // ����Ƶ��ʱ�����ܾ����ֵ���ҳ����

		Document doc = page.getDoc(); // ��ȡҳ��DOM
		String title = doc.title(); // ��ȡ��ҳ����
		if (title.matches(refusedTitle)) {
			System.err
					.println("���ʹ���Ƶ�������ܾ��ˣ���������������������������������������������������������������������������������������������������������");
			return null;
		}
		Elements ele1 = doc.select(reviewReg); // ��ȡ���������Ϣ�ļ���
		System.out.println("URL:" + page.getUrl() + " \n����:" + title);
		// ��ʱ�����ⱻ������
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (ele1.isEmpty()) {
			System.err
					.println("û�ҵ�������Ϣ��Ӧ��Ԫ�أ�------------------------------------");
		} else {
			for (Element element : ele1) {// ��ȡ����Ԫ�أ�����review�У�ÿ��Ԫ�������ۡ����ߡ�ʱ���
				Review review = new Review();
				Elements textElements = element.select(textReg);// ��ȡ�����ı�
				String cString = textElements.get(0).text();
				// System.out.println(cString);
				review.setText(cString);

				Elements levelElements = element.select(levelReg);// ��ȡ�Ǽ���Ϣ
				cString = levelElements.get(0).text();
				int level = cString.charAt(2) - '0';
				// System.out.println(level);
				review.setLevel(level);

				Elements titleEle = element.select(titleReg);
				cString = titleEle.get(0).text(); // ����
				// System.out.println(cString);
				review.setReTitle(cString);

				Elements timeEle = element.select(timeReg);
				cString = timeEle.get(0).text(); // ��ȡ����
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

				Elements userElements = element.select(userReg);// ��ȡ����������
				cString = userElements.get(0).text();
				// System.out.println(cString);
				review.setUserName(cString);

				reviews.add(review);
			}
			System.out
					.println("�����걾ҳ������Ϣ============================================================");
		}

		Links nextLinks = new Links();
		Elements as = doc.select(otherReviewReg).select("a[href]");
		//��ȡ����һҳ��������
		if (as.isEmpty()) {
			System.out.println("û�ҵ���������ҳ��ĳ����ӣ���������������������ֻ��һҳ");
		} else {
			String href = as.last().attr("abs:href"); // ��ȡָ����������ҳ�ĳ����ӵ����һ��
			System.out.println("Amazon��ȡ-------------------" + href + '\n');
			if (href.matches(nextReview)) { // ����Ƿ��ǡ���һҳ��������ǣ�����Ҫ������ȡ
				nextLinks.add(href);
				allLinksHashSet.add(href);
			}
		}
		return nextLinks;
	}

	/**
	 * �Ѹ����Ӽ��뵽��¼���������Ӽ�����
	 * 
	 * @param link
	 */
	public void addToLinks(String link) {
		allLinksHashSet.add(link);
	}

	/**
	 * ��ȡ���е���������
	 * 
	 * @return reviews
	 */
	public Vector<Review> getReviews() {
		return reviews;
	}

	/**
	 * ��ӡ��¼�����г�����
	 */
	public void printAllLinks() {
		System.out.println("���е�����url��");
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
	// /* �����Ƿ�ϵ���ȡ */
	// crawler.setResumable(false);
	// crawler.start(5);
	// crawler.printAllLinks();
	// }
}