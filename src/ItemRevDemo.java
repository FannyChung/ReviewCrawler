
import java.util.Vector;
import java.util.regex.Pattern;





import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

    public class ItemRevDemo extends DeepCrawler{  
        RegexRule regexRule = new RegexRule();
        Vector<Review> reviews=new Vector<Review>();
    	AmazonCrawler crawler1=new AmazonCrawler("/home/hu/data/az");
    	Links reviewLinks=new Links();

        public ItemRevDemo(String crawlPath) {
			super(crawlPath);
		}
		public Links visitAndGetNextLinks(Page page) {
	        Links nextLinks = new Links();
	        org.jsoup.nodes.Document doc=page.getDoc();
	        String url=page.getUrl();
	        System.out.println("URL:\t" + url + "\n����:\t" + doc.title());
	        
	        if(Pattern.matches("^http://www.amazon.cn/product-reviews/.*", url)){	//���������ҳ�棬���롱��һҳ��ָ�������
	        	System.out.println("����Amazon������============================================");
	        	crawler1.addSeed(url);
	        	crawler1.addToLinks(url);
//	        	Elements as = doc.select("span.paging>*").select("a[href]");
//	        	String href=as.last().attr("abs:href");//��һҳ
//	        	nextLinks.add(href);
	        	//����ǵ�һҳ����ȡ���ҳ����������nextLinks
//	        	
//	        	Elements ele2=doc.select("span.page>a");
//	        	String numString=ele2.last().text();
//	        	int maxNum=Integer.parseInt(numString);
//	        	
//	        	for (int i = 2; i < maxNum; i++) {
//					nextLinks.add("");
//				}
	        }else if (Pattern.matches("^http://www.amazon.cn/.*/dp/.*", url)) {	//�������Ʒҳ�棬���롱�鿴�������ۡ���Ӧ������
//		        nextLinks.addAllFromDocument(page.getDoc(), "a[class=a-link-emphasis a-text-bold]");
	        	Elements as = doc.select("a[class=a-link-emphasis a-text-bold]");
//	        	Elements as = doc.select("div[id=revF]>div>a");
	        	if (as.isEmpty()) {
					System.out.println("û�ҵ�������ҳ����������������������������������������������������������");
				} else {
		        	String hrefString=as.get(0).attr("abs:href");
		        	System.out.println("��ȡ-------------------\n"+hrefString);
		        	nextLinks.add(hrefString);
				}
			}
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return nextLinks;
		}  
		
		public void startRev() {
			   Proxys proxys = new Proxys();
	            crawler1.setProxys(proxys);
	        	crawler1.setThreads(50);
	            /*�����Ƿ�ϵ���ȡ*/
	            crawler1.setResumable(false);
	            try {
					crawler1.start(5);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
        /*��������*/  
        public static void main(String[] args) throws Exception{    
            ItemRevDemo crawler=new ItemRevDemo("/home/hu/data/az123");  
            crawler.setThreads(50);
            crawler.addSeed("http://www.amazon.cn/SAMSUNG-%E4%B8%89%E6%98%9F-E1200R-GSM%E6%89%8B%E6%9C%BA/dp/B00KW0QFGG/ref=sr_1_1?ie=UTF8&qid=1428679002&sr=8-1&keywords=%E6%89%8B%E6%9C%BA");
            Proxys proxys = new Proxys();
            crawler.setProxys(proxys);
            /*�����Ƿ�ϵ���ȡ*/
            crawler.setResumable(false);
            crawler.start(2);
            crawler.startRev();
        }
    }  