package ifeng.component.mongo.query;

public enum WhereType {
	Equal("$eq"),
	Like("$regex"),
	NotEqual("$ne"),
	GreaterThan("$gt"),
	LessThan("$lt"),
	GreaterAndEqual("$gte"),
	LessAndEqual("$lte"),
	All("$all"),Not("$not"),In("$in"),ElemMatch("$elemMatch"),
	NotIn("$nin");
	private String value;

	WhereType(String v) {
		this.value = v;
	}

	public String value() {
		return this.value;
	}
}
