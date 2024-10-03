package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestApplication {

    private Application application;

    @BeforeEach
    public void setUp() {
        application = new Application();
        application.setId(1L);
        application.setEmployer("Test Employer");
        application.setTitle("Software Engineer");
        application.setLink("http://example.com");
        application.setCompanyId(123);

        Note note1 = new Note();
        note1.setId(1L);
        note1.setNoteText("Note 1 for the application");
        note1.setApplicationId(application.getId());

        Note note2 = new Note();
        note2.setId(2L);
        note2.setNoteText("Note 2 for the application");
        note2.setApplicationId(application.getId());

        List<Note> notes = Arrays.asList(note1, note2);
        application.setNotes(notes);
    }

    @Test
    public void testApplicationProperties() {
        assertEquals(1L, application.getId());
        assertEquals("Test Employer", application.getEmployer());
        assertEquals("Software Engineer", application.getTitle());
        assertEquals("http://example.com", application.getLink());
        assertEquals(123, application.getCompanyId());

        List<Note> notes = application.getNotes();
        assertEquals(2, notes.size());

        Note note1 = notes.get(0);
        assertEquals(1L, note1.getId());
        assertEquals("Note 1 for the application", note1.getNoteText());
        assertEquals(application.getId(), note1.getApplicationId());

        Note note2 = notes.get(1);
        assertEquals(2L, note2.getId());
        assertEquals("Note 2 for the application", note2.getNoteText());
        assertEquals(application.getId(), note2.getApplicationId());
    }
}
