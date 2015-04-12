import java.util.HashSet;


public class Controler {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Search search = new Search();
		String s=search.search("http://www.amazon.cn/ref=nav_logo", "手机");//设置主页和搜索内容
		HashSet<ProductUrl> productsStrings=search.getProductPage(s);//获取搜索后得到的所有商品url
		ProductReview crawler = new ProductReview("/home/hu/data/az123");//爬取所有的商品
		crawler.setThreads(1);
		crawler.setResumable(false);
		int i=0;
		final int productNum=2;//设置爬取商品的个数
		for (ProductUrl productUrl : productsStrings) {//把要爬取的商品作为爬虫的种子
			if(i==productNum)
				break;
			crawler.addSeed(productUrl.getString());
			i++;
		}
		crawler.openFile("test1.xls");//打开文件
		crawler.start(2);//启动爬虫
		crawler.closeFile();//关闭文件
	}
}
