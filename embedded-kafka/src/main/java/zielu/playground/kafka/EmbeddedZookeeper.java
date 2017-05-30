/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zielu.playground.kafka;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedZookeeper extends ExternalResource {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private int port = -1;
    private int tickTime = 500;

    private ServerCnxnFactory cnxnFactory;
    private File snapshotDir;
    private File logDir;
    private ZooKeeperServer zooKeeperServer;


    public EmbeddedZookeeper() {
        this(AvailablePortFinder.getNextAvailable());
    }

    public EmbeddedZookeeper(int port) {
        this.port = port;
    }

    @Override
    public void before() throws IOException {
        this.snapshotDir = TestUtil.createTempDir("zk-snapshot");
        LOG.info("Snapshot dir: {}", snapshotDir.getAbsolutePath());
        this.logDir = TestUtil.createTempDir("zk-log");
        LOG.info("Log dir: {}", logDir.getAbsolutePath());

        try {
            zooKeeperServer = new ZooKeeperServer(snapshotDir, logDir, tickTime);
            cnxnFactory = new NIOServerCnxnFactory();
            cnxnFactory.configure(new InetSocketAddress("localhost", port), 1024);
            cnxnFactory.startup(zooKeeperServer);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void after() {
        cnxnFactory.shutdown();
        zooKeeperServer.shutdown();
        try {
            zooKeeperServer.getZKDatabase().close();
        } catch (IOException e) {
            LOG.error("Failed to close ZK Database", e);
        }

        TestUtil.deleteDir(snapshotDir);
        TestUtil.deleteDir(logDir);
    }

    public String getConnection() {
        return "localhost:" + port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EmbeddedZookeeper{");
        sb.append("connection=").append(getConnection());
        sb.append('}');
        return sb.toString();
    }
}