package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 4. 3..
 */

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Realm Manager
 */
public class RealmDatabaseManager {

    private static RealmDatabaseManager instance;

    private Realm realm;

    private RealmDatabaseManager() {
        realm = Realm.getDefaultInstance();
    }

    /**
     * Single tone
     *
     * @return
     */
    public static RealmDatabaseManager getInstance() {
        if (instance == null) {
            instance = new RealmDatabaseManager();
        }
        return instance;
    }

    /**
     * 사용하지 않은 경우 반드시 destroy 되어야 한다.
     *
     * 없으면 memory leek 발생
     */
    public static void destroy() {
        if (instance != null) {
            instance.realm.close();
        }
    }

    /**
     * Auto Increase에서 사용되는 해당 클래스(테이블)의 다음 primary 번호를 가져온다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param <E> 지정 클래스
     * @return 다음 primaryid값을 가져온다.
     */
    public <E extends RealmObject> long getNextID(Class<E> clazz, String fieldName) {
        try {
            return realm.where(clazz).max(fieldName).longValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     *
     * @param realm executeTransaction 되어진 realm을 받아야 한다.
     * @param clazz 저장할 클래스(테이블)
     * @param <E> 지정 클래스
     * @return 새로 만들어진 클래스
     */
    public <E extends RealmObject> E create(Realm realm, Class<E> clazz) {
        return realm.createObject(clazz);
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     *
     * @param realm executeTransaction 되어진 realm을 받아야 한다.
     * @param clazz 저장할 클래스(테이블)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     * @return 새로 만들어진 클래스
     */
    public <E extends RealmObject> E create(Realm realm, Class<E> clazz, Object primaryKeyValue) {
        return realm.createObject(clazz, primaryKeyValue);
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     *
     * @param clazz 저장할 클래스(테이블)
     * @param realmCreate executeTransction 이 되고, 틀이 마련된 후 데이터를 삽입 할 수 있는 callback 함수
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void create(Class<E> clazz, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz));
        });
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     *
     * @param clazz 저장할 클래스(테이블)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param realmCreate executeTransction 이 되고, 틀이 마련된 후 데이터를 삽입 할 수 있는 callback 함수
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void create(Class<E> clazz, Object primaryKeyValue, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz, primaryKeyValue));
        });
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     * Auto Increase 자동으로 PrimaryKey값을 올려준다. (단 key가 int 나 long인 경우만)
     *
     * @param realm executeTransaction 되어진 realm을 받아야 한다.
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param <E> 지정 클래스
     * @return 새로 만들어진 클래스
     */
    public <E extends RealmObject> E createAI(Realm realm, Class<E> clazz, String fieldName) {
        return realm.createObject(clazz, getNextID(clazz, fieldName));
    }

    /**
     * 데이터베이스 클래스로 테이블 생성 (데이터를 저장하기위한 틀 마련)
     * Auto Increase 자동으로 PrimaryKey값을 올려준다. (단 key가 int 나 long인 경우만)
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param realmCreate executeTransction 이 되고, 틀이 마련된 후 데이터를 삽입 할 수 있는 callback 함수
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void createAI(Class<E> clazz, String fieldName, RealmCreate<E> realmCreate) {
        realm.executeTransaction(_realm -> {
            realmCreate.create(_realm, _realm.createObject(clazz, getNextID(clazz, fieldName)));
        });
    }

    /**
     * 해당 클래스의 첫번째 내용을 가져온다
     *
     * @param clazz 저장할 클래스(테이블)
     * @param <E> 지정 클래스
     * @return 결과 클래스
     */
    public <E extends RealmObject> E getFirst(Class<E> clazz) {
        return realm.where(clazz).findFirst();
    }

    /**
     * 해당 클래스의 저장된 내용 전부를 불러온다
     *
     * @param clazz 저장할 클래스(테이블)
     * @param <E> 지정 클래스
     * @return 결과 클래스 리스트
     */
    public <E extends RealmObject> RealmResults<E> getAll(Class<E> clazz) {
        return realm.where(clazz).findAll();
    }

    /**
     * 해당 클래스의 내용을 커스텀으로 검색하기위한 함수
     *
     * @param clazz 저장할 클래스(테이블)
     * @param <E> 지정 클래스
     * @return 이후 커스텀화 시킬 수 있도록 반환
     */
    public <E extends RealmObject> RealmQuery<E> get(Class<E> clazz) {
        return realm.where(clazz);
    }

    /**
     * 해당 클래스의 position의 위치의 내용을 가져온다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param position 위치
     * @param <E> 지정 클래스
     * @return 결과 클래스
     */
    public <E extends RealmObject> E get(Class<E> clazz, int position) {
        return realm.where(clazz).findAll().get(position);
    }

    /**
     * DB에 저장된 내용을 갱신 하는 함수
     *
     * @param clazz 저장할 클래스(테이블)
     * @param realmUpdate 업데이트 작업이 일어나는 callback
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void update(Class<E> clazz, RealmUpdate realmUpdate) {
        realm.executeTransaction(_realm -> {
            realmUpdate.update();
        });
    }

    /**
     * DB에 저장된 내용 모두 삭제 한다.
     *
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void deleteAll() {
        realm.executeTransaction(_realm -> {
            _realm.deleteAll();
        });
    }

    /**
     * DB에 저장된 해당 클래스의 내용 모두 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void deleteAll(Class<E> clazz) {
        realm.executeTransaction(_realm -> {
            _realm.delete(clazz);
        });
    }

    /**
     * 해당 Object를 삭제 한다. (DB에 저장 되어 있어야 한다.) (create나 get으로 받은것으로 사용 가능)
     *
     * @param object 삭제할 Object
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(E object) {
        realm.executeTransaction(_realm -> {
            object.deleteFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, boolean primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, short primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, byte primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, Short primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, int primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, long primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, float primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, double primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, byte[] primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 해당 키를 가진 내용을 삭제 한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param fieldName PrimaryKey인 필드명(id)
     * @param primaryKeyValue PrimaryKey의 Default 키(직접 입력)
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, String fieldName, String primaryKeyValue) {
        realm.executeTransaction(_realm -> {
            _realm.where(clazz).equalTo(fieldName, primaryKeyValue).findAll().deleteAllFromRealm();
        });
    }

    /**
     * 해당 클래스의 내용을 조건에 따라 검색하여 결과를 전부 삭제한다.
     *
     * @param clazz 저장할 클래스(테이블)
     * @param realmDeleteResults 검색을 커스텀 하기 위한 callback
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, RealmDeleteResults<E> realmDeleteResults) {
        realm.executeTransaction(_realm -> {
            realmDeleteResults.deleteResults(_realm.where(clazz)).deleteAllFromRealm();
        });
    }

    /**
     *
     * @param clazz 저장할 클래스(테이블)
     * @param realmDeleteResults 검색을 커스텀 하기 위한 callback
     * @param position 위치
     * @param <E> 지정 클래스
     */
    public <E extends RealmObject> void delete(Class<E> clazz, RealmDeleteResults<E> realmDeleteResults, int position) {
        realm.executeTransaction(_realm -> {
            realmDeleteResults.deleteResults(_realm.where(clazz)).deleteFromRealm(position);
        });
    }

    /**
     * 생성 하여 데이터를 삽입 하기위한 interface
     *
     * @param <E> 지정 클래스
     */
    public interface RealmCreate<E> {
        /**
         * 생성하여 데이터를 삽입하기 위한 함수
         *
         * @param realm executeTransaction되어진 realm을 받아옴으로 써 class in class의 내용을 create할때 사용 된다.
         * @param realmObject DB에 생성된 해당 클래스
         */
        void create(Realm realm, E realmObject);
    }

    /**
     * 내용을 갱신 하기위한 interface
     */
    public interface RealmUpdate {
        /**
         * 내용을 갱신 하기위한 함수
         */
        void update();
    }

    /**
     * 삭제하기위해 검색하기 위한 interface
     *
     * @param <E> 지정 클래스
     */
    public interface RealmDeleteResults<E extends RealmObject> {
        /**
         * 검색 내용을 추리기위한 함수
         *
         * @param realmResults query내용
         * @return 추려낸 검색 결과를 return 해준다.
         */
        RealmResults deleteResults(RealmQuery<E> realmResults);
    }
}
