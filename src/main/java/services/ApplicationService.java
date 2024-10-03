package services;

import models.Application;
import java.util.List;

import exceptions.RecordNotFoundException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface ApplicationService {
    Future<Application> createApplication(Application application);

    Future<Application> getApplication(Long id) throws RecordNotFoundException;

    Future<List<Application>> getAllApplications();

    Future<Application> updateApplication(Long id, JsonObject application) throws RecordNotFoundException;

    Future<Boolean> deleteApplication(Long id);
}
