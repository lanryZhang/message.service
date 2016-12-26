package ifeng.component.mongo.query;

import java.util.List;

/**
 * Created by zhanglr on 2016/10/9.
 */
public class Select {
    protected List<SelectField> fields = null;
    protected int pageIndex;
    protected int pageSize;


    public Select(List<SelectField> fields, int pageIndex, int pageSize){
        this.fields =fields;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    /**
     * 添加查询字段
     * @param field 字段名称
     * @return
     */
    public Select addField(String field) {
        this.fields.add(new SelectField(field));
        return this;
    }
    /**
     * 添加查询字段
     * @param field 字段名称
     * @param alias 字段别名
     * @return
     */
    public Select addField(String field, String alias) {
        this.fields.add(new SelectField(field,alias));
        return this;
    }

    public Select page(int pageIndex,int pageSize){
        this.pageIndex = (pageIndex - 1) * pageSize;
        this.pageSize = pageSize;
        return this;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<SelectField> getFields() {
        return fields;
    }
}
