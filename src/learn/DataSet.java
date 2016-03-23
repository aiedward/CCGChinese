/***************************  LICENSE  *******************************
* This file is part of UBL.
* 
* UBL is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as 
* published by the Free Software Foundation, either version 3 of the 
* License, or (at your option) any later version.
* 
* UBL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public 
* License along with UBL.  If not, see <http://www.gnu.org/licenses/>.
***********************************************************************/



package learn;

import java.util.*;
import java.io.*;

import lambda.*;
import parser.*;

public class DataSet {


	public DataSet(String filename){
		sents = new LinkedList();  //sentences
		sems = new LinkedList();  //semantics

		try{
			// open the file
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line = "";
			Chart c;
			int count=0;

			while (line!=null){
				// read sentence
				line = "";
				while (line!=null && (line.equals("") || line.startsWith("//"))){
					line = in.readLine();
					if (line!=null)
						line = line.trim();
				}

				if (line!=null && !line.equals("") && !line.startsWith("//")){
					sents.add(line);

					count++;

					// read semantics
					line = in.readLine();
					if (line!=null){
						//System.out.println(count+": "+line);
						Exp e = Exp.makeExp(line);
						e.simplify();
						if (e==null || !e.wellTyped()){
							System.err.println("ERROR: Mistyped Exp in DataSet at"+
									" example # "+count+
									"\nSENT: "+sents.get(sents.size()-1)+
									"\nINPUT: "+line);
							if (e!=null)
								System.out.println(e);
							System.exit(-1);
						}
						sems.add(e);
					}

				}
			}
		} catch (IOException e){ System.err.println(e); };	
	}

	
	public DataSet(){
		sems = new LinkedList();
		sents = new LinkedList();
	}

	public String toString(){
		StringBuffer result = new StringBuffer();
		for (int i=0; i<sents.size(); i++){
			result.append(sents.get(i).toString()).append("\n");
			result.append(sems.get(i).toString()).append("\n\n");
		}	
		return result.toString();
	}

	public int size(){
		return sents.size();
	}

	public String sent(int i){
		return (String)sents.get(i);
	}

	public Exp sem(int i){
		return (Exp)sems.get(i);
	}


	public void copy(DataSet d){
		sems.clear();
		sems.addAll(d.sems);
		sents.clear();
		sents.addAll(d.sents);
	}

	// set entries used when pruning the lexicon
	public void setBestLex(int i, List<LexEntry> lex){
		bestEntries.set(i,lex);
	}

	public List<LexEntry> getBestLex(int i){
		return bestEntries.get(i);
	}

	public Set<LexEntry> getBestLex(){
		Set<LexEntry> result = new LinkedHashSet<LexEntry>();
		for (List<LexEntry> lex : bestEntries)
			result.addAll(lex);
		return result;
	}
	
	List<List<LexEntry>> bestEntries;

	// make a lexical entry for each sentence
	public List<LexEntry> makeSentEntries(){
		bestEntries = new LinkedList<List<LexEntry>>();
		List<LexEntry> result = new LinkedList<LexEntry>();
		for (int i=0; i<sems.size(); i++){
			LexEntry l = new LexEntry(sent(i),"S : "+sem(i));
			result.add(l);
			List<LexEntry> b = new LinkedList<LexEntry>();
			b.add(l);
			bestEntries.add(b);
		}
		return result;
	}
	
	//split the data set into n folds
	public void splitFolds(int folds) {
		int foldSize = size() / folds;
		String foldName = "";
		
		//clear the data/fold directory
		String path = "data/folds";
		File file = new File(path);
		if (! file.isDirectory()) {
			return;
		}
		
		String[] tempList = file.list();
	    File temp = null, tmp = null;
	    for (int i = 0; i < tempList.length; i++) {
	    	temp = new File(path + File.separator + tempList[i]);
	    	if (temp.isFile()) {
	    		temp.delete();
	    	} else if (temp.isDirectory()) {
	    		String[] tmplist = temp.list();
	    		for (int kk=0; kk<tmplist.length; kk++) {
	    			tmp = new File(path + File.separator + temp + File.separator + tmplist[kk]);
	    			tmp.delete();
	    		}
	    	}
	    }
		
		//split the whole dataset and print into files
		for (int i=0; i<folds; i++) {
			try {
				foldName = path + File.separator + "fold" + i;
				File foldDir = new File(foldName);
				foldDir.mkdir();
				BufferedWriter trainOut = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(foldName + File.separator + "train"),"UTF-8"));
				BufferedWriter testOut = new BufferedWriter(new OutputStreamWriter
						(new FileOutputStream(foldName + File.separator + "test"),"UTF-8"));
				//the last fold
				/*if (i == folds - 1) {
					for (int j=0; j<foldSize*i; j++) {
						trainOut.write(sent(j));
						trainOut.write("\n");
						trainOut.write(sem(j).toString());
						trainOut.write("\n\n");
					}
					for (int j=foldSize*i; j<size(); j++) {
						testOut.write(sent(j));
						testOut.write("\n");
						testOut.write(sem(j).toString());
						testOut.write("\n\n");
					}
				} else {*/
					for (int j=0; j<foldSize*i; j++) {
						trainOut.write(sent(j));
						trainOut.write("\n");
						trainOut.write(sem(j).toString());
						trainOut.write("\n\n");
					}
					for (int j=foldSize*i; j<foldSize*(i+1); j++) {
						testOut.write(sent(j));
						testOut.write("\n");
						testOut.write(sem(j).toString());
						testOut.write("\n\n");
					}
					for (int j=foldSize*(i+1); j<size(); j++) {
						trainOut.write(sent(j));
						trainOut.write("\n");
						trainOut.write(sem(j).toString());
						trainOut.write("\n\n");
					}
				//}
				trainOut.close();
				testOut.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){

	}


	List sents;
	List sems;



}
