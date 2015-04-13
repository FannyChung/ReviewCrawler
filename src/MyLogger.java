import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**��־��¼��������д��ָ�����ļ��У�д�ĸ�ʽ��һ��һ����Ϣ
 * @author Fanny
 *
 */
public class MyLogger {

	private Logger logger;
	public MyLogger(String filename) {
		logger = Logger.getLogger(filename);

		FileAppender appender = null;
		
		String pattern="%m\r\n";
		PatternLayout layout=new PatternLayout(pattern);
		try {
			appender = new FileAppender(layout , filename);
		} catch (Exception e) {
		}
		logger.addAppender(appender);
	}
	public void info(String info) {
		logger.info(info);
	}
	public static void main(String[] args) {
		MyLogger logger=new MyLogger("log");
		logger.info("hell");
		logger.info("abc");
	}
}