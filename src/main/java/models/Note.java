package models;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    private Long id;
    private String noteText;
    private Long applicationId;

    public Note(JsonObject json) {
        this.id = json.getLong("id");
        this.noteText = json.getString("note_text");
        this.applicationId = json.getLong("application_id");
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("note_text", noteText)
                .put("application_id", applicationId);
    }
}
