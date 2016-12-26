package ifeng.component.mongo;

import com.ifeng.core.data.IDecode;
import com.ifeng.core.data.IEncode;
import com.ifeng.core.data.ILoader;
import org.bson.Document;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhanglr on 2016/2/24.
 */

/**
 * 实体序列化成Bson
 */
public abstract class MongoCodec implements IDecode,IEncode {
    /**
     * 将Bson转换成实体类
     * @param loader
     */
    @Override
    public abstract void decode(ILoader loader);

    /**
     * 将实体转换成Bson
     * @param <T>
     * @return
     */
    @Override
    public <T> T encode(){
        return (T) encode(this);
    }

    private <T> T encode(T t){
        Document en = new Document();
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            if (fields != null){
                for (Field field : fields) {

                    if (MongoCodec.class.isAssignableFrom(field.getType())
                            && MongoCodec.class.isAssignableFrom(field.getType())){
                        Document res = (Document) encode(field.get(t));
                        en.put(field.getName(), res);
                    }else if (Collection.class.isAssignableFrom(field.getType())){
                        Collection cols = (Collection) field.get(t);
                        List<T> res = new ArrayList<>();
                        cols.forEach(r->{
                            if (MongoCodec.class.isAssignableFrom(r.getClass())){
                                res.add(encode((T)r));
                            }
                        });

                        en.put(field.getName(), res);
                    }else{
                        en.put(field.getName(), field.get(t));
                    }
                }
            }
        } catch (Exception e) {
        }

        return (T) en;
    }
}
