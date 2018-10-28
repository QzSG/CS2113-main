package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPENSE_CATEGORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPENSE_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPENSE_VALUE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditExpenseCommand;
import seedu.address.logic.commands.EditExpenseCommand.EditExpenseDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditExpenseCommand object
 */

public class EditExpenseCommandParser implements Parser<EditExpenseCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditExpenseCommand
     * and returns an EditExpenseCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditExpenseCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_EXPENSE_CATEGORY,
                PREFIX_EXPENSE_DATE, PREFIX_EXPENSE_VALUE, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditExpenseCommand.MESSAGE_USAGE), pe);
        }

        EditExpenseDescriptor editExpenseDescriptor = new EditExpenseDescriptor();
        if (argMultimap.getValue(PREFIX_EXPENSE_CATEGORY).isPresent()) {
            editExpenseDescriptor.setExpenseCategory(
                    ParserUtil.parseExpenseCategory(argMultimap.getValue(PREFIX_EXPENSE_CATEGORY).get()));
        }
        if (argMultimap.getValue(PREFIX_EXPENSE_DATE).isPresent()) {
            editExpenseDescriptor.setExpenseDate(
                    ParserUtil.parseExpenseDate(argMultimap.getValue(PREFIX_EXPENSE_DATE).get()));
        }
        if (argMultimap.getValue(PREFIX_EXPENSE_VALUE).isPresent()) {
            editExpenseDescriptor.setExpenseValue(
                    ParserUtil.parseExpenseValue(argMultimap.getValue(PREFIX_EXPENSE_VALUE).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editExpenseDescriptor::setTags);

        if (!editExpenseDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditExpenseCommand.MESSAGE_NOT_EDITED);
        }

        return new EditExpenseCommand(index, editExpenseDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
