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
	
	private static String districtFile = "configure/np_district";
	private static String zoneFile = "configure/np_zone";
	private static String labelFile = "configure/np_label";
	private static String restaurantFile = "configure/np_restaurant";
	private static String dishFile = "configure/np_dish";

	private VirtGraph graph;
	HashSet<String> district;
	HashSet<String> zone;
	HashSet<String> resName;
	HashSet<String> cat;
	HashSet<String> dishName;
	
	private int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	
	private void extract2file(String p, String type, HashSet<String> set, String File) {
				
		clearFile(File);
		
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
			FileWriter resOut = new FileWriter(File, true);     
			
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
			    if (obj.contains("/")) {
			    	int index = obj.indexOf("/");
			    	String obj0 = obj.substring(0, index);
			    	if (!set.contains(obj0)) {
				    	set.add(obj0);
				    	resOut.write(obj0 + " :- NP : " + obj0 + ":" + type + "\n");
				    }
			    	String obj1 = obj.substring(index+1, obj.length());
			    	if (!set.contains(obj1)) {
				    	set.add(obj1);
				    	resOut.write(obj1 + " :- NP : " + obj1 + ":" + type + "\n");
				    }
			    }
			    else {
			    	if (!set.contains(obj)) {
				    	set.add(obj);
				    	resOut.write(obj + " :- NP : " + obj + ":" + type + "\n");
				    }
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
		extract2file("restruant:district", "dt", district, districtFile);
		extract2file("restruant:zone", "zn", zone, zoneFile);
		extract2file("restruant:name", "r", resName, restaurantFile);
		extract2file("restruant:category", "lb", cat, labelFile);
		extract2file("dish:dishname", "c", dishName, dishFile);
		return;
	}
		
	
	public void clearFile(String npFile) {
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
		return;
		
	}
	
	public extractNP(){
		district = new HashSet<String>();
		zone = new HashSet<String>();
		resName = new HashSet<String>();
		cat = new HashSet<String>();
		dishName = new HashSet<String>();
		
		
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
