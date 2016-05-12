package basic;

public class Constant {
	public   final String READ_FILENAME_MP3 = "etc/corpus/mp3_mp4.ltp.format.xml";
	public   final String READ_FILENAME_CAMERA = "etc/corpus/camera.ltp.format.xml";
	public   final String READ_FILENAME_MOBILE = "etc/corpus/mobile.ltp.format.xml";
	public   final String READ_FILENAME_NOTEBOOK = "etc/corpus/notebook.ltp.format.xml";
	
	public   String CURRENT_READER = "";
	
	public    String CURPUS_MP3 = "etc/corpus/mp3_mp4.corpus";
	public    String CURPUS_CAMERE = "etc/corpus/camera.corpus";
	public    String CURPUS_MOBILE = "etc/corpus/mobile.corpus";
	public    String CURPUS_NOTEBOOK = "etc/corpus/notebook.corpus";
	
	public   String CURRENT_CORPUS = "";
	
	public  String PATH_PREFIX = "etc/";
	
	public    String ALL_WORDS_FILE = PATH_PREFIX + "all_words.txt";
	public    String FEATURE_WORDS_RULE_GEN_FILE = PATH_PREFIX + "feature_words_rule_gen.txt";
	public    String FEATURE_WORDS_RULE_FILTER_FILE = PATH_PREFIX + "feature_words_rule_filter.txt";
	public    String FEATURE_WORDS_STATISTIC_FILE = PATH_PREFIX + "feature_words_statistics.txt";
	public    String RECORD_FILE = PATH_PREFIX + "record.txt";
	public    String FEATURE_WORDS_STATISTIC_FILTER_FILE = PATH_PREFIX + "feature_words_statistics_filter.txt";
	public    String ALL_WORDS_STATISTIC_FILE = PATH_PREFIX + "all_words_statistics_filter.txt";
	public    String FO_RULE1_FILE = PATH_PREFIX + "FO_Rule1.txt";
	public    String FO_RULE2_FILE = PATH_PREFIX + "FO_Rule2.txt";
	public    String FO_BASESTAT_FILE = PATH_PREFIX + "FO_BaseStat.txt";
	public    String FEATURE_OPINION_POLARITY_FILE = PATH_PREFIX + "feature_opinion_polarity.txt";
	public    String FEATURE_OPINION_POLARITY_UPDATED_FILE = PATH_PREFIX + "feature_opinion_polarity_updated.txt";
	public    String RESULT_FILE = PATH_PREFIX + "result.txt";
	public    String FO_ALL_FILE = PATH_PREFIX + "FO_all.txt";
	
	
	public  static final int MAX_CORPUS_SIZE = 10000;
	
	public void setMP3(){
		CURRENT_READER = READ_FILENAME_MP3;
		CURRENT_CORPUS = CURPUS_MP3;
		PATH_PREFIX = "etc/mp3/";
		updateFilenames();
	}
	
	public void setCamera(){
		CURRENT_READER = READ_FILENAME_CAMERA;
		CURRENT_CORPUS = CURPUS_CAMERE;
		PATH_PREFIX = "etc/camera/";
		updateFilenames();
	}
	
	public void setMobile(){
		CURRENT_READER = READ_FILENAME_MOBILE;
		CURRENT_CORPUS = CURPUS_MOBILE;
		PATH_PREFIX = "etc/mobile/";
		updateFilenames();
	}
	
	public void setNoteBook(){
		CURRENT_READER = READ_FILENAME_NOTEBOOK;
		CURRENT_CORPUS = CURPUS_NOTEBOOK;
		PATH_PREFIX = "etc/notebook/";
		updateFilenames();
	}
	
	public void updateFilenames(){
		 ALL_WORDS_FILE = PATH_PREFIX + "all_words.txt";
		 FEATURE_WORDS_RULE_GEN_FILE = PATH_PREFIX + "feature_words_rule_gen.txt";
		 FEATURE_WORDS_RULE_FILTER_FILE = PATH_PREFIX + "feature_words_rule_filter.txt";
		 FEATURE_WORDS_STATISTIC_FILE = PATH_PREFIX + "feature_words_statistics.txt";
		 RECORD_FILE = PATH_PREFIX + "record.txt";
		 FEATURE_WORDS_STATISTIC_FILTER_FILE = PATH_PREFIX + "feature_words_statistics_filter.txt";
		 ALL_WORDS_STATISTIC_FILE = PATH_PREFIX + "all_words_statistics_filter.txt";
		 FO_RULE1_FILE = PATH_PREFIX + "FO_Rule1.txt";
		 FO_RULE2_FILE = PATH_PREFIX + "FO_Rule2.txt";
		 FO_BASESTAT_FILE = PATH_PREFIX + "FO_BaseStat.txt";
		 FEATURE_OPINION_POLARITY_FILE = PATH_PREFIX + "feature_opinion_polarity.txt";
		 FEATURE_OPINION_POLARITY_UPDATED_FILE = PATH_PREFIX + "feature_opinion_polarity_updated.txt";
		 RESULT_FILE = PATH_PREFIX + "result.txt";
		 FO_ALL_FILE = PATH_PREFIX + "FO_all.txt";
	}
}
