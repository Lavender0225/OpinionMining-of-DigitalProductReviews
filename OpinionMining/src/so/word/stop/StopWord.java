package so.word.stop;

import java.io.IOException;
import java.util.HashSet;

import basic.FileManipulator;

public class StopWord {
	private static HashSet<String> stopWords = null;

	public static boolean isStopWord(String word) throws IOException {
		if (stopWords == null) {
			stopWords = FileManipulator
					.loadHashSetFromFile("etc/stopwords.txt");
		}
		return stopWords.contains(word);
	}
}
