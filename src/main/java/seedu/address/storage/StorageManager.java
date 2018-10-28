package seedu.address.storage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

import com.google.common.eventbus.Subscribe;

import javafx.concurrent.Task;
import javafx.util.Duration;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.AddressBookLocalRestoreEvent;
import seedu.address.commons.events.model.AddressBookOnlineRestoreEvent;
import seedu.address.commons.events.model.BooksLocalBackupEvent;
import seedu.address.commons.events.model.ExpenseBookChangedEvent;
import seedu.address.commons.events.model.ExpenseBookLocalRestoreEvent;
import seedu.address.commons.events.model.ExpenseBookOnlineRestoreEvent;
import seedu.address.commons.events.model.UserPrefsChangedEvent;
import seedu.address.commons.events.storage.DataRestoreExceptionEvent;
import seedu.address.commons.events.storage.DataSavingExceptionEvent;
import seedu.address.commons.events.storage.LocalRestoreEvent;
import seedu.address.commons.events.storage.OnlineBackupEvent;
import seedu.address.commons.events.storage.OnlineBackupSuccessResultEvent;
import seedu.address.commons.events.storage.OnlineRestoreEvent;
import seedu.address.commons.events.ui.NewNotificationAvailableEvent;
import seedu.address.commons.events.ui.NewResultAvailableEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.exceptions.OnlineBackupFailureException;
import seedu.address.commons.util.XmlUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ExpenseBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyExpenseBook;
import seedu.address.model.UserPrefs;

