package repositories;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import models.Note;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    private final Pool client;

    public NoteRepository(Pool client) {
        this.client = client;
    }

    public Future<List<Note>> findAllNotes() {
        String sql = "SELECT * FROM notes";
        return client.query(sql).execute()
                .map(rowSet -> {
                    List<Note> notes = new ArrayList<>();
                    for (Row row : rowSet) {
                        notes.add(new Note(
                                row.getLong("id"),
                                row.getString("note_text"),
                                row.getLong("application_id")));
                    }
                    return notes;
                });
    }

    public Future<List<Note>> findByApplicationId(Long applicationId) {
        String sql = "SELECT * FROM notes WHERE application_id = $1";
        return client.preparedQuery(sql).execute(Tuple.of(applicationId))
                .map(rowSet -> {
                    List<Note> notes = new ArrayList<>();
                    rowSet.forEach(row -> {
                        JsonObject noteJson = row.toJson();
                        notes.add(new Note(noteJson));
                    });
                    return notes;
                });
    }

    public Future<Void> updateAll(List<Note> existingNotes) {
        List<Future<Void>> futures = new ArrayList<>();

        String sql = "UPDATE notes SET note_text = $1 WHERE id = $2";

        for (Note note : existingNotes) {
            Future<Void> future = client.preparedQuery(sql)
                    .execute(Tuple.of(note.getNoteText(), note.getId()))
                    .mapEmpty();
            futures.add(future);
        }

        return Future.join(futures)
                .mapEmpty();
    }

    public Future<Void> saveAll(List<Note> notes) {
        List<Future<Void>> futures = new ArrayList<>();

        String sql = "INSERT INTO notes (id, note_text, application_id) VALUES ($1, $2, $3)";

        for (Note note : notes) {
            Future<Void> future = client.preparedQuery(sql)
                    .execute(Tuple.of(note.getId(), note.getNoteText(), note.getApplicationId()))
                    .mapEmpty();
            futures.add(future);
        }

        return Future.join(futures)
                .mapEmpty();
    }
}
