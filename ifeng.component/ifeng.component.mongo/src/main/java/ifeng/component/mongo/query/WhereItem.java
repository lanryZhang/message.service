package ifeng.component.mongo.query;

public class WhereItem {
	private String name;
	private WhereType whereType;
	private Object value;

	public WhereItem(String name, Object value) {
		this.name = name;
		this.whereType = WhereType.Equal;
		this.value = value;
	}

	public WhereItem(String name, WhereType whereType, Object value) {
		this.name = name;
		this.whereType = whereType;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public WhereType getWhereType() {
		return whereType;
	}

	public Object getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWhereType(WhereType whereType) {
		this.whereType = whereType;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
