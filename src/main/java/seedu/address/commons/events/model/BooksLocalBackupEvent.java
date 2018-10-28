//@@author QzSG
package seedu.address.commons.events.model;

import java.nio.file.Path;

import seedu.address.commons.events.BaseEvent;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyExpenseBook;


/** Indicates a AddressBook restore request*/
public class BooksLocalBackupEvent extends BaseEvent {

    public final ReadOnlyAddressBook readOnlyAddressBook;
    public final ReadOnlyExpenseBook readOnlyExpenseBook;
    public final Path addressBookPath;
    public final Path expenseBookPath;

    public BooksLocalBackupEvent(ReadOnlyAddressBook readOnlyAddressBook, Path addressBookPath,
                                 ReadOnlyExpenseBook readOnlyExpenseBook, Path expenseBookPath) {
        this.readOnlyAddressBook = readOnlyAddressBook;
        this.addressBookPath = addressBookPath;
        this.readOnlyExpenseBook = readOnlyExpenseBook;
        this.expenseBookPath = expenseBookPath;
    }

    @Override
    public String toString() {
        return "Sending local backup request to storage manager";
    }
}
