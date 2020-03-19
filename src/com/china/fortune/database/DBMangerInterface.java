package com.china.fortune.database;

import com.china.fortune.os.database.DbAction;

public interface DBMangerInterface {
    DbAction get();
    void free(DbAction dbObj);
    void closeAll();
}
