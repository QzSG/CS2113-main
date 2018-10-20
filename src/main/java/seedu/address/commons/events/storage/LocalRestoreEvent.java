package seedu.address.commons.events.storage;

import java.nio.file.Path;

import seedu.address.commons.events.BaseEvent;

//@@author QzSG

/** Indicates a request for local restore*/
public class LocalRestoreEvent extends BaseEvent {

    public final Path path;

    public LocalRestoreEvent(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Restoring local backup";
    }
}
