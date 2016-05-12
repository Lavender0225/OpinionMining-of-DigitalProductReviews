package beans;

public class Word {
	private String content;
	private String pos;
	private String parent;
	private String relation;

	public Word(String content, String pos, String parent, String relation) {
		this.content = content;
		this.pos = pos;
		this.parent = parent;
		this.relation = relation;
	}

	public String toString() {
		return content + "(" + pos + ")";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}
}
