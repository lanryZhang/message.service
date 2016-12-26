package ifeng.component.mongo.query;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Where {
    private List<WhereItem> andList = new ArrayList<WhereItem>();
    private List<WhereItem> orList = new ArrayList<WhereItem>();
    private String whereScript = "";

    public Where() {
    }

    public Where(String name, Object value) {
        andList.add(new WhereItem(name, value));
    }

    public Where(String name, WhereType whereType, Object value) {
        andList.add(new WhereItem(name, whereType, value));
    }

    public Where and(String name, Object value) {
        andList.add(new WhereItem(name, value));
        return this;
    }

    public Where and(String name, WhereType whereType, Object value) {
        andList.add(new WhereItem(name, whereType, value));
        return this;
    }

    public Where or(String name, Object value) {
        orList.add(new WhereItem(name, value));
        return this;
    }

    public Where or(String name, WhereType whereType, Object value) {
        orList.add(new WhereItem(name, whereType, value));
        return this;
    }

    public Where $where(String script){
        whereScript = script;
        return this;
    }
    private Document parseAndList() {
        Document condition = new Document();
        if (this.andList != null) {
            for (WhereItem item : this.andList) {
                if (condition.containsKey(item.getName())) {
                    Document doc = (Document) condition.get(item.getName());
                    doc.append(item.getWhereType().value(), item.getValue());
                    condition.put(item.getName(), doc);
                } else {
                    if (item.getWhereType() == WhereType.Equal) {
                        condition.put(item.getName(), item.getValue());
                    } else {
                        condition.put(item.getName(), new Document(item.getWhereType().value(), item.getValue()));
                    }
                }
            }
        }
        return condition;
    }

    private List<Document> parseOrList() {
        List<Document> docs = new ArrayList<Document>();
        if (this.orList != null) {
            Map<String, Integer> keys = new HashMap<String, Integer>();
            for (WhereItem item : this.orList) {
                int i = 0;
                if (keys.containsKey(item.getName())) {
                    int index = keys.get(item.getName());
                    Document doc = (Document) docs.get(index);
                    doc.append(item.getWhereType().value(), item.getValue());
                } else {
                    if (item.getWhereType() == WhereType.Equal) {
                        docs.add(new Document(item.getName(), item.getValue()));
                    } else {
                        docs.add(new Document(item.getName(), new Document(item.getWhereType().value(), item.getValue())));
                    }
                    keys.put(item.getName(), i++);
                }
            }
        }
        return docs;
    }

    public Document toDocument() {
        Document condition = parseAndList();
        List<Document> docs = parseOrList();
        if (docs.size() > 0) {
            condition.append("$or", docs);
        }
        if (!whereScript.equals("")){
            condition.append("$where",whereScript);
        }
        return condition;
    }

    public List<WhereItem> getAndList() {
        return andList;
    }

    public List<WhereItem> getOrList() {
        return orList;
    }
}
