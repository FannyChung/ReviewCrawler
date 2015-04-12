import java.util.Date;

/**
 * 评论的数据结构
 * @author Fanny
 *
 */
public class Review {
	/**
	 * 评论文本
	 */
	private String text;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 评论标题
	 */
	private String reTitle;
	/**
	 * 评论星级
	 */
	private int level;
	/**
	 * 评论时间
	 */
	private Date time;
	/**
	 * 评论产品的款式
	 */
	private String style;

	/**
	 * @return 评论文本
	 */
	public String getText() {
		return text;
	}

	/**
	 * 设置评论文本
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 获取用户名称
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置用户名称
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 获取评论的标题
	 * @return
	 */
	public String getReTitle() {
		return reTitle;
	}

	/**
	 * 设置评论的标题
	 * @param reTitle
	 */
	public void setReTitle(String reTitle) {
		this.reTitle = reTitle;
	}

	/**
	 * 设置评论等级
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 获取评论等级
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * 获取评论的时间
	 * @return
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * 设置评论的时间
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * 获取评论的商品款式
	 * @return
	 */
	public String getStyle() {
		return style;
	}

	/**设置评论的商品款式
	 * @param style
	 */
	public void setStyle(String style) {
		this.style = style;
	}
}
