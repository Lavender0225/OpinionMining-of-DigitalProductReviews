package so.pair.polarity;

import java.io.*;
import java.util.*;

import basic.Constant;
import beans.*;

import so.dom4j.Dom4jParser;
import so.lexicon.conjunctive.ConjunctiveWord;
import so.lexicon.disjunctive.DisjunctiveWord;
import so.lexicon.escape.EscapeWord;
import so.lexicon.negative.NegativeWord;
import so.lexicon.positive.PositiveWord;

public class Polarity {

	private static List<Sentence> sentenceList;
	private static HashMap<Pair, HashSet<String>> featureOpinion;
	private static HashMap<Pair, Integer> featureOpinionPolarity;

	private static Integer polarity(String opinionContent,
			HashSet<String> sentenceIDs) throws IOException {

		if (PositiveWord.isPositiveWord(opinionContent))
			return 1;
		else if (NegativeWord.isNegativeWord(opinionContent))
			return -1;
		else {
			for (String sentenceID : sentenceIDs) {
				Sentence sentence = sentenceList.get(Integer
						.parseInt(sentenceID));
				List<Word> wordList = sentence.getWordList();
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
							if (j == 0)
								return 0;

							// int sPosition = i;
							// int tPosition = i + j;
							for (ii = i; ii <= i + j; ii++) {
								Word word = wordList.get(ii);
								if (EscapeWord.isEscapeWord(word.getContent())) {
									String rightPart = "";
									for (jj = ii + 1; jj <= i + j; jj++)
										rightPart += wordList.get(jj)
												.getContent();
									return -polarity(rightPart, sentenceIDs);
								}
							}
							return 0;
						}
					}
				}
			}
			return 0;
		}
	}

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

	private static Double relation(Position pos1, Position pos2,
			String sentenceID) throws IOException {
		Integer lSide = (pos1.getRightSide() < pos2.getLeftSide()) ? pos1
				.getRightSide() : pos2.getRightSide();
		Integer rSide = (pos1.getRightSide() < pos2.getLeftSide()) ? pos2
				.getLeftSide() : pos1.getLeftSide();

		Sentence sentence = sentenceList.get(Integer.parseInt(sentenceID));
		List<Word> wordList = sentence.getWordList();
		int conjunctive = 0;
		int disjunctive = 0;
		for (int i = lSide + 1; i < rSide; i++) {
			Word word = wordList.get(i);
			if (ConjunctiveWord.isConjunctiveWord(word.getContent()))
				conjunctive++;
			if (DisjunctiveWord.isDisjunctiveWord(word.getContent()))
				disjunctive++;
		}

		if (conjunctive == 0 && disjunctive == 0)
			return 0.5;
		if (disjunctive % 2 == 1)
			return -1.0;
		return 1.0;
	}

	private static Double score(Pair currentPair, String sentenceID,
			HashSet<Pair> pairs) throws IOException {
		if (pairs.size() < 2)
			return 0.0;

		HashSet<Pair> pairSet = new HashSet<Pair>();
		for (Pair pair : pairs) {
			Integer polarity = featureOpinionPolarity.get(pair);
			if (polarity != 0)
				pairSet.add(pair);
		}
		if (pairSet.size() == 0)
			return 0.0;

		double newPolarity = 0.0;
		for (Pair other : pairSet) {
			Integer otherPolarity = featureOpinionPolarity.get(other);

			Position position = position(currentPair, sentenceID);
			Position otherPosition = position(other, sentenceID);
			Integer distance = position.distance(otherPosition);

			if (distance > 8)
				continue;
			newPolarity += relation(position, otherPosition, sentenceID)
					* otherPolarity / distance;
		}
		return newPolarity;
	}

	private static Integer categorize(Double value) {
		double T = 0.2;
		if (value > T)
			return 1;
		else if (value < -T)
			return -1;
		else
			return 0;
	}

	@SuppressWarnings("unchecked")
	public static void pairPolarity(Constant constant, String pairsFilename) throws Exception {
		
		sentenceList = Dom4jParser.getAllElements(new File(constant.CURRENT_READER));

		// initiate <f, o, occurence>
		featureOpinion = new HashMap<Pair, HashSet<String>>();
		Scanner in = new Scanner(new BufferedInputStream(new FileInputStream(
				pairsFilename)));
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
		HashMap<String, HashSet<Pair>> sentenceIDPair = new HashMap<String, HashSet<Pair>>();
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

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				constant.FEATURE_OPINION_POLARITY_FILE)));

		// initiate <feature,opinion,polarity>
		featureOpinionPolarity = new HashMap<Pair, Integer>();
		for (Map.Entry<Pair, HashSet<String>> entry : featureOpinion.entrySet()) {
			Pair pair = entry.getKey();
			Integer polarity = polarity(pair.getValue(), featureOpinion
					.get(pair));
			featureOpinionPolarity.put(pair, polarity);
			out.println(pair + " | " + polarity);
			out.flush();
		}
		out.close();
		System.out.println("pair polarity completed, size:"+featureOpinionPolarity.size());
		
		featureOpinionPolarity = (HashMap<Pair, Integer>) featureOpinionPolarity
				.clone();

		int count = 300;
		while (count-- > 0) {

			for (Map.Entry<Pair, Integer> entry : featureOpinionPolarity
					.entrySet()) {
				Integer polarity = entry.getValue();
				if (polarity != 0)
					continue;

				Pair pair = entry.getKey();
				double newPolarity = 0.0;
				HashSet<String> sentenceIDs = featureOpinion.get(pair);
				for (String sentenceID : sentenceIDs) {
					HashSet<Pair> pairs = sentenceIDPair.get(sentenceID);
					newPolarity += score(pair, sentenceID, pairs);
				}
				int N = sentenceIDs.size();
				newPolarity /= N;
				polarity = categorize(newPolarity);
//				if (polarity != 0) {
//					System.out.println(count + " | " + pair + " | " + newPolarity + " | " + polarity);
//				}
			}
		}

		out = new PrintWriter(new BufferedWriter(new FileWriter(
				constant.FEATURE_OPINION_POLARITY_UPDATED_FILE)));
		for (Map.Entry<Pair, Integer> entry : featureOpinionPolarity.entrySet()) {
			Pair pair = entry.getKey();
			out.println(pair.getKey() + "\t\t" + pair.getValue() + "\t\t"
					+ entry.getValue());
			out.flush();
		}
		out.close();
		System.out.println("pair polarity updating completed, size:"+featureOpinionPolarity.size());
		
	}
}
