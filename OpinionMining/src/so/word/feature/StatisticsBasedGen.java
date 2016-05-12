package so.word.feature;

import java.util.*;
import java.util.Map.Entry;
import java.io.*;

import so.dom4j.Dom4jParser;
import so.word.stop.StopWord;
import basic.Constant;
import basic.FileManipulator;
import beans.Sentence;
import beans.Word;

public class StatisticsBasedGen {

	private static List<Sentence> sentenceList;

	private static int JL(String wordContent, HashSet<String> sentenceIDs)
			throws IOException {
		int result = 0;

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

	private static int JR(String wordContent, HashSet<String> sentenceIDs)
			throws IOException {
		int result = 0;

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

	private static HashMap<String, HashSet<String>> LCN(String wordContent,
			HashSet<String> sentenceIDs, int wordMaxLen) throws IOException {
		HashMap<String, HashSet<String>> lcn = new HashMap<String, HashSet<String>>();

		for (String sentenceID : sentenceIDs) {
			Sentence sentence = sentenceList.get(Integer.parseInt(sentenceID));
			List<Word> wordList = sentence.getWordList();

			for (int i = 1; i < wordList.size(); i++) {
				boolean sentenceEnd = false;
				for (int j = 0; j < wordMaxLen; j++) {
					int ii = i;
					int jj = j;
					String wordSeg = "";
					while (jj-- >= 0) {
						if (ii == wordList.size()) {
							sentenceEnd = true;
							break;
						}
						wordSeg += wordList.get(ii++).getContent();
					}
					if (sentenceEnd)
						break;

					if (wordContent.equals(wordSeg)) {
						String lcnWordContent = wordList.get(i - 1)
								.getContent();
						if (StopWord.isStopWord(lcnWordContent))
							continue;
						HashSet<String> senIDs = lcn.get(lcnWordContent);
						if (senIDs == null) {
							senIDs = new HashSet<String>();
							senIDs.add(sentenceID);
							lcn.put(lcnWordContent, senIDs);
						} else
							senIDs.add(sentenceID);
						break;
					}
				}
			}
		}

		return lcn;
	}

	private static HashMap<String, HashSet<String>> RCN(String wordContent,
			HashSet<String> sentenceIDs, int wordMaxLen) throws IOException {
		HashMap<String, HashSet<String>> rcn = new HashMap<String, HashSet<String>>();

		for (String sentenceID : sentenceIDs) {
			Sentence sentence = sentenceList.get(Integer.parseInt(sentenceID));
			List<Word> wordList = sentence.getWordList();

			for (int i = 0; i < wordList.size() - 1; i++) {
				boolean sentenceEnd = false;
				for (int j = 0; j < wordMaxLen; j++) {
					int ii = i;
					int jj = j;
					String wordSeg = "";
					while (jj-- >= 0) {
						if (ii == wordList.size()) {
							sentenceEnd = true;
							break;
						}
						wordSeg += wordList.get(ii++).getContent();
					}
					if (sentenceEnd)
						break;

					if (wordContent.equals(wordSeg)) {
						String rcnWordContent = wordList.get(i + 1)
								.getContent();
						if (StopWord.isStopWord(rcnWordContent))
							continue;
						HashSet<String> senIDs = rcn.get(rcnWordContent);
						if (senIDs == null) {
							senIDs = new HashSet<String>();
							senIDs.add(sentenceID);
							rcn.put(rcnWordContent, senIDs);
						} else
							senIDs.add(sentenceID);
						break;
					}
				}
			}
		}

		return rcn;
	}

	private static double LCE(String wordContent, HashSet<String> sentenceIDs,
			int wordMaxLen) throws IOException {
		double lce = 0.0;

		int N = sentenceIDs.size();
		HashMap<String, HashSet<String>> LCN = LCN(wordContent, sentenceIDs,
				wordMaxLen);

		for (Map.Entry<String, HashSet<String>> e : LCN.entrySet()) {
			int C = e.getValue().size();
			lce += C * Math.log((double) C / N);
		}

		lce = -1 * lce / N;

		return lce;
	}

	private static double RCE(String wordContent, HashSet<String> sentenceIDs,
			int wordMaxLen) throws IOException {
		double rce = 0.0;

		int N = sentenceIDs.size();
		HashMap<String, HashSet<String>> RCN = RCN(wordContent, sentenceIDs,
				wordMaxLen);

		for (Map.Entry<String, HashSet<String>> e : RCN.entrySet()) {
			int C = e.getValue().size();
			rce += C * Math.log((double) C / N);
		}

		rce = -1 * rce / N;

		return rce;
	}

	private static double PMI(int NW, int NI, int NWI) {
		return (double) NWI / (NI * NW);
	}

	@SuppressWarnings("unchecked")
	public static void staticBasedFilter(Constant constant) throws Exception {
		double T1 = 0.0, T2 = 0.001, T3 = 0.7;
		double T4 = 0.0, T5 = 0.001, T6 = 0.7;
		
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));

		HashMap<String, HashSet<String>> featureWords = FileManipulator
				.loadOneToMany(constant.FEATURE_WORDS_RULE_FILTER_FILE, "\t\t", ";");
		HashMap<String, HashSet<String>> allWords = FileManipulator
				.loadOneToMany(constant.ALL_WORDS_FILE, "\t\t", ";");
		HashMap<String, HashSet<String>> extendibleFeatureWords = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> extendibleAllWords = new HashMap<String, HashSet<String>>();

		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
				constant.FEATURE_WORDS_STATISTIC_FILE)));
		PrintWriter recordPw = new PrintWriter(new BufferedWriter(
				new FileWriter(constant.RECORD_FILE)));

		for (int count = 0; count < 3; count++) {
			Iterator<Entry<String, HashSet<String>>> iter = featureWords.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, HashSet<String>> entry = (Entry<String, HashSet<String>>) iter
						.next();
				String wordContent = entry.getKey();
				HashSet<String> sentenceIDs = entry.getValue();

				boolean iterRemoved = false;

				int JR = JR(wordContent, sentenceIDs);
				if (JR > 0) {
					double lce1 = LCE(wordContent, sentenceIDs, 2 + count);
					double ler = 0.0;
					HashMap<String, HashSet<String>> lcn = LCN(wordContent,
							sentenceIDs, 2 + count);
					for (Map.Entry<String, HashSet<String>> e : lcn.entrySet()) {
						String lcnContent = e.getKey();
						HashSet<String> lcnSenIDs = e.getValue();
						double lce2 = LCE(lcnContent + wordContent, lcnSenIDs,
								3 + count);

						double subLCE = lce2 - lce1;
						double lpmi = PMI(sentenceIDs.size(), allWords.get(
								lcnContent).size(), lcnSenIDs.size());

						if (subLCE > T1 && lpmi > T2) {
							// lcnContent+wordContent is a extendible feature
							// word
							extendibleFeatureWords.put(
									lcnContent + wordContent, lcnSenIDs);
							extendibleAllWords.put(lcnContent + wordContent,
									lcnSenIDs);
							ler += lcnSenIDs.size();

							recordPw.println("L " + lcnContent + wordContent
									+ " added!");
							recordPw.flush();
						}
						pw.println("L " + lcnContent + " | " + wordContent
								+ " : " + subLCE + " | " + lpmi);
						pw.flush();
					}
					ler /= sentenceIDs.size();
					if (ler > T3) {
						iterRemoved = true;
						iter.remove();
						featureWords.remove(wordContent);
						recordPw.println("L " + wordContent + " removed!");
						recordPw.flush();
					}
				}

				int JL = JL(wordContent, sentenceIDs);
				if (JL > 0) {
					double rce1 = RCE(wordContent, sentenceIDs, 2 + count);
					double rer = 0.0;
					HashMap<String, HashSet<String>> rcn = RCN(wordContent,
							sentenceIDs, 2 + count);
					for (Map.Entry<String, HashSet<String>> e : rcn.entrySet()) {
						String rcnContent = e.getKey();
						HashSet<String> rcnSenIDs = e.getValue();
						double rce2 = RCE(rcnContent + wordContent, rcnSenIDs,
								3 + count);

						double subRCE = rce2 - rce1;
						double rpmi = PMI(sentenceIDs.size(), allWords.get(
								rcnContent).size(), rcnSenIDs.size());

						if (subRCE > T4 && rpmi > T5) {
							// rcnContent+wordContent is a extendible feature
							// word
							extendibleFeatureWords.put(
									rcnContent + wordContent, rcnSenIDs);
							extendibleAllWords.put(rcnContent + wordContent,
									rcnSenIDs);
							rer += rcnSenIDs.size();

							recordPw.println("R " + rcnContent + wordContent
									+ " added!");
							recordPw.flush();
						}
						pw.println("R " + rcnContent + " | " + wordContent
								+ " : " + subRCE + " | " + rpmi);
						pw.flush();
					}
					rer /= sentenceIDs.size();
					if (rer > T6) {
						if (!iterRemoved)
							iter.remove();
						featureWords.remove(wordContent);
						recordPw.println("R " + wordContent + " removed!");
						recordPw.flush();
					}
				}
			}

			pw.close();
			recordPw.close();

			featureWords.putAll(extendibleFeatureWords);
			allWords.putAll(extendibleAllWords);
			allWords = (HashMap<String, HashSet<String>>) allWords.clone();
		}

		FileManipulator.outputOneToMany(featureWords,
				constant.FEATURE_WORDS_STATISTIC_FILTER_FILE, "\t\t", ";");
		FileManipulator.outputOneToMany(allWords,
				constant.ALL_WORDS_STATISTIC_FILE, "\t\t", ";");
		System.out.println("statistic based generating completed, feature words size:"+featureWords.size());
	}

}
