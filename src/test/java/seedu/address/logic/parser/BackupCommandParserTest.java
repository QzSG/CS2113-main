package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import seedu.address.logic.commands.BackupCommand;
import seedu.address.logic.parser.exceptions.ParseException;


//@@author QzSG
public class BackupCommandParserTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private BackupCommandParser parser = new BackupCommandParser();

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

    @Test
    public void parse_invalidArgs_throwsParseException() throws Exception {
        thrown.expect(ParseException.class);
        parser.parse("\\ /  /"); //Throws on windows
        parser.parse("\\0"); //Throws on Linux
    }

}
