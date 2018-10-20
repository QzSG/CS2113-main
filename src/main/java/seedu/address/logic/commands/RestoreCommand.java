//@@author QzSG
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.Optional;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.storage.LocalRestoreEvent;
import seedu.address.commons.events.storage.OnlineRestoreEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.storage.OnlineStorage;

/**
 * Lists all persons in the address book to the user.
 */
public class RestoreCommand extends Command {

    public static final String COMMAND_WORD = "restore";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restore student planner data from location specified "
            + "(restores from default backup data path if not provided)\n"
            + "Parameters: [github authToken] OR [PATH] (must be a writable path)\n"
            + "Example: " + COMMAND_WORD + " data\\addressbook.bak OR\n"
            + "Example: " + COMMAND_WORD + " github my_personal_access_token";

    public static final String MESSAGE_SUCCESS = "Restoring Backup from %s";
    public static final String MESSAGE_FAILURE = "Please perform an online backup using %s first or set relevant"
             + " settings in user prefs";
    private Optional<Path> backupPath;
    private boolean isLocal = true;
    private OnlineStorage.Type target;
    private Optional<String> authToken;

    /**
     * Creates a RestoreCommand to backup data to storage
     */
    public RestoreCommand(Optional<Path> backupPath, boolean isLocal,
                          Optional<OnlineStorage.Type> target, Optional<String> authToken) {
        this.backupPath = backupPath;
        this.isLocal = isLocal;
        this.target = target.orElse(OnlineStorage.Type.GITHUB);
        this.authToken = authToken;

    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        if (isLocal) {
            EventsCenter.getInstance().post(new LocalRestoreEvent(retrievePath(model)));
            return new CommandResult(String.format(MESSAGE_SUCCESS, retrievePath(model).toString()));
        } else {
            String gistId = model.getUserPrefs().getAddressBookGistId();
            if (gistId == null) {
                return new CommandResult(String.format(MESSAGE_FAILURE, ": backup github [personal_access_token]"));
            }
            EventsCenter.getInstance().post(new OnlineRestoreEvent(target,
                    model.getUserPrefs().getAddressBookGistId(), authToken));
            return new CommandResult(String.format(MESSAGE_SUCCESS, "GitHub Gists"));
        }

    }

    private Path retrievePath(Model model) {
        return backupPath.orElse(model.getUserPrefs().getAddressBookBackupFilePath());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RestoreCommand // instanceof handles nulls
                && backupPath.equals(((RestoreCommand) other).backupPath));
    }
}
