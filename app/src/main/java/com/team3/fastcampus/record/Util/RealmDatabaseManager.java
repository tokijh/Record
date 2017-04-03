package com.team3.fastcampus.record.Util;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
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

    public <E extends RealmObject> long getNextID(Class<E> clazz, String fieldName) {
        try {
            return realm.where(clazz).max(fieldName).longValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public <E extends RealmObject> E create(Realm realm, Class<E> clazz) {
        return realm.createObject(clazz);
    }

    public <E extends RealmObject> E create(Realm realm, Class<E> clazz, Object primaryKeyValue) {
        return realm.createObject(clazz, primaryKeyValue);
    }

    public <E extends RealmObject> void create(Class<E> clazz, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz));
        });
    }

    public <E extends RealmObject> void create(Class<E> clazz, Object primaryKeyValue, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz, primaryKeyValue));
        });
    }

    public <E extends RealmObject> E createAI(Realm realm, Class<E> clazz, String fieldName) {
        return realm.createObject(clazz, getNextID(clazz, fieldName));
    }

    public <E extends RealmObject> void createAI(Class<E> clazz, String fieldName, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz, getNextID(clazz, fieldName)));
        });
    }

    public <E extends RealmObject> E getFirst(Class<E> clazz) {
        return realm.where(clazz).findFirst();
    }

    public <E extends RealmObject> RealmResults<E> getAll(Class<E> clazz) {
        return realm.where(clazz).findAll();
    }

    public <E extends RealmObject> RealmQuery<E> get(Class<E> clazz) {
        return realm.where(clazz);
    }

    public <E extends RealmObject> E get(Class<E> clazz, int poisition) {
        return realm.where(clazz).findAll().get(poisition);
    }

    public <E extends RealmObject> void update(Class<E> clazz, RealmUpdate realmUpdate) {
        realm.executeTransaction(_realm -> {
            realmUpdate.update();
        });
    }

    public <E extends RealmObject> void deleteAll() {
        realm.executeTransaction(_realm -> {
            _realm.deleteAll();
        });
    }

    public <E extends RealmObject> void deleteAll(Class<E> clazz) {
        realm.executeTransaction(_realm -> {
            _realm.delete(clazz);
        });
    }

    public <E extends RealmObject> void delete(E object) {
        realm.executeTransaction(_realm -> {
            object.deleteFromRealm();
        });
    }

    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, long primaryKey) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKey).findAll().deleteAllFromRealm();
        });
    }

    public <E extends RealmObject> void delete(Class<E> clazz, RealmDeleteResults<E> realmDeleteResults) {
        realm.executeTransaction(_realm -> {
            realmDeleteResults.deleteResults(_realm.where(clazz)).deleteAllFromRealm();
        });
    }

    public <E extends RealmObject> void delete(Class<E> clazz, RealmDeleteResults<E> realmDeleteResults, int position) {
        realm.executeTransaction(_realm -> {
            realmDeleteResults.deleteResults(_realm.where(clazz)).deleteFromRealm(position);
        });
    }

    public interface RealmCreate<E> {
        void create(Realm realm, E realmObject);
    }

    public interface RealmUpdate {
        void update();
    }

    public interface RealmDeleteResults<E extends RealmObject> {
        RealmResults deleteResults(RealmQuery<E> realmResults);
    }
}
