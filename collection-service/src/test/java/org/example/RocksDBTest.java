package org.example;

import org.junit.jupiter.api.Test;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

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
        System.out.println("read : " + new String(readValue, StandardCharsets.UTF_8));
    }

    private static void ensureDirectories() throws IOException {
        if (Files.notExists(transientPath)) {
            Files.createDirectories(Files.createDirectories(transientPath));
        }
    }
}
