package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNote {

    private Note note;
    private Application application;

    @BeforeEach
    public void setUp() {
        application = new Application();
        application.setId(1L);
        application.setEmployer("Test Employer");
        application.setTitle("Test Title");
        application.setCompanyId(123);

        note = new Note();

        note.setId(1L);
        note.setNoteText("This is a note");
        note.setApplicationId(application.getId());
    }

    @Test
    public void testNoteProperties() {
        assertEquals(1L, note.getId());
        assertEquals("This is a note", note.getNoteText());
        assertEquals(application.getId(), note.getApplicationId());
    }
}
