package online.work.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

/**
 * Created by 84825 on 2018/2/5.
 */
public class LuceneCURD {
    public IndexWriter getIndexWriter() throws Exception {
        //第一步，创建一个indexWriter对象
        //（1）制定索引库存放位置Directory对象
        Directory directory = FSDirectory.open(new File("D://temp//index"));
        //（2）制定一个分析器，对文档内容进行分析
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        return new IndexWriter(directory, indexWriterConfig);
    }

    //删除所有文档对象
    public void deleteAllIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }

    //根据条件精准删除
    public void deleteOneIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Query query = new TermQuery(new Term("fileName", "spring.txt"));
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    //修改,先删除，再存储
    public void updateIndex() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Document document = new Document();
        document.add(new TextField("filedC", "测试内容", Field.Store.YES));
        document.add(new TextField("filedN", "测试名字", Field.Store.YES));
        indexWriter.updateDocument(new Term("fileName", "springmvc.txt"), document, new IKAnalyzer());
    }

    public IndexSearcher getIndexSearcher() throws Exception {
        //第一步，创建一个directory对象，即索引库存放位置
        Directory directory = FSDirectory.open(new File("D://temp//index"));
        //第二步，创建一个indexReader对象，需要制定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //第三步，创建一个indexsearch对象，需要指定IndexReader对象
        return new IndexSearcher(indexReader);
    }

    public void printResult(IndexSearcher indexSearcher, Query query) throws Exception {
        //第五步，执行查询
        TopDocs topDocs = indexSearcher.search(query, 2);
        //第六步，返回查询结果，遍历查询结果并输出
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;//查询的是id
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;//遍历获得每一个文档的id
            Document document = indexSearcher.doc(doc);
            //文件名字
            String fileName = document.get("fileName");
            System.out.println(fileName);
            //文件大小
            String fileSize = document.get("fileSize");
            System.out.println(fileSize);
            //文件路径
            String filePath = document.get("filePath");
            System.out.println(filePath);
            //文件内容
            String fileContent = document.get("fileContent");
            System.out.println(fileContent);
        }
    }

    //查询所有
    public void findMatchAllDocsQUery() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new MatchAllDocsQuery();
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }
    //根据数值范围查询
    public void findNumericRangeQuery() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = NumericRangeQuery.newLongRange("fileSize",200l,400l,true,true);
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }
    //组合查询
    public void findBooleanQuery() throws  Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        BooleanQuery booleanQuery=new BooleanQuery();
        Query query1 =new TermQuery(new Term("fileName","java"));
        Query query2=new TermQuery(new Term("fileName","springmvc"));
        //Occur.MUST表示必须满足该查询条件
        booleanQuery.add(query1, BooleanClause.Occur.MUST);
        //Occur.SHOULD表示可有可无
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
                printResult(indexSearcher, booleanQuery);
        indexSearcher.getIndexReader().close();
    }
    //解析查询
    public void findQueryParser() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        //设置一个默认值，默认解析器
        QueryParser queryParser=new QueryParser("fileName",new IKAnalyzer());
        //当指定域名之后，默认的域名将会被覆盖
        Query query=queryParser.parse("fileName:java AND filename:spring");
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }

    //解析查询，多个默认域
    public void findMulitiFiledQueryParser() throws Exception{
        IndexSearcher indexSearcher = getIndexSearcher();
        //设置一个默认值，默认解析器
        String[] fields={"filename","fileContent"};
        MultiFieldQueryParser multiFieldQueryParser=new MultiFieldQueryParser(fields,new IKAnalyzer());
        Query query=multiFieldQueryParser.parse("spring");
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();

    }
}
