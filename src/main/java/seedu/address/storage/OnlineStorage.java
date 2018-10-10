package seedu.address.storage;

//@@author QzSG

import seedu.address.commons.exceptions.OnlineBackupFailureException;

import java.io.IOException;

/**
 * API of the OnlineStorage component
 */
public interface OnlineStorage {

    enum OnlineStorageType {
        GITHUB
    }
    /**
     * Saves the given {@code content} to the online storage.
     * @param string cannot be null.
     * @param fileName cannot be null.
     * @throws OnlineBackupFailureException if there was any problem saving to online storage.
     */
    void saveContentToStorage(String content, String fileName) throws IOException, OnlineBackupFailureException;

    /**
     * Saves the given {@code content} to the online storage.
     * @param string cannot be null.
     * @param fileName cannot be null.
     * @param description can be null.
     * @throws OnlineBackupFailureException if there was any problem saving to online storage.
     */
    void saveContentToStorage(String content, String fileName, String description) throws IOException, OnlineBackupFailureException;
}
