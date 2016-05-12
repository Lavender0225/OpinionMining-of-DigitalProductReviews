package so.word.opinion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import so.dom4j.Dom4jParser;
import basic.Constant;
import basic.FileManipulator;
import beans.Sentence;
import beans.Word;

public class Rule_1 {

	public static List<Sentence> sentenceList;
	// init: word->FO属性观点对
	static HashMap<String, HashSet<String>> FO = new HashMap<String, HashSet<String>>();

	public static void Rule1(Map.Entry<String, HashSet<String>> entry)
			throws IOException {
		String fword = entry.getKey();
		HashSet<String> sentenceIDs = entry.getValue();
		for (String sentenceIDStr : sentenceIDs) {
			// System.out.println(sentenceIDStr);
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
			// System.out.println("s= " + s);
			if (s > 0) {
				for (int j = s - 1; j > 0; j--) {
					// if (!wordList.get(j).getContent().equals("的")) {
					// System.out.println("非的");
					// }
					// if (!wordList.get(j).getPos().equals("a")) {
					// System.out.println("非形容词");
					// }
					// if (wordList.get(j).getPos().equals("wp")) {
					// System.out.println("标点");
					// }
					if ((!wordList.get(j).getContent().equals("的") && (!wordList
							.get(j).getPos().equals("a")))
							|| (wordList.get(j).getPos().equals("wp"))) {
						// System.out.println("here");
						break;
					} else if (wordList.get(j).getPos().equals("a")) {
						String fo = fword + "|" + wordList.get(j).getContent();
						HashSet<String> hs = new HashSet<String>();
						String initemotion = "im0";
						hs.add(initemotion);
						hs.add(sentenceIDStr);
						if (!FO.containsKey(fo))
							FO.put(fo, hs);
						else{
							FO.get(fo).add(sentenceIDStr);
						}
						// System.out.println("形容词");
					}
				}
			}
			if (s < wordList.size() - 1) {
				for (int j = s + 1; j < wordList.size(); j++) {
					// if (!wordList.get(j).getContent().equals("d")) {
					// System.out.println("非d");
					// }
					// if (!wordList.get(j).getPos().equals("a")) {
					// System.out.println("非形容词");
					// }
					// if (!wordList.get(j).getPos().equals("c")) {
					// System.out.println("非c");
					// }
					// if (wordList.get(j).getPos().equals("wp")) {
					// System.out.println("标点");
					// }
					if ((!wordList.get(j).getContent().equals("d")
							&& !wordList.get(j).getPos().equals("a") && !wordList
							.get(j).getPos().equals("c"))
							|| wordList.get(j).getPos().equals("wp")) {
						// System.out.println("here");
						break;
					} else if (wordList.get(j).getPos().equals("a")) {
						String fo = fword + "|" + wordList.get(j).getContent();
						HashSet<String> hs = new HashSet<String>();
						String initemotion = "im0";
						hs.add(initemotion);
						hs.add(sentenceIDStr);
						if (!FO.containsKey(fo))
							FO.put(fo, hs);
						else{
							FO.get(fo).add(sentenceIDStr);
						}
					}
				}
			}
		}
	}

	public static void rule1Extract(Constant constant) throws Exception {
		// input:featureWords,senteceList
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));
		HashMap<String, HashSet<String>> featureWords = FileManipulator
				.loadOneToMany(constant.FEATURE_WORDS_STATISTIC_FILTER_FILE,
						"\t\t", ";");

		Iterator<Entry<String, HashSet<String>>> iter = featureWords.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, HashSet<String>> entry = (Entry<String, HashSet<String>>) iter
					.next();
			Rule1(entry);
		}
		// output:FO
		FileManipulator.outputOneToMany(FO, constant.FO_RULE1_FILE, "\t\t",
				";");
		System.out.println("rule 1 generating completed, feature words size:"+FO.size());

	}
}
