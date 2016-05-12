package so.sentence.polarity;

import java.io.*;
import java.util.*;

import basic.Constant;
import beans.*;

import so.dom4j.Dom4jParser;
import so.lexicon.degree.DegreeWord;
import so.lexicon.escape.EscapeWord;
import so.lexicon.negative.NegativeWord;
import so.lexicon.positive.PositiveWord;

public class Polarity {

	private static List<Sentence> sentenceList;
	private static HashMap<Pair, HashSet<String>> featureOpinion;
	private static HashMap<String, HashSet<Pair>> sentenceIDPair;
	private static HashMap<Pair, Integer> featureOpinionPolarity;

	private static Position position(Pair pair, String sentenceID) {
		int featureSPos = 0, featureTPos = 0, opinionSPos = 0, opinionTPos = 0;

		String featureContent = pair.getKey();
		String opinionContent = pair.getValue();

		Sentence sentence = sentenceList.get(Integer.parseInt(sentenceID));
		List<Word> wordList = sentence.getWordList();

		// find the position of opinion word
		int wordMaxLen = opinionContent.length();
		for (int i = 0; i < wordList.size(); i++) {
			for (int j = 0; j < wordMaxLen; j++) {
				int ii = i;
				int jj = j;
				String wordSeg = "";
				while (jj-- >= 0) {
					if (ii == wordList.size())
						break;
					wordSeg += wordList.get(ii++).getContent();
				}

				if (opinionContent.equals(wordSeg)) {
					opinionSPos = i;
					opinionTPos = i + j;
				}
			}
		}

		// go left to find feature word
		wordMaxLen = featureContent.length();
		for (int i = 0; i < opinionSPos; i++) {
			for (int j = 0; j < wordMaxLen; j++) {
				int ii = i;
				int jj = j;
				String wordSeg = "";
				while (jj-- >= 0) {
					if (ii == opinionSPos)
						break;
					wordSeg += wordList.get(ii++).getContent();
				}

				if (featureContent.equals(wordSeg)) {
					featureSPos = i;
					featureTPos = i + j;
					return new Position(featureSPos, featureTPos, opinionSPos,
							opinionTPos);
				}
			}
		}

		// go right to find feature word
		wordMaxLen = featureContent.length();
		for (int i = opinionTPos + 1; i < wordList.size(); i++) {
			for (int j = 0; j < wordMaxLen; j++) {
				int ii = i;
				int jj = j;
				String wordSeg = "";
				while (jj-- >= 0) {
					if (ii == wordList.size())
						break;
					wordSeg += wordList.get(ii++).getContent();
				}

				if (featureContent.equals(wordSeg)) {
					featureSPos = i;
					featureTPos = i + j;
					return new Position(featureSPos, featureTPos, opinionSPos,
							opinionTPos);
				}
			}
		}
		return new Position(featureSPos, featureTPos, opinionSPos, opinionTPos);
	}

	private static double weight(Position position, String sentenceID)
			throws IOException {

		Integer mLeftSide = position.getmLeftSide();
		Integer mRightSide = position.getmRightSide();

		Sentence sentence = sentenceList.get(Integer.parseInt(sentenceID));
		List<Word> wordList = sentence.getWordList();
		int escape = 0;
		int degree = 0;
		for (int i = mLeftSide + 1; i < mRightSide; i++) {
			Word word = wordList.get(i);
			if (EscapeWord.isEscapeWord(word.getContent()))
				escape++;
			if (DegreeWord.isDegreeWord(word.getContent()))
				degree++;
		}

		if (escape % 2 == 1)
			return -1 * Math.pow(2, degree);
		return Math.pow(2, degree);
	}

	private static int categorize(Double value) {
		if (value >= 0.75)
			return 1;
		else if (value <= 0)
			return -1;
		else
			return 0;
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static void sentencePolarity(Constant constant) throws Exception {
		
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER.toString()));

