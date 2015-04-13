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
	 * ��¼��������Ʒurl
	 */
	MyLogger log1 = new MyLogger("producturl");
	/**
	 * ��¼�Ѿ������ı����ͱ��
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
		String refusedTitle = "Amazon CAPTCHA"; // ����Ƶ��ʱ�����ܾ����ֵ���ҳ����
		String reviewUrlReg = "^http://www.amazon.cn/product-reviews/.*";// ������ҳurl���������ʽ
		String productUrlReg = "^http://www.amazon.cn/.*/dp/.*"; // ��Ʒ��ҳurl������ʽ
		String reviewReg = "a[class=a-link-emphasis a-text-bold]"; // ��Ʒ��ҳ��ָ��������ҳ��Ԫ�ظ�ʽ
		String nextPos = "span.paging>*"; // ��һҳ���ڵ�Ԫ�ظ�ʽ

		Links nextLinks = new Links();
		org.jsoup.nodes.Document doc = page.getDoc();
		String url = page.getUrl();
		String title = doc.title();
		System.out.println("URL:\t" + url + "\n����:\t" + title + "\n");
		log1.info(url);

		if (doc.title().matches(refusedTitle)) {
			System.err
					.println("���ʹ���Ƶ�������ܾ��ˣ���������������������������������������������������������������������������������������������������������");
			return null;
		}

		// ��ʱ�����ⱻ������
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (Pattern.matches(reviewUrlReg, url)) { // ���������ҳ�棬���롱��һҳ��ָ�������
			// ��ȡ���ҳ����������������������
			Elements as = doc.select(nextPos);
			if (as.isEmpty()) {
				System.out
						.println("û����������ҳ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ֻ��һҳ����");
				MAXNUM = 1;
			} else {
				String numString = as.get(as.size() - 2).text();
				MAXNUM = Integer.parseInt(numString);
			}
			System.out.println("��ȡ��ҳ����" + MAXNUM + '\n');
			log1.info("ҳ����"+MAXNUM);
			System.out
					.println("����Amazon������============================================");

			// ������ȡ����ҳ������
			ReviewCrawler crawler1 = new ReviewCrawler(".review");
			crawler1.addSeed(url);
			crawler1.addToLinks(url);
			crawler1.setThreads(1);
			crawler1.setResumable(false); /* �����Ƿ�ϵ���ȡ */
			try {
				crawler1.start(MAXNUM);
			} catch (Exception e) {
				e.printStackTrace();
			}
			crawler1.printAllLinks();
			//��ӡ����Ʒ���۵������
//			openFile("ttt"+productNum+".xls");
//			WritableSheet sheet = book.createSheet(title, productNum);// ���ñ����ֺͱ��
//			log2.info("��" + title + '\t' + productNum);
//
//			try {
//				printReviews(crawler1.getReviews(), sheet);// �����е����۴�ӡ�������
//			} catch (RowsExceededException e) {
//				e.printStackTrace();
//			} catch (WriteException e) {
//				e.printStackTrace();
//			}
//			closeFile();
			productNum++;
		} else if (Pattern.matches(productUrlReg, url)) { // �������Ʒҳ�棬���롱�鿴�������ۡ���Ӧ������
			Elements as = doc.select(reviewReg);
			if (as.isEmpty()) {
				System.err.println("û�ҵ�������ҳ����������������������������������������������������������");
			} else {
				String hrefString = as.get(0).attr("abs:href");
				System.out.println("��ȡ������ҳ-------------------\n" + hrefString);
				nextLinks.add(hrefString);
			}
		}
		return nextLinks;
	}

	/**
	 * ��ӡ���е�����
	 * 
	 * @param reviews
	 * @param sheet
	 *            xls�ļ��е�һ��sheet��
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	public void printReviews(Vector<Review> reviews, WritableSheet sheet)
			throws RowsExceededException, WriteException {
		int col = 0;
		Label newLabel;
		// newLabel=new Label(0,0,"�ı�");
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
	 * ��excel�ļ�
	 * 
	 * @param fileName
	 */
	public void openFile(String fileName) {
		File file = new File(fileName);
		try {
			book = Workbook.createWorkbook(file);
		} catch (IOException e) {
			System.err.println("excel���ʧ��");
			e.printStackTrace();
		}
	}

	/**
	 * �ر�excel�ļ�
	 */
	public void closeFile() {
		try {
			book.write();
			book.close();
		} catch (IOException e) {
			System.err.println("excel��д��ʧ��");
			e.printStackTrace();
		} catch (WriteException e) {
			System.err.println("excel��ر�ʧ��");
			e.printStackTrace();
		}catch (IndexOutOfBoundsException e) {
			System.err.println("û�д�����");
		}
	}

	public WritableWorkbook getBook() {
		return book;
	}

	/**����վ�ϻ�ȡ�������뵽�ļ���
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
	// // setPro(crawler);//���ô���,�����Դ���
	// /* �����Ƿ�ϵ���ȡ */
	// crawler.setResumable(false);
	// crawler.start(2);//��������
	// }
}