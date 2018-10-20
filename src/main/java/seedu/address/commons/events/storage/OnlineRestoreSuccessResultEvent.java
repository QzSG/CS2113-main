package seedu.address.commons.events.storage;

import seedu.address.commons.events.BaseEvent;
import seedu.address.storage.OnlineStorage;

//@@author QzSG

/** Indicates a request for online backup*/
public class OnlineRestoreSuccessResultEvent extends BaseEvent {

    public final OnlineStorage.Type target;
    public final String ref;

    public OnlineRestoreSuccessResultEvent(OnlineStorage.Type target, String ref) {
        this.target = target;
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "Signaling user preference update with success reference from specific online service";
    }
}
