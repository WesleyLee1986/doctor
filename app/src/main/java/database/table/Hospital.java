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
public class Hospital implements Table, IData {
    public static final String TABLE_NAME = "hospital";
    //自增id
    public static final String COLUMN_ID = "id";
    //医院名称
    public static final String COLUMN_NAME = "name";
    //医院种类，私营or民营
    public static final String COLUMN_TYPE = "type";
    //医院地址
    public static final String COLUMN_ADDRESS = "address";
    //区域
    public static final String COLUMN_REGION = "region";
    //备注
    public static final String COLUMN_REMARK = "remark";

    private int id;
    private String name;
    private String address;
    private String region;
    private String type;
    private String remark;
    private Map<String, String> customerFields;
    private static List<TableColumnDef> originFieldInfos = new ArrayList<>();
    private static TreeSet<TableColumnDef> customerFieldInfos = null;//自定义字段

    public Hospital() {
        originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_NAME, DBConstants.DATA_TYPE_TEXT, "医院名称"));
        originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_ADDRESS, DBConstants.DATA_TYPE_TEXT, "医院地址"));
        originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_REGION, DBConstants.DATA_TYPE_TEXT, "区域"));
        originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_TYPE, DBConstants.DATA_TYPE_TEXT, "医院类别(私立or公里)"));
        originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_REMARK, DBConstants.DATA_TYPE_TEXT, "备注"));
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
            case COLUMN_ADDRESS:
                setAddress(value);
                break;
            case COLUMN_REGION:
                setRegion(value);
                break;
            case COLUMN_TYPE:
                setType(value);
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
            case COLUMN_ADDRESS:
                return getAddress();
            case COLUMN_REGION:
                return getRegion();
            case COLUMN_TYPE:
                return getType();
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
                COLUMN_ADDRESS + " " + DBConstants.DATA_TYPE_TEXT + "," +
                COLUMN_REGION + " " + DBConstants.DATA_TYPE_TEXT + "," +
                COLUMN_TYPE + " " + DBConstants.DATA_TYPE_TEXT + "," +
                COLUMN_REMARK + " " + DBConstants.DATA_TYPE_TEXT +
                ")";
    }

    /**
     * 从数据库解析Message
     *
     * @param cursor
     * @return
     */
    public static Hospital parse(Cursor cursor) {
        Hospital hospital = new Hospital();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case COLUMN_ID:
                    hospital.setId(cursor.getInt(i));
                    break;
                case COLUMN_NAME:
                    hospital.setName(cursor.getString(i));
                    break;
                case COLUMN_ADDRESS:
                    hospital.setAddress(cursor.getString(i));
                    break;
                case COLUMN_REGION:
                    hospital.setRegion(cursor.getString(i));
                    break;
                case COLUMN_TYPE:
                    hospital.setType(cursor.getString(i));
                    break;
                case COLUMN_REMARK:
                    hospital.setRemark(cursor.getString(i));
                    break;
                default:
                    hospital.addCustomerField(cursor.getColumnName(i), cursor.getString(i));
                    break;
            }
        }

        return hospital;
    }

    private ContentValues convertToContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, getName());
        contentValues.put(COLUMN_TYPE, getType());
        contentValues.put(COLUMN_ADDRESS, getAddress());
        contentValues.put(COLUMN_REGION, getRegion());
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
