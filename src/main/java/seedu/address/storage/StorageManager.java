package seedu.address.storage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.concurrent.Task;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.AddressBookLocalBackupEvent;
import seedu.address.commons.events.model.AddressBookLocalRestoreEvent;
import seedu.address.commons.events.model.AddressBookOnlineRestoreEvent;
import seedu.address.commons.events.model.UserPrefsChangedEvent;
import seedu.address.commons.events.storage.DataRestoreExceptionEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.events.storage.LocalRestoreEvent;
import seedu.address.commons.events.storage.OnlineBackupEvent;
import seedu.address.commons.events.storage.OnlineBackupSuccessResultEvent;
import seedu.address.commons.events.storage.OnlineRestoreEvent;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.OnlineBackupFailureException;
import seedu.address.commons.util.XmlUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;


/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private UserPrefsStorage userPrefsStorage;

    private GitHubStorage gitHubStorage;

    public StorageManager(AddressBookStorage addressBookStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    //@@author QzSG
    @Subscribe
    public void handleUserPrefsChangedEvent(UserPrefsChangedEvent event) throws IOException {
        saveUserPrefs(event.data);
    }
    //@@author


    // ================ AddressBook methods ==============================

    @Override
    public Path getAddressBookFilePath() {
        return addressBookStorage.getAddressBookFilePath();
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataConversionException, IOException {
        return readAddressBook(addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return addressBookStorage.readAddressBook(filePath);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, addressBookStorage.getAddressBookFilePath());
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        addressBookStorage.saveAddressBook(addressBook, filePath);
    }

    @Override
    public void backupAddressBook(ReadOnlyAddressBook addressBook, Path backupFilePath) throws IOException {
        addressBookStorage.backupAddressBook(addressBook, backupFilePath);
    }

    @Override
    @Subscribe
    public void handleAddressBookChangedEvent(AddressBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveAddressBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }
    //@@author QzSG
    @Override
    @Subscribe
    public void handleAddressBookLocalBackupEvent(AddressBookLocalBackupEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Saving student planner data as backup"));
        try {
            backupAddressBook(event.data, event.filePath);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    // ================ GitHub Storage methods ==============================
    /*
        Listens directly to BackupCommand
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void handleOnlineBackupEvent(OnlineBackupEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Saving data to online storage"));
        backupOnline(event.target, event.data, event.fileName, event.authToken);
    }

    /*
        Listens directly to RestoreCommand
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void handleOnlineRestoreEvent(OnlineRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring data from online storage"));
        restoreOnline(event.target, event.ref, event.authToken);
    }

    /*
    Listens directly to RestoreCommand
    */
    @SuppressWarnings("unused")
    @Subscribe
    public void handleLocalRestoreEvent(LocalRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Retrieving student planner data from storage"));
        try {
            ReadOnlyAddressBook restoredReadOnlyAddressBook = readAddressBook(event.path).get();
            raise(new AddressBookLocalRestoreEvent(restoredReadOnlyAddressBook));
        } catch (IOException | DataConversionException | NoSuchElementException e) {
            raise(new DataRestoreExceptionEvent(e));
        }
    }

    /**
     * Performs online backup to supported online storage
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param data  {@code ReadOnlyAddressBook} data
     * @param fileName Name of save backup file
     * @param authToken Personal Access Token for GitHub Authentication
     */
    private void backupOnline(OnlineStorage.Type target, ReadOnlyAddressBook data,
                              String fileName, Optional<String> authToken) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Task backupTask = getOnlineBackupTask(target, data, fileName, authToken);

        executorService.submit(backupTask);
    }

    /**
     * Performs restoration from supported online storage
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param ref   Reference String to uniquely identify a file or a url to the backup resource.
     * @param authToken JWT or any other form of access token required by specific online backup service
     */
    private void restoreOnline(OnlineStorage.Type target, String ref, Optional<String> authToken) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Task restoreTask = getOnlineRestoreTask(target, ref, authToken);

        executorService.submit(restoreTask);
    }

    /**
     * Performs restoration from local storage
     * @param path File path to local backup
     */
    private void restoreLocal(Path path) {

    }

    private Task getOnlineRestoreTask(OnlineStorage.Type target, String ref, Optional<String> authToken) {
        Task restoreTask = new Task<AddressBook>() {
            @Override public AddressBook call() throws Exception {
                switch(target) {
                    case GITHUB:
                    default:
                        gitHubStorage = new GitHubStorage(
                            authToken.orElseThrow(() -> new OnlineBackupFailureException("Invalid auth "
                                    + "token received")));
                        AddressBook restoredAddressBook = XmlUtil.getDataFromString(
                                gitHubStorage.readContentFromGist(ref), XmlSerializableAddressBook.class).toModelType();
                        return restoredAddressBook;
                }
            }
        };
        restoreTask.setOnSucceeded(event -> {
            raise(new AddressBookOnlineRestoreEvent(((Task<AddressBook>) restoreTask).getValue()));
        });
        restoreTask.setOnFailed(event -> {
            raise(new DataRestoreExceptionEvent((Exception) restoreTask.getException()));
        });
        return restoreTask;
    }

    /**
     * Creates an online backup tasks based on {@code OnlineStorage.Type} and returns the created task.
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param data  {@code ReadOnlyAddressBook} data
     * @param fileName Name of save backup file
     * @param authToken Personal Access Token for GitHub Authentication
     * @return
     */
    private Task getOnlineBackupTask(OnlineStorage.Type target, ReadOnlyAddressBook data, String fileName,
                                     Optional<String> authToken) {
        Task backupTask = new Task<OnlineBackupSuccessResultEvent>() {
            @Override public OnlineBackupSuccessResultEvent call() throws Exception {
                switch(target) {
                    case GITHUB:
                    default:
                        gitHubStorage = new GitHubStorage(
                                authToken.orElseThrow(() -> new OnlineBackupFailureException("Invalid auth "
                                        + "token received")));
                        URL url = gitHubStorage.saveContentToStorage(XmlUtil.convertDataToString(
                                new XmlSerializableAddressBook(data)), fileName, "Address Book Backup");
                        String successMessage = String.format(GitHubStorage.SUCCESS_MESSAGE, url);
                        updateMessage(successMessage);
                        String ref = url.getPath().substring(1);
                        return new OnlineBackupSuccessResultEvent(OnlineStorage.Type.GITHUB, ref);
                }
            }
        };
        backupTask.setOnSucceeded(event -> {
            raise(new NewResultAvailableEvent(backupTask.getMessage()));
            raise((OnlineBackupSuccessResultEvent) backupTask.getValue());
        });
        backupTask.setOnFailed(event -> {
            raise(new DataSavingExceptionEvent((Exception) backupTask.getException()));
        });
        return backupTask;
    }
}
