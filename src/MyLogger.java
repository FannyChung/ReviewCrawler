import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class MyLogger {

	private Logger logger;
	public MyLogger(String filename) {
		logger = Logger.getLogger(filename);

		FileAppender appender = null;
		SimpleLayout layout=new SimpleLayout();
		try {
			appender = new FileAppender(layout , filename);
		} catch (Exception e) {
		}
		logger.addAppender(appender);
	}
	public void info(String info) {
		logger.info(info);
	}
}