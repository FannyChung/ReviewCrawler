
//package cn.edu.hfut.dmic.webcollector.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Vector;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;

public class AmazonCrawler extends DeepCrawler {
    RegexRule regexRule = new RegexRule();
	BufferedWriter bufferWritter;

    JdbcTemplate jdbcTemplate = null;
    private static HashSet<String> allLinksHashSet=new HashSet<String>();
    Vector<Review> reviews=new Vector<Review>();
    /*
     * 构造函数
     */
    public AmazonCrawler(String crawlPath) {
        super(crawlPath);
        regexRule.addRule("http://www.amazon.cn/product-reviews/.*");
    }

    public Links visitAndGetNextLinks(Page page) {
        Document doc = page.getDoc();
        String title = doc.title();
        Elements ele1=doc.select("div[style=margin-left:0.5em;]");//评论相关信息的集合
        System.out.println("URL:" + page.getUrl() + "  标题:" + title+"    "+page.getHtml());
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if (ele1.isEmpty()) {
			System.out.println("没找到评论信息对应的元素！------------------------------------");
		} else {
	        for (Element element : ele1) {//每个元素有评论、作者、时间等
	            Review review = new Review();
	            Elements textElements=element.select(".reviewText");//获取评论文本
	    		String cString=textElements.get(0).text();
	    		System.out.println(cString);
	    		review.setText(cString);
	    		
	    		Elements levelElements=element.select("span[class~=swSprite s_star_.*]");//获取星级信息
	    		cString=levelElements.get(0).text();
	    		int level=cString.charAt(2)-'0';
	    		System.out.println(level);
	    		review.setLevel(level);
	    		
	    		Elements titleElements=element.select("span[style=vertical-align:middle;]");//获取评论的标题
	    		cString=titleElements.select("b").text();
	    		System.out.println(cString);
	    		review.setReTitle(cString);
	    		cString=titleElements.select("nobr").text();						//获取日期
	    		System.out.println(cString);
	    		SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月dd日");
	    		Date d=null; 
	    		try {
					long t = s.parse(cString).getTime();
		    		 d=new Date(t);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		review.setTime(d);
	    		
	    		Elements userElements=element.select("span[style = font-weight: bold;]");//获取评论者名称
	    		cString=userElements.get(0).text();
	    		System.out.println(cString);
	    		review.setUserName(cString);
	    		
	    		reviews.add(review);
			}
	        System.out.println("处理完评论信息============================================================");
	        printText(reviews);
		}
        
//        Elements as = doc.select("span.paging>*").select("a[href]");
//    	String href=as.last().attr("abs:href");//下一页
        Links nextLinks = new Links();
//        nextLinks.addAllFromDocument(doc, regexRule);
//        nextLinks.addAllFromDocument(doc, "span.paging>*");// 
        Elements as = doc.select("span.paging>*").select("a[href]");
        for (Element a : as) {
            String href = a.attr("abs:href");
        	System.out.println("Amazon获取-------------------\n"+href);
            if(allLinksHashSet.add(href))
            	nextLinks.add(href);
        }
        
        return nextLinks;
    }

    public void addToLinks(String link) {
		allLinksHashSet.add(link);
	}
    public void printText(Vector<Review> reviews) {
    	File file = new File("out");
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
    public static void main(String[] args) throws Exception {
        AmazonCrawler crawler = new AmazonCrawler("/home/hu/data/az");
        crawler.setThreads(50);
        crawler.addSeed("http://www.amazon.cn/product-reviews/B00OB5T26S/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending");
        Proxys proxys = new Proxys();
        crawler.setProxys(proxys);

        /*设置是否断点爬取*/
        crawler.setResumable(false);
        crawler.start(1);
    }
}