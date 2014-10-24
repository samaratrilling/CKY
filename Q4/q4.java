import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class q4{

	public static void main (String args[]) {
  
		String trainingFile;
		String outputFile;
		
		trainingFile = "parse_train.dat";
		outputFile = "replacedRare.dat";
		
		if (args.length > 0) {
			String mode = args[0];
			if (mode.equals("vert")) {
				trainingFile = "parse_train_vert.dat";
				outputFile = "replacedRareVert.dat";
			}
		}
		
		try {
			// Read in parse_train.dat; put the lines in an arraylist.
			System.out.println("Reading in " + trainingFile + "...");
			ArrayList<JSONArray> lines = readTrainingData(trainingFile);
			//System.out.println(lines);
			
		    // for each line, find the words at the fringe of the tree.
		    // keep trying to get the next token until there are no more to get
		    // then add that token to the list of real words.
			ArrayList<String> realWords = findWords(lines);
			
		    // Once you've read in the whole file and generated a list of real
		    // words, generate a lookup table of rare words (<5 occurrences).
			HashSet<String> rareWords = findRareWords(realWords);
			
			// Use the find words at fringe method to decide if each word you
		    // encounter is already in the hash table of rare words (O(1)).
		    // try a get call and if it returns null it's not rare.
		    // If it is rare, replace it with the _RARE_ symbol.
			// This will output a file named "replacedRare.dat"
			replaceRareWords(lines, rareWords, outputFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void replaceRareWords(ArrayList<JSONArray> lines, HashSet<String> rareWords, String outputFile) throws IOException{
		ArrayList<String> toWrite = new ArrayList<String>();
		
		for (JSONArray tree : lines) {
			if (tree.size() == 2) {
				if (rareWords.contains((String) tree.get(1))) {
					tree.set(1, "_RARE_");
				}
			}
			else if(tree.size() == 3) {
				recurseReplace((JSONArray)tree.get(1), rareWords);
				recurseReplace((JSONArray)tree.get(2), rareWords);
			}
			toWrite.add(tree.toString());
		}
		writeReplacements(toWrite, outputFile);
	}
	
	public static void writeReplacements(ArrayList<String> toWrite, String outputFile) throws IOException {
		PrintWriter writer = new PrintWriter(new File(outputFile));
		for (String str : toWrite) {
			writer.write(str);
			writer.write('\n');
			writer.flush();
		}
		writer.close();
	}
	
	public static JSONArray recurseReplace(JSONArray tree, HashSet<String> rareWords) {
		
		if (tree.size() == 2) {
			if (rareWords.contains((String) tree.get(1))) {
				tree.set(1, "_RARE_");
				return tree;
			}
		}
		else if (tree.size() == 3) {
			recurseReplace((JSONArray) tree.get(1), rareWords);
			recurseReplace((JSONArray) tree.get(2), rareWords);
		}
		return null;
	}
	
	public static HashSet<String> findRareWords (ArrayList<String> realWords) {
		System.out.println("real words: " + realWords.size());
		
		HashMap<String, Integer> wordFreqs = new HashMap<String, Integer>();
		HashSet<String> commonWords = new HashSet<String>();
		for (String s : realWords) {
			if (wordFreqs.containsKey(s)) {
				wordFreqs.put(s, wordFreqs.get(s) + 1);
			}
			else {
				wordFreqs.put(s, 1);
			}
		}
		HashSet<String> rareWords = new HashSet<String>();
		Set<Map.Entry<String, Integer>> allWords = wordFreqs.entrySet();
		for (Map.Entry<String, Integer> e : allWords) {
			if ((Integer) e.getValue() < 5) {
				rareWords.add((String) e.getKey());
			}
		}
		return rareWords;
	}
	
	public static ArrayList<JSONArray> readTrainingData(String trainingFile) throws IOException, ParseException{
		ArrayList<JSONArray> lines = new ArrayList<JSONArray>();
		BufferedReader readTraining = new BufferedReader(new FileReader(trainingFile));
		String line = readTraining.readLine();
		while(line != null) {
			JSONArray source = (JSONArray) new JSONParser().parse(line);
			lines.add(source);
			line = readTraining.readLine();
		}
		readTraining.close();
		return lines;
	}
	
	public static ArrayList<String> findWords(ArrayList<JSONArray> lines) {
		ArrayList<String> words = new ArrayList<String>();
		for (JSONArray tree : lines) {
			if (tree.size() == 2) {
				words.add((String) tree.get(1)); 
			}
			if(tree.size() == 3) {
				ArrayList<String> leftList = recurseFind((JSONArray)tree.get(1));
				ArrayList<String> rightList = recurseFind((JSONArray)tree.get(2));
				words.addAll(leftList);
				words.addAll(rightList);
			}
		}
		return words;
	}
	
	public static ArrayList<String> recurseFind(JSONArray tree) {

		if (tree.size() == 2) {
			ArrayList<String> words = new ArrayList<String>();
			words.add((String) tree.get(1));
			//System.out.println((String) tree.get(1));
			return words;
		}
		if (tree.size() == 3) {
			ArrayList<String> sides = new ArrayList<String>();
			ArrayList<String> left = recurseFind((JSONArray) tree.get(1));
			ArrayList<String> right = recurseFind((JSONArray)tree.get(2));
			sides.addAll(left);
			sides.addAll(right);
			return sides;
		}
		return null;
	}
}