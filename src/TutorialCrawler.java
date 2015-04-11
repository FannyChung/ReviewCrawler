
//package cn.edu.hfut.dmic.webcollector.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.JDBCHelper;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import org.jsoup.nodes.Document;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * WebCollector 2.x�汾��tutorial
 * 2.x�汾���ԣ�
 *   1���Զ���������ԣ�����ɸ�Ϊ���ӵı���ҵ�������ҳ��AJAX
 *   2������Berkeley DB����URL�����Դ��������������ҳ
 *   3������selenium�����Զ�javascript������Ϣ���г�ȡ
 *   4��ֱ��֧�ֶ��������л�
 *   5������spring jdbc��mysql connection���������ݳ־û�
 *   6������json������
 *   7��ʹ��slf4j��Ϊ��־����
 *   8���޸�http����ӿڣ��û��Զ���http������ӷ���
 * 
 * ����cn.edu.hfut.dmic.webcollector.example�����ҵ�����(Demo)
 * 
 * @author hu
 */
public class TutorialCrawler extends DeepCrawler {
	BufferedWriter bufferWritter;

    /*2.x�汾�У�����ı������û��Զ���(���ʻ��ǹ�ȱ���������ÿ��ҳ��
     ���ɵ�URL��Ҳ���Ǳ�������ÿ���ڵ�ĺ��ӽڵ㣬�����û��Զ����)��
      
     1.x�汾�У�Ĭ�Ͻ�ÿ��ҳ���У�������������Լ�������ӣ�����������ȡURL��ͨ��
     ���ַ������������һ����Χ��(������վ)����ȡ(��������Լ��)��
    
     ������2.x�汾�У�����ֻҪ��ȡҳ�������������URL����ΪLinks���أ��Ϳ���
     ���1.x�汾��BreadthCrawler�Ĺ��ܡ�
      
     */
    RegexRule regexRule = new RegexRule();

    JdbcTemplate jdbcTemplate = null;

    /*
     * ���캯��
     */
    public TutorialCrawler(String crawlPath) {
        super(crawlPath);

        regexRule.addRule("http://.*amazon.cn/.*");
//        regexRule.addRule("-.*jpg.*");

        /*����һ��JdbcTemplate����,"mysql1"���û��Զ�������ƣ��Ժ����ͨ��
         JDBCHelper.getJdbcTemplate("mysql1")����ȡ�������
         �����ֱ��ǣ����ơ�����URL���û��������롢��ʼ�������������������
        
         �����JdbcTemplate�����Լ����Դ������ӳأ����������ڶ��߳��У����Թ���
         һ��JdbcTemplate����(ÿ���߳���ͨ��JDBCHelper.getJdbcTemplate("����")
         ��ȡͬһ��JdbcTemplate����)             
         */
//
//        try {
//            jdbcTemplate = JDBCHelper.createMysqlTemplate("mysql1",
//                    "jdbc:mysql://localhost/testdb?useUnicode=true&characterEncoding=utf8",
//                    "root", "password", 5, 30);
//
//            /*�������ݱ�*/
//            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_content ("
//                    + "id int(11) NOT NULL AUTO_INCREMENT,"
//                    + "title varchar(50),url varchar(200),html longtext,"
//                    + "PRIMARY KEY (id)"
//                    + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;");
//            System.out.println("�ɹ��������ݱ� tb_content");
//        } catch (Exception ex) {
//            jdbcTemplate = null;
//            System.out.println("mysqlδ������JDBCHelper.createMysqlTemplate�в������ò���ȷ!");
//        }
    }

    public Links visitAndGetNextLinks(Page page) {
        Document doc = page.getDoc();
        String title = doc.title();
        System.out.println("URL:" + page.getUrl() + "  ����:" + title);

        /*�����ݲ���mysql*/
        if (jdbcTemplate != null) {
            int updates=jdbcTemplate.update("insert into tb_content (title,url,html) value(?,?,?)",
                    title, page.getUrl(), page.getHtml());
            if(updates==1){
                System.out.println("mysql����ɹ�");
            }
        }
    	File file = new File("out1.txt");
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
			bufferWritter.write( title+"\t"+page.getUrl()+"\t"
//         +page.getHtml()
					);
			bufferWritter.newLine(); 
			 bufferWritter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        /*������2.0�汾�¼��������*/
        /*��ȡpage�е����ӷ��أ���Щ���ӻ�����һ����ȡʱ����ȡ��
         ���õ���URLȥ�أ�������Զ������ظ�URL��*/
        Links nextLinks = new Links();

        /*����ֻϣ����ȡ��������Լ����URL��
         Links.addAllFromDocumentΪ�����ṩ����Ӧ�Ĺ���*/
        nextLinks.addAllFromDocument(doc, regexRule);

        /*Links��̳�ArrayList<String>,����ʹ��add��addAll�ȷ����Լ����URL
         �����ǰҳ��������У�û����Ҫ��ȡ�ģ�����return null
         ������������ȡ����ֻ����ȡseed�б��е��������ӣ��������Ӧ��return null
         */
        return nextLinks;
    }

    public static void main(String[] args) throws Exception {
        /*���캯���е�string,�������crawlPath���������ȡ��Ϣ������crawlPath�ļ�����,
          ��ͬ��������ʹ�ò�ͬ��crawlPath
        */
        TutorialCrawler crawler = new TutorialCrawler("/home/hu/data/b1");
        crawler.setThreads(50);
        crawler.addSeed("https://www.amazon.cn/");
        crawler.setResumable(false);

        /*2.x�汾ֱ��֧�ֶ��������л�*/
        Proxys proxys = new Proxys();
        /*
         ���ô�����Ե� http://www.brieftools.info/proxy/ ��ȡ
         ��Ӵ���ķ�ʽ:
         1)ip�Ͷ˿�
         proxys.add("123.123.123.123",8080);
         2)�ļ�
         proxys.addAllFromFile(new File("xxx.txt"));
         �ļ���������:
         123.123.123.123:90
         234.234.324.234:8080
         һ������ռһ��
         */

        crawler.setProxys(proxys);

        /*�����Ƿ�ϵ���ȡ*/
        crawler.setResumable(false);

        crawler.start(2);
    }
}

