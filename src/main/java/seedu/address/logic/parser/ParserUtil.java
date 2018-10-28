package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.event.Date;
import seedu.address.model.event.EventName;
import seedu.address.model.event.Time;
import seedu.address.model.eventContacts.EventContacts;
import seedu.address.model.expense.ExpenseCategory;
import seedu.address.model.expense.ExpenseDate;
import seedu.address.model.expense.ExpenseValue;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Body;
import seedu.address.model.task.DateTime;
import seedu.address.model.task.Priority;
import seedu.address.model.task.TaskName;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";
    public static final String MESSAGE_INVALID_PATH = "Path is not a valid file location";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_PHONE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_ADDRESS_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_EMAIL_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_TAG_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses {@code string} into a {@code Path}.
     */
    public static Optional<Path> parsePath(String stringPath) throws ParseException {
        if (!FileUtil.isValidPath(stringPath.trim())) {
            throw new ParseException(MESSAGE_INVALID_PATH);
        }
        return Optional.ofNullable(Paths.get(stringPath));
    }

    /**
     * Parses a {@code String name} into a {@code TaskName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static TaskName parseTaskName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new TaskName(trimmedName);
    }

    /**
     * Parses a {@code String body} into a {@codeBody}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code body} is invalid.
     */
    public static Body parseBody(String body) throws ParseException {
        return new Body(body);
    }

    /**
     * Parses a {@code String stareDateTime } into an {@code DateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code startDateTime} is invalid.
     */
    public static DateTime parseDateTime(String startDateTime) throws ParseException {
        requireNonNull(startDateTime);
        String trimmedStartDateTime = startDateTime.trim();
        if (!DateTime.isValidDateTime(trimmedStartDateTime)) {
            throw new ParseException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        return new DateTime(trimmedStartDateTime);
    }

    /**
     * Parses a {@code String endDateTime} into an {@code DateTime}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code endDateTime} is invalid.
     */
    public static DateTime parseTagDateTime(String endDateTime) throws ParseException {
        requireNonNull(endDateTime);
        String trimmedEndDateTime = endDateTime.trim();
        if (!DateTime.isValidDateTime(trimmedEndDateTime)) {
            throw new ParseException(DateTime.MESSAGE_DATETIME_CONSTRAINTS);
        }
        return new DateTime(trimmedEndDateTime);
    }

    /**
     * Parses a {@code String priority} into an {@code Priority}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code priority} is invalid.
     */
    public static Priority parsePriority(String priority) throws ParseException {
        String trimmedPriority = priority.trim();
        if (!Priority.isValidPriority(trimmedPriority)) {
            throw new ParseException(Priority.MESSAGE_PRIORITY_CONSTRAINTS);
        }
        return new Priority(trimmedPriority);
    }

    /**
     * Parses a {@code String name} into a {@code EventName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static EventName parseEventName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_NAME_CONSTRAINTS);
        }
        return new EventName(trimmedName);
    }

    /**
     * Parses a {@code String eventDate} into an {@code Event Date}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code eventDate} is invalid.
     */
    public static Date parseEventDate(String eventDate) throws ParseException {
        requireNonNull(eventDate);
        String trimmedEventDate = eventDate.trim();
        if (!Date.isValidDate(trimmedEventDate)) {
            throw new ParseException(Date.MESSAGE_EVENT_DATE_CONSTRAINTS);
        }
        return new Date(trimmedEventDate);
    }

    /**
     * Parses a {@code String eventTime} into an {@code Event Time}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code eventTime} is invalid.
     */
    public static Time parseEventTime(String eventTime) throws ParseException {
        requireNonNull(eventTime);
        String trimmedEventTime = eventTime.trim();
        if (!Time.isValidTime(trimmedEventTime)) {
            throw new ParseException(Time.MESSAGE_EVENT_TIME_CONSTRAINTS);
        }
        return new Time(trimmedEventTime);
    }

    /**
     * Parses a {@code String eventContacts} into a {@code EventContacts}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code eventContacts} is invalid.
     */
    public static EventContacts parseEventContacts(String eventContacts) throws ParseException {
        requireNonNull(eventContacts);
        String trimmedEventContacts = eventContacts.trim();
        if (!Tag.isValidTagName(trimmedEventContacts)) {
            throw new ParseException(EventContacts.MESSAGE_EVENT_CONTACTS_CONSTRAINTS);
        }
        return new EventContacts(trimmedEventContacts);
    }

    /**
     * Parses {@code Collection<String> eventContacts} into a {@code Set<EventContacts>}.
     */
    public static Set<EventContacts> parseEventContacts(Collection<String> eventContacts) throws ParseException {
        requireNonNull(eventContacts);
        final Set<EventContacts> eventContactsSet = new HashSet<>();
        for (String eventContactsName : eventContacts) {
            eventContactsSet.add(parseEventContacts(eventContactsName));
        }
        return eventContactsSet;
    }

    //@@author ChenSongJian
    /**
     * Parses a {@code String expenseValue} into a {@code expenseValue}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ExpenseCategory parseExpenseCategory(String expenseCategory) throws ParseException {
        requireNonNull(expenseCategory);
        String trimmedExpenseCategory = expenseCategory.trim();
        if (!ExpenseCategory.isValidExpenseCategory(trimmedExpenseCategory)) {
            throw new ParseException(ExpenseCategory.MESSAGE_EXPENSE_CATEGORY_CONSTRAINTS);
        }
        return new ExpenseCategory(trimmedExpenseCategory);
    }

    /**
     * Parses a {@code String expenseDate} into a {@code expenseDate}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ExpenseDate parseExpenseDate(String expenseDate) throws ParseException {
        requireNonNull(expenseDate);
        String trimmedExpenseDate = expenseDate.trim();
        if (!ExpenseDate.isValidDate(trimmedExpenseDate)) {
            throw new ParseException(ExpenseDate.MESSAGE_EXPENSE_DATE_CONSTRAINTS);
        }
        return new ExpenseDate(trimmedExpenseDate);
    }

    /**
     * Parses a {@code String expenseValue} into a {@code expenseValue}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static ExpenseValue parseExpenseValue(String expenseValue) throws ParseException {
        requireNonNull(expenseValue);
        String trimmedExpenseValue = expenseValue.trim();
        if (!ExpenseValue.isValidExpenseValue(trimmedExpenseValue)) {
            throw new ParseException(ExpenseValue.MESSAGE_EXPENSE_VALUE_CONSTRAINTS);
        }
        return new ExpenseValue(trimmedExpenseValue);
    }
    //@@author
}
