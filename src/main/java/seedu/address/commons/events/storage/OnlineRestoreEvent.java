package seedu.address.commons.events.storage;

import seedu.address.commons.events.BaseEvent;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.storage.OnlineStorage;

import java.util.Optional;

//@@author QzSG

/** Indicates a request for online backup*/
public class OnlineRestoreEvent extends BaseEvent {

    public final OnlineStorage.OnlineStorageType target;
    public final String ref;
    public final Optional<String> authToken;

    public OnlineRestoreEvent(OnlineStorage.OnlineStorageType target, String ref, Optional<String> authToken) {
        this.target = target;
        this.ref = ref;
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "Restoring online backup";
    }
}
