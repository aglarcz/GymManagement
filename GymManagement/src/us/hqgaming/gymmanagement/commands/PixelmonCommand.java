package us.hqgaming.gymmanagement.commands;

import org.bukkit.command.CommandSender;

public abstract class PixelmonCommand {

	String name;
	CommandType commandType;

	public PixelmonCommand(String name, CommandType commandType) {

		this.name = name;
		this.commandType = commandType;

	}

	public abstract void runCommand(CommandSender sender, String[] args);

	public String getName() {
		return name;
	}

	public CommandType getCommandType() {
		return commandType;
	}
}
