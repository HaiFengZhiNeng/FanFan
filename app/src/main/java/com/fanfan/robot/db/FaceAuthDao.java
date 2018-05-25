package com.fanfan.robot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fanfan.robot.model.FaceAuth;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "FACE_AUTH".
*/
public class FaceAuthDao extends AbstractDao<FaceAuth, Long> {

    public static final String TABLENAME = "FACE_AUTH";

    /**
     * Properties of entity FaceAuth.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SaveTime = new Property(1, long.class, "saveTime", false, "saveTime");
        public final static Property PersonId = new Property(2, String.class, "personId", false, "personId");
        public final static Property AuthId = new Property(3, String.class, "authId", false, "authId");
        public final static Property FaceCount = new Property(4, int.class, "faceCount", false, "faceCount");
        public final static Property Job = new Property(5, String.class, "job", false, "job");
        public final static Property Synopsis = new Property(6, String.class, "synopsis", false, "synopsis");
    }


    public FaceAuthDao(DaoConfig config) {
        super(config);
    }
    
    public FaceAuthDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FACE_AUTH\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"saveTime\" INTEGER NOT NULL ," + // 1: saveTime
                "\"personId\" TEXT," + // 2: personId
                "\"authId\" TEXT," + // 3: authId
                "\"faceCount\" INTEGER NOT NULL ," + // 4: faceCount
                "\"job\" TEXT," + // 5: job
                "\"synopsis\" TEXT);"); // 6: synopsis
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FACE_AUTH\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FaceAuth entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSaveTime());
 
        String personId = entity.getPersonId();
        if (personId != null) {
            stmt.bindString(3, personId);
        }
 
        String authId = entity.getAuthId();
        if (authId != null) {
            stmt.bindString(4, authId);
        }
        stmt.bindLong(5, entity.getFaceCount());
 
        String job = entity.getJob();
        if (job != null) {
            stmt.bindString(6, job);
        }
 
        String synopsis = entity.getSynopsis();
        if (synopsis != null) {
            stmt.bindString(7, synopsis);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FaceAuth entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSaveTime());
 
        String personId = entity.getPersonId();
        if (personId != null) {
            stmt.bindString(3, personId);
        }
 
        String authId = entity.getAuthId();
        if (authId != null) {
            stmt.bindString(4, authId);
        }
        stmt.bindLong(5, entity.getFaceCount());
 
        String job = entity.getJob();
        if (job != null) {
            stmt.bindString(6, job);
        }
 
        String synopsis = entity.getSynopsis();
        if (synopsis != null) {
            stmt.bindString(7, synopsis);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public FaceAuth readEntity(Cursor cursor, int offset) {
        FaceAuth entity = new FaceAuth( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // saveTime
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // personId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // authId
            cursor.getInt(offset + 4), // faceCount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // job
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // synopsis
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FaceAuth entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSaveTime(cursor.getLong(offset + 1));
        entity.setPersonId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAuthId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFaceCount(cursor.getInt(offset + 4));
        entity.setJob(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSynopsis(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(FaceAuth entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(FaceAuth entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FaceAuth entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}