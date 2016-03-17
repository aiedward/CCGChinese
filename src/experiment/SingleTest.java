/************************
 * @author: Zhang Ye
 ************************/

package experiment;

import java.io.File;

import learn.*;
import lambda.*;
import parser.*;

public class SingleTest {
	
	public static void main(String[] args) {
		String words = "雍和宫附近有哪些餐厅";
		
		PType.addTypesFromFile("data/types");
		Lang.loadLangFromFile("data/relations");
		
		LexiconFeatSet.initWeightMultiplier = 10.0;
		LexiconFeatSet.initLexWeight = 10.0;
		Parser.pruneN=200;
		
		Parser p = new Parser();
		//load lexicon
		String LexFileName = "configure/final_lexicon";
		File LexFile = new File(LexFileName);
		if (!LexFile.exists()) {
			System.out.println("ERROR: No configure file!");
			return;
		}
		p.loadLexiconFromFile(LexFileName);
		p.setGlobals();
		System.out.println("after load lexicon");
		//p.returnLex().printLexiconWithWeightsToFile();
		
		p.parseTimed(words,null,null);
		Exp best = p.bestSem();
		
		System.out.println(best);	
		
	}

}

