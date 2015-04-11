import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;

public class ItemRevDemo extends DeepCrawler {
	private AmazonCrawler crawler1 = new AmazonCrawler("/home/hu/data/az");
	private int MAXNUM;
	
	private final int SLEEP_TIME=10000;

	public ItemRevDemo(String crawlPath) {
		super(crawlPath);
	}

	public Links visitAndGetNextLinks(Page page) {
		Links nextLinks = new Links();
		org.jsoup.nodes.Document doc = page.getDoc();
		String url = page.getUrl();
		System.out.println("URL:\t" + url + "\n标题:\t" + doc.title() + "\n");
		if (doc.title().matches("Amazon CAPTCHA")) {
			System.out
					.println("访问过于频繁，被拒绝了！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			return null;
		}
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (Pattern.matches("^http://www.amazon.cn/product-reviews/.*", url)) { // 如果是评论页面，加入”下一页“指向的链接
			System.out
					.println("加入Amazon的种子============================================");
			crawler1.addSeed(url);
			crawler1.addToLinks(url);
			
			//获取最大页数，以设置评论爬虫的深度
			Elements as = doc.select("span.paging>*");
			if(as.isEmpty()){
				System.out.println("没有其他评论页~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~只有一页评论");
				return null;
			}
			String numString=as.get(as.size()-2).text();
			MAXNUM=Integer.parseInt(numString);
			System.out.println("获取到页数："+MAXNUM+'\n');
		} else if (Pattern.matches("^http://www.amazon.cn/.*/dp/.*", url)) { // 如果是商品页面，加入”查看所有评论“对应的链接
			Elements as = doc.select("a[class=a-link-emphasis a-text-bold]");
			if (as.isEmpty()) {
				System.out.println("没找到评论网页！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
			} else {
				String hrefString = as.get(0).attr("abs:href");
				System.out.println("获取评论首页-------------------\n" + hrefString);
				nextLinks.add(hrefString);
			}
		}
		return nextLinks;
	}

	public void startRev() {
		crawler1.setThreads(1);
		/* 设置是否断点爬取 */
		crawler1.setResumable(false);
		try {
			crawler1.start(MAXNUM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		crawler1.printAllLinks();
	}

	/*
	 * 设置代理
	 */
	public void setPro() {
		String dlWebUrl = "http://www.kuaidaili.com/free/inha/6";
		Document doc = null;
		try {
			doc = Jsoup.connect(dlWebUrl).timeout(5000).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element listDiv = doc.getElementById("list");  
        Elements trs = listDiv.select("table tbody tr");
		Proxys proxys = new Proxys();
		for (final Element tr : trs) {  
            String ip = tr.child(0).text();  
            int port = Integer.parseInt(tr.child(1).text()); 
            System.out.println("代理："+ip+"\nport: "+port);
            proxys.add(ip, port);
        }  
		crawler1.setProxys(proxys);
		this.setProxys(proxys);
		
		this.setRetry(10);
		crawler1.setRetry(10);
	}
	/* 启动爬虫 */
	public static void main(String[] args) throws Exception {
		ItemRevDemo crawler = new ItemRevDemo("/home/hu/data/az123");
		crawler.setThreads(1);
//		crawler.addSeed("http://www.amazon.cn/HUAWEI-%E5%8D%8E%E4%B8%BA-%E8%8D%A3%E8%80%80-3X%E7%95%85%E7%8E%A9%E7%89%88-G750-T01%E7%A7%BB%E5%8A%A83G%E6%89%8B%E6%9C%BA-%E5%8F%8C%E5%8D%A1%E5%8F%8C%E5%BE%85-%E7%9C%9F8%E6%A0%B8%E5%A4%84%E7%90%86%E5%99%A8-2GB-RAM-8GB-ROM-1300%E4%B8%87-500%E4%B8%87%E5%83%8F%E7%B4%A0/dp/B00JO9DCQM/ref=sr_1_3?ie=UTF8&qid=1428729973&sr=8-3&keywords=%E6%89%8B%E6%9C%BA");
		crawler.addSeed("http://www.amazon.cn/%E6%9C%97%E6%A0%BC%E5%A4%A9%E5%90%AFL80%E6%89%8B%E6%9C%BA-%E5%85%AB%E6%A0%B84G%E6%89%8B%E6%9C%BA-4G%E6%99%BA%E8%83%BD%E6%89%8B%E6%9C%BA-1600%E4%B8%87%E9%AB%98%E6%B8%85%E5%83%8F%E7%B4%A0-%E7%A7%BB%E5%8A%A84G-%E5%8F%8C%E5%8D%A1%E5%8F%8C%E5%BE%852G-RAM-16G-ROM-%E5%A4%A7%E5%86%85%E5%AD%98%E6%99%BA%E8%83%BD%E6%89%8B%E6%9C%BA-5-5%E8%8B%B1%E5%AF%B8%E5%A4%A7%E5%B1%8F-%E5%AE%89%E5%8D%934-3%E7%B3%BB%E7%BB%9F/dp/B00RMUGQ18/ref=sr_1_302?s=wireless&ie=UTF8&qid=1428734065&sr=1-302&keywords=%E6%89%8B%E6%9C%BA");
		crawler.setPro();
		/* 设置是否断点爬取 */
		crawler.setResumable(false);
		crawler.start(2);
		crawler.startRev();
	}
}