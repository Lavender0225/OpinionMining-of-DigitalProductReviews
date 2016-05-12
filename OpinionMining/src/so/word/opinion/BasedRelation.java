package so.word.opinion;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import so.dom4j.Dom4jParser;
import basic.Constant;
import basic.FileManipulator;
import beans.Sentence;
import beans.Word;

public class BasedRelation {
	public static List<Sentence> sentenceList;
	private static Constant constant = new Constant();
	
	static HashMap<String, HashSet<String>> FO = new HashMap<String, HashSet<String>>();
	static HashMap<String, HashSet<String>> featureWords = new HashMap<String, HashSet<String>>();

	public static void BasedRelation() {
		for (int SentenceID = 0; SentenceID < sentenceList.size(); SentenceID++) {
			Sentence sentence = sentenceList.get(SentenceID);
			List<Word> wordList = sentence.getWordList();
			for (int i = 0; i < wordList.size(); i++) {
				if(wordList.get(i).getRelation().equals("VOB"))
					continue;
				if(wordList.get(i).getRelation().equals("ATT")){
					//根据定中关系扩展主语
					for(int j = 0;j<wordList.size();j++){
						//if(wordList.get(j).getRelation())
					}
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		// input:featureWords,senteceList
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));
		featureWords = FileManipulator.loadOneToMany(
				"etc/feature_words_statistics_filter.txt", "\t\t", ";");

		BasedRelation();
		// output:FO
		FileManipulator.outputOneToMany(FO, "etc/FO_BaseRelation.txt", "\t\t",
				";");
	}
}
