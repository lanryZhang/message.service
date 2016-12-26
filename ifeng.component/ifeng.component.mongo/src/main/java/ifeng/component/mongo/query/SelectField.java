package ifeng.component.mongo.query;


public class SelectField {
	private String name;
	private String alias;

	/**
	 * Only for Hbase
	 * @param name
	 */
	private String family;


	public SelectField(String name) {
		this.name= name;
		this.alias = name;
	}
	public  SelectField(String name, String alias) {
		this.name=name;
		this.alias = alias;
	}
	
	public String getName() {
		return name;
	}
	public String getAlias() {
		return alias;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
}
