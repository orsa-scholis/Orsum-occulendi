package client.application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import client.stones.StoneColors;

public class UserDefaults implements Serializable {
	private static final long serialVersionUID = -1762048258028508433L;
	private static UserDefaults sharedDefaults = null;
	private HashMap<String, Object> store = new HashMap<>();
	
	public static final String PORT_KEY = "port";
	public static final String ADRESS_KEY = "adress";
	public static final String NAME_KEY = "name";
	public static final String STONE_COLORS_KEY = "colors";
	
	public static UserDefaults sharedDefaults() {
		if (sharedDefaults == null) {
			sharedDefaults = new UserDefaults();
		}
		
		return sharedDefaults;
	}
	
	public UserDefaults() {
		loadDefaults();
		
		if (store.size() <= 0) {
			setupDefaults();
		}
	}
	
	private void loadDefaults() {
		FileInputStream in;
		ObjectInputStream is = null;
		try {
			in = new FileInputStream("save/save.txt");
			is = new ObjectInputStream(in);
			Object loaded = is.readObject();
			
			if (loaded != null) {
				System.out.println("loaded");
				if (loaded instanceof HashMap) {
					System.out.println(loaded);
					
					@SuppressWarnings("unchecked")
					HashMap<String, Object> defaults = (HashMap<String, Object>)loaded;
					
					this.store = defaults;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveDefaults() {
		FileOutputStream stream;
		try {
			stream = new FileOutputStream("save/save.txt");
			ObjectOutput objectOutput = new ObjectOutputStream(stream);
			objectOutput.writeObject(this.store);
			objectOutput.flush();
			objectOutput.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setupDefaults() {
		setKey(PORT_KEY, 4560);
		setKey(ADRESS_KEY, "127.0.0.1");
		setKey(NAME_KEY, "Philipp's Client");
		setKey(STONE_COLORS_KEY, StoneColors.defaultColors());
	}
	
	public void setKey(String key, Object value) {
		store.put(key, value);
	}
	
	public Object valueForKey(String key) {
		return store.get(key);
	}
	
	public Object valueForKey(String key, Object defaultObj) {
		if (store.containsKey(key)) {
			return store.get(key);
		} else {
			return defaultObj;
		}
	}
}
