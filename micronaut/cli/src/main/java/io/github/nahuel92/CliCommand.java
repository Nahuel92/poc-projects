package io.github.nahuel92;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.json.JsonMapper;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Introspected
@Command(
        name = "logparser",
        description = "Log-parsing tool that pretty-prints Rely Health integration service logs to help humans understand the errors faster.",
        version = "log-parser 0.1",
        header = {
                "@|green .____                   __________                                   |@",
                "@|green |    |    ____   ____   \\______   \\_____ _______  ______ ___________ |@",
                "@|green |    |   /  _ \\ / ___\\   |     ___/\\__  \\\\_  __ \\/  ___// __ \\_  __ \\ |@",
                "@|green |    |__(  <_> ) /_/  >  |    |     / __ \\|  | \\/\\___ \\\\  ___/|  | \\/ |@",
                "@|green |_______ \\____/\\___  /   |____|    (____  /__|  /____  >\\___  >__|   |@",
                "@|green        \\/    /_____/                   \\/           \\/     \\/       |@"
        },
        mixinStandardHelpOptions = true,
        sortOptions = false

)
public class CliCommand implements Runnable {
    private static final String TIMESTAMP_REGEX = "\\{\"timestamp\":\"(.*?)\"";
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(TIMESTAMP_REGEX);

    private static final String SHORT_MESSAGE_REGEX = "\"short-message\":\"(.*?)\"}$";
    private static final Pattern SHORT_MESSAGE_PATTERN = Pattern.compile(SHORT_MESSAGE_REGEX, Pattern.DOTALL);

    private static final Pattern TASK_PATTERN = Pattern.compile("Task '(.*?)'");
    private static final String LOG_LINE_REGEX = "Task '(.*?)'.*?customer '(.*?)'(?:.*?AMX patientId '(.*?)')?(?:.*?Rely Health patientId '(.*?)')?(?:.*?surveyId '(.*?)')?(?:.*?statusId '(.*?)')?(?:.*?encounterId '(.*?)')?.*?Error message '(?:.*\"Response\":\"(.*?)\".*?|(.*?))\\s*'*$";
    private static final Pattern LOG_LINE_PATTERN = Pattern.compile(LOG_LINE_REGEX, Pattern.DOTALL);

    @NotBlank(message = "The path to the log file can't be blank")
    @Option(
            names = {"-f", "--file"},
            description = "Path to the log file to parse.",
            paramLabel = "FILE",
            arity = "1..1",
            required = true
    )
    private Path path;

    @Option(
            names = {"-c", "--customer"},
            description = "Show errors matching this customer only.",
            defaultValue = "",
            arity = "0..1",
            paramLabel = "CUSTOMER"
    )
    private String customer;

    @Option(
            names = {"-t", "--task"},
            description = "Show errors matching this task type only.",
            defaultValue = "",
            arity = "0..1",
            paramLabel = "TASK"
    )
    private String task;

    @Option(
            names = {"-k", "--keyword"},
            description = "Show errors matching this keyword only (for example, encounterId, patientId, etc.).",
            defaultValue = "",
            arity = "0..1",
            paramLabel = "KEYWORD"
    )
    private String keyword;

    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    public static void main(final String... args) {
        setCurrentWorkingDirectory();
        PicocliRunner.run(CliCommand.class, args);
    }

    public void run() {
        final var start = System.nanoTime();
        validateFileExtension();
        final var commandOutput = getCommandOutput();
        printResults(commandOutput);
        final var end = System.nanoTime();
        System.out.println("Took: " + (end - start) / 1_000_000.0 + " ms");
    }

    private static void setCurrentWorkingDirectory() {
        /* Required for cross-platform compilation only */
        System.setProperty("user.dir", Path.of("").toAbsolutePath().toString());
    }

    private void validateFileExtension() {
        if (!path.getFileName().toString().endsWith(".log")) {
            throw new CommandLine.ParameterException(spec.commandLine(), "File '%s' is not a valid '*.log' file. Verify the file name and try again.".formatted(path));
        }
    }

    private CommandOutput getCommandOutput() {
        try (final var lines = Files.lines(path)) {
            return lines
                    .parallel()
                    .filter(e -> e.contains("ERROR"))
                    .filter(byPossibleCriteria())
                    .map(this::extractLogMessage)
                    .sorted(byTaskNameAndTimestamp())
                    .distinct()
                    .map(this::parseLogLine)
                    .collect(
                            Collectors.teeing(
                                    Collectors.toList(),
                                    Collectors.counting(),
                                    CommandOutput::new
                            )
                    );
        } catch (final IOException e) {
            System.err.println(
                    CommandLine.Help.Ansi.AUTO.string(
                            "@|bold,red Failed to read the file. Error: '" + e.getMessage() + "' |@"
                    )
            );
            System.exit(-1);
            throw new IllegalStateException("Should never happen due to previous System.exit() call");
        }
    }

