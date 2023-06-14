package database.table;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import database.model.DBConstants;
import lombok.Data;

@Data
public class TableColumnDef {
    public static final String TABLE_NAME = "table_update";
    //自增id
    public static final String COLUMN_ID = "id";
    //table名称
    public static final String COLUMN_TABLE_NAME = "table_name";
    //新增字段名称
    public static final String COLUMN_COL_NAME = "column_name";
    //字段类型
    public static final String COLUMN_TYPE = "column_type";
    //字段描述
    public static final String COLUMN_DESC = "column_desc";

    private int id;
    private String table_name;
    private String column_name;
    private String column_type;
    private String column_desc;

    public TableColumnDef() {

    }

    public TableColumnDef(String table_name, String column_name, String column_type, String column_desc) {
        this.table_name = table_name;
        this.column_name = column_name;
        this.column_type = column_type;
        this.column_desc = column_desc;
    }


    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +
                "(" +
                COLUMN_ID + " " + DBConstants.DATA_TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TABLE_NAME + " " + DBConstants.DATA_TYPE_TEXT + " NOT NULL," +
                COLUMN_COL_NAME + " " + DBConstants.DATA_TYPE_TEXT + " NOT NULL," +
                COLUMN_TYPE + " " + DBConstants.DATA_TYPE_TEXT + " NOT NULL," +
                COLUMN_DESC + " " + DBConstants.DATA_TYPE_TEXT +
                ")";
    }

    /**
     * 从数据库解析Message
     *
     * @param cursor
     * @return
     */
    public static TableColumnDef parse(Cursor cursor) {
        TableColumnDef tableUpdate = new TableColumnDef();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case COLUMN_ID:
                    tableUpdate.setId(cursor.getInt(i));
                    break;
                case COLUMN_TABLE_NAME:
                    tableUpdate.setTable_name(cursor.getString(i));
                    break;
                case COLUMN_COL_NAME:
                    tableUpdate.setColumn_name(cursor.getString(i));
                    break;
                case COLUMN_TYPE:
                    tableUpdate.setColumn_type(cursor.getString(i));
                    break;
                case COLUMN_DESC:
                    tableUpdate.setColumn_desc(cursor.getString(i));
                    break;
            }
        }

        return tableUpdate;
    }

    private ContentValues convertToContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TABLE_NAME, getTable_name());
        contentValues.put(COLUMN_COL_NAME, getColumn_name());
        contentValues.put(COLUMN_TYPE, getColumn_type());
        contentValues.put(COLUMN_DESC, getColumn_desc());
        return contentValues;
    }

    //将更改或新增的数据存储到数据库
    public void saveToDatabase() {
        DatabaseManager.getInstance().insertOrUpdateData(TABLE_NAME, getId(), convertToContentValue());
    }
}