/**
 * Manages storage of AddressBook data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private AddressBookStorage addressBookStorage;
    private ExpenseBookStorage expenseBookStorage;
    private UserPrefsStorage userPrefsStorage;

    private GitHubStorage gitHubStorage;

    public StorageManager(AddressBookStorage addressBookStorage,
                          ExpenseBookStorage expenseBookStorage,
                          UserPrefsStorage userPrefsStorage) {
        super();
        this.addressBookStorage = addressBookStorage;
        this.expenseBookStorage = expenseBookStorage;
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
        logger.fine("Attempting to backup address book data file: " + backupFilePath);
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

    /*
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
    */
    @Subscribe
    public void handleBooksLocalBackupEvent(BooksLocalBackupEvent event) {
        try {
            logger.info(LogsCenter.getEventHandlingLogMessage(event, "Saving student planner data as backup"));
            backupAddressBook(event.readOnlyAddressBook, event.addressBookPath);
            backupExpenseBook(event.readOnlyExpenseBook, event.expenseBookPath);
            raise(new NewNotificationAvailableEvent("Backup Operation", "Local Backup succeeded!",
                    Optional.ofNullable(Duration.seconds(5))));
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    /*
    Listens directly to RestoreCommand
    */
    @SuppressWarnings("unused")
    @Subscribe
    public void handleLocalRestoreEvent(LocalRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Retrieving student planner data from storage"));
        try {
            ReadOnlyAddressBook restoredReadOnlyAddressBook = readAddressBook(event.addressBookPath).get();
            ReadOnlyExpenseBook restoredReadOnlyExpenseBook = readExpenseBook(event.expenseBookPath).get();
            raise(new AddressBookLocalRestoreEvent(restoredReadOnlyAddressBook));
            raise(new ExpenseBookLocalRestoreEvent(restoredReadOnlyExpenseBook));
        } catch (IOException | DataConversionException | NoSuchElementException e) {
            raise(new DataRestoreExceptionEvent(e));
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
        backupOnline(event.target, event.addressData, event.expenseData, event.authToken);
    }

    /*
        Listens directly to RestoreCommand
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void handleOnlineRestoreEvent(OnlineRestoreEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Restoring data from online storage"));
        restoreOnline(event.target, event.targetBook, event.ref, event.authToken);
    }

    /**
     * Performs online backup to supported online storage
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param addressData  {@code ReadOnlyAddressBook} addressData
     * @param expenseData  {@code ReadOnlyExpenseBook} expenseData
     * @param authToken Personal Access Token for GitHub Authentication
     */
    private void backupOnline(OnlineStorage.Type target, ReadOnlyAddressBook addressData,
                              ReadOnlyExpenseBook expenseData, Optional<String> authToken) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(getOnlineBackupTask(target, addressData, "AddressBook.bak", authToken));
        executorService.submit(getOnlineBackupTask(target, expenseData, "ExpenseBook.bak", authToken));
    }

    /**
     * Performs restoration from supported online storage
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param ref   Reference String to uniquely identify a file or a url to the backup resource.
     * @param authToken JWT or any other form of access token required by specific online backup service
     */
    private void restoreOnline(OnlineStorage.Type target, UserPrefs.TargetBook targetBook,
                               String ref, Optional<String> authToken) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Task restoreTask = getOnlineRestoreTask(target, targetBook, ref, authToken);

        executorService.submit(restoreTask);
    }

    /**
     * Performs restoration from local storage
     * @param path File path to local backup
     */
    private void restoreLocal(Path path) {
        //TODO
    }

    private Task getOnlineRestoreTask(OnlineStorage.Type target, UserPrefs.TargetBook targetBook,
                                      String ref, Optional<String> authToken) {
        Task restoreTask = new Task<>() {
            @Override public Object call() throws Exception {
                switch(target) {
                    case GITHUB:
                    default:
                        gitHubStorage = new GitHubStorage(
                            authToken.orElseThrow(() -> new OnlineBackupFailureException("Invalid auth "
                                    + "token received")));
                        if (targetBook == UserPrefs.TargetBook.AddressBook) {
                            AddressBook restoredAddressBook = XmlUtil.getDataFromString(
                                    gitHubStorage.readContentFromGist(targetBook, ref),
                                    XmlSerializableAddressBook.class).toModelType();
                            return restoredAddressBook;
                        }
                        if (targetBook == UserPrefs.TargetBook.ExpenseBook) {
                            ExpenseBook restoredExpenseBook = XmlUtil.getDataFromString(
                                    gitHubStorage.readContentFromGist(targetBook, ref),
                                    XmlSerializableExpenseBook.class).toModelType();
                            return restoredExpenseBook;
                        } else {
                            throw (new IllegalValueException("Invalid book data"));
                        }
                }
            }
        };
        restoreTask.setOnSucceeded(event -> {
            if (targetBook == UserPrefs.TargetBook.AddressBook) {
                raise(new AddressBookOnlineRestoreEvent(((Task<AddressBook>) restoreTask).getValue()));
            } else if (targetBook == UserPrefs.TargetBook.ExpenseBook) {
                raise(new ExpenseBookOnlineRestoreEvent(((Task<ExpenseBook>) restoreTask).getValue()));
            }

        });
        restoreTask.setOnFailed(event -> {
            restoreTask.getException().printStackTrace();
            raise(new DataRestoreExceptionEvent((Exception) restoreTask.getException()));
        });
        return restoreTask;
    }

    /**
     * Creates an online backup tasks based on {@code OnlineStorage.Type} and returns the created task.
     * @param target {@code OnlineStorage.Type} such as GITHUB
     * @param data  {@code Object} data
     * @param fileName Name of save backup file
     * @param authToken Personal Access Token for GitHub Authentication
     * @return
     */
    private Task getOnlineBackupTask(OnlineStorage.Type target, Object data, String fileName,
                                     Optional<String> authToken) {
        Task backupTask = new Task<OnlineBackupSuccessResultEvent>() {
            @Override public OnlineBackupSuccessResultEvent call() throws Exception {
                switch(target) {
                    case GITHUB:
                    default:
                        gitHubStorage = new GitHubStorage(
                                authToken.orElseThrow(() -> new OnlineBackupFailureException("Invalid auth "
                                        + "token received")));
                        URL url = gitHubStorage.saveContentToStorage(handleBookData(data), fileName,
                                "Student Book Backup");
                        String successMessage = GitHubStorage.SUCCESS_MESSAGE;
                        updateMessage(successMessage);
                        String ref = url.getPath().substring(1);
                        return new OnlineBackupSuccessResultEvent(OnlineStorage.Type.GITHUB,
                                handleUserPrefsUpdateField(data), ref);
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

    /**
     * Returns the proper xml to string content based on the type of book data
     * @param data
     * @return Book data serialised as a string
     * @throws IllegalValueException
     * @throws JAXBException
     */
    private String handleBookData(Object data) throws IllegalValueException, JAXBException {
        if (data instanceof ReadOnlyAddressBook) {
            return XmlUtil.convertDataToString(
                    new XmlSerializableAddressBook((ReadOnlyAddressBook) data));
        }
        if (data instanceof ReadOnlyExpenseBook) {
            return XmlUtil.convertDataToString(
                    new XmlSerializableExpenseBook((ReadOnlyExpenseBook) data));
        } else {
            throw (new IllegalValueException("Invalid data provided"));
        }
    }

    /**
     * Converts data object to its book data type used to update specific User Preference fields
     * @param data
     * @return Type of data book
     * @throws IllegalValueException
     * @throws JAXBException
     */
    private UserPrefs.TargetBook handleUserPrefsUpdateField(Object data) throws IllegalValueException, JAXBException {
        if (data instanceof ReadOnlyAddressBook) {
            return UserPrefs.TargetBook.AddressBook;
        }
        if (data instanceof ReadOnlyExpenseBook) {
            return UserPrefs.TargetBook.ExpenseBook;
        } else {
            throw (new IllegalValueException("Invalid data provided"));
        }
    }

    //============ Expense ===============================================================================

    @Override
    public Optional<ReadOnlyExpenseBook> readExpenseBook() throws DataConversionException, IOException {
        return readExpenseBook(expenseBookStorage.getExpenseBookFilePath());
    }

    @Override
    public Optional<ReadOnlyExpenseBook> readExpenseBook(Path filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return expenseBookStorage.readExpenseBook(filePath);
    }

    @Override
    public Path getExpenseBookFilePath() {
        return expenseBookStorage.getExpenseBookFilePath();
    }

    @Override
    public void saveExpenseBook(ReadOnlyExpenseBook expenseBook) throws IOException {
        saveExpenseBook(expenseBook, expenseBookStorage.getExpenseBookFilePath());
    }

    @Override
    public void saveExpenseBook(ReadOnlyExpenseBook expenseBook, Path filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        expenseBookStorage.saveExpenseBook(expenseBook, filePath);
    }

    @Override
    public void backupExpenseBook(ReadOnlyExpenseBook expenseBook, Path backupFilePath) throws IOException {
        logger.fine("Attempting to backup expense book data file: " + backupFilePath);
        expenseBookStorage.backupExpenseBook(expenseBook, backupFilePath);
    }

    @Override
    @Subscribe
    public void handleExpenseBookChangedEvent(ExpenseBookChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveExpenseBook(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

    /*
    @Override
    @Subscribe
    public void handleExpenseBookLocalBackupEvent(ExpenseBookLocalBackupEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Saving student planner data as backup"));
        try {
            backupExpenseBook(event.data, event.filePath);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }
    */
}
