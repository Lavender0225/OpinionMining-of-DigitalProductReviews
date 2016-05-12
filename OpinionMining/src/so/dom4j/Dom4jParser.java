package so.dom4j;

import java.io.*;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.*;

import beans.Sentence;

public class Dom4jParser {
	public Dom4jParser() {

	}

	@SuppressWarnings("unchecked")
	public static List<Sentence> getAllElements(File inputXML) throws Exception {
		List<Sentence> sentenceList = new ArrayList<Sentence>();

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(inputXML);
		int para_id = 0;
		int sent_id = 0;
		List<Node> paraList = document.selectNodes("//para");
		Iterator<Node> paraIter = paraList.iterator();
		while (paraIter.hasNext()) {
			Element para = (Element) paraIter.next();
			Iterator sentenceIter = para.elementIterator("sent");
			while (sentenceIter.hasNext()) {
				Sentence sen = new Sentence(String.valueOf(sent_id));
				sent_id ++;
				Element sentence = (Element) sentenceIter.next();
				Iterator wordIter = sentence.elementIterator("word");
				while (wordIter.hasNext()) {
					Element word = (Element) wordIter.next();
					sen.addWord(word.attributeValue("cont"), word
							.attributeValue("pos"), word
							.attributeValue("parent"), word
							.attributeValue("relation"));
				}
				if (sen.getWordList().size() > 0){
					sen.setPara_id(para_id);
					sentenceList.add(sen);
				}
			}
			
			para_id ++;
			
		}
//		for(Sentence ss : sentenceList){
//			System.out.println(ss);
//		}
		return sentenceList;
	}
}
