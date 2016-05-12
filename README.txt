本项目是基于规则方法的对电子商品评论的情感分析，主要流程：
——数据预处理（包括LTP分词）
——基于规则的方法抽取特征词+过滤，基于统计的方法抽取特征词+过滤
——基于规则的方法抽取特征词对应的观点词，基于统计的方法抽取观点词
——<特征词，观点词>的倾向性标注
——根据每条评论中的<特征词，观点词>词对进行分类打分：-1, 0, 1
——统计准确率、召回率

**********************************程序结构说明***************************************
总程序包含两个工程：

1. PreProcess
	数据预处理程序，包括三个模块：
	1) getCategory.py 获取数据集中产品的分类——mp3/mp4,相机，手机，笔记本
	2) src/ProductReviewList.java 解析xml文档，获得每条review的tile及内容、score、date、time信息，输出在corpus文件夹中，分别为：
		mp3_mp4.corpus
		camera.corpus
		mobile.corpus
		notebook.corpus
	3) src/LTPProcess.java 将抽出的评论进行LTP分词处理，将结果以xml格式存储在corpus文件夹中，分别是：
		mp3_mp4.ltp.format.xml
		camera.ltp.format.xml 
		mobile.ltp.format.xml
		notebook.ltp.format.xml
2. OpinionMining
	程序包含以下几大模块：
	1) so.dom4j Dom4jParser负责读取经过LTP分析后的XML文件，将分词结果、词性标注、依存句法关系等信息载入到经过Java类封装的列表，方便后续处理。
	2) so.lexicon是判断某个词性质的包，主要包括以下几个子包：
		negative 负向情感词
		positive 正向情感词
		escape   转义副词
		degree	 程度副词
		conjunctive 表承接关系的连词
		disjunctive 表转折关系的连词
	3) so.word.stop是判断某个词是否为停用词的类
	4)so.word.feature是抽取特征词的包，其中有
		RuleBasedGen 基于规则抽取属性词
		RuleBasedFilter 基于规则过滤属性词
		StatisticsBasedGen 基于统计扩展和过滤属性词
	5) so.word.opinion是抽取属性词观点词对的包，其中有
		Rule_1 基于规则1抽取属性观点对
		Rule_2 基于规则2抽取属性观点对
		BasedRelation 基于统计抽取属性观点对	
		BasedStat 基于统计过滤属性观点对
	6) so.pair.polarity是对属性观点对进行倾向性标注的包，其中
		Polarity是对其进行倾向性标注的类
	7) so.sentence.polarity是对语料库进行观点分类的包，其中
		Polarity是对语料库中的每条评论进行逐一分类的类
	8) basic FileManipulator类是进行主要读写文件操作的包
	9) test 是进行评价指标计算的类，计算准确率和召回率

	程序使用的主要使用的数据结构：
	beans包，其中
		Pair用来封装一对键值对
		Word用来封装一个词，包括词的文本内容、词性、与其相关的依存句法关系等
		Sentence用来封装一个句子，主要是由词构成的列表
		Position用来封装一个属性观点对在句子中的位置信息

	输入输出信息：
	本程序的所有输入输出结果均在etc文件夹下，程序采用递增式方法，每一步的输出都保存为中间结果并存储于文件中，作为后续工作的输入
		test_parser.xml 语料库经LTP分词后的结果
		pos.txt正向情感词表
		neg.txt负向情感词表
		stopwords.txt停用词表
		wp.txt标点符号表
		escape.txt转移副词表
		degree.txt程度副词表
		conjunctive.txt承接连词表
		disjunctive.txt转折连词表
		all_words.txt每个词所在句子的记录
		feature_words_rule_gen.txt基于规则抽取的属性词
		feature_words_rule_filter.txt基于规则过滤后的属性词
		feature_words_statistics.txt基于统计扩展和过滤后的属性词
		FO_Rule1.txt基于规则1抽取的属性观点对
		FO_Rule2.txt基于规则2抽取的属性观点对
		feature_opinion_polarity.txt属性观点对倾向性标注结果1
		feature_opinion_polarity_updated.txt属性观点对倾向性迭代更新后的结果2
		result.txt语料库观点分类的结果
		label.txt测试数据

**********************************运行方式说明***************************************

在terminal/cmd中cd到本文件夹下后，

1. getCategory.py 运行
	在安装了python的情况下，输入命令：
	>cd PreProcess
	>python PreProcess/getCategory.py 

2. PreProcess.jar 运行
	在安装了java的情况下，输入命令：
	>cd PreProcess
	>java -jar preprocess.java
	(注：由于LTP是http get方式获取分词信息的，因此该程序运行时间长，且输出信息较多)

3. OpinionMing 运行
	该程序的jar包在OpinionMining文件夹下，名称：run.jar，运行命令：

	>cd OpinionMining
	>java -jar run.jar [opt]

	注：参数opt说明
	- 无参数：默认选择mp3_mp4分类下的评论分析
	- mp3: 运行mp3_mp4分类下的评论分析
	- camera: 运行相机分类下的评论分析
	- mobile: 运行手机分类下的评论分析
	- notebook: 运行笔记本电脑下的评论分析
	
	