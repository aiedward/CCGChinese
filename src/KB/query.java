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

	private String convertLit(Lit exp) {
		String pred = exp.getHeadString();
		if (!pMap.containsKey(pred)) {
			return "NO Pred: " + pred + " in KB\n";
		}
		String p = pMap.get(pred);
		int argNum = exp.arity();
		if (argNum != 1) {
			return "Exp: " + exp + " has more than one arg\n";
		}
		Exp arg = exp.getArg(0);
		System.out.println(arg.getClass().toString());
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
					  + "?s restruant:name ?name . \n"
					  + "FILTER regex(?name, \"" + argg + "\" ) . \n"
					  + "} \n";
			return res;
		}
		else if (argType.equals("c")) {
			String res = PREFIX
					  + "SELECT ?s ?name ?o \n"
					  + "WHERE \n"
					  + "{ \n"
					  + "?s dish:dish ?d . \n"
					  + "?d dish:dishname ?dname . \n"
					  + "FILTER regex(?dname, \"" + argg + "\" ) . \n"
					  + "?d " + p + " ?o . \n"
					  + "?s restruant:name ?name . \n"
					  + "} \n";
			return res;
		}
		return "Unknown Const Type: " + argType + "\n";
	}
	
	private String convertSingleLit(Lit exp) {
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
		if (!argType.equals("s")) return "Unknown Const Type: " + argType + "\n";
		
		String p = pMap.get(pred);
		String xx = "x" + Math.abs(p.hashCode());
		String res = "?s " + p + " ?" + xx + " . \n"
				   + "FILTER regex(?" + xx + ", \"" + argg + "\" ) . \n";
		return res;
	}
	
	private String convertSingleIntBoolOps(IntBoolOps exp) {
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
		System.out.println(exp.getClass().toString());
		if (exp.getClass().toString().equals("class lambda.Lit")) {
			return convertLit((Lit)exp);
		}
		else if (exp.getClass().toString().equals("class lambda.Funct")) {
			return convertFunct((Funct)exp);
		}
		System.out.print(res);
		return res;
	}
	
	public String convert(String sem) {
		Exp exp = Exp.makeExp(sem);
		return convert(exp);
	}
	
	public String execute(String spq) {
		if (!spq.startsWith("PREFIX")) return "ERROR\n";
		String KBresult = "";
		VirtGraph graph = new VirtGraph ("restrauntknowledge", "jdbc:virtuoso://localhost:1111", "dba", "dba");
		Query sparql = QueryFactory.create(spq);
		//System.out.println(sparql.getResultVars());
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);

		ResultSet results = vqe.execSelect();
		String res;
		int nullNum = 0;
		while (results.hasNext()) {
			nullNum = 0;
			QuerySolution result = results.nextSolution();
		    RDFNode graph_name = result.get("graph");
		    res = graph_name + " { ";
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
		    	System.out.print(res);
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
		String spq = qr.convert("(lambda $0 e (and (restaurant:t $0) (zone:t $0 西单:s) (label:t $0 火锅:s) (> (tasteScore:i $0) 9.0:i) (> (price:i $0) 100:i)))");
		System.out.println(spq);
		qr.execute(spq);
	}

}
