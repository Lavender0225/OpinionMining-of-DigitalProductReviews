package so.word.feature;

import java.io.*;
import java.util.*;

import so.dom4j.*;
import so.word.stop.StopWord;
import basic.Constant;
import basic.FileManipulator;
import beans.*;

public class RuleBasedGen {
	
	public static HashMap<String, HashSet<String>> ruleBasedGen(
			List<Sentence> sentenceList) throws IOException {
		HashMap<String, HashSet<String>> featureWords = new HashMap<String, HashSet<String>>();
		for (int index = 0; index < sentenceList.size(); index++) {
			Sentence sentence = sentenceList.get(index);
			List<Word> wordList = sentence.getWordList();
			for (int i = 0; i < wordList.size(); i++) {
				Word word1 = wordList.get(i);
				if (StopWord.isStopWord(word1.getContent()))
					continue;
				String pos = word1.getPos();
				if (pos.equals("n") || pos.equals("v") || pos.equals("j")) {
					HashSet<String> sentenceIDs = featureWords.get(word1
							.getContent());
					if (sentenceIDs == null) {
						sentenceIDs = new HashSet<String>();
						sentenceIDs.add(Integer.toString(index));
						featureWords.put(word1.getContent(), sentenceIDs);
					} else
						sentenceIDs.add(Integer.toString(index));
				} else
					continue;

				if (i == wordList.size() - 1)
					break;

				Word word2 = wordList.get(i + 1);
				if (StopWord.isStopWord(word2.getContent()))
					continue;
				if (!word2.getPos().equals("n"))
					continue;

				if (!pos.equals("j")) {
					HashSet<String> sentenceIDs = featureWords.get(word1
							.getContent()
							+ word2.getContent());
					if (sentenceIDs == null) {
						sentenceIDs = new HashSet<String>();
						sentenceIDs.add(Integer.toString(index));
						featureWords.put(word1.getContent()
								+ word2.getContent(), sentenceIDs);
					} else
						sentenceIDs.add(Integer.toString(index));
				}
			}
		}
		return featureWords;
	}

	public static void ruleBasedGenerate(Constant constant) throws Exception {
		List<Sentence> sentenceList = Dom4jParser.getAllElements(new File(
				constant.CURRENT_READER));
		HashMap<String, HashSet<String>> featureWords = ruleBasedGen(sentenceList);
		FileManipulator.outputOneToMany(featureWords,
				constant.FEATURE_WORDS_RULE_GEN_FILE, "\t\t", ";");
		System.out.println("rule based generating completed, feature words size:"+featureWords.size());
	}
}