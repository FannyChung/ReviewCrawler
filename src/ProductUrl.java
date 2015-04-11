import java.util.HashSet;


public class ProductUrl{
	private String string;
	public ProductUrl(String s) {
		string=s;
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		ProductUrl other=(ProductUrl)arg0;
		String str1=string.split("/")[5];
		String str2=other.string.split("/")[5];
        if (str1.equals(str2)) 
        	return true; 
        else return false; 
	}
    public int hashCode() 
    { 
		String str1=string.split("/")[5];
        return new String(str1).hashCode(); 
    } 
    
	public static void main(String[] args) {
		String s1="http://www.amazon.cn/SAMSUNG-%E4%B8%89%E6%98%9F-E1200R-GSM%E6%89%8B%E6%9C%BA/dp/B00KW0QFGG/ref=sr_1_1/475-9759494-0113316?ie=UTF8&qid=1428743825&sr=8-1&keywords=%E6%89%8B%E6%9C%BA";
		String s2="http://www.amazon.cn/SAMSUNG-%E4%B8%89%E6%98%9F-E1200R-GSM%E6%89%8B%E6%9C%BA/dp/B00KW0QFGG/ref=sr_1_1/475-9759494-0113316?ie=UTF8&qid=1428743825&sr=8-1&keywords=%E6%89%8B%E6%9C%BA#customerReviews";
		ProductUrl url1=new ProductUrl(s1);
		ProductUrl url2=new ProductUrl(s2);
		System.out.println(url1.equals(url2));
		HashSet<ProductUrl> h=new HashSet<ProductUrl>();
		h.add(url2);
		h.add(url1);
		for (ProductUrl productUrl : h) {
			System.out.println(productUrl.string);
		}
	}
}
