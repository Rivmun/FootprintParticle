package com.rimo.footprintparticle.config;

public enum WorkMode {
	DISABLED("disabled"),
	PLAYER_ONLY("player_only"),
	ALL("all");

	private final String key;

	WorkMode(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return this.key;
	}
}
