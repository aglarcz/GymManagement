package us.hqgaming.gymmanagement.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    String name;
    CommandType commandType;

    public SubCommand(String name, CommandType commandType) {

        this.name = name;
        this.commandType = commandType;

    }

    public abstract void runCommand(CommandSender sender, String[] args)
                        throws Exception;

    public String getName() {
        return name;
    }

    public CommandType getCommandType() {
        return commandType;
    }


}
