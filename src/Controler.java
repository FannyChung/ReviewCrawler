import java.util.HashSet;


public class Controler {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Search search = new Search();
		String s=search.search("http://www.amazon.cn/ref=nav_logo", "�ֻ�");//������ҳ����������
		HashSet<ProductUrl> productsStrings=search.getProductPage(s);//��ȡ������õ���������Ʒurl
		ProductReview crawler = new ProductReview("/home/hu/data/az123");//��ȡ���е���Ʒ
		crawler.setThreads(1);
		crawler.setResumable(false);
		int i=0;
		final int productNum=2;//������ȡ��Ʒ�ĸ���
		for (ProductUrl productUrl : productsStrings) {//��Ҫ��ȡ����Ʒ��Ϊ���������
			if(i==productNum)
				break;
			crawler.addSeed(productUrl.getString());
			i++;
		}
		crawler.openFile("test1.xls");//���ļ�
		crawler.start(2);//��������
		crawler.closeFile();//�ر��ļ�
	}
}
