package so.dom4j;

import java.io.*;
import java.util.*;

import basic.Constant;
import basic.FileManipulator;
import beans.Sentence;
import beans.Word;

public class AllWords {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void GetAllwords(Constant constant) throws Exception {
		File inputXML = new File(constant.CURRENT_READER);

		List<Sentence> sentenceList = Dom4jParser.getAllElements(inputXML);

		HashMap<String, HashSet<String>> allWords = new HashMap<String, HashSet<String>>();
		for (int index = 0; index < sentenceList.size(); index++) {
			Sentence sentence = sentenceList.get(index);
			List<Word> wordList = sentence.getWordList();
			for (int i = 0; i < wordList.size(); i++) {
				Word word1 = wordList.get(i);
				HashSet<String> sentenceIDs = allWords.get(word1.getContent());
				if (sentenceIDs == null) {
					sentenceIDs = new HashSet<String>();
					sentenceIDs.add(Integer.toString(index));
					allWords.put(word1.getContent(), sentenceIDs);
				} else
					sentenceIDs.add(Integer.toString(index));
			}
		}
		FileManipulator.outputOneToMany(allWords, constant.ALL_WORDS_FILE, "\t\t",";");
		System.out.println("all_words.txt generated, all_words list size:"+allWords.size());
	}

}
