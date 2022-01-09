package org.dilireum.serverbrowser;

public class LanguageEntry {
	private final String		isoKey;
	private final String		name;
	
	public LanguageEntry(String key, String value) {
		isoKey = key;
		name = value;
	}

	/**
	 * Get the ISO639 value
	 * @return the isoKey
	 */
	public String getIsoKey() {
		return isoKey;
	}

	/**
	 * Get the language name
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
