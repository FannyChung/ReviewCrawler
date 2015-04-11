import java.io.IOException;

import com.sleepycat.je.Environment;

import cn.edu.hfut.dmic.webcollector.generator.StandardGenerator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;

public class generatorTest {
    public static void main(String[] args) throws IOException{  
        /*CollectionGenerator可以根据指定的url或者url列表生成任务*/  
    	StandardGenerator generator=new StandardGenerator(ev);  
        generator.addUrl("http://lucene.apache.org/core/");  
        generator.addUrl("http://lucene.apache.org/core/documentation.html");  
        generator.addUrl("http://abc.abc.abc.abc.abc/");  
          
        CrawlDatum crawldatum=null;  
        while((crawldatum=generator.next())!=null){  
            System.out.println(crawldatum.url);  
        }  
  }  
}
