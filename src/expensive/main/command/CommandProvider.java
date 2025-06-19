package expensive.main.command;

public interface CommandProvider {
    Command command(String alias);
}
