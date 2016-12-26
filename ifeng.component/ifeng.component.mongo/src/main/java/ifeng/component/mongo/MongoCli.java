package ifeng.component.mongo;
import com.mongodb.MapReduceCommand;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.MapReduceAction;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import ifeng.component.mongo.query.SelectField;
import ifeng.component.mongo.query.Where;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.*;
import java.util.Map.Entry;

public class MongoCli implements IMongo {

	private MongoClient mongoClient;
	private MongoDatabase db;
	private MongoCollection collection;
	private List<ServerAddress> serverAddresses;
	private List<MongoCredential> credentials;
	private static final Logger logger = Logger.getLogger(MongoCli.class);

	public MongoCli(List<ServerAddress> serverAddresses , List<MongoCredential> credentials) {
	    this.serverAddresses=serverAddresses;
	    this.credentials =credentials;
		initMongo();
	}
	private void initMongo() {
		try {
			mongoClient = new MongoClient(serverAddresses, credentials);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 切换数据库
	 * 
	 * @param dbname
	 *            数据库名称
	 */
	public void changeDb(String dbname) {
		if (mongoClient == null) {
			throw new NullPointerException();
		}

		db = mongoClient.getDatabase(dbname);
	}

	/**
	 * 获取集合
	 *
	 * @param name
	 *            集合名称
	 */
	public void getCollection(String name) {
		if (db == null) {
			throw new NullPointerException();
		}
		collection = db.getCollection(name);
	}

	public MongoCollection getCollections(String name) {
		if (db == null) {
			throw new NullPointerException();
		}
		return  db.getCollection(name);
	}


	private Document createUpdateFields(Map<String, Object> fields) {
		Document result = new Document();
		if (fields != null) {
			for (Entry<String, Object> item : fields.entrySet()) {
				result.put(item.getKey(), item.getValue());
			}
		}
		return new Document("$set",result);
	}

	private Map<String, String> createMapReduce(MongoSelect select) throws Exception {
		if (select.getGroupBy() != null && select.getGroupBy().size() > 0) {
			StringBuilder values = new StringBuilder();
			StringBuilder recudeVar = new StringBuilder();
			List<String> vars = new ArrayList<String>();
			boolean hasAvgMethod = false;
			for (SelectField item : select.getFields()) {
				String fn = item.getAlias().trim();
				String name = item.getName().trim().toLowerCase();
				if (name.contains("sum(")) {
					values.append(fn).append(":this.").append(item.getName().replace("sum(", "").replace(")", "")).append(",");
					recudeVar.append("var ").append(fn).append("=0;");
					if (!vars.contains(fn)) {
						vars.add(fn);
					} else {
						throw new Exception("查询字段名不能重复.");
					}
				}
				if (name.contains("count(")) {
					values.append(fn).append(":1,");
					recudeVar.append("var ").append(fn).append("=0;");
					if (!vars.contains(fn)) {
						vars.add(fn);
					} else {
						throw new Exception("查询字段名不能重复.");
					}
				}
				if (name.contains("max(")) {
					values.append(fn).append(":Math.max(this.").append(item.getName().replace("max(", "")).append(",");
					recudeVar.append("var ").append(fn).append("=0;");
					if (!vars.contains(fn)) {
						vars.add(fn);
					} else {
						throw new Exception("查询字段名不能重复.");
					}
				}
				if (name.contains("min(")) {
					values.append(fn).append(":Math.min(this.").append(item.getName().replace("min(", "")).append(",");
					recudeVar.append("var ").append(fn).append("=0;");
					if (!vars.contains(fn)) {
						vars.add(fn);
					} else {
						throw new Exception("查询字段名不能重复.");
					}
				}
				if (name.contains("avg(")) {
					hasAvgMethod = true;
					values.append(fn).append(":this.").append(item.getName().replace("avg(", "").replace(")", "")).append(",");
					recudeVar.append("var ").append(fn).append("=0;");
					if (!vars.contains(fn)) {
						vars.add(fn);
					} else {
						throw new Exception("查询字段名不能重复.");
					}
				}
			}
			if (values.length() <= 0) {
				return null;
			}
			values = values.deleteCharAt(values.length() - 1);
			StringBuilder groupKeys = new StringBuilder();
			for (String item : select.getGroupBy()) {
				groupKeys.append(item).append(":").append("this.").append(item).append(",");
			}
			groupKeys = groupKeys.deleteCharAt(groupKeys.length() - 1);

			StringBuilder mapCode = new StringBuilder();
			mapCode.append("function(){emit({").append(groupKeys).append("},{").append(values).append("});}");

			StringBuilder finalizeFunc = new StringBuilder();
			StringBuilder reduceCode = new StringBuilder();
			StringBuilder returnObj = new StringBuilder("return {");

			reduceCode.append("function(key,values){");
			reduceCode.append(recudeVar);
			reduceCode.append("for(var i = 0; i < values.length;i++){");

			for (String var : vars) {

				reduceCode.append(var).append(" += values[i].").append(var).append(";");

				returnObj.append(var).append(":").append(var).append(",");
			}
			if (hasAvgMethod) {
				finalizeFunc.append("function (key, reducedValue) {").append("reducedValue.Avg = reducedValue.Sum/reducedValue.Count;")
						.append("return reducedValue;}");
			}

			returnObj = returnObj.length() > 8 ? returnObj.deleteCharAt(returnObj.length() - 1).append("}") : new StringBuilder("");
			reduceCode.append("} ").append(returnObj).append(";}");
			Map<String, String> result = new HashMap<String, String>();
			result.put("map", mapCode.toString());
			result.put("reduce", reduceCode.toString());
			return result;
		}
		return null;
	}

	@Override
	public <T extends MongoCodec> List<T> distinct(MongoSelect select, Class<T> classType){
//		Document fields = select.createFieldsDocument();
//		Document where = select.getCondition().toDocument();
//		Document sort = select.createSortDocument();
//
//		MongoCursor cursor = collectionfilter(where).skip(select.getPageIndex()).limit(select.getPageSize()).batchSize(select.getPageSize()).sort(sort)
//				.iterator();
		return null;
	}
	@Override
    public DistinctIterable<String> distinct(String distinctName, Document where) throws IllegalAccessException, InstantiationException {

		return collection.distinct(distinctName,where,String.class);

    }
	@Override
	public <T extends MongoCodec> List<T> selectAll(MongoSelect select, Class<T> classType) throws Exception {
		if (collection == null || select == null){
			throw new NullPointerException();
		}

		Document sort = select.createSortDocument();
		Document fields = select.createFieldsDocument();
		MongoCursor cursor = null;
		boolean isMapReduce = false;
		if (select.getGroupBy() != null && select.getGroupBy().size() > 0) {
			Map<String, String> mapReduce = createMapReduce(select);
			MapReduceIterable output = collection.mapReduce(mapReduce.get("map"), mapReduce.get("reduce"));
			cursor = output.action(MapReduceAction.REPLACE).collectionName("reduceCollection")
					.limit(select.getPageIndex()).batchSize(select.getPageSize()).sort(sort).iterator();
			isMapReduce = true;
		} else {
			cursor =collection.find(fields).skip(select.getPageIndex()).limit(select.getPageSize()).batchSize(select.getPageSize()).sort(sort).iterator();
		}

		ArrayList<T> list = new ArrayList<T>();
		T en ;
		while (cursor.hasNext()) {
			en = classType.newInstance();
			MongoDataLoader loader = new MongoDataLoader((Document)cursor.next(),isMapReduce);
			en.decode(loader);
			list.add(en);
		}
		return list;
	}
	@Override
	public Map<String, Object> selectAll(MongoSelect select) throws Exception {
		if (collection == null || select == null){
			throw new NullPointerException();
		}

		Document sort = select.createSortDocument();
		Document fields = select.createFieldsDocument();
		MongoCursor cursor = null;
		if (select.getGroupBy() != null && select.getGroupBy().size() > 0) {
			Map<String, String> mapReduce = createMapReduce(select);
			MapReduceIterable output = collection.mapReduce(mapReduce.get("map"), mapReduce.get("reduce"));
			cursor = output.action(MapReduceAction.REPLACE).collectionName("reduceCollection")
					.limit(select.getPageIndex()).batchSize(select.getPageSize()).sort(sort).iterator();
		} else {
			cursor =collection.find(fields).skip(select.getPageIndex()).limit(select.getPageSize()).batchSize(select.getPageSize()).sort(sort).iterator();
		}

		while (cursor.hasNext()) {
			Document temp = (Document) cursor.next();
		}
		return (Document)cursor;
	}

	@Override
	public <T extends MongoCodec> T selectOne(MongoSelect select, Class<T> classType) throws Exception {
		if (collection == null || select == null){
			throw new NullPointerException();
		}
		T en = classType.newInstance();
		Document where = select.getCondition().toDocument();
		Document fields = select.createFieldsDocument();
		Document sort = select.createSortDocument();
		FindIterable res = collection.find(fields).filter(where).sort(sort);
		if (res == null || !res.iterator().hasNext())
			return null;

		MongoDataLoader loader = new MongoDataLoader((Document)res.iterator().next());
		en.decode(loader);

		return en;
	}

	@Override
	public <T extends MongoCodec> List<T> selectList(MongoSelect select, Class<T> classType) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		Document fields =  select.createFieldsDocument();
		Document where = select.getCondition().toDocument();
		Document sort = select.createSortDocument();

		boolean isMapReduce = false;

		MongoCursor cursor = null;
		if (select != null) {

			if (select.getGroupBy() != null && select.getGroupBy().size() > 0) {
				Map<String, String> mapReduce = createMapReduce(select);
				MapReduceIterable output = collection.mapReduce(mapReduce.get("map"), mapReduce.get("reduce"));
				cursor = output.filter(where).action(MapReduceAction.REPLACE).collectionName("reduceCollection")
						.limit(select.getPageIndex()).batchSize(select.getPageSize()).sort(sort).iterator();

				isMapReduce = true;
			} else {
				cursor = collection.find(fields).filter(where).skip(select.getPageIndex()).limit(select.getPageSize()).batchSize(select.getPageSize()).sort(sort)
				.iterator();
			}
		}

		ArrayList<T> list = new ArrayList<T>();
		T en = null;
		while (cursor.hasNext()) {
			en = classType.newInstance();
			MongoDataLoader loader = new MongoDataLoader((Document)cursor.next(), isMapReduce);
			en.decode(loader);
			list.add(en);
		}
		return list;
	}

	@Override
	public <T extends MongoCodec> List<T> selectListByAggregate(MongoSelect select, Class<T> classType) throws Exception {

		if (collection == null || select == null){
			throw new NullPointerException();
		}

		Document sort = select.createSortDocument();
		Document fields = select.createFieldsDocument();
		Document where = select.getCondition().toDocument();


		List list = new ArrayList();
		if(where.size()>0){
			list.add(new Document("$match",where));
		}
		if(fields.size()>0 && !(select.getGroupBy() != null && select.getGroupBy().size()>0)){//
			list.add(new Document("$project",fields));
		}
		if(sort.size()>0){
			list.add(new Document("$sort",sort));
		}
		if (select.getGroupBy() != null && select.getGroupBy().size() > 0) {

			Document document=new Document();
			if(select.getGroupBy().size() >1) {
				//多groupby,下边方法正确,但序列化没实现,所以注释了
				//Document docGroupBy=new Document();
				//for (String fieldName : select.getGroupBy()) {
				//			docGroupBy.append(fieldName,"$"+fieldName);//( 别名,$字段名 )
				//}
				//document.append("_id",docGroupBy);
				return null;//多groupby 方式返回结果,_id格式为数组格式,{ "_id" : { "subjname" : "英语" , "province" : "海南"}}
			}else{
				document.append("_id","$"+select.getGroupBy().get(0));
			}

			for (SelectField item : select.getFields()) {
				String fn=item.getAlias().trim(); ;//别名
				String field=item.getName();//count(字段名)

				if (field.contains("count(")) {
					document.append(fn, new Document("$sum", 1));
				}
				if (field.contains("sum(")) {
					document.append(fn, new Document("$sum", "$"+ field.replace("sum(","").replace(")","")));;//字段名
				}
				if (field.contains("avg(")) {
					document.append(fn, new Document("$avg", "$"+ field.replace("avg(","").replace(")","")));
				}
				if (field.contains("max(")) {
					document.append(fn, new Document("$max", "$"+ field.replace("max(","").replace(")","")));
				}
				if (field.contains("min(")) {
					document.append(fn, new Document("$min", "$"+ field.replace("min(","").replace(")","")));
				}
				if (field.contains("first(")) {
					document.append(fn, new Document("$first", "$"+ field.replace("first(","").replace(")","")));
				}
				if (field.contains("last(")) {
					document.append(fn, new Document("$last", "$"+ field.replace("last(","").replace(")","")));
				}
			}
			list.add(new Document("$group",document));
		}
		if(select.getPageIndex()>0){
			list.add(new Document("$skip",select.getPageIndex()));
		}
		if(select.getPageSize()>0){
			list.add(new Document("$limit",select.getPageSize()));
		}

		MongoCursor cursor = collection.aggregate(list).iterator();
		ArrayList<T> listResult = new ArrayList<T>();
		T en;
		while (cursor.hasNext()) {
			en = classType.newInstance();
			MongoDataLoader loader = new MongoDataLoader((Document)cursor.next(),false);
			en.decode(loader);
			listResult.add(en);
		}
		return listResult;
	}

	@Override
	public DeleteResult remove(Where where) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		Document condition = where == null ? new Document():where.toDocument();
		return collection.deleteMany(condition);
	}

