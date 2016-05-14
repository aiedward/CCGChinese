package KB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import parser.Globals;
import parser.LexEntry;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;
import learn.*;
import lambda.*;
import parser.*;

public class query {
	
	private HashMap<String, String> pMap;
	public static String PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
								+ "PREFIX restruant: <http://csaixyz.org/restraunt#> \n"
								+ "PREFIX dish: <http://dishname/> \n"
								+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n";

	private String convertSingleConst(Const exp, String x) {
		////System.out.println("convertSingleConst" + exp);
		String argg = exp.toString();
		String argType = argg.substring(argg.indexOf(":")+1, argg.length());
		argg = argg.substring(0, argg.indexOf(":"));
		if (argType.equals("r")) {
			String res = "?" + x + " restruant:name ?name . \n"
					   + "FILTER regex(?name, \"" + argg + "\" ) . \n";
			return res;
		}
		else if (argType.equals("c")) {
			String res = "?" + x + " dish:dishname ?dname . \n"
					   + "FILTER regex(?dname, \"" + argg + "\" ) . \n";
			return res;
		}
		return "Unresolved Const Type: " + argType + "\n";
	}
	
	private String convertSingleLit(Lit exp) {
		//System.out.println("convertSingleLit" + exp);

		String pred = exp.getHeadString();
		if (!pMap.containsKey(pred)) {
			return "NO Pred: " + pred + " in KB\n";
		}
		int argNum = exp.arity();
		if (argNum != 2) return "ERROR: " + exp.toString() + " \n";
		Exp arg = exp.getArg(1);
		if (!arg.getClass().toString().equals("class lambda.Const")) {
			return "Arg: " + arg + " is not Const\n";
		}
		String argg = arg.toString();
		String argType = argg.substring(argg.indexOf(":")+1, argg.length());
		argg = argg.substring(0, argg.indexOf(":"));
		if (argType.equals("s")) {
			String p = pMap.get(pred);
			String xx = "x" + Math.abs(p.hashCode());
			String res = "?s " + p + " ?" + xx + " . \n"
					   + "FILTER regex(?" + xx + ", \"" + argg + "\" ) . \n";
			return res;
		}
		else {
			String p = pMap.get(pred);
			String xx = "x" + Math.abs(p.hashCode());
			String res = "?s " + p + " ?" + xx + " . \n"
					   + convertSingleConst((Const)arg, xx);
			return res;		
		}
	}
	
	private String convertSingleIntBoolOps(IntBoolOps exp) {
		//System.out.println("convertSingleIntBoolOps" + exp);

		String op = exp.getHeadString();
		Exp left = exp.getLeft();
		Exp right =exp.getRight();
		String rt = right.toString().substring(0, right.toString().indexOf(":"));
		
		String pred = left.getHeadString();
		if (!pMap.containsKey(pred))	return "NO Pred: " + pred + " in KB\n";
		String p = pMap.get(pred);
		String xx = "x" + Math.abs(p.hashCode());
		String res = "?s " + p + " ?" + xx + " . \n"
				   + "FILTER (xsd:float(?" + xx + ") " + op + " " + rt + ") . \n";
		return res;
	}
	
	private String convertSingleExp(Exp exp) {
		if (exp.toString().startsWith("(restaurant:t")) return "";
		if (exp.getClass().toString().equals("class lambda.Lit")) {
			return convertSingleLit((Lit)exp);
		}else if (exp.getClass().toString().equals("class lambda.IntBoolOps")) {
			return convertSingleIntBoolOps((IntBoolOps)exp);
		}
		return "ERROR: " + exp.toString() + " \n";
	}
	
