package org.lucene_uk.test;


import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;

public class LuceneTest {

	public static void main(String[] args) throws Exception {
        Analyzer analyzer = new UkrainianMorfologikAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        
        Document doc = new Document();
        String text = "Голова є важливим предметом для важливої людини.";
        doc.add(new TextField("Content", text, Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.close();
        
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Assert.assertEquals(1, query(analyzer, indexSearcher, "важливий"));

        Assert.assertEquals(1, query(analyzer, indexSearcher, "важливим"));
        
        Assert.assertEquals(1, query(analyzer, indexSearcher, "голова"));

        Assert.assertEquals(0, query(analyzer, indexSearcher, "гол"));
	}

	private static int query(Analyzer analyzer, IndexSearcher indexSearcher, String queryText) throws ParseException, IOException {
		QueryParser parser = new QueryParser( "Content", analyzer);
        Query query = parser.parse(queryText);
        
        int hitsPerPage = 10;
        TopDocs docs = indexSearcher.search(query, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        int end = Math.min(docs.totalHits, hitsPerPage);
        
        System.out.println("Total Hits: " + docs.totalHits + " for " + queryText);
        System.out.print("Results: ");
        
        for (int i = 0; i < end; i++) {
            Document d = indexSearcher.doc(hits[i].doc);
            System.out.println("Content: " + d.get("Content"));
            System.out.println("Score: " + hits[i].score);
        }
        
        return docs.totalHits;
	}

}
