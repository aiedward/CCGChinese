package KB;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import parser.Globals;
import parser.LexEntry;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;

import learn.*;
import lambda.*;
import parser.*;

public class extractNP {
	
	private static String npFile = "configure/np_lexicon";
	private VirtGraph graph;
	HashSet<String> district;
	HashSet<String> zone;
	HashSet<String> resName;
	HashSet<String> cat;
	HashSet<String> dishName;
	
	private int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	
	private void extract2file(String p, String type, HashSet<String> set) {
				
		Query sparql = QueryFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
										+ "PREFIX restruant: <http://csaixyz.org/restraunt#> "
										+ "PREFIX dish: <http://dishname/> "
										+ "SELECT ?s ?o "
										+ "WHERE "
										+ "{ "
										+ "?s " + p + " ?o . "
										+ "} ");

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);

		ResultSet results = vqe.execSelect();
		try {
			System.out.println("Start printing " + p);
			FileWriter resOut = new FileWriter(npFile, true);     
			
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
			    RDFNode graph_name = result.get("graph");
			    RDFNode s = result.get("s");
			    RDFNode o = result.get("o");
			    String obj = o.toString();
			    //System.out.println(obj);
			    if (p.equalsIgnoreCase("restruant:name")) {
			    	int index0 = obj.indexOf("(");
			    	int index1 = obj.indexOf("（");
			    	int index;
				    if (index0 >= 0 && index1 >= 0) {
				    	index = min(index0, index1);
				    	obj = obj.substring(0, index);
				    } else if (index0 >= 0 || index1 >= 0) {
				    	if (index0 >= 0) {
				    		index = index0;
				    	} else {
				    		index = index1;
				    	}
				    	obj = obj.substring(0, index);
				    }
			    }
			    obj.trim();

			    if (obj.equals("") || obj == null || obj.contains(":")) continue;
			    if (!set.contains(obj)) {
			    	set.add(obj);
			    	resOut.write(obj + " :- NP : " + obj + ":" + type + "\n");
			    }
			}
			resOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Finish " + p);
	}
	
	public void extract2file() {
		extract2file("restruant:district", "dt", district);
		extract2file("restruant:zone", "zn", zone);
		extract2file("restruant:name", "nm", resName);
		extract2file("restruant:category", "lb", cat);
		extract2file("dish:dishname", "c", dishName);
		return;
	}
	
	
	private void extract2lex(String p, String type, HashSet<String> set, Lexicon lex) {
		
				
		Query sparql = QueryFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
										+ "PREFIX restruant: <http://csaixyz.org/restraunt#> "
										+ "PREFIX dish: <http://dishname/> "
										+ "SELECT ?s ?o "
										+ "WHERE "
										+ "{ "
										+ "?s " + p + " ?o . "
										+ "} ");

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);

		ResultSet results = vqe.execSelect();
		try {
			System.out.println("Start extracting " + p);
			FileWriter resOut = new FileWriter(npFile, true);     
			
			while (results.hasNext()) {
				QuerySolution result = results.nextSolution();
			    RDFNode graph_name = result.get("graph");
			    RDFNode s = result.get("s");
			    RDFNode o = result.get("o");
			    String obj = o.toString();
			    //System.out.println(obj);
			    if (p.equalsIgnoreCase("restruant:name")) {
			    	int index0 = obj.indexOf("(");
			    	int index1 = obj.indexOf("（");
			    	int index;
				    if (index0 >= 0 && index1 >= 0) {
				    	index = min(index0, index1);
				    	obj = obj.substring(0, index);
				    } else if (index0 >= 0 || index1 >= 0) {
				    	if (index0 >= 0) {
				    		index = index0;
				    	} else {
				    		index = index1;
				    	}
				    	obj = obj.substring(0, index);
				    }
			    }
			    obj.trim();
			    
			    if (obj.equals("") || obj == null || obj.contains(":")) continue;
			    
			    // make a new LexEntry by splitting the string
				LexEntry le = new LexEntry(obj, "NP : " + obj + ":" + type);
				le.setDomainSpecific(true);
				le.loaded=true;
				lex.addLexEntry(le);
			}
			resOut.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Finish extract " + p);
	}
	
	public void extract2lex(Lexicon lex) {
		extract2lex("restruant:district", "dt", district, lex);
		extract2lex("restruant:zone", "zn", zone, lex);
		extract2lex("restruant:name", "r", resName, lex);
		extract2lex("restruant:category", "lb", cat, lex);
		extract2lex("dish:dishname", "c", dishName, lex);
		return;
	}
	
	public extractNP(){
		district = new HashSet<String>();
		zone = new HashSet<String>();
		resName = new HashSet<String>();
		cat = new HashSet<String>();
		dishName = new HashSet<String>();
		
		File npout = new File(npFile);
		if (npout.exists()) {
			npout.delete();
		}
		try {
			npout.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		graph = new VirtGraph ("restrauntknowledge", "jdbc:virtuoso://localhost:1111", "dba", "dba");
		//System.out.println("graph.isEmpty() = " + graph.isEmpty());
		//System.out.println("graph.getCount() = " + graph.getCount());
		
		return;
		
	}

	void printSize(){
		System.out.println("districts size = " + district.size());
		System.out.println("zone size = " + zone.size());
		System.out.println("restaurant size = " + resName.size());
		System.out.println("category size = " + cat.size());
		System.out.println("dish size = " + dishName.size());
	}
	
	public static void main(String[] args) {
		
		extractNP ext = new extractNP();
		ext.extract2file();
		
		ext.printSize();
	}

}
