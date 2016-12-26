package com.ifeng.core.data;

/**
 * Created by zhanglr on 2016/10/13.
 */
public abstract class AbsDataLoader implements ILoader {
    @Override
    public int getInt(String key, int defaultValue) {
        return 0;
    }

    @Override
    public int getInt(String key) {
        return 0;
    }

    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public Long getLong(String key) {
        return null;
    }

    @Override
    public ILoader getLoader(String key) throws Exception {
        return null;
    }
    public abstract String getString(String family, String key);
    public abstract String getRowKey();
}
