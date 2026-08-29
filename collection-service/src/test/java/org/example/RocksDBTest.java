package org.example;

import org.junit.jupiter.api.Test;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * RocksDB 학습테스트
 */
public class RocksDBTest {

    private static final Path transientPath = new File("state/transient").toPath();
    private static final Path failPath = new File("state/fail").toPath();
    private static final Options options = new Options().setCreateIfMissing(true);

    @Test
    void writeAndRead() throws RocksDBException, IOException {
        // 데이터베이스의 데이터를 저장할 디렉토리 존재 확인 후 없으면 생성
        ensureDirectories();

        // 데이터베이스 실행
        RocksDB transientDB = RocksDB.open(options, transientPath.toString());

        // 데이터 쓰기
        byte[] key = "name".getBytes(StandardCharsets.UTF_8);
        byte[] value = "park".getBytes(StandardCharsets.UTF_8);
        transientDB.put(key, value);

        // 데이터 읽기
        byte[] readValue = transientDB.get(key);
    }

    @Test
    void readAllKeysAndValues() throws RocksDBException, IOException {
        ensureDirectories();

        // 데이터베이스 실행 및 자원 자동 반환 (try-with-resources)
        try (RocksDB db = RocksDB.open(options, transientPath.toString())) {

            // RocksIterator를 이용해 모든 키, 값 순회
            try (RocksIterator iterator = db.newIterator()) {
                System.out.println("--- RocksDB 전체 데이터 조회 시작 ---");
                
                // Iterator를 가장 처음 데이터로 이동
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    String key = new String(iterator.key(), StandardCharsets.UTF_8);
                    String value = new String(iterator.value(), StandardCharsets.UTF_8);
                    System.out.println("Key: " + key + ", Value: " + value);
                }
                
                System.out.println("--- RocksDB 전체 데이터 조회 종료 ---");
            }
        }
    }

    @Test
    void deleteAllKeys() throws RocksDBException, IOException {
        ensureDirectories();

        // 데이터베이스 실행 및 자원 자동 반환
        try (RocksDB db = RocksDB.open(options, transientPath.toString())) {

            // RocksIterator를 이용해 모든 키를 순회하며 삭제
            try (RocksIterator iterator = db.newIterator()) {
                System.out.println("--- RocksDB 전체 데이터 삭제 시작 ---");
                
                for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                    byte[] key = iterator.key();
                    db.delete(key); // 해당 키 삭제
                    System.out.println("삭제된 Key: " + new String(key, StandardCharsets.UTF_8));
                }
                
                System.out.println("--- RocksDB 전체 데이터 삭제 완료 ---");
            }
            
            // 삭제 검증 (순회했을 때 아무것도 안 나오는지 확인)
            try (RocksIterator iterator = db.newIterator()) {
                iterator.seekToFirst();
                if (!iterator.isValid()) {
                    System.out.println("검증: RocksDB가 완전히 비워졌습니다.");
                } else {
                    System.out.println("검증 실패: 아직 데이터가 남아 있습니다.");
                }
            }
        }
    }

    private static void ensureDirectories() throws IOException {
        if (Files.notExists(transientPath)) {
            Files.createDirectories(Files.createDirectories(transientPath));
        }
    }
}
