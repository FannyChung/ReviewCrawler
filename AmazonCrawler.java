//package cn.edu.hfut.dmic.webcollector.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * WebCollector 2.x版本的tutorial
 * 2.x版本特性：
 *   1）自定义遍历策略，可完成更为复杂的遍历业务，例如分页、AJAX
 *   2）内置Berkeley DB管理URL，可以处理更大量级的网页
 *   3）集成selenium，可以对javascript生成信息进行抽取
 *   4）直接支持多代理随机切换
 *   5）集成spring jdbc和mysql connection，方便数据持久化
 *   6）集成json解析器
 *   7）使用slf4j作为日志门面
 *   8）修改http请求接口，用户自定义http请求更加方便
 * 
 * 可在cn.edu.hfut.dmic.webcollector.example包中找到例子(Demo)
 * 
 * @author hu
 */
public class TutorialCrawler extends DeepCrawler {
	BufferedWriter bufferWritter;
    /*2.x版本中，爬虫的遍历由用户自定义(本质还是广度遍历，但是每个页面
     生成的URL，也就是遍历树中每个节点的孩子节点，是由用户自定义的)。
      
     1.x版本中，默认将每个页面中，所有满足正则约束的链接，都当作待爬取URL，通过
     这种方法可以完成在一定范围内(例如整站)的爬取(根据正则约束)。
    
     所以在2.x版本中，我们只要抽取页面中满足正则的URL，作为Links返回，就可以
     完成1.x版本中BreadthCrawler的功能。
      
     */
    RegexRule regexRule = new RegexRule();

    JdbcTemplate jdbcTemplate = null;
    private HashSet<String> allLinksHashSet=new HashSet<String>();
    Vector<Review> reviews=new Vector<Review>();
    /*
     * 构造函数
     */
    public TutorialCrawler(String crawlPath) {
        super(crawlPath);
        regexRule.addRule("http://www.amazon.cn/product-reviews/.*");
    }
    
    /* (non-Javadoc)
     * @see cn.edu.hfut.dmic.webcollector.fetcher.Visitor#visitAndGetNextLinks(cn.edu.hfut.dmic.webcollector.model.Page)
     */
    @Override
    public Links visitAndGetNextLinks(Page page) {
    	String reviewReg="div[style=margin-left:0.5em;]";
    	String textReg=".reviewText";
    	String levelReg="span[class~=swSprite s_star_.*]";
    	String titleReg="b";
    	String timeReg="nobr";
    	String userReg="span[style = font-weight: bold;]";
    	String dateFormat="yyyy年MM月dd日";
    	
        Document doc = page.getDoc();
        String title = doc.title();
        Elements ele1=doc.select(reviewReg);//评论相关信息的集合
        System.out.println("URL:" + page.getUrl() + "  标题:" + title);

        for (Element element : ele1) {//每个元素有评论、作者、时间等
            Review review = new Review();
            Elements textElements=element.select(textReg);//获取评论文本
    		String cString=textElements.get(0).text();
    		System.out.println(cString);
    		review.setText(cString);
    		
    		Elements levelElements=element.select(levelReg);//获取星级信息
    		cString=levelElements.get(0).text();
    		/*
    		 * TODO
    		 */
    		int level=cString.charAt(2)-'0';
    		System.out.println(level);
    		review.setLevel(level);
    		
    		Elements titleEle=element.select(titleReg);
    		cString=titleEle.get(0).text();				//标题
    		System.out.println(cString);
    		review.setReTitle(cString);
    		
    		Elements timeEle=element.select(timeReg);
    		cString=timeEle.get(0).text();						//获取日期
    		System.out.println(cString);
    		SimpleDateFormat s = new SimpleDateFormat(dateFormat);
    		Date d=null; 
    		try {
				long t = s.parse(cString).getTime();
	    		 d=new Date(t);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		review.setTime(d);
    		
    		Elements userElements=element.select(userReg);//获取评论者名称
    		cString=userElements.get(0).text();
    		System.out.println(cString);
    		review.setUserName(cString);
    		
    		reviews.add(review);
		}
        System.out.println("============================================================");
        printText(reviews,"out");
        Links nextLinks = new Links();
        Elements as = doc.select("span.paging>*").select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
        	System.out.println("获取-------------------\n"+href);
            if(allLinksHashSet.add(href))
            	nextLinks.add(href);
        }
        return nextLinks;
    }

    /*
     * 将Review集合打印到文件中
     */
    public void printText(Vector<Review> reviews,String fileName) {
    	File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        FileWriter fileWritter = null;
		try {
			fileWritter = new FileWriter(file.getName(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         bufferWritter = new BufferedWriter(fileWritter);
         
         try {
        	 for (Review review : reviews) {
        		 bufferWritter.write(review.getLevel()+review.getText()+'\t'+review.getReTitle()+'\t'+review.getTime()+'\t'+review.getUserName());
     			bufferWritter.newLine();
			}
			bufferWritter.newLine();
			 bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
	}
    
    
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TutorialCrawler crawler = new TutorialCrawler("/home/hu/data/az");
        crawler.setThreads(50);
        crawler.addSeed("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
        Proxys proxys = new Proxys();
        crawler.setProxys(proxys);
        
        /*设置是否断点爬取*/
        crawler.setResumable(false);
        crawler.start(1);
    }
}
