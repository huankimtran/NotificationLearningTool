package hba.testing.notificatingspcedlearning.DatabaseHelper;

import java.util.Objects;

/**
 * Created by TranKim on 12/16/2016.
 */
public final class dtbConst {
    private dtbConst(){}
    //ITEM TABLE DIAGRAM
    public static final String[] ITEM_TABLE_COLS ={"_id","NAME","CONTENT","LEVEL","TIME_NOW","TIME_NEXT","CATEGORY","DATE_IN","HINT","REVERSE_ITEM_ID"};
    //Index                                         0    1        2        3      4           5           6             7       8         9
    //CATEGORY TABLE DIAGRAM
    public static final String[] CATEGORY_TABLE_COLS ={"_id","NAME","ITEM_COUNTER"};
    //Index                                              0    1        2

    public static final String[] TABLE={"ITEM","CATEGORY"};
    public static final String[] DTBNAME={"UserDatabse.db"};
    //Command
    public static final String ADD_COLUMN="ALTER TABLE ? ADD COLUMN ? ?";
    public static final String DELETE_COLUMN="ALTER TABLE ? DROP COLUMN ?";
    //Creating database
    public static final String ITEMTABLEINITIATING ="CREATE TABLE ITEM (_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,CONTENT TEXT,LEVEL INT NOT NULL,TIME_NOW NUMERIC NOT NULL,TIME_NEXT NUMERIC NOT NULL,CATEGORY TEXT,DATE_IN NUMERIC,HINT TEXT,REVERSE_ITEM_ID INTEGER);";
    public static final String CATEGORYTABLEINITIATING ="CREATE TABLE CATEGORY (_id INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,ITEM_COUNTER NUMERIC);";
    //Chaging table diagram
    public static final String ITEMTABLEADDCOLS ="ALTER TABLE ITEM ADD HINT TEXT";
    //Constant
    public static final String COLUMN_TYPE[]={"TEXT","NUMERIC"};
    public static final String UNKNOWN_GROUP="Unknown";
}
