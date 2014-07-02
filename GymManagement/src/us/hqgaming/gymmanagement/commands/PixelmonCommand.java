package us.hqgaming.gymmanagement.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bukkit.command.CommandSender;

public abstract class PixelmonCommand {

    String name;
    CommandType commandType;

    public PixelmonCommand(String name, CommandType commandType) {

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface PluginRequired {}
}
