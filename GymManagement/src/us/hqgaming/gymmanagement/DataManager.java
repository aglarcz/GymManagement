package us.hqgaming.gymmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class DataManager {

	@SuppressWarnings("unused")
	private Plugin p;
	private File dfile;
	private File dataFolder;
	static DataManager instance = new DataManager();

	public static DataManager getInstance() {
		return instance;
	}

	public void setupData(Plugin p) {

		this.p = p;

		dataFolder = new File(p.getDataFolder(), "data");

		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		if (dfile == null) {
			dfile = new File(dataFolder.getPath(), "badges.data");
		}

		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
				GymManagement.newFile = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (isDataTransfered(p)) {
			Bukkit.getServer()
					.getLogger()
					.severe(ChatColor.RED
							+ "Old data has been transfered to new data file!");
		}

	}

	private boolean isDataTransfered(Plugin p) {
		File oldFile = new File(p.getServer().getWorldContainer()
				.getAbsolutePath()
				+ "/plugins/PixelmonGym/data/badges.dll");

		if (oldFile.exists()) {
			InputStream inputStream = null;
			OutputStream outputStream = null;
			try {
				// read this file into InputStream
				inputStream = new FileInputStream(oldFile);

				dfile.delete();

				dfile = new File(dataFolder.getPath(), "badges.data");
				// write the inputStream to a FileOutputStream
				outputStream = new FileOutputStream(dfile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				if (oldFile.delete()) {

					return true;
				}

				return true;

			} catch (IOException e) {
				e.printStackTrace();
				Bukkit.getServer()
						.getLogger()
						.severe(ChatColor.RED + "Could not create badges.data!");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						// outputStream.flush();
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
		}
		return false;
	}

	public File getData() {
		return dfile;
	}

	public void flushData(Object o) {
		try {

			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(dfile));
			oos.writeObject(o);
			oos.flush();
			oos.close();

		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save data file!");
		}
	}

	public Object loadData(File file) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					file));
			Object result = ois.readObject();
			ois.close();
			return result;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
