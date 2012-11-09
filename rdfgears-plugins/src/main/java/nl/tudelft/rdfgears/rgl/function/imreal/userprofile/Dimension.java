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
		private String topic;
		private String value;

		public DimensionEntry(String topic, String value) {
			this.topic = topic;
			this.value = value;
		}

		public String getTopic() {
			return topic;
		}

		public String getValue() {
			return value;
		}

	}
}