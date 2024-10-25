package configurations;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Future;

public class DatabaseConfiguration {

    private static Pool pgPool;

    public static Future<Void> initialize(Vertx vertx, JsonObject dbConfig) {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(dbConfig.getInteger("port"))
                .setHost(dbConfig.getString("host"))
                .setDatabase(dbConfig.getString("database"))
                .setUser(dbConfig.getString("user"))
                .setPassword(dbConfig.getString("password"));

        PoolOptions poolOptions = new PoolOptions().setMaxSize(15);

        pgPool = Pool.pool(vertx, connectOptions, poolOptions);
        return Future.succeededFuture();
    }

    public static Pool getPgPool() {
        return pgPool;
    }

    public static void close() {
        if (pgPool != null) {
            pgPool.close();
        }
    }
}
