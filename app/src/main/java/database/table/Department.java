package database.table;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import database.DatabaseManager;
import database.model.DBConstants;
import database.model.IData;
import lombok.Data;

@Data
public class Department implements Table, IData {
    public static final String TABLE_NAME = "department";
    //自增id
    public static final String COLUMN_ID = "id";
    //科室名称
    public static final String COLUMN_NAME = "name";

    //备注
    public static final String COLUMN_REMARK = "remark";

    private int id = -1;
    private String name;
    private String remark;
    private Map<String, String> customerFields;
    private static List<TableColumnDef> originFieldInfos = null;//原始字段
    private static TreeSet<TableColumnDef> customerFieldInfos = null;//客户自定义字段

    public Department() {
        if(null == originFieldInfos) {
            originFieldInfos = new ArrayList<>();
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_NAME, DBConstants.DATA_TYPE_TEXT, "科室名称"));
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_REMARK, DBConstants.DATA_TYPE_TEXT, "备注"));
        }

        if (null == customerFieldInfos) {
            customerFieldInfos = new TreeSet<>(new Comparator<TableColumnDef>() {
                @Override
                public int compare(TableColumnDef o1, TableColumnDef o2) {
                    return Integer.compare(o1.getId(), o2.getId());
                }
            });
            customerFieldInfos.addAll(DatabaseManager.getInstance().getCustomerField(TABLE_NAME));
        }
    }

    @Override
    public void setFieldValue(String columnName, String value) {
        if (null == columnName || "".equals(columnName)) {
            return;
        }

        switch (columnName) {
            case COLUMN_NAME:
                setName(value);
                break;
            case COLUMN_REMARK:
                setRemark(value);
                break;
            default:
                addCustomerField(columnName, value);
                break;
        }
    }

    @Override
    public String getFieldValue(String columnName) {
        if (null == columnName || "".equals(columnName)) {
            return "";
        }

        switch (columnName) {
            case COLUMN_NAME:
                return getName();
            case COLUMN_REMARK:
                return getRemark();
            default:
                return customerFields.get(columnName);
        }
    }

    public void addCustomerField(String columnName, String columnValue) {
        if (null == customerFields) {
            customerFields = new HashMap<>();
        }
        customerFields.put(columnName, columnValue);
    }

    public static String getCreateSql() {
        return "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +
                "(" +
                COLUMN_ID + " " + DBConstants.DATA_TYPE_INTEGER + " PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " " + DBConstants.DATA_TYPE_TEXT + " NOT NULL," +
                COLUMN_REMARK + " " + DBConstants.DATA_TYPE_TEXT +
                ")";
    }

    /**
     * 从数据库解析Message
     *
     * @param cursor
     * @return
     */
    public static Department parse(Cursor cursor) {
        Department department = new Department();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case COLUMN_ID:
                    department.setId(cursor.getInt(i));
                    break;
                case COLUMN_NAME:
                    department.setName(cursor.getString(i));
                    break;
                case COLUMN_REMARK:
                    department.setRemark(cursor.getString(i));
                    break;
                default:
                    department.addCustomerField(cursor.getColumnName(i), cursor.getString(i));
                    break;
            }
        }

        return department;
    }

    private ContentValues convertToContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, getName());
        contentValues.put(COLUMN_REMARK, getRemark());
        //添加自定义字段信息
        if (null != customerFieldInfos) {
            for (TableColumnDef customerFieldInfo : customerFieldInfos) {
                contentValues.put(customerFieldInfo.getColumn_name(), customerFields.get(customerFieldInfo.getColumn_name()));
            }
        }
        return contentValues;
    }

    //将更改或新增的数据存储到数据库
    @Override
    public void saveToDatabase() {
        DatabaseManager.getInstance().insertOrUpdateData(TABLE_NAME, getId(), convertToContentValue());
    }

    @Override
    public List<TableColumnDef> getColumnDef() {
        List<TableColumnDef> columnDefs = new ArrayList<>();
        columnDefs.addAll(originFieldInfos);
        columnDefs.addAll(customerFieldInfos);
        return columnDefs;
    }
}
