# ReviewCrawler
爬取商品评论信息，利用现有java爬虫框架webcollector
##任务
###爬取评论
1. ~~完成评论基本信息的爬取~~
2. 抽象    
    * 变量（完成部分）    
    * 结构    
3. 尝试其他动态网站    
    * 淘宝    
    * 京东   

###遍历多页评论  
1. 在商品评论页面第一页获取数字：页数    
2. 用for构造url，加入爬取集合   
        利用任务生成器 http://blog.csdn.net/ajaxhu/article/details/38787453  
        存在问题：新版的Generator没有使用实例，涉及Environment类

###~~商品页面转评论页面~~    


###模拟搜索获取多个商品url    
1. ~~模拟搜索~~  
2. 加入多个url  
          困难：F12也找不到对应的元素  
          利用任务生成器 http://blog.csdn.net/ajaxhu/article/details/38787453

##参考资料：
jsoup api<http://www.brieftools.info/document/jsoup/>  
github上的webcollector，有具体实现和例子[https://github.com/CrawlScript/WebCollector]

##日志：
2015年4月10日：  
今天主要做如何遍历N页评论网页，有两种解决方法：
1. 获取最大页数N，然后构造url，这种情况的扩展性不强
2. 不断获取下一页对应的链接，但是这种方法存在的问题是最顶层的爬虫深度在最开始是确定的  

目前想使用的方法是：  
在顶层爬虫中获取评论首页和最大页数，并把评论相关链接加入到另一个爬虫Amazon爬虫中  
Amazon爬虫能够处理本页的评论信息，并获取相关的评论链接，加入到nextLinks中。ps，用HashSet对整体的评论页链接去重  

不知道为什么，下午实验正确的“从商品页转到评论页”功能，晚上出错了，现象是：跳转的url中间多了一串数字，和手动点击跳转不同。  
另外，存在的问题是有的链接对应网页的标题是'AMAZON CAPTCHA'。目前猜想可能是下午最后的一次运行访问亚马逊太多了，导致现在被拒绝？
果然遇到了反爬虫：
 ![image](https://github.com/FannyChung/ReviewCrawler/pictures/pic1.jpg)
即使只爬取一次也会遇见这种问题，估计是今天爬多了吧。有一次跑了太多了