	private String convertLit(Lit exp) {
		String pred = exp.getHeadString();
		if (!pMap.containsKey(pred)) {
			return "NO Pred: " + pred + " in KB\n";
		}
		String p = pMap.get(pred);
		int argNum = exp.arity();
		if (argNum == 1) {
			Exp arg = exp.getArg(0);
			////System.out.println(arg.getClass().toString());
			if (!arg.getClass().toString().equals("class lambda.Const")) {
				return "Arg: " + arg + " is not Const\n";
			}
			String argg = arg.toString();
			String argType = argg.substring(argg.indexOf(":")+1, argg.length());
			argg = argg.substring(0, argg.indexOf(":"));
			if (argType.equals("r")) {
				String res = PREFIX
						  + "SELECT ?s ?name ?o \n"
						  + "WHERE \n"
						  + "{ \n"
						  + "?s " + p + " ?o . \n"
						  + convertSingleConst((Const)arg, "s")
						  + "} \n";
				return res;
			}
			else if (argType.equals("c")) {
				String res = PREFIX
						  + "SELECT ?s ?name ?o \n"
						  + "WHERE \n"
						  + "{ \n"
						  + "?s dish:dish ?d . \n"
						  + convertSingleConst((Const)arg, "d")
						  + "?d " + p + " ?o . \n"
						  + "?s restruant:name ?name . \n"
						  + "} \n";
				return res;
			}
			return "Unknown Const Type: " + argType + "\n";
		}
		else if (argNum == 2) {
			Exp arg0 = exp.getArg(0);
			Exp arg1 = exp.getArg(1);
			if (!arg0.getClass().toString().equals("class lambda.Const")) {
				return "Arg: " + arg0 + " is not Const\n";
			}
			if (!arg1.getClass().toString().equals("class lambda.Const")) {
				return "Arg: " + arg1 + " is not Const\n";
			}
			String res = PREFIX
					  + "SELECT ?s ?name \n"
					  + "WHERE \n"
					  + "{ \n"
					  + "?s " + p + " ?o . \n"
					  + convertSingleConst((Const)arg0, "s")
					  + convertSingleConst((Const)arg1, "o")
					  + "} \n";
			return res;
		}
		
		return "Exp: " + exp + " has more than two arg\n";

	}
	
	private String convertFunct(Funct exp) {
		Exp body = exp.getBody();
		if (body.getClass().toString().equals("class lambda.BoolBoolOps")) {
			BoolBoolOps bd = (BoolBoolOps)body;
			// (and
			if (bd.getOP() == BoolBoolOps.CONJ) {
				String res = PREFIX
						  + "SELECT ?s ?name \n"
						  + "WHERE \n"
						  + "{ \n";
				List<Exp> exps = bd.getExps();
				for (Exp e : exps) {
					res += convertSingleExp(e);
				}
				res = res + "?s restruant:name ?name . \n"
						  + "} \n";
				return res;
			}
			return "Unresolved Bool Op\n";
		}
		return "ERROR\n";
	}
	
	public String convert(Exp exp) {
		String res = "";
		//System.out.println(exp.getClass().toString());
		if (exp.getClass().toString().equals("class lambda.Lit")) {
			return convertLit((Lit)exp);
		}
		else if (exp.getClass().toString().equals("class lambda.Funct")) {
			return convertFunct((Funct)exp);
		}
		//System.out.print(res);
		return res;
	}
	
	public String convert(String sem) {
		Exp exp = Exp.makeExp(sem);
		return convert(exp);
	}
	
	public String execute(String spq) {
		if (!spq.startsWith("PREFIX")) return "ERROR\n";
		//System.out.println(spq);
		String KBresult = "";
		VirtGraph graph = new VirtGraph ("restrauntknowledge", "jdbc:virtuoso://localhost:1111", "dba", "dba");
		Query sparql = QueryFactory.create(spq);
		////System.out.println(sparql.getResultVars());
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);

		ResultSet results = vqe.execSelect();
		String res;
		int nullNum = 0;
		while (results.hasNext()) {
			nullNum = 0;
			QuerySolution result = results.nextSolution();
		    //RDFNode graph_name = result.get("graph");
		    res = " { ";
		    for (int i=0; i<sparql.getResultVars().size(); i++) {
		    	String tmp = result.get(sparql.getResultVars().get(i)).toString();
		    	if (tmp.equals("") || tmp.equals("-")) {
		    		nullNum ++;
		    		break;
		    	}
		    	res += tmp + " ";
		    }
		    res += "}\n";
		    if (nullNum == 0 ) {
		    	//System.out.print(res);
		    	KBresult += res;
		    }

		}
		return KBresult;
	}
	
	public query() {
		pMap = new HashMap<String, String>();
		try {
			BufferedReader fin = new BufferedReader(new FileReader("configure/p2KB"));
			String line = fin.readLine();
			String key, value;
			while (line != null) {
				line.trim();
				if (line == "") {
					line = fin.readLine();
					continue;
				}
				key = line.split(" ")[0];
				value = line.split(" ")[1];
				if (!pMap.containsKey(key)) {
					pMap.put(key, value);
				}
				line = fin.readLine();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PType.addTypesFromFile("data/types");
		Lang.loadLangFromFile("data/relations");
		
		query qr = new query();
		String spq = qr.convert("(hasCuisine:t 麻辣诱惑:r 麻辣小龙虾:c)");
		System.out.println(spq);
		qr.execute(spq);
	}

}
