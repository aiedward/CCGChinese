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
		
		for (int beam=200; beam<=200; beam=beam*2) {
		System.out.println("\n\n\n******************************");
		System.out.println("Beam size = " + beam);
		System.out.println("******************************\n");
		
		//cross test
		String train = "";
		String test = "";
		testResult t_res = new testResult();
		testResult d_res = new testResult();
		double t_p = 0, t_r = 0, t_f1 = 0;
		double d_p = 0, d_r = 0, d_f1 = 0;

		for (int i=0; i<folds; i++) {
			System.out.println("fold " + i);
			train = "data/folds/fold" + i + "/train";
			test = "data/folds/fold" + i + "/test";
			DevTrain.trainAndTest(train, test, d_res, t_res, beam);
			t_p += t_res.getPrecision();
			t_r += t_res.getRecall();
			t_f1 += t_res.getF1();
			d_p += d_res.getPrecision();
			d_r += d_res.getRecall();
			d_f1 += d_res.getF1();
		}
		
		t_p = t_p / (double)folds;
		t_r = t_r / (double)folds;
		t_f1 = t_f1 / (double)folds;
		
		d_p = d_p / (double)folds;
		d_r = d_r / (double)folds;
		d_f1 = d_f1 / (double)folds;
		
		System.out.println("=================================");
		System.out.println("Average dev precision : " + d_p);
		System.out.println("Average dev recall : " + d_r);
		System.out.println("Average dev F1 : " + d_f1);
		System.out.println("=================================");
		
		System.out.println("=================================");
		System.out.println("Average test precision : " + t_p);
		System.out.println("Average test recall : " + t_r);
		System.out.println("Average test F1 : " + t_f1);
		System.out.println("=================================");
		
		}

	}

}
