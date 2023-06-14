package database.model;

import android.database.Cursor;

public interface IData {
    String getFieldValue(String columnName);

    void setFieldValue(String columnName, String value);
}
