package zielu.playground.kafka;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtil {
    private static final Logger LOG = LoggerFactory.getLogger(TestUtil.class);

    public static File createTempDir(String prefix) {
        try {
            File dir = Files.createTempDirectory(prefix).toFile();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteDir(dir)));
            return dir;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteDir(File dir) {
        if (dir.exists()) {
            boolean deleted = FileUtils.deleteQuietly(dir);
            LOG.info("Deleted, success={}, dir: {}", deleted, dir.getAbsolutePath());
        } else {
            LOG.info("Deletion skipped, dir: {}", dir.getAbsolutePath());
        }
    }
}
