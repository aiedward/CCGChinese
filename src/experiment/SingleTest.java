/************************
 * @author: Zhang Ye
 ************************/

package experiment;

import java.io.File;
import java.util.List;

import learn.*;
import lambda.*;
import parser.*;

public class SingleTest {
	
	private Parser p;
	
	public SingleTest(){
		PType.addTypesFromFile("data/types");
		Lang.loadLangFromFile("data/relations");
		
		LexiconFeatSet.initWeightMultiplier = 10.0;
		LexiconFeatSet.initLexWeight = 10.0;
		Parser.pruneN = 200;
		
		p = new Parser();
		//load lexicon
		String LexFileName = "configure/final_lexicon";
		File LexFile = new File(LexFileName);
		if (!LexFile.exists()) {
			System.out.println("ERROR: No configure file!");
			return;
		}
		p.loadLexiconFromFile(LexFileName);
		System.out.println("load lexicon finish");
	}
	
	public Exp getSem(String words){
		p.parseTimed(words,null,null);
		Exp best = p.bestSem();
		return best;
	}
	
	public Cell getCell() {
		return p.bestParses().get(0).getCell();
	}
	
	public List ParseEntries(Exp sem) {
		return p.getMaxLexEntriesFor(sem);
	}
	
	public static void main(String[] args) {
		String words = "雍和宫附近比较好吃的日本料理， 人均70";
		
		SingleTest test = new SingleTest();
		
		test.getSem(words);
		
	}

}

