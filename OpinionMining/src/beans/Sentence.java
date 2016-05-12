package beans;

import java.util.*;

public class Sentence {
	private String id;
	private String content;
	private List<Word> wordList;
	private int para_id;
	private double sentencePolarity;

	public Sentence(String id) {
		this.id = id;
		content = null;
		wordList = new ArrayList<Word>();
	}

	public void addWord(String content, String pos, String parent,
			String relation) {
		Word word = new Word(content, pos, parent, relation);
		wordList.add(word);
	}

	public String toString() {
		String lineSeparator = System.getProperty("line.separator");
		return "sent_id:" + id + ", para_id:" + para_id + ", content: " + this.getContent()
		+ lineSeparator+ wordList.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		if (content == null) {
			content = "";
			for (Word word : wordList)
				content += word.getContent();
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
	}

	public int getPara_id() {
		return para_id;
	}

	public void setPara_id(int para_id) {
		this.para_id = para_id;
	}

	public double getSentencePolarity() {
		return sentencePolarity;
	}

	public void setSentencePolarity(double sentencePolarity) {
		this.sentencePolarity = sentencePolarity;
	}
	
	
}
