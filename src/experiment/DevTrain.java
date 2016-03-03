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
package experiment;

import learn.*;
import lambda.*;
import parser.*;

public class DevTrain extends Train {

	public static void main(String[] args){

	PType.addTypesFromFile("data/types");
	Lang.loadLangFromFile("data/relations");

	/*int runNum = Integer.parseInt(args[0]);
	int splitNum = Integer.parseInt(args[1]);
	String language = args[2];*/

	DataSet train = new DataSet("data/train");
	System.out.println("Train Size: "+train.size());
	System.out.println();

	DataSet test = new DataSet("data/train");
	System.out.println("Test Size: "+test.size());
	System.out.println();

    //Initialize parameters
	/*String giza_probs = "data/"+language+"/run-"+runNum+"/fold-"+splitNum+"/w-c.giza_probs";
	LexiconFeatSet.loadCoOccCounts(giza_probs);*/

	//fixed np lexicon
    /*String fixedlex =null;
	fixedlex =  language+"-np-fixedlex.geo";*/

	Lexicon fixed = new Lexicon();
	//fixed.addEntriesFromFile(fixedlex,true);

	Train.EPOCHS=10;
	Train.alpha_0 = 1.0;
	Train.c = 0.00001;
	Train.maxSentLen=50;

	LexiconFeatSet.initWeightMultiplier = 10.0;
	LexiconFeatSet.initLexWeight = 10.0;
	Parser.pruneN=200;

	System.out.println("alpha_0 = "+Train.alpha_0);
	System.out.println("C = "+Train.c);
	System.out.println("initialMultiplier = "+LexiconFeatSet.initWeightMultiplier);
	System.out.println("NP init = "+LexiconFeatSet.initLexWeight);
	System.out.println("Parser beam  = "+Parser.pruneN);
	System.out.println();

	Parser p = new Parser(fixed);
	p.makeFeatures();

	System.out.println("Start train");
	Train t = new Train();
	t.setDataSet(train);
	t.setTestSet(test);

	Train.verbose = true;
	
	System.out.println("Start training...");
	t.stocGradTrain(p,true);
	
	System.out.println("Print lexicon...");
	t.printLex(p.returnLex().getLexicon());

	System.out.println("Start testing...");
	t.test(p,Train.pruneLex);

	}
}


