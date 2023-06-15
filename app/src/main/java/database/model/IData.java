package database.model;

import android.database.Cursor;

public interface IData {
    int getId();
    String getName();
    String getRemark();
    String getFieldValue(String columnName);

    void setFieldValue(String columnName, String value);
}
