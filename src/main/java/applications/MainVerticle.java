package applications;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.json.JsonObject;
import configurations.DatabaseConfiguration;
import repositories.ApplicationRepository;
import repositories.NoteRepository;
import services.ApplicationServiceImpl;
import handlers.ApplicationHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    String database = System.getenv("DATABASE");
    String dbUser = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PWD");

    JsonObject dbConfig = new JsonObject()
        .put("host", "localhost")
        .put("port", 5432)
        .put("database", database != null ? database : "default_db")
        .put("user", dbUser != null ? dbUser : "default_user")
        .put("password", dbPassword != null ? dbPassword : "default_password");

    DatabaseConfiguration.initialize(vertx, dbConfig).onSuccess(v -> {
      NoteRepository noteRepository = new NoteRepository(DatabaseConfiguration.getPgPool());
      ApplicationRepository applicationRepository = new ApplicationRepository(
          DatabaseConfiguration.getPgPool(),
          noteRepository);

      ApplicationServiceImpl applicationService = new ApplicationServiceImpl(applicationRepository, noteRepository);

      Router router = Router.router(vertx);
      router.route().handler(BodyHandler.create());

      router.post("/application").handler(ctx -> ApplicationHandler.handleAddApplication(ctx, applicationService));
      router.get("/application").handler(ctx -> ApplicationHandler.handleGetApplications(ctx, applicationService));
      router.get("/application/:id")
          .handler(ctx -> ApplicationHandler.handleGetApplicationById(ctx, applicationService));
      router.put("/application/:id")
          .handler(ctx -> ApplicationHandler.handleUpdateApplication(ctx, applicationService));
      router.delete("/application/:id")
          .handler(ctx -> ApplicationHandler.handleDeleteApplication(ctx, applicationService));

      vertx.createHttpServer().requestHandler(router).listen(8001, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          System.out.println("HTTP server started on port 8001");
        } else {
          startPromise.fail(http.cause());
        }
      });
    }).onFailure(startPromise::fail);
  }

  @Override
  public void stop() {
    DatabaseConfiguration.close();
  }
}
