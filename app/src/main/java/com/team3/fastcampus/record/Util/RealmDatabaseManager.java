package com.team3.fastcampus.record.Util;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by tokijh on 2017. 4. 3..
 */

public class RealmDatabaseManager {

    private static RealmDatabaseManager instance;

    private Realm realm;

    private RealmDatabaseManager() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmDatabaseManager getInstance() {
        if (instance == null) {
            instance = new RealmDatabaseManager();
        }
        return instance;
    }

    public static void destroy() {
        if (instance != null) {
            instance.realm.close();
        }
    }

    public <E extends RealmObject> long getNextLongID(Class<E> clazz, String fieldName) {
        try {
            return realm.where(clazz).max(fieldName).longValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public <E extends RealmObject> long getNextIntegerID(Class<E> clazz, String fieldName) {
        try {
            return realm.where(clazz).max(fieldName).intValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public <E extends RealmObject> void create(Class<E> clazz, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm.createObject(clazz));
        });
    }

    public <E extends RealmObject> void create(Class<E> clazz, Object primaryKeyValue, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm.createObject(clazz, primaryKeyValue));
        });
    }

    public <E extends RealmObject> void createLongAI(Class<E> clazz, String fieldName, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm.createObject(clazz, getNextLongID(clazz, fieldName)));
        });
    }

    public <E extends RealmObject> E getFirst(Class<E> clazz) {
        return realm.where(clazz).findFirst();
    }

    public <E extends RealmObject> RealmResults<E> getAll(Class<E> clazz) {
        return realm.where(clazz).findAll();
    }

    public interface RealmCreate<E> {
        void create(E realmObject);
    }
}
