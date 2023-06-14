package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.model.IData;
import database.table.Department;
import database.table.Doctor;
import database.table.Hospital;
import database.table.TableColumnDef;

public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final int VERSION = 1;//起始版本为1,起始版本必须大于0

    private static DatabaseManager instance;
    private DatabaseOpenHelper openHelper;

    private DatabaseManager() {

    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.openHelper = new DatabaseOpenHelper(context.getApplicationContext(), context.getPackageName(), null, VERSION);
    }


    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            int version = db.getVersion();//数据库刚创建时(仅首次使用App时会调用)，初始值为0;且onCreate仅在version==0时才会被调用
            //新安装的APP，其数据库VERSION非0时，需从第一版本开始依次更新数据库
            for (int i = version; i < VERSION; i++) {
                execSQL(db, i);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //覆盖安装后，若已有数据库版本低于当前需支持的版本时，依次更新之后的数据库
            for (int i = oldVersion; i < newVersion; i++) {
                execSQL(db, i);
            }
        }

        /**
         * 根据版本号增量更新数据库
         *
         * @param db
         * @param version
         */
        private void execSQL(SQLiteDatabase db, int version) {
            if (0 == version) {
                db.execSQL(Hospital.getCreateSql());
                db.execSQL(Department.getCreateSql());
                db.execSQL(Doctor.getCreateSql());
                db.execSQL(TableColumnDef.getCreateSql());
            }
        }
    }

    /**
     * 检查某表列是否存在
     *
     * @param db
     * @param tableName  表名
     * @param columnName 列名
     * @return
     */
    private boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            Log.e(TAG, "checkColumnExists=" + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public void addColumn(String tableName, String columnName, String columnType) {
        SQLiteDatabase database = null;
        try {
            database = openHelper.getWritableDatabase();
            if (!checkColumnExist(database, tableName, columnName)) {
                database.execSQL("alter table " + tableName + " add " + columnName + " " + columnType);
                //新增的列，将其记录到table_update表中
                ContentValues values = new ContentValues();
                values.put(TableColumnDef.COLUMN_TABLE_NAME, tableName);
                values.put(TableColumnDef.COLUMN_COL_NAME, columnName);
                values.put(TableColumnDef.COLUMN_TYPE, columnType);
                database.insert(TableColumnDef.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public List<TableColumnDef> getCustomerField(String tableName) {
        SQLiteDatabase database = null;
        List<TableColumnDef> customerFields = new ArrayList<>();
        Cursor cursor = null;
        try {
            database = openHelper.getReadableDatabase();
            cursor = database.query(TableColumnDef.TABLE_NAME, null, TableColumnDef.COLUMN_TABLE_NAME + "=?", new String[]{tableName}, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                customerFields.add(TableColumnDef.parse(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return customerFields;
    }

    /**
     * 新增数据
     */
    public void insert(String tableName, ContentValues contentValues) {
        insertOrUpdateData(tableName, -1, contentValues);
    }

    /**
     * 新增或更新数据
     */
    public void insertOrUpdateData(String tableName, int id, ContentValues contentValues) {
        if (tableName == null || "".equals(tableName) || null == contentValues) {
            return;
        }
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = openHelper.getWritableDatabase();
            if (id < 0) {
                database.insert(tableName, null, contentValues);
            } else {
                cursor = database.query(tableName, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    database.update(tableName, contentValues, "id=?", new String[]{String.valueOf(id)});
                } else {
                    database.insert(tableName, null, contentValues);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<Hospital> getHospital(int id) {
        List<Hospital> hospitals = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = openHelper.getWritableDatabase();
            cursor = database.query(Hospital.TABLE_NAME, null, id < 0 ? null : "id=?", id < 0 ? null : new String[]{String.valueOf(id)}, null, null, null);
            while (null != cursor && cursor.moveToNext()) {
                hospitals.add(Hospital.parse(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return hospitals;
    }

    public List<Department> getDepartment(int id) {
        List<Department> departments = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = openHelper.getWritableDatabase();
            cursor = database.query(Department.TABLE_NAME, null, id < 0 ? null : "id=?", id < 0 ? null : new String[]{String.valueOf(id)}, null, null, null);
            while (null != cursor && cursor.moveToNext()) {
                departments.add(Department.parse(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return departments;
    }

    public List<Doctor> getDoctor(int id) {
        List<Doctor> doctors = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;
        try {
            database = openHelper.getWritableDatabase();
            String sql = "select * from doctor LEFT JOIN hospital LEFT JOIN department ON doctor.hospital_id=hospital.id ON doctor.department_id=department.id";
            String where = "";
            if (id > 0) {
                where = " where doctor.id = " + id;
            }
            cursor = database.rawQuery(sql + where, null);
            while (null != cursor && cursor.moveToNext()) {
                doctors.add(Doctor.parse(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return doctors;
    }

    public void deleteData(String tableName, int id) {
        if (tableName == null || "".equals(tableName) || id < 0) {
            return;
        }
        SQLiteDatabase database = null;
        try {
            database = openHelper.getWritableDatabase();
            database.delete(tableName, "id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
