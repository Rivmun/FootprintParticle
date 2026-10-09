package com.rimo.footprintparticle.config;

public enum WorkMode {
	DISABLED("text.footprintparticle.disabled"),
	PLAYER_ONLY("text.footprintparticle.player_only"),
	ALL("text.footprintparticle.all");

	private final String key;

	WorkMode(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return this.key;
	}
}