    private Predicate<String> byPossibleCriteria() {
        return e -> byCustomer()
                .or(byTask())
                .or(byKeyword())
                .test(e);
    }

    private Predicate<String> byCustomer() {
        return e -> customer == null || customer.isEmpty() ||
                    e.contains(customer.toLowerCase()) || e.contains(customer.toUpperCase());
    }

    private Predicate<String> byTask() {
        return e -> task == null || task.isEmpty() || e.contains(task.toUpperCase());
    }

    private Predicate<String> byKeyword() {
        return e -> keyword == null || keyword.isEmpty() || e.toLowerCase().contains(keyword.toLowerCase());
    }

    private Map.Entry<String, String> extractLogMessage(final String line) {
        var timestampMatcher = TIMESTAMP_PATTERN.matcher(line);
        final var matcherShortMessage = SHORT_MESSAGE_PATTERN.matcher(line);
        if (!(timestampMatcher.find() && matcherShortMessage.find())) {
            return Map.entry("", "");
        }
        return Map.entry(
                timestampMatcher.group(1),
                matcherShortMessage.group(1).replace("\\", "")
        );
    }

    private static Comparator<Map.Entry<String, String>> byTaskNameAndTimestamp() {
        return byTaskName().thenComparing(byTimestamp());
    }

    private static Comparator<Map.Entry<String, String>> byTaskName() {
        return (a, b) -> {
            final var matcherA = TASK_PATTERN.matcher(a.getValue());
            final var matcherB = TASK_PATTERN.matcher(b.getValue());
            boolean ignored = matcherA.find();
            boolean ignored2 = matcherB.find();
            return matcherA.group(1).compareTo(matcherB.group(1));
        };
    }

    private static Comparator<Map.Entry<String, String>> byTimestamp() {
        return Comparator.comparing(a -> ZonedDateTime.parse(a.getKey()));
    }

    private LogOutput parseLogLine(final Map.Entry<String, String> line) {
        final var matcher = LOG_LINE_PATTERN.matcher(line.getValue());
        if (!matcher.find()) {
            return LogOutput.builder().build();
        }
        return LogOutput.builder()
                .timestamp(line.getKey())
                .task(matcher.group(1).strip())
                .customer(matcher.group(2).strip().toUpperCase())
                .amxPatientId(Objects.requireNonNullElse(matcher.group(3), "N/A").strip())
                .relyHealthPatientId(Objects.requireNonNullElse(matcher.group(4), "N/A").strip())
                .surveyId(Objects.requireNonNullElse(matcher.group(5), "N/A").strip())
                .statusId(Objects.requireNonNullElse(matcher.group(6), "N/A").strip())
                .encounterId(Objects.requireNonNullElse(matcher.group(7), "N/A").strip())
                .error(Objects.requireNonNullElse(matcher.group(8), matcher.group(9)).strip())
                .build();
    }

    private static void printResults(final CommandOutput commandOutput) {
        commandOutput.logLines().forEach(System.out::println);
        final var totalErrors = CommandLine.Help.Ansi.AUTO.string(
                "@|blue Errors parsed: '" + commandOutput.totalErrors() + "' |@"
        );
        System.out.println(totalErrors);
    }
}


@Introspected
@Serdeable.Deserializable
record LogLine(@JsonProperty("short-message") String shortMessage) {
}


@Builder
record LogOutput(
        String task,
        String timestamp,
        String customer,
        String amxPatientId,
        String relyHealthPatientId,
        String surveyId,
        String statusId,
        String encounterId,
        String error) {
    @Override
    public String toString() {
        return """
                Task: '%s'
                Timestamp: '%s'
                Customer: '%s'
                AMX PatientId: '%s'
                Rely Health PatientId: '%s'
                SurveyId: '%s'
                StatusId: '%s'
                EncounterId: '%s'
                Error: '%s'
                """
                .formatted(
                        task,
                        timestamp,
                        customer,
                        amxPatientId,
                        relyHealthPatientId,
                        surveyId,
                        statusId,
                        encounterId,
                        error == null ? "Couldn't parse error" :
                                (error.toLowerCase().contains("invalid email") ? "Invalid email" : error)
                );
    }
}


record CommandOutput(List<LogOutput> logLines, long totalErrors) {
}