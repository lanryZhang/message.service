package ifeng.component.mongo;

import ifeng.component.mongo.query.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoSelect {
	private List<SelectField> fields = null;
	private Where condition = new Where();
	private int pageIndex;
	private int pageSize;
	private List<String> groupBy;
	private List<OrderBy> orderBy;
	
	public MongoSelect(){
		this.fields = new ArrayList<>();
		pageIndex = 0;
		pageSize = Integer.MAX_VALUE;
		this.groupBy  = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		condition = new Where();
	}

	public MongoSelect(Where where){
		this.fields = new ArrayList<>();
		pageIndex = 0;
		pageSize = Integer.MAX_VALUE;
		this.groupBy  = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		condition = where;
	}

	public MongoSelect(List<SelectField> fields,Where where ,int pageIndex,int pageSize){
		this.fields =fields;
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.groupBy  = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		condition = where;
	}
	public MongoSelect(List<SelectField> fields){
		this.fields =fields;
		pageIndex = 0;
		pageSize = Integer.MAX_VALUE;
		this.groupBy  = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		condition = new Where();
	}
	
	public MongoSelect(List<SelectField> fields,int pageIndex,int pageSize){
		this.fields =fields; 
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.groupBy  = new ArrayList<>();
		this.orderBy = new ArrayList<>();
		condition = new Where();
	}
	
	/**
	 * 增加筛选条件 按照等于处理
	 * @param name
	 * @param value
	 * @return
	 */
	public MongoSelect where(String name, Object value)
    {
       	where(name, WhereType.Equal, value);
       	return this;
    }
	
	/**
	 * 增加筛选条件
	 * @param name 字段名称
	 * @param whereType 条件
	 * @param value 值
	 * @return
	 */
	public MongoSelect where(String name, WhereType whereType, Object value)
    {
        this.condition.getAndList().add(new WhereItem(name, whereType, value));
        return this;
    }
	
	/**
	 * 增加分组字段
	 * @param field
	 * @return
	 */
	public MongoSelect groupBy(String field)
    {
		groupBy.add(field);
        return this;
    }

	/**
	 * 添加排序字段
	 * @param orderBy 排序实例
	 * @return
	 */
	public MongoSelect orderBy(OrderBy orderBy)
    {
		this.orderBy.add(orderBy);
        return this;
    }

	/**
	 * 添加排序字段
	 * @param name
	 * @param direction
	 * @return
	 */
	public MongoSelect orderBy(String name,OrderByDirection direction)
	{
		this.orderBy.add(new OrderBy(name,direction));
		return this;
	}

	/**
	 * 添加查询字段
	 * @param field 字段名称
	 * @return
	 */
	public MongoSelect addField(String field) {
		this.fields.add(new SelectField(field));
		return this;
	}
	/**
	 * 添加查询字段
	 * @param field 字段名称
	 * @param alias 字段别名
	 * @return
	 */
	public MongoSelect addField(String field,String alias) {
		this.fields.add(new SelectField(field,alias));
		return this;
	}

	public MongoSelect page(int pageIndex,int pageSize){
		this.pageIndex = (pageIndex - 1) * pageSize;
		this.pageSize = pageSize;
		return this;
	}

	int getPageIndex() {
		return pageIndex;
	}

	int getPageSize() {
		return pageSize;
	}

	List<String> getGroupBy() {
		return groupBy;
	}

	List<OrderBy> getOrderBy() {
		return orderBy;
	}

	void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	void setGroupBy(List<String> groupBy) {
		this.groupBy = groupBy;
	}


	public Document createFieldsDocument(){
		Document fields = new Document();
		if (this.getFields() != null) {
			for (SelectField item : this.getFields()) {
				fields.put(item.getAlias(), true);
			}
		}

		return fields;
	}

	Document createSortDocument() {
		Document sort = new Document();
		if (this.getOrderBy() != null && this.getOrderBy().size() > 0) {

			for (OrderBy item : this.getOrderBy()) {
				sort.put(item.name, item.direction.value());
			}
		}
		return sort;
	}
	List<SelectField> getFields() {
		return fields;
	}

	public Where getCondition() {
		return condition;
	}
}
