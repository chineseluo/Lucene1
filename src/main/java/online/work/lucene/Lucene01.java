package online.work.lucene;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


import java.io.File;


/**
 * Created by 84825 on 2018/2/5.
 */
public class Lucene01 {
//创建索引
    public void testIndex() throws Exception {
        //第一步，创建一个indexWriter对象
        //（1）制定索引库存放位置Directory对象
        Directory directory = FSDirectory.open(new File("D://temp//index"));
        //（2）制定一个分析器，对文档内容进行分析
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        //第三步，创建field对象，将field添加到document对象中去

        File file = new File("D:\\search");
        File[] files = file.listFiles();
        for (File file1 : files) {
            //第二步，创建document对象
            Document document = new Document();
            //文件名称
            String file_name = file1.getName();
            Field fileNameField = new TextField("fileName", file_name, Field.Store.YES);

            //文件大小
            Long file_size = FileUtils.sizeOf(file1);
            Field fileSizeFiled = new LongField("fileSize", file_size, Field.Store.YES);
            //文件路劲
            String file_path = file.getPath();
            Field filePathField = new StoredField("filePath", file_path);
            //文件内容
            String file_content = FileUtils.readFileToString(file1);
            Field fielContentField = new TextField("fileContent", file_content, Field.Store.YES);
            document.add(fileNameField);
            document.add(filePathField);
            document.add(fileSizeFiled);
            document.add(fielContentField);
            //第四步，使用indexWriter对象将document对象写入索引库中，此过程中进行索引创建，并将索引和docuemnt对象写入索引库
            indexWriter.addDocument(document);

        }
        //第五步，关闭indexWriter对象
        indexWriter.close();
    }

    //查询索引
    public void searchindex() throws  Exception{
        //第一步，创建一个directory对象，即索引库存放位置
        Directory directory = FSDirectory.open(new File("D://temp//index"));
        //第二步，创建一个indexReader对象，需要制定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
        //第三步，创建一个indexsearch对象，需要指定IndexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //第四步，创建一个TermQuery对象，需要指定查询的域和查询的值
        Query query = new TermQuery(new Term("fileName","springmvc.txt"));
        //第五步，执行查询
        TopDocs topDocs = indexSearcher.search(query,2);
        //第六步，返回查询结果，遍历查询结果并输出
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;//查询的是id
        for(ScoreDoc scoreDoc:scoreDocs){
            int doc=scoreDoc.doc;//遍历获得每一个文档的id
            Document document = indexSearcher.doc(doc);
            //文件名字
            String fileName=document.get("fileName");
            System.out.println(fileName);
            //文件大小
            String fileSize = document.get("fileSize");
            System.out.println(fileSize);
            //文件路径
            String filePath=document.get("filePath");
            System.out.println(filePath);
            //文件内容
            String fileContent=document.get("fileContent");
            System.out.println(fileContent);

        }
        //第七步，关闭IndexReader对象
        indexReader.close();
    }
}
