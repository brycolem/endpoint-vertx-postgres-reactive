package configurations;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import io.vertx.core.json.JsonObject;

import static org.mockito.Mockito.*;

public class TestDatabaseConfig {

    private Vertx vertx;
    private MockedStatic<DatabaseConfiguration> mockedDatabaseConfig;
    private Pool mockPgPool;

    @BeforeEach
    public void setUp() {
        vertx = Vertx.vertx();
        mockPgPool = mock(Pool.class);

        mockedDatabaseConfig = mockStatic(DatabaseConfiguration.class);

        mockedDatabaseConfig.when(() -> DatabaseConfiguration.initialize(any(Vertx.class), any(JsonObject.class)))
                .thenReturn(Future.succeededFuture());

        mockedDatabaseConfig.when(DatabaseConfiguration::getPgPool).thenReturn(mockPgPool);

        JsonObject mockConfig = new JsonObject()
                .put("host", "localhost")
                .put("port", 5432)
                .put("database", "testdb")
                .put("user", "testuser")
                .put("password", "testpassword");
        DatabaseConfiguration.initialize(vertx, mockConfig).toCompletionStage().toCompletableFuture().join();
    }

    @AfterEach
    public void tearDown() {
        if (mockedDatabaseConfig != null) {
            mockedDatabaseConfig.close();
        }
        vertx.close();
    }

    @Test
    public void testDatabaseInitialization() {
        mockedDatabaseConfig.verify(() -> DatabaseConfiguration.initialize(any(Vertx.class), any(JsonObject.class)));

        DatabaseConfiguration.close();
        mockedDatabaseConfig.verify(DatabaseConfiguration::close);
    }

    @Test
    public void testGetPgPool() {
        Pool pool = DatabaseConfiguration.getPgPool();
        Assertions.assertNotNull(pool);
        Assertions.assertEquals(mockPgPool, pool);
    }
}
