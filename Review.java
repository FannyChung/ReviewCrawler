import java.util.Date;

/**
 * 
 */

/**
 * @author Fanny
 *
 */
/**
 * @author Fanny
 *
 */
/**
 * @author Fanny
 * 
 */
public class Review {
	private String text;
	private String userName;
	private String reTitle;
	private int level;
	private Date time;
	private String style;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getReTitle() {
		return reTitle;
	}

	public void setReTitle(String reTitle) {
		this.reTitle = reTitle;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

}
