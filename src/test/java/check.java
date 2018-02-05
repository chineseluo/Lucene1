import online.work.lucene.Lucene01;
import online.work.lucene.LuceneCURD;
import org.junit.Test;

/**
 * Created by 84825 on 2018/2/5.
 */
public class check {
    Lucene01 lucene01=new Lucene01();
    LuceneCURD luceneCURD=new LuceneCURD();
    //测试创建索引
    @Test
    public void fun() throws Exception{
        lucene01.testIndex();
    }
    //测试查询索引
    @Test
    public void fun2() throws  Exception{
        lucene01.searchindex();
    }
    //测试删除全部文档对象
    @Test
    public void fun3() throws Exception{
        luceneCURD.deleteAllIndex();
    }
    //测试根据条件精准删除
    @Test
    public void fun4() throws  Exception{
        luceneCURD.deleteOneIndex();
    }

}
