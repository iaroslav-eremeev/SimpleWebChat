package dto;

import java.nio.file.Path;
import java.util.StringJoiner;

public class Event {
    private final String action;
    private final String path;

    public Event(String action, Path path) {
        this.action = action;
        this.path = path.toString();
    }

    public String getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                .add("action='" + action + "'")
                .add("path='" + path + "'")
                .toString();
    }
}