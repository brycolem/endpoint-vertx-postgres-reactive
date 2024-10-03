package models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Application {
    private Long id;
    private String employer;
    private String title;
    private String link;
    private Integer companyId;

    private List<Note> notes;

    public Application(JsonObject json) {
        this.id = json.getLong("id");
        this.employer = json.getString("employer");
        this.title = json.getString("title");
        this.link = json.getString("link");
        this.companyId = json.getInteger("company_id");
        if (json.containsKey("notes")) {
            this.notes = json.getJsonArray("notes").stream()
                    .map(o -> new Note((JsonObject) o))
                    .collect(Collectors.toList());
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject()
                .put("id", id)
                .put("employer", employer)
                .put("title", title)
                .put("link", link)
                .put("company_id", companyId);

        json.put("notes", notes.stream().map(Note::toJson).collect(Collectors.toList()));

        return json;
    }

    public List<Note> getNotes() {
        return notes != null ? notes : new ArrayList<>();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
    }
}
