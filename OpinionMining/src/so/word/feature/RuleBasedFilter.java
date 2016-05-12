package so.word.feature;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import basic.Constant;
import basic.FileManipulator;
import beans.*;

import so.dom4j.Dom4jParser;
import so.word.stop.StopWord;

public class RuleBasedFilter {

	public static List<Sentence> sentenceList;
	public static int JL(Map.Entry<String, HashSet<String>> entry)
			throws IOException {
		int result = 0;

		String wordContent = entry.getKey();
		HashSet<String> sentenceIDs = entry.getValue();
		for (String sentenceIDStr : sentenceIDs) {
			int sentenceID = Integer.parseInt(sentenceIDStr);
			Sentence sentence = sentenceList.get(sentenceID);
			List<Word> wordList = sentence.getWordList();
			for (int i = 2; i < wordList.size(); i++) {
				if (!wordContent.equals(wordList.get(i).getContent())
						&& !(i != wordList.size() - 1 && wordContent
								.equals(wordList.get(i).getContent()
										+ wordList.get(i + 1).getContent())))
					continue;
				int j = i - 1;
				while (j > 0 && wordList.get(j).getContent().equals("的"))
					j--;
				if (j < i && wordList.get(j).getPos().equals("a")
						&& !StopWord.isStopWord(wordList.get(j).getContent())) {
					result++;
					break;
				}
			}
		}
		return result;
	}

	public static int JR(Map.Entry<String, HashSet<String>> entry)
			throws IOException {
		int result = 0;

		String wordContent = entry.getKey();
		HashSet<String> sentenceIDs = entry.getValue();
		for (String sentenceIDStr : sentenceIDs) {
			int sentenceID = Integer.parseInt(sentenceIDStr);
			Sentence sentence = sentenceList.get(sentenceID);
			List<Word> wordList = sentence.getWordList();
			for (int i = 0; i < wordList.size() - 2; i++) {
				if (!wordContent.equals(wordList.get(i).getContent())
						&& !(i != wordList.size() - 3 && wordContent
								.equals(wordList.get(i).getContent()
										+ wordList.get(i + 1).getContent())))
					continue;
				int j = i + 1;
				while (j < wordList.size() - 1
						&& wordList.get(j).getPos().equals("d"))
					j++;
				if (j > i && wordList.get(j).getPos().equals("a")
						&& !StopWord.isStopWord(wordList.get(j).getContent())) {
					result++;
					break;
				}
			}
		}
		return result;
	}

	public static int N(Map.Entry<String, HashSet<String>> entry) {
		return entry.getValue().size();
	}

	public static void filter(Constant constant) throws Exception {
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));

		HashMap<String, HashSet<String>> featureWords = FileManipulator
				.loadOneToMany(constant.FEATURE_WORDS_RULE_GEN_FILE, "\t\t", ";");

		// List<Pair> pairList = new ArrayList<Pair>();
		Iterator<Entry<String, HashSet<String>>> iter = featureWords.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, HashSet<String>> entry = (Entry<String, HashSet<String>>) iter
					.next();
			int JL = JL(entry);
			int JR = JR(entry);
			int N = N(entry);
			double PJ = (double) (JL + JR) / N;
			//System.out.println(entry.getKey() + " PJ value is " + PJ);
			if (PJ <= 0.0) {
				iter.remove();
				featureWords.remove(entry.getKey());
			}
			// pairList.add(new Pair(entry.getKey(), PJ));
		}

		FileManipulator.outputOneToMany(featureWords,
				constant.FEATURE_WORDS_RULE_FILTER_FILE, "\t\t", ";");
		System.out.println("rule based filter completed, feature words size:"+featureWords.size());
		// Collections.sort(pairList);
		// PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
		// "etc/feature_words_rule_based_filter.txt")));
		// for (Pair pair : pairList) {
		// pw.println(pair.getKey() + "\t" + pair.getValue());
		// pw.flush();
		// }
		// pw.close();
	}
}