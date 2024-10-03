package services;

import java.util.List;

import models.Application;
import models.Note;
import repositories.ApplicationRepository;
import repositories.NoteRepository;
import exceptions.RecordNotFoundException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NoteRepository noteRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, NoteRepository noteRepository) {
        this.applicationRepository = applicationRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public Future<Application> createApplication(Application application) {
        return applicationRepository.create(application)
                .compose(createdApp -> {
                    List<Note> notes = application.getNotes();
                    for (Note note : notes) {
                        note.setApplicationId(application.getId());
                    }
                    return noteRepository.saveAll(notes).map(v -> createdApp);
                });
    }

    @Override
    public Future<Application> getApplication(Long id) {
        return applicationRepository.findById(id)
                .compose(application -> {
                    if (application != null) {
                        return Future.succeededFuture(application);
                    } else {
                        return Future.failedFuture(new RecordNotFoundException("Application not found"));
                    }
                });
    }

    @Override
    public Future<List<Application>> getAllApplications() {
        return applicationRepository.findAllApplicationsWithNotes();
    }

    @Override
    public Future<Application> updateApplication(Long id, JsonObject applicationData) {
        return applicationRepository.findById(id)
                .compose(existingApplication -> {
                    if (existingApplication != null) {
                        JsonObject mergedData = JsonObject.mapFrom(existingApplication);
                        applicationData.remove("id");
                        mergedData.mergeIn(applicationData);
                        Application updatedApplication = mergedData.mapTo(Application.class);

                        List<Note> existingNotes = updatedApplication.getNotes();
                        for (int i = 0; i < existingNotes.size(); i++) {
                            Note existingNote = existingNotes.get(i);
                            JsonObject newNoteData = applicationData.getJsonArray("notes").getJsonObject(i);
                            Note newNote = newNoteData.mapTo(Note.class);
                            existingNote.setNoteText(newNote.getNoteText());
                            existingNote.setApplicationId(id);
                        }

                        return applicationRepository.update(updatedApplication)
                                .compose(app -> noteRepository.updateAll(existingNotes).map(v -> app));
                    } else {
                        return Future.failedFuture(new RecordNotFoundException("Application not found"));
                    }
                });
    }

    @Override
    public Future<Boolean> deleteApplication(Long id) {
        return applicationRepository.findById(id)
                .compose(application -> {
                    if (application != null) {
                        return applicationRepository.delete(application)
                                .map(deleted -> true);
                    } else {
                        return Future.succeededFuture(false);
                    }
                });
    }
}
