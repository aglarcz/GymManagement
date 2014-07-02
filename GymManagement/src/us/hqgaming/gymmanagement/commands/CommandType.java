package us.hqgaming.gymmanagement.commands;

/**
 * @author Xeno
 */
public enum CommandType {

    BADGE, GYM;

    public static CommandType getCommandType(String commandType) {
        for (CommandType c : CommandType.values()) {
            if (c.name().equalsIgnoreCase(commandType)) {
                return c;
            }
        }
        return null;
    }
}
