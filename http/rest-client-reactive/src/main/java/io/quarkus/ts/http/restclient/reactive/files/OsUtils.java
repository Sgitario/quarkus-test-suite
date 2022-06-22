package io.quarkus.ts.http.restclient.reactive.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class OsUtils {
    public static final long SIZE_2049MiB = 2148532224L;

    public abstract String getSum(Path path);

    public abstract void createFile(Path path, long size);

    public static OsUtils get() {
        return new JavaUtils();
    }
}

class JavaUtils extends OsUtils {

    @Override
    public String getSum(Path path) {
        try (InputStream stream = Files.newInputStream(path)) {
            return DigestUtils.md5Hex(stream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void createFile(Path path, long size) {
        try (RandomAccessFile f = new RandomAccessFile(path.toAbsolutePath().toString(), "rw");) {
            f.setLength(size);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
