package database.table;

import java.util.List;

import database.model.IData;

public interface Table extends IData {
    List<TableColumnDef> getColumnDef();

    void saveToDatabase();
}
