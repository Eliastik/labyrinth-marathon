package game;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * The updater
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class UpdateChecker {
	private String updateSource;
	private String currentVersion;
	private String versionUpdate;
	private Date dateUpdate;
	private String changesUpdate;
	private String urlUpdate;
	
	public boolean checkUpdate() throws Exception {
		boolean isUpdate = false;
		
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/appInfo.properties"));
		this.updateSource = properties.getProperty("updateSource") + "?platform=" + URLEncoder.encode(System.getProperty("os.name").trim(), "UTF-8");
		this.currentVersion = properties.getProperty("version");
		
		URL request = new URL(updateSource);
		System.setProperty("http.agent", "Chrome");
		URLConnection connection = request.openConnection();
		connection.setDoOutput(true);
		
		JSONTokener tokener = new JSONTokener(request.openStream());
		JSONObject root = new JSONObject(tokener);
		
		if(root != null) {
			if(root.getString("version") != null) {
				this.versionUpdate = root.getString("version");
				isUpdate = this.currentVersion.compareTo(this.versionUpdate) < 0;
			}
			
			if(root.getString("date") != null) {
				this.dateUpdate = new SimpleDateFormat("dd/MM/yy").parse(root.getString("date"));
			}
			
			if(root.getString("changes") != null) {
				this.changesUpdate = root.getString("changes");
			}
			
			if(root.getString("url") != null) {
				this.urlUpdate = root.getString("url");
			}
		}
		
		return isUpdate;
	}

	public String getUpdateSource() {
		return updateSource;
	}

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public String getVersionUpdate() {
		return versionUpdate;
	}

	public String getChangesUpdate() {
		return changesUpdate;
	}

	public String getUrlUpdate() {
		return urlUpdate;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}

	public static void main(String[] args) {
		try {
			System.out.println("Update available? " + (new UpdateChecker().checkUpdate() ? "Yes" : "No"));
		} catch (Exception e) {
			System.out.println("Error :");
			e.printStackTrace();
		}
	}
}