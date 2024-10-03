package repositories;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import models.Application;
import models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApplicationRepository {

    private final Pool client;
    private final NoteRepository noteRepository;

    public ApplicationRepository(Pool client, NoteRepository noteRepository) {
        this.client = client;
        this.noteRepository = noteRepository;
    }

    public Future<Application> create(Application application) {
        String sql = "INSERT INTO applications (id, employer, title, link, company_id) VALUES ($1, $2, $3, $4, $5) RETURNING *";
        return client.preparedQuery(sql).execute(Tuple.of(application.getId(), application.getEmployer(),
                application.getTitle(), application.getLink(), application.getCompanyId()))
                .map(rowSet -> mapRowToApplication(rowSet.iterator().next()));
    }

    public Future<Application> findById(Long id) {
        String sql = "SELECT * FROM applications WHERE id = $1";
        return client.preparedQuery(sql).execute(Tuple.of(id))
                .map(rowSet -> {
                    if (rowSet.iterator().hasNext()) {
                        return mapRowToApplication(rowSet.iterator().next());
                    } else {
                        return null;
                    }
                });
    }

    public Future<List<Application>> findAll() {
        String sql = "SELECT * FROM applications";
        return client.query(sql).execute()
                .map(rowSet -> {
                    List<Application> applications = new ArrayList<>();
                    for (Row row : rowSet) {
                        applications.add(mapRowToApplication(row));
                    }
                    return applications;
                });
    }

    public Future<List<Application>> findAllApplicationsWithNotes() {
        Future<List<Application>> applicationsFuture = findAll();
        Future<List<Note>> notesFuture = noteRepository.findAllNotes();

        return Future.all(applicationsFuture, notesFuture).map(comp -> {
            List<Application> applications = comp.resultAt(0);
            List<Note> notes = comp.resultAt(1);

            Map<Long, List<Note>> notesMap = notes.stream()
                    .collect(Collectors.groupingBy(Note::getApplicationId));

            for (Application application : applications) {
                List<Note> applicationNotes = notesMap.getOrDefault(application.getId(), new ArrayList<>());
                application.setNotes(applicationNotes);
            }

            return applications;
        });
    }

    public Future<Application> update(Application application) {
        String sql = "UPDATE applications SET employer = $1, title = $2, link = $3, company_id = $4 WHERE id = $5";
        return client.preparedQuery(sql).execute(Tuple.of(application.getEmployer(), application.getTitle(),
                application.getLink(), application.getCompanyId(), application.getId()))
                .map(rowSet -> application);
    }

    public Future<Void> delete(Application application) {
        String sql = "DELETE FROM applications WHERE id = $1";
        return client.preparedQuery(sql).execute(Tuple.of(application.getId()))
                .map(rowSet -> null);
    }

    private Application mapRowToApplication(Row row) {
        Application application = new Application();
        application.setId(row.getLong("id"));
        application.setEmployer(row.getString("employer"));
        application.setTitle(row.getString("title"));
        application.setLink(row.getString("link"));
        application.setCompanyId(row.getInteger("company_id"));
        return application;
    }
}
