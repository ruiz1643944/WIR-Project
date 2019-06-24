/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.BooleanQuery;

/** Simple command-line based search demo. */
public class Top_Searcher {
   
	public static void doPagingSearch(IndexSearcher searcher, Query query, PrintWriter writer) throws IOException {

			// Collect enough docs to show 10 pages
			TopDocs results = searcher.search(query, 10);
			ScoreDoc[] hits = results.scoreDocs;
			
			String completePath = "";
			
			for (int i=0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				String path = doc.get("path");
				
				//remove duplicates and obtain a new free duplicates string
				
				completePath += doc.get("title").replace(".txt", "") + " ";
				//writer.print(doc.get("title").replace(".txt", "") + " ");
				System.out.println((i+1) + ". " + path);

			}
			String nuovaStringa = removeDuplicates(completePath);		
			writer.print(nuovaStringa);
	}
	
	//remove duplicates and alphabetically order the string
	private static String removeDuplicates(String stringa) {
		List<String> items = Arrays.asList(stringa.split(" "));
		Collections.sort(items);
		Set<String> s = new LinkedHashSet<>(items);
		String nuovaStringa = s.toString().toLowerCase();
		return nuovaStringa.replace("[", "").replace("]", "").replace(",", "");
	}
	
  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
	    
		String query_string=null;
	    //String index = "D:/Desktop/wir/WIR-Project/Java/index_Marshmello"; //rename index path
	    
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer();
	    
	    QueryParser parser = new QueryParser("contents", analyzer);
	    //String path_short = "D:/Desktop/wir/WIR-Project/Java/Short_Repr_Marshmello"; //rename output path
	    new File(path_short).mkdir();
	    
	    for(int i = 0; i < reader.numDocs();i++) {
	    	Document doc = reader.document(i);
	        String titleID = doc.get("title");
	        if(titleID.equals(".DS_Store")|| titleID.equals(".txt"))
	        	continue;
	        System.out.println(titleID);
	        String contenuto = doc.get("contents").toString();
	        query_string = contenuto;
	        if(query_string.equals("")) continue;
	        BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
	        Query query = parser.parse(query_string);   
		    System.out.println("Searching for: " + query.toString("contents"));
		    PrintWriter writer = new PrintWriter(path_short + "/" +titleID, "UTF-8");
		    doPagingSearch(searcher,query, writer);
		    writer.close();
		    System.out.println();
      
	    }   
	    System.out.println("FINISHED");
	      reader.close();
   }
    
}