		// initiate <f, o, occurence>
		featureOpinion = new HashMap<Pair, HashSet<String>>();
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				constant.FO_RULE1_FILE)));
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			String[] array = line.split("\t\t");
			String[] pair = array[0].split("\\|");
			String[] sentenceIDs = array[1].split(";");

			Pair p = new Pair(pair[0], pair[1]);
			HashSet<String> senIDs = new HashSet<String>();
			for (String senID : sentenceIDs) {
				// 
				if (!senID.equals("im0"))
					senIDs.add(senID);
			}
			featureOpinion.put(p, senIDs);
		}

		// initiate <sentenceID, pairs>
		sentenceIDPair = new HashMap<String, HashSet<Pair>>();
		for (Map.Entry<Pair, HashSet<String>> entry : featureOpinion.entrySet()) {
			Pair pair = entry.getKey();
			HashSet<String> sentenceIDs = entry.getValue();

			for (String sentenceID : sentenceIDs) {
				HashSet<Pair> pairs = sentenceIDPair.get(sentenceID);
				if (pairs == null) {
					pairs = new HashSet<Pair>();
					pairs.add(pair);
					sentenceIDPair.put(sentenceID, pairs);
				} else
					pairs.add(pair);
			}
		}
		sentenceIDPair = (HashMap<String, HashSet<Pair>>) sentenceIDPair
				.clone();

		featureOpinionPolarity = new HashMap<Pair, Integer>();
		in = new Scanner(new BufferedInputStream(new FileInputStream(
				constant.FEATURE_OPINION_POLARITY_UPDATED_FILE)));
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			String[] array = line.split("\t\t");
			Pair p = new Pair(array[0], array[1]);
			featureOpinionPolarity.put(p, Integer.parseInt(array[2]));
		}

		HashSet<String> positiveWord = new HashSet<String>();
		HashSet<String> negativeWord = new HashSet<String>();

		for (Map.Entry<Pair, Integer> entry : featureOpinionPolarity.entrySet()) {
			Integer polarity = entry.getValue();
			if (polarity == 1) {
				positiveWord.add(entry.getKey().getValue());
			} else if (polarity == -1)
				negativeWord.add(entry.getKey().getValue());
		}
		HashMap<Integer, List<Sentence>> paragraph = new HashMap<Integer, List<Sentence>>();
		for (int i = 0; i < sentenceList.size(); i++) {
			double sentencePolarity = 0.0;

			Sentence sentence = sentenceList.get(i);
			HashSet<Pair> pairs = sentenceIDPair.get(Integer.toString(i));
			if (pairs != null)
				for (Pair pair : pairs) {
					Integer polarity = featureOpinionPolarity.get(pair);
					if (polarity == 0)
						continue;
					Position position = position(pair, Integer.toString(i));
					sentencePolarity += polarity
							* weight(position, Integer.toString(i));
				}

			List<Word> wordList = sentence.getWordList();
			for (int j = 0; j < wordList.size(); j++) {
				Word word = wordList.get(j);

				double wordPolarity = 0.0;
				if (PositiveWord.isPositiveWord(word.getContent())
						|| positiveWord.contains(word.getContent()))
					wordPolarity = 1.0;
				if (NegativeWord.isNegativeWord(word.getContent())
						|| negativeWord.contains(word.getContent()))
					wordPolarity = -1.0;
				if (wordPolarity == 0.0)
					continue;

				int escape = 0;
				int degree = 0;
				for (int ii = j - 1; ii >= j - 5 && ii >= 0; ii--) {
					Word w = wordList.get(ii);
					if (EscapeWord.isEscapeWord(w.getContent()))
						escape++;
					if (DegreeWord.isDegreeWord(w.getContent()))
						degree++;
				}

				if (escape % 2 == 1)
					sentencePolarity += wordPolarity * -1 * Math.pow(2, degree);
				else
					sentencePolarity += wordPolarity * Math.pow(2, degree);
			}
			sentence.setSentencePolarity(sentencePolarity);
			
			if(paragraph.containsKey(sentence.getPara_id())){
				paragraph.get(sentence.getPara_id()).add(sentence);
			}
			else{
				paragraph.put(sentence.getPara_id(), new ArrayList<Sentence>());
				paragraph.get(sentence.getPara_id()).add(sentence);
			}
			
			
		}
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				constant.RESULT_FILE)));
		for(List<Sentence> sentlist : paragraph.values()){
			String content = "";
			double polarity = 0.0;
			for(Sentence sent : sentlist){
				content += sent.getContent();
				polarity += sent.getSentencePolarity();
			}
			polarity /= sentlist.size();
			out.println(categorize(polarity) + "\t\t" + polarity+ "\t\t"
					+ content);
		}
		
		out.flush();
		out.close();
		System.out.println("sentence polarity completed, size:"+paragraph.size());
	}
}
