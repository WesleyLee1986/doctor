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
public class Doctor implements Table, IData {
    public static final String TABLE_NAME = "doctor";
    //自增id
    public static final String COLUMN_ID = "id";
    //医生名称
    public static final String COLUMN_NAME = "name";
    //科室
    public static final String COLUMN_DEPARTMENT = "department_id";
    //医生所在医院
    public static final String COLUMN_HOSPITAL = "hospital_id";
    public static final String COLUMN_VISIT_DATE = "visit_date";

    //备注
    public static final String COLUMN_REMARK = "remark";

    private int id;
    private String name;
    private int department_id;
    private String departmentName;
    private int hospital_id;
    private String hospitalName;
    private int visit_date;
    private String remark;
    private Map<String, String> customerFields;
    private static List<TableColumnDef> originFieldInfos = null;
    private static TreeSet<TableColumnDef> customerFieldInfos = null;//标识字段顺序

    public Doctor() {
        if(null == originFieldInfos) {
            originFieldInfos = new ArrayList<>();
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_NAME, DBConstants.DATA_TYPE_TEXT, "医生名称"));
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_DEPARTMENT, DBConstants.DATA_TYPE_TEXT, "科室"));
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_HOSPITAL, DBConstants.DATA_TYPE_TEXT, "所在医院"));
            originFieldInfos.add(new TableColumnDef(TABLE_NAME, COLUMN_VISIT_DATE, DBConstants.DATA_TYPE_TEXT, "出诊日期"));
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
            case COLUMN_DEPARTMENT:
                setDepartmentName(value);
                break;
            case COLUMN_HOSPITAL:
                setHospitalName(value);
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
            case COLUMN_DEPARTMENT:
                return getDepartmentName();
            case COLUMN_HOSPITAL:
                return getHospitalName();
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
                COLUMN_DEPARTMENT + " " + DBConstants.DATA_TYPE_INTEGER + "," +
                COLUMN_HOSPITAL + " " + DBConstants.DATA_TYPE_INTEGER + "," +
                COLUMN_VISIT_DATE + " " + DBConstants.DATA_TYPE_INTEGER + "," +
                COLUMN_REMARK + " " + DBConstants.DATA_TYPE_TEXT +
                ")";
    }

    /**
     * 从数据库解析Message
     *
     * @param cursor
     * @return
     */
    public static Doctor parse(Cursor cursor) {
        Doctor doctor = new Doctor();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            switch (cursor.getColumnName(i)) {
                case COLUMN_ID:
                    doctor.setId(cursor.getInt(i));
                    break;
                case COLUMN_NAME:
                    doctor.setName(cursor.getString(i));
                    break;
                case COLUMN_DEPARTMENT:
                    doctor.setDepartment_id(cursor.getInt(i));
                    break;
                case COLUMN_HOSPITAL:
                    doctor.setHospital_id(cursor.getInt(i));
                    break;
                case COLUMN_VISIT_DATE:
                    doctor.setVisit_date(cursor.getInt(i));
                    break;
                case COLUMN_REMARK:
                    doctor.setRemark(cursor.getString(i));
                    break;
                default:
                    doctor.addCustomerField(cursor.getColumnName(i), cursor.getString(i));
                    break;
            }
        }

        return doctor;
    }

    private ContentValues convertToContentValue() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, getName());
        contentValues.put(COLUMN_DEPARTMENT, getDepartment_id());
        contentValues.put(COLUMN_HOSPITAL, getHospital_id());
        contentValues.put(COLUMN_VISIT_DATE, getVisit_date());
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
