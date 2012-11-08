package nl.tudelft.rdfgears.rgl.function.imreal.userprofile;

import java.util.List;

public class Dimension {
	private String name;

	private List<DimensionEntry> dimensionEntries;

	public Dimension(String name, List<DimensionEntry> dimensionEntries) {
		super();
		this.name = name;
		this.dimensionEntries = dimensionEntries;
	}

	public String getName() {
		return name;
	}

	public List<DimensionEntry> getDimensionEntries() {
		return dimensionEntries;
	}

	public static class DimensionEntry {
		private String scope;
		private String value;
		private String provider;

		public DimensionEntry(String scope, String value, String provider) {
			this.scope = scope;
			this.value = value;
			this.provider = provider;
		}

		public String getScope() {
			return scope;
		}

		public String getValue() {
			return value;
		}

		public String getProvider() {
			return provider;
		}
	}
}