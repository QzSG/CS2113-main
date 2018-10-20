package seedu.address.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs {

    private GuiSettings guiSettings;
    private Path addressBookFilePath;
    private Path addressBookBackupFilePath;

    private String addressBookGistId;

    public UserPrefs() {
        setGuiSettings(500, 500, 0, 0);
        setAddressBookFilePath(getAddressBookFilePath());
        setAddressBookBackupFilePath(getAddressBookBackupFilePath());
    }

    public GuiSettings getGuiSettings() {
        return guiSettings == null ? new GuiSettings() : guiSettings;
    }

    public void updateLastUsedGuiSetting(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    public void setGuiSettings(double width, double height, int x, int y) {
        guiSettings = new GuiSettings(width, height, x, y);
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath == null ? Paths.get("data" , "addressbook.xml") : addressBookFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        this.addressBookFilePath = addressBookFilePath;
    }

    public Path getAddressBookBackupFilePath() {
        return addressBookBackupFilePath == null ? Paths.get("data" , "addressbook.bak") : addressBookBackupFilePath;
    }

    public void setAddressBookBackupFilePath(Path addressBookBackupFilePath) {
        this.addressBookBackupFilePath = addressBookBackupFilePath;
    }

    public String getAddressBookGistId() {
        return addressBookGistId;
    }

    public void setAddressBookGistId(String addressBookGistId) {
        this.addressBookGistId = addressBookGistId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { //this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return Objects.equals(guiSettings, o.guiSettings)
                && Objects.equals(addressBookFilePath.toAbsolutePath(), o.addressBookFilePath.toAbsolutePath())
                && Objects.equals(
                        addressBookBackupFilePath.toAbsolutePath(), o.addressBookBackupFilePath.toAbsolutePath())
                && Objects.equals(addressBookGistId, o.addressBookGistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath,
                addressBookBackupFilePath, addressBookGistId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings.toString());
        sb.append("\nLocal data file location : " + addressBookFilePath);
        sb.append("\nLocal data backup file location : " + addressBookBackupFilePath);
        sb.append("\nOnline data backup gist id : " + addressBookGistId);
        return sb.toString();
    }

}
