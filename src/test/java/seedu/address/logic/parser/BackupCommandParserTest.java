package seedu.address.logic.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import seedu.address.logic.commands.BackupCommand;
import seedu.address.model.UserPrefs;

import java.nio.file.Path;
import java.util.Optional;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

//@@author QzSG
public class BackupCommandParserTest {

    private BackupCommandParser parser = new BackupCommandParser();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void parse_emptyArg_parsesPasses() {
        BackupCommand expectedBackupCommand =
                new BackupCommand(Optional.empty());

        assertParseSuccess(parser, "  ", expectedBackupCommand);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        Path tempBackupFilePath = testFolder.getRoot().toPath().resolve("Temp.bak");

        BackupCommand expectedBackupCommand =
                new BackupCommand(Optional.ofNullable(tempBackupFilePath));
        assertParseSuccess(parser, tempBackupFilePath.toString(), expectedBackupCommand);
    }
}
