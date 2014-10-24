import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class q5 {
	
	public static HashMap<String, Integer> ruleCounts;
	// nonterminals in form "nonterminal"
	public static HashSet<String> nonterminals;
	// unary rules in form "nonterminal word"
	public static HashSet<String> unaryRules;
	// binary rules in form "src nonterm1 nonterm2"
	public static HashSet<String> binaryRules;
	// words seen
	public static HashSet<String> wordsSeen;
	public static long startTime = System.nanoTime();
	
	public static void main (String[] args) {
		
		String sentenceFile = "parse_dev.dat";
		String countsFile;
		String outputFile;
		
		countsFile = "replaced.counts";
		outputFile = "parse_trees.dat";
		
		if (args.length > 0) {
			String mode = args[0];
			if (mode.equals("vert")) {
				countsFile = "replacedVert.counts";
				outputFile = "parse_trees_vert.dat";
			}
		}
		
		nonterminals = new HashSet<String>();
		unaryRules = new HashSet<String>();
		binaryRules = new HashSet<String>();
		wordsSeen = new HashSet<String>();
		
		ArrayList<String> sentences;
		ArrayList<String> generatedTrees;

		try {
			// Read in the cfg.counts file.
			// Store count(x -> y1 y2), count(x->w) and count(x) in a hashmap.
			ruleCounts = readCounts(countsFile);
			
			// Read in sentences to parse.
			sentences = readSentences(sentenceFile);

			// Use the CKY algorithm to generate trees.
			generatedTrees = generateTrees(sentences);
			
			// Print the final parse trees.
			printParseTrees(generatedTrees, outputFile);
			
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns"); 
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> readSentences (String sentenceFile) throws IOException{
		ArrayList<String> sentences = new ArrayList<String>();
		BufferedReader readSentence =  new BufferedReader(new FileReader(sentenceFile));
		String line = readSentence.readLine();
		while (line != null) {
			sentences.add(line);
			line = readSentence.readLine();
		}
		readSentence.close();
		return sentences;
	}
	
	public static HashMap<String, Integer> readCounts(String countsFile) throws IOException{
		HashMap<String, Integer> counts = new HashMap<String, Integer>();
		BufferedReader readCounts = new BufferedReader(new FileReader(countsFile));
		String line = readCounts.readLine();
		while(line != null) {
			String[] components = line.split(" ");
			int count = Integer.parseInt(components[0]);
			String type = components[1];
			if (type.equals("NONTERMINAL")) {
				counts.put(components[2], count);
				nonterminals.add(components[2]);
			}
			else if (type.equals("UNARYRULE")) {
				// unary rule
				String key = components[2] + " " + components[3];
				wordsSeen.add(components[3]);
				counts.put(key, count);
				unaryRules.add(key);
			}
			else if (type.equals("BINARYRULE")) {
				// binary rule
				String key = components[2] + " " + components[3] + " " + components[4];
				counts.put(key, count);
				binaryRules.add(key);
			}
			line = readCounts.readLine();
		}
		readCounts.close();
		return counts;
	}
	
	public static double computeQ(String rule) {
		String[] components = rule.split(" ");
		String nonterm = components[0];
		
		double ruleCount;
		if (components.length == 2) {
			// You're dealing with a unary rule
			if (!wordsSeen.contains(components[1])) {
				String rareRule = nonterm + " " + "_RARE_";
				ruleCount = ruleCounts.get(rareRule);
			}
			else {
				ruleCount = ruleCounts.get(rule);
			}
		}
		else {
			// You're dealing with a binary rule
			ruleCount = ruleCounts.get(rule);
		}
			
		double nontermCount = ruleCounts.get(nonterm);
		return ruleCount / nontermCount;
	}
	
	public static ArrayList<String> generateTrees(ArrayList<String> sentences) {
		ArrayList<String> generatedTrees = new ArrayList<String>();
		for (String s : sentences) {
			String tree = CKY(s);
			generatedTrees.add(tree);
		}
		return generatedTrees;
	}
	
	public static String CKY (String sentence) {
		// (i, j, X) storage
		HashMap<String, Double> piStorage = new HashMap<String, Double>();
		// bp stores an object array containing [String rule, Integer s] for the max rule and split point.
		HashMap<String, Object[]> bpStorage = new HashMap<String, Object[]>();
		
		// INITIALIZATION
		String[] words = sentence.split(" ");
		int n = words.length;
		for (int i = 1; i <= words.length; i++) {
			for (String nt : nonterminals) {
				String key = i + " " + i + " " + nt;
				double value;
				String unaryLookup;
				
				// Check for rare
				if (wordsSeen.contains(words[i-1])) {
					unaryLookup = nt + " " + words[i-1];
				}
				else {
					unaryLookup = nt + " " + "_RARE_";
				}
				
				if (unaryRules.contains(unaryLookup)) {
					value = computeQ(unaryLookup);
				}
				else {
					value = 0;
				}
				piStorage.put(key, value);
			}
		}
		
		// ALGORITHM
		for (int l = 1; l<n; l++) { //iterate over lengths
			for (int i = 1; i<=n-l; i++) {
				int j = i + l;
				for (String nonterm : nonterminals) {
					// Find the maximum value for pi over all rules and all split points.
					double maxIJX = 0;
					String maxRule = "";
					int maxSplit = -1;
					// Find all binary rules that begin with X.
					ArrayList<String> XsrcBinaryRules = getBinaryRules(nonterm);
					
					// Iterate over rules that begin with X
					for (String rule : XsrcBinaryRules) {
						String Y = rule.split(" ")[1];
						String Z = rule.split(" ")[2];
						// Iterate over all split points
						for (int s = i; s < j; s++) {
							String leftKey = "" + i + " " + s + " " + Y;
							String rightKey = (s+1) + " " + j + " " + Z;
							
							double possibleMaxIJX = computeQ(rule) * piStorage.get(leftKey) * 
									piStorage.get(rightKey);

							// Update best IJX
							if (possibleMaxIJX > maxIJX) {	
								maxIJX = possibleMaxIJX;
								maxRule = rule;
								maxSplit = s;
							}
						}
					}
					String newKey = i + " " + j + " " + nonterm;

					// Store the new max pi value.
					if (getBinaryRules(nonterm) != null) {
						piStorage.put(newKey, maxIJX);
						bpStorage.put(newKey, new Object[] {maxRule, maxSplit});
					}
				}
			}
		}
		String returnKey = 1 + " " + n + " S";
		double piReturn = piStorage.get(returnKey);
		Object[] bpreturn = (bpStorage.get(returnKey));
		
		// Modification to allow the sentence not to start with S
		if (piReturn == 0) {
			double maxPiReturn = 0;
			Object[] maxBpReturn = null;
			for (String nt : nonterminals) {
				String ntKey = 1 + " " + n + " " + nt;
				if (piStorage.get(ntKey) > maxPiReturn) {
					maxPiReturn = piStorage.get(ntKey);
					maxBpReturn = bpStorage.get(ntKey);
				}
			}
			piReturn = maxPiReturn;
			bpreturn = maxBpReturn;
		}
		
		String bpRule = (String) bpreturn[0];
		int bpSplit = (int) bpreturn[1];
		String[] bpRuleComps = bpRule.split(" ");
		String src = bpRuleComps[0];
		
		String bpKey1 = 1 + " " + bpSplit + " " + bpRuleComps[1];
		String bpKey2 = (bpSplit+1) + " " + n + " " + bpRuleComps[2];
		String tree = createTree(src, bpKey1, bpKey2, bpStorage, words);
		System.out.println(tree);
		return tree;		
	}
	
	public static String createTree(String src, String key1, String key2, HashMap<String, Object[]> bpStorage, String[] words) {
		
		// DEAL WITH KEY1
		String[] key1Comp = key1.split(" ");
		int key1Index1 = Integer.parseInt(key1Comp[0]);
		int key1Index2 = Integer.parseInt(key1Comp[1]);
		String key1Result = "";
		
		// if the index values are the same
		// base case.
		if (key1Index1 == key1Index2) {
			key1Result = "[\"" + key1Comp[2] + "\", \"" + words[key1Index1-1] + "\"]";
		}
		// recursive case
		else {
			Object[] bpreturn1 = bpStorage.get(key1);
			String bpRule = (String) bpreturn1[0];
			int bpSplit = (int) bpreturn1[1];
			String[] bpRuleComps = bpRule.split(" ");
			String bpKey1 = key1Index1 + " " + bpSplit + " " + bpRuleComps[1];
			String bpKey2 = (bpSplit+1) + " " + key1Index2 + " " + bpRuleComps[2];
			key1Result = createTree(key1Comp[2], bpKey1, bpKey2, bpStorage, words);
		}
		
		// DEAL WITH KEY2
		String[] key2Comp = key2.split(" ");
		int key2Index1 = Integer.parseInt(key2Comp[0]);
		int key2Index2 = Integer.parseInt(key2Comp[1]);
		String key2Result = "";
		// if the index values are the same
		// base case.
		if (key2Index1 == key2Index2) {
			key2Result = "[\"" + key2Comp[2] + "\", \"" + words[key2Index1-1] + "\"]";
		}
		
		// recursive case
		else {
			Object[] bpreturn2 = bpStorage.get(key2);
			String bpRule = (String) bpreturn2[0];
			int bpSplit = (int) bpreturn2[1];
			String[] bpRuleComps = bpRule.split(" ");
			//String bpSrc = bpRuleComps[0];
			String bpKey1 = key2Index1 + " " + bpSplit + " " + bpRuleComps[1];
			String bpKey2 = (bpSplit+1) + " " + key2Index2 + " " + bpRuleComps[2];
			key2Result = createTree(key2Comp[2], bpKey1, bpKey2, bpStorage, words);
		}
				
		// ["source", createTree(, bpStorage)
			// recursive case = fill the other two parts with calls to createTree
		String tree = "[\"" + src + "\", " + key1Result + ", " + key2Result + "]";
		return tree;
	}
	
	/**
	 * Given a specific nonterminal, returns all binary rules that have the nonterm as the source.
	 * @param nonterm, source to look for
	 * @return specificRules, rules that have nonterm as the source.
	 */
	public static ArrayList<String> getBinaryRules(String nonterm) {
		ArrayList<String> specificRules = new ArrayList<String>();
		for (String str : binaryRules) {
			// str in form 
			String[] components = str.split(" ");
			if (components[0].equals(nonterm)) {
				specificRules.add(str);
			}
		}
		return specificRules;
	}
	
	public static void printParseTrees(ArrayList<String> generatedTrees, String outputFile) throws IOException{
		PrintWriter writeTrees = new PrintWriter(new File(outputFile));
		for (String s : generatedTrees) {
			writeTrees.write(s);
			writeTrees.write('\n');
			writeTrees.flush();
		}
		writeTrees.close();
	}
}
