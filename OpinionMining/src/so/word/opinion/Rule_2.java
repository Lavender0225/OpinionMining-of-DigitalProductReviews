package so.word.opinion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import so.dom4j.Dom4jParser;
import basic.Constant;
import basic.FileManipulator;
import beans.Freq;
import beans.Sentence;
import beans.Word;


public class Rule_2 {
	public static List<Sentence> sentenceList;
	static HashMap<String, HashSet<String>> FO = new HashMap<String, HashSet<String>>();
	static int window = 4;
	static int alladj = 1;
	static HashMap<String, Freq> O = new HashMap<String, Freq>();
	static HashSet<String> ToRemove = new HashSet<String>();
	static HashSet<String> wp = new HashSet<String>();
	
	public static boolean iswp(String word) {
		boolean result = false;
		if (wp.contains(word))
			result = true;
		else
			result = false;
		return result;
	}
	public static void GetWp() throws FileNotFoundException {
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				"etc/wp.txt")));
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			wp.add(line);
		}
		in.close();
		// System.out.println(wp.size());
	}
	
	public static void Rule2(Map.Entry<String, HashSet<String>> entry)
			throws IOException {
		String fword = entry.getKey();
		HashSet<String> sentenceIDs = entry.getValue();
		for (String sentenceIDStr : sentenceIDs) {
			int sentenceID = Integer.parseInt(sentenceIDStr);
			Sentence sentence = sentenceList.get(sentenceID);
			List<Word> wordList = sentence.getWordList();
			int s = 0;
			for (int i = 0; i < wordList.size(); i++) {// find j
				if (wordList.get(i).getContent().equals(fword)) {
					s = i;
					// if(s==0)
					// System.out.println(word);
					break;
				}
				// System.out.println(wordList.get(i).getContent());
			}
			// System.out.print("s+2= " + (s + 2));
			// System.out.print(" wordList.size-2= "+(wordList.size()-2));
			// System.out.println();
			if (s < wordList.size() - 2) {
				if (wordList.get(s + 1).getPos().equals("d")) {
					int j = 0;
					for (j = s + 2; j < wordList.size(); j++) {
						if (j - s > window
								|| iswp(wordList.get(j).getContent())
								|| wordList.get(j).getPos().equals("c")) {
							// System.out.println("here");
							break;
						}
					}
					// System.out.print("s+2= " + (s + 2));
					// System.out.print(" j-1= " + (j - 1));
					// System.out.println();
					alladj = 1;
					for (int i = s + 2; i < j - 1; i++) {
						// System.out.println("s+2= " + (s + 2));
						// System.out.println("j-1= " + (j - 1));
						if (!wordList.get(i).getPos().equals("a")) {
							alladj = 0;
							break;
						}
					}
					// System.out.println("alladj=" + alladj);
					if (alladj == 1 && (s + 2 < j - 1)) {
						String o = "";
						for (int i = s + 2; i < j - 1; i++) {
							// System.out.print(wordList.get(i).getContent().toString());
							o += wordList.get(i).getContent().toString();
						}
						String fo = fword + "|" + o;
						if (!FO.containsKey(fo)) {
							HashSet<String> hs = new HashSet<String>();
							String initemotion = "im0";
							hs.add(initemotion);
							hs.add(sentenceIDStr);
							FO.put(fo, hs);
						} else {
							FO.get(fo).add(sentenceIDStr);
						}
						// System.out.println(o);
						if (!O.containsKey(o)) {
							Freq f = new Freq();
							O.put(o, f);
						} else {
							O.get(o).freq++;
						}
						// System.out.println("o= " + o);
					}
				}
			}
		}
		// System.out.println(O.size());
		Iterator<Entry<String, HashSet<String>>> iter = FO.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, HashSet<String>> entryFO = (Entry<String, HashSet<String>>) iter
					.next();
			String key = entryFO.getKey();
			// System.out.println(key);
			String a[] = key.split("\\|");
			// String key1 = a[0];
			String key2 = a[1];
			// System.out.println(key1+"   "+key2);
			// System.out.println(key2+" = "+O.get(key2).freq);
			if (O.get(key2).freq < 1) {
				ToRemove.add(key);
				//System.out.println("delete" + key);
			}
		}
		Iterator <String>iter2 = ToRemove.iterator();
		while(iter2.hasNext()){
			String key = iter2.next();
			System.out.println("delete" + key);
			FO.remove(key);
		}
	}

	public static void rule2Extract(Constant constant) throws Exception {
		// input:featureWords,senteceList,O
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));
		HashMap<String, HashSet<String>> featureWords = FileManipulator
				.loadOneToMany(constant.FEATURE_WORDS_STATISTIC_FILTER_FILE,
						"\t\t", ";");
		Iterator<Entry<String, HashSet<String>>> iter = featureWords.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, HashSet<String>> entry = (Entry<String, HashSet<String>>) iter
					.next();
			Rule2(entry);
		}
		// output:FO
		FileManipulator.outputOneToMany(FO, constant.FO_RULE2_FILE, "\t\t", ";");
		System.out.println("rule 2 generating completed, feature words size:"+FO.size());

	}

}