	@Override
	public UpdateResult update(Map<String, Object> fields, Where where) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		Document condition = where == null ? new Document():where.toDocument();
		Document updates = createUpdateFields(fields);
		return collection.updateMany(condition,updates);
	}

	@Override
	public UpdateResult update(Map<String, Object> fields, Where where,Boolean upsert) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		Document condition = where == null ? new Document():where.toDocument();
		Document updates = createUpdateFields(fields);
		return collection.updateMany(condition, updates,new UpdateOptions().upsert(upsert));
	}

	@Override
	public <T extends MongoCodec> void insert(T en) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}

		Document document = en.encode();
		collection.insertOne(document, new InsertOneOptions().bypassDocumentValidation(true));
	}

	@Override
	public <T extends MongoCodec> void insert(List<T> list) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		List<Document> documents = new ArrayList<Document>();
		for (T t : list) {
			documents.add(t.encode());
		}
		collection.insertMany(documents);
	}


	@Override
	public <T extends MongoCodec> void insert(T en, Date expire) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}

		Document document = en.encode();
		expire = expire == null ? expire : new Date();
		document.put("delkey",expire);
		collection.insertOne(document, new InsertOneOptions().bypassDocumentValidation(true));
	}

	@Override
	public <T extends MongoCodec> void insert(List<T> list, Date expire) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		List<Document> documents = new ArrayList<Document>();

		Document doc = null;
		for (T t : list) {
			doc = t.encode();
			doc.put("delkey",expire);
			documents.add(doc);
		}
		collection.insertMany(documents);
	}

	@Override
	public MongoCursor mapReduce(String map, String reduce, String outputTarget,
                                 MapReduceCommand.OutputType outputType, Where where) throws Exception{
		if (collection == null){
			throw new NullPointerException();
		}
		Document cond = where == null ? new Document():where.toDocument();
		MapReduceIterable mapReduceIterable = collection.mapReduce(map, reduce);
		MongoCursor cusor = mapReduceIterable.action(MapReduceAction.REPLACE).collectionName(outputTarget)
				.filter(cond).iterator();
		return cusor;
	}

	@Override
	public int count(MongoSelect select) throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		Document fields = select.getCondition().toDocument();
		return (int)collection.count(fields);
	}
	@Override
	public int count() throws Exception {
		if (collection == null){
			throw new NullPointerException();
		}
		return (int)collection.count();
	}
	@Override
	public void close() throws Exception{
		if (mongoClient == null){
			throw new NullPointerException();
		}
		try {
			mongoClient.close();
		} catch (Exception e) {
		}
		finally{
			collection = null;
			mongoClient =null;
			db = null;
		}
	}

	// ===========Setter Getter======================
	public List<MongoCredential> getCredentials() {
		return credentials;
	}

	public void setCredentials(List<MongoCredential> credentials) {
		this.credentials = credentials;
	}

	public List<ServerAddress> getServerAddresses() {
		return serverAddresses;
	}

	public void setServerAddresses(List<ServerAddress> serverAddresses) {
		this.serverAddresses = serverAddresses;
	}
}
