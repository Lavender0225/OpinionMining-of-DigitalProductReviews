package beans;

public class Position {
	private Integer featureSPos;
	private Integer featureTPos;
	private Integer opinionSPos;
	private Integer opinionTPos;

	private Integer leftSide;
	private Integer rightSide;

	private Integer mLeftSide;
	private Integer mRightSide;

	public Position(int featureSPos, int featureTPos, int opinionSPos,
			int opinionTPos) {
		this.featureSPos = featureSPos;
		this.featureTPos = featureTPos;
		this.opinionSPos = opinionSPos;
		this.opinionTPos = opinionTPos;

		this.leftSide = featureSPos;
		if (featureSPos > opinionSPos)
			this.leftSide = opinionSPos;

		this.rightSide = featureTPos;
		if (featureTPos > opinionTPos)
			this.rightSide = opinionTPos;

		this.mLeftSide = featureTPos;
		this.mRightSide = opinionSPos;
		if (featureSPos > opinionTPos) {
			this.mLeftSide = opinionTPos;
			this.mRightSide = featureSPos;
		}
	}

	public int distance(Position other) {
		int lSide = other.getLeftSide();
		int rSide = other.getRightSide();
		
		if (this.leftSide > rSide)
			return this.leftSide - rSide;
		if (this.rightSide < lSide)
			return lSide - this.rightSide;

		return 1;
	}

	@Override
	public String toString() {
		return "Position [featureSPos=" + featureSPos + ", featureTPos="
				+ featureTPos + ", leftSide=" + leftSide + ", mLeftSide="
				+ mLeftSide + ", mRightSide=" + mRightSide + ", opinionSPos="
				+ opinionSPos + ", opinionTPos=" + opinionTPos + ", rightSide="
				+ rightSide + "]";
	}

	public int getmLeftSide() {
		return mLeftSide;
	}

	public void setmLeftSide(int mLeftSide) {
		this.mLeftSide = mLeftSide;
	}

	public int getmRightSide() {
		return mRightSide;
	}

	public void setmRightSide(int mRightSide) {
		this.mRightSide = mRightSide;
	}

	public int getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(int leftSide) {
		this.leftSide = leftSide;
	}

	public int getRightSide() {
		return rightSide;
	}

	public void setRightSide(int rightSide) {
		this.rightSide = rightSide;
	}

	public int getFeatureSPos() {
		return featureSPos;
	}

	public void setFeatureSPos(int featureSPos) {
		this.featureSPos = featureSPos;
	}

	public int getFeatureTPos() {
		return featureTPos;
	}

	public void setFeatureTPos(int featureTPos) {
		this.featureTPos = featureTPos;
	}

	public int getOpinionSPos() {
		return opinionSPos;
	}

	public void setOpinionSPos(int opinionSPos) {
		this.opinionSPos = opinionSPos;
	}

	public int getOpinionTPos() {
		return opinionTPos;
	}

	public void setOpinionTPos(int opinionTPos) {
		this.opinionTPos = opinionTPos;
	}
}
