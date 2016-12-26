
package ifeng.component.mongo;
import com.ifeng.core.data.ILoader;
import org.bson.Document;

public class MongoDataLoader implements ILoader {
	private Document dbObject;
	
	public MongoDataLoader(Document obj) {
		this.dbObject = obj;
	}
	
	public MongoDataLoader(Document obj, boolean isMapReduceResult) {
		if (isMapReduceResult){
			dbObject = new Document();
			Document _id = (Document)obj.get("_id");
			for (String item : _id.keySet()) {
				dbObject.put(item, _id.get(item));
			}

			Document _values = (Document)obj.get("value");
			for (String item : _values.keySet()) {
				dbObject.put(item, _values.get(item));
			}
		}
		else {
			this.dbObject = obj;
		}
	}

	@Override
	public int getInt(String key, int defaultValue) {
		if (dbObject.containsKey(key)){
			return (int) Double.parseDouble(dbObject.get(key).toString());
		}
		return defaultValue;
	}
	@Override
	public int getInt(String key) {
		return getInt(key,0);
	}
	@Override
	public String getString(String key) {
		if (dbObject.containsKey(key)){
			return dbObject.get(key).toString();
		}
		return "";
	}
	@Override
	public Long getLong(String key) {
		if (dbObject.containsKey(key)){
			return Long.parseLong(dbObject.get(key).toString());
		}
		return Long.valueOf(0);
	}

	@Override
	public MongoDataLoader getLoader(String key) throws Exception {
		try {
			if (dbObject.containsKey(key)){
				return new MongoDataLoader((Document)dbObject.get(key));
			}
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	@Override
	public Object getObject(String key) {
		if (dbObject.containsKey(key) && dbObject.get(key) != null){
			return dbObject.get(key);
		}
		return null;
	}
}
