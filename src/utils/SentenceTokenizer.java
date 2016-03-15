/*
 * @author: Zhang Ye
 * tokenize sentence of English and Chinese in different ways
 */
package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

public class SentenceTokenizer {
	
	public SentenceTokenizer(String input) {
		tokens = new LinkedList();
		
		//parse with whitespace (e.g. English)
		Pattern pattern = Pattern.compile("[a-zA-Z]");
		Matcher matcher = pattern.matcher(input.substring(0, 1));
		if (matcher.matches()) {
			StringTokenizer st = new StringTokenizer(input);
			while (st.hasMoreTokens()){
				tokens.add(st.nextToken());
			}
		}
		
		//Chinese
		else {
		
			//parse with character (e.g. chinese)
			/*for (int ii=0; ii<input.length(); ii++) {
				tokens.add(input.charAt(ii));
				//System.out.println("add token: " + input.charAt(ii));
			}*/
			
			//Chinese. parse after segmentation
			/**
			 * four segmentation methods
			 * BaseAnalysis.parse(input);
			 * ToAnalysis.parse(input)
			 * NlpAnalysis.parse(input)
			 * IndexAnalysis.parse(input)
			 * return type List<Term>
			 */
			List<Term> myTokens= NlpAnalysis.parse(input);
			String tmp;
			for (Term tt : myTokens) {
				tmp = tt.toString();
				int tmpIndex = tmp.indexOf("/");
				if (tmpIndex != -1) tmp = tmp.substring(0, tmpIndex);
				if (tmp.equalsIgnoreCase("，")) continue;
				tokens.add(tmp);
				//System.out.print(tmp + " ");
			}
			//System.out.println();
	
			//System.out.println(tokens);
		}
	}
	
	public List getTokens() {
		return tokens;
	}
	
	private List tokens;
}
