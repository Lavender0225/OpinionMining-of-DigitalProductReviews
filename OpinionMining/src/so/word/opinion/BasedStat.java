package so.word.opinion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import so.dom4j.Dom4jParser;
import basic.Constant;
import basic.FileManipulator;
import beans.Freq;
import beans.Sentence;
import beans.Word;

public class BasedStat {
	public static List<Sentence> sentenceList;
	
	static HashMap<String, HashSet<String>> FO = new HashMap<String, HashSet<String>>();
	static HashSet<String> ToRemove = new HashSet<String>();
	static HashMap<String, Freq> O = new HashMap<String, Freq>();
	static HashSet<String> D = new HashSet<String>();
	static HashSet<String> U = new HashSet<String>();
	static HashMap<String, HashSet<String>> featureWords = new HashMap<String, HashSet<String>>();
	static HashSet<String> wp = new HashSet<String>();
	static int window = 3;
	static float totalemotionwords = 0;

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

	public static void GetDAndU() {
		for (int SentenceID = 0; SentenceID < sentenceList.size(); SentenceID++) {
			Sentence sentence = sentenceList.get(SentenceID);
			List<Word> wordList = sentence.getWordList();
			for (int i = 0; i < wordList.size(); i++) {
				if (wordList.get(i).getPos().equals("d")) {
					D.add(wordList.get(i).getContent());
				} else if (wordList.get(i).getPos().equals("u")) {
					U.add(wordList.get(i).getContent());
				}
			}
		}
	}

	public static void BasedStat() {
		for (int SentenceID = 0; SentenceID < sentenceList.size(); SentenceID++) {
			Sentence sentence = sentenceList.get(SentenceID);
			List<Word> wordList = sentence.getWordList();
			int i = 0;
			int j = 0;
			while (i < wordList.size()) {
				if (iswp(wordList.get(i).getContent())
						|| featureWords.containsKey(wordList.get(i)
								.getContent())
						|| D.contains(wordList.get(i).getContent())) {
					if (i + 1 + window < wordList.size() && i > 1) {
						j = i + 1;
						// for (j = i + 1; j < i + 1 + window; j++) {
						while (j < i + 1 + window) {
							if (iswp(wordList.get(j).getContent())
									|| featureWords.containsKey(wordList.get(j)
											.getContent())
									|| U.contains(wordList.get(j).getContent())) {
								// System.out.println("i= "+i);
								// System.out.println("j= "+j);
								// System.out.println(sentence
								// .getContent());
								String o = "";
								int check = 0;
								if (j - 1 > i + 1) {
									for (int k = i + 1; k <= j - 1; k++) {
										if (iswp(wordList.get(k).getContent())) {
											System.out.println((i + 1) + "  "
													+ (j - 1));
											System.out.println(sentence
													.getContent());
											check = 1;
										}
										o += wordList.get(k).getContent();
									}
									if (check == 1)
										System.out.println("o=" + o);
									// System.out.println();
									if (O.containsKey(o)) {
										O.get(o).freq++;
									} else {
										Freq f = new Freq();
										O.put(o, f);
									}
									if (featureWords.containsKey(wordList.get(
											i - 1).getContent())) {
										String fo = wordList.get(i - 1)
												.getContent() + "|" + o;
										if (!FO.containsKey(fo)) {
											HashSet<String> hs = new HashSet<String>();
											String initemotion = "im0";
											hs.add(initemotion);
											hs.add("" + SentenceID);
											FO.put(fo, hs);
										} else {
											FO.get(fo).add("" + SentenceID);
										}
									}
									if (featureWords.containsKey(wordList
											.get(j).getContent())) {
										String fo = wordList.get(j)
												.getContent() + "|" + o;
										if (!FO.containsKey(fo)) {
											HashSet<String> hs = new HashSet<String>();
											String initemotion = "im0";
											hs.add(initemotion);
											hs.add("" + SentenceID);
											FO.put(fo, hs);
										} else {
											FO.get(fo).add("" + SentenceID);
										}
									}
									// System.out.println("here");
									i = j;
									break;
								}
								i = j;
								break;
							}
							j++;
						}

					}
				}
				// if (iswp(wordList.get(i).getContent())) {
				// System.out.println(wordList.get(i).getContent());
				// }
				// if (D.contains(wordList.get(i).getContent())) {
				// System.out.println("containsbyD");
				// }
				// if (U.contains(wordList.get(i).getContent())) {
				// System.out.println("containsbyU");
				// }
				i++;
			}
		}

		Iterator<Entry<String, Freq>> iter = O.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Freq> entry = (Entry<String, Freq>) iter
					.next();
			String key = entry.getKey();
			totalemotionwords += O.get(key).freq;
		}
		Iterator<Entry<String, HashSet<String>>> iter2 = FO.entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry<String, HashSet<String>> entry = (Entry<String, HashSet<String>>) iter2
					.next();
			String key = entry.getKey();
			String a[] = key.split("\\|");
			String key2 = a[1];
			float p = (O.get(key2).freq / totalemotionwords);
			if (p < 1.0991167E-4) {
			//	System.out.println(p);
				ToRemove.add(key);
				// System.out.println("delete" + key2);
			}
		}
		Iterator<String> iter3 = ToRemove.iterator();
		while (iter3.hasNext()) {
			String key = iter3.next();
			// System.out.println("delete" + key);
			FO.remove(key);
		}
	}

	public static void basedStatExtract(Constant constant) throws Exception {
		// input:featureWords,senteceList,D,U
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));
		featureWords = FileManipulator.loadOneToMany(
				constant.FEATURE_WORDS_STATISTIC_FILTER_FILE, "\t\t", ";");
		GetWp();
		GetDAndU();
		BasedStat();
		// output:FO
		FileManipulator.outputOneToMany(FO, constant.FO_BASESTAT_FILE, "\t\t", ";");
		System.out.println("statistic based generating completed, feature words size:"+FO.size());
	}
}
