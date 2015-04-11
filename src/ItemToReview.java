import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * 
 */

/**
 * @author Fanny
 *
 */
public class ItemToReview {

	public String itemToReviewPage(String itemPage) {
		String[] strings=itemPage.split("/");
		String proSeq=strings[5];
		String revPageString="http://www.amazon.cn/product-reviews/"+proSeq+"/ref=cm_cr_dp_see_all_btm?ie=UTF8&showViewpoints=1&sortBy=bySubmissionDateDescending";
		return revPageString;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ItemToReview itemToReview=new ItemToReview();
		Page page=new Page();
		page.setUrl(itemToReview.itemToReviewPage("http://www.amazon.cn/NOKIA%E8%AF%BA%E5%9F%BA%E4%BA%9A1050-%E8%B6%85%E9%95%BF%E5%BE%85%E6%9C%BA%E7%9B%B4%E6%9D%BF%E6%89%8B%E6%9C%BA/dp/B00CK2U10U/ref=sr_1_2?ie=UTF8&qid=1428586742&sr=8-2&keywords=%E6%89%8B%E6%9C%BA"));
        Document doc = page.getDoc();
        String title = doc.title();
        System.out.println("URL:" + page.getUrl() + "  БъЬт:" + title);
	}
}
