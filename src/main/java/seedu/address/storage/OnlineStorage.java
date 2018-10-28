package seedu.address.storage;

//@@author QzSG

import java.io.IOException;

import seedu.address.commons.exceptions.OnlineBackupFailureException;

/**
 * API of the OnlineStorage component
 */
public interface OnlineStorage {

    /**
     * Enum types for support online storage
     */
    enum Type {
        GITHUB
    }

    /**
     * Saves the given {@code content} to the online storage.
     * @param content cannot be null.
     * @param fileName cannot be null.
     * @throws OnlineBackupFailureException if there was any problem saving to online storage.
     */
    void saveContentToStorage(String content, String fileName) throws IOException, OnlineBackupFailureException;

    /**
     * Saves the given {@code content} to the online storage.
     * @param content cannot be null.
     * @param fileName cannot be null.
     * @param description can be null.
     * @return Object representing the return of a successful online backup, can be a String or a URL
     * @throws OnlineBackupFailureException if there was any problem saving to online storage.
     */
    Object saveContentToStorage(String content, String fileName, String description)
            throws IOException, OnlineBackupFailureException;
}
