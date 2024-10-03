package handlers;

import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import models.Application;
import services.ApplicationService;

public class ApplicationHandler {

    public static void handleAddApplication(RoutingContext ctx, ApplicationService applicationService) {
        Application bodyApplication = ctx.body().asPojo(Application.class);
        applicationService.createApplication(bodyApplication)
                .onSuccess(returnApplication -> {
                    JsonObject jsonApplication = JsonObject.mapFrom(returnApplication);
                    ctx.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json")
                            .end(jsonApplication.toString());
                })
                .onFailure(ctx::fail);
    }

    public static void handleGetApplications(RoutingContext ctx, ApplicationService applicationService) {
        applicationService.getAllApplications()
                .onSuccess(applications -> {
                    List<JsonObject> jsonApplications = applications.stream()
                            .map(JsonObject::mapFrom)
                            .collect(Collectors.toList());
                    ctx.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/json")
                            .end(jsonApplications.toString());
                })
                .onFailure(ctx::fail);
    }

    public static void handleGetApplicationById(RoutingContext ctx, ApplicationService applicationService) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        applicationService.getApplication(id)
                .onSuccess(application -> {
                    JsonObject jsonApplication = JsonObject.mapFrom(application);
                    ctx.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/json")
                            .end(jsonApplication.toString());
                })
                .onFailure(ctx::fail);
    }

    public static void handleUpdateApplication(RoutingContext ctx, ApplicationService applicationService) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        JsonObject bodyApplication = ctx.body().asJsonObject();

        applicationService.updateApplication(id, bodyApplication)
                .onSuccess(updatedApplication -> {
                    JsonObject jsonApplication = JsonObject.mapFrom(updatedApplication);
                    ctx.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/json")
                            .end(jsonApplication.toString());
                })
                .onFailure(ctx::fail);
    }

    public static void handleDeleteApplication(RoutingContext ctx, ApplicationService applicationService) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        applicationService.deleteApplication(id)
                .onSuccess(deletedApplication -> {
                    ctx.response()
                            .setStatusCode(204)
                            .end();
                })
                .onFailure(ctx::fail);
    }
}
