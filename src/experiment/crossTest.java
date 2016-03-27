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
		testResult res = new testResult();
		double p = 0, r = 0, f1 = 0;
		for (int i=0; i<folds; i++) {
			System.out.println("fold " + i);
			train = "data/folds/fold" + i + "/train";
			test = "data/folds/fold" + i + "/test";
			res = DevTrain.trainAndTest(train, test);
			p += res.getPrecision();
			r += res.getRecall();
			f1 += res.getF1();
		}
		
		p = p / (double)folds;
		r = r / (double)folds;
		f1 = f1 / (double)folds;
		
		System.out.println("=================================");
		System.out.println("Average precision : " + p);
		System.out.println("Average recall : " + r);
		System.out.println("Average F1 : " + f1);
		System.out.println("=================================");

	}

}
