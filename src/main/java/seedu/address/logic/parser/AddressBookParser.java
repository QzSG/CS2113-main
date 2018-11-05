package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddEventCommand;
import seedu.address.logic.commands.AddExpenseCommand;
import seedu.address.logic.commands.AddTaskCommand;
import seedu.address.logic.commands.BackupCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ClearExpenseCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteEventCommand;
import seedu.address.logic.commands.DeleteExpenseCommand;
import seedu.address.logic.commands.DeleteTaskCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditExpenseCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.ExpenseTrendCommand;
import seedu.address.logic.commands.FindAddressCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindNameCommand;
import seedu.address.logic.commands.FindPhoneCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListTaskCommand;
import seedu.address.logic.commands.MonthlyExpenseCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.RedoExpenseCommand;
import seedu.address.logic.commands.RestoreCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UndoExpenseCommand;
import seedu.address.logic.commands.UpdateTaskCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case SelectCommand.COMMAND_WORD:
            return new SelectCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindAddressCommand.COMMAND_WORD:
            return new FindAddressCommandParser().parse(arguments);

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case FindNameCommand.COMMAND_WORD:
            return new FindNameCommandParser().parse(arguments);

        case FindPhoneCommand.COMMAND_WORD:
            return new FindPhoneCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case HistoryCommand.COMMAND_WORD:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();
        //@@author QzSG
        case BackupCommand.COMMAND_WORD:
            return new BackupCommandParser().parse(arguments);

        case RestoreCommand.COMMAND_WORD:
            return new RestoreCommandParser().parse(arguments);

        //@@author luhan02
        case AddTaskCommand.COMMAND_WORD:
            return new AddTaskCommandParser().parse(arguments);

        case ListTaskCommand.COMMAND_WORD:
            return new ListTaskCommand();

        case UpdateTaskCommand.COMMAND_WORD:
            return new UpdateTaskCommandParser().parse(arguments);

        //case SelectTaskCommand.COMMAND_WORD:
            //return new SelectTaskCommandParser().parse(arguments);

        case DeleteTaskCommand.COMMAND_WORD:
            return new DeleteTaskCommandParser().parse(arguments);

        //@@author ian-tjahjono
        case AddEventCommand.COMMAND_WORD:
            return new AddEventCommandParser().parse(arguments);

        case DeleteEventCommand.COMMAND_WORD:
            return new DeleteEventCommandParser().parse(arguments);

        //@@author ChenSongJian
        case AddExpenseCommand.COMMAND_WORD:
            return new AddExpenseCommandParser().parse(arguments);

        case ClearExpenseCommand.COMMAND_WORD:
            return new ClearExpenseCommand();

        case DeleteExpenseCommand.COMMAND_WORD:
            return new DeleteExpenseCommandParser().parse(arguments);

        case EditExpenseCommand.COMMAND_WORD:
            return new EditExpenseCommandParser().parse(arguments);

        case ExpenseTrendCommand.COMMAND_WORD:
            return new ExpenseTrendCommand();

        case MonthlyExpenseCommand.COMMAND_WORD:
            return new MonthlyExpenseCommandParser().parse(arguments);

        case RedoExpenseCommand.COMMAND_WORD:
            return new RedoExpenseCommand();

        case UndoExpenseCommand.COMMAND_WORD:
            return new UndoExpenseCommand();
        //@@author
        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
