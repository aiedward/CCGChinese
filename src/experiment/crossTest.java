package experiment;

import java.io.File;
import java.util.*;

import learn.*;
import lambda.*;
import parser.*;

public class crossTest {

	public static void main(String[] args) {
		PType.addTypesFromFile("data/types");
		Lang.loadLangFromFile("data/relations");
		
		DataSet whole = new DataSet("data/train");
		System.out.println("Whole Size: " + whole.size());
		
		int folds = 10;
		int foldSize = whole.size() / folds;
		
		whole.splitFolds(folds);
		System.out.println("split finish");
		
		//cross test
		String train = "";
		String test = "";
		for (int i=0; i<folds; i++) {
			System.out.println("fold " + i);
			train = "data/folds/fold" + i + "/train";
			test = "data/folds/fold" + i + "/test";
			DevTrain.trainAndTest(train, test);
		}
	}

}
