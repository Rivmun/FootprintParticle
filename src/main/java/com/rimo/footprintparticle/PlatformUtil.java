package com.rimo.footprintparticle;

import java.nio.file.Path;

/**
 * Cross-loader capability abstraction.<br>
 * Each {@code loaders.<platform>.Platform} assigns its implementation to {@link #PLATFORM} in a static
 *  initializer; common code only touches loader differences through this interface.<br>
 *
 * @see com.rimo.footprintparticle.particle.FppSpriteSet#sprites()
 */
public final class PlatformUtil {
	/** The current loader implementation, assigned by {@code loaders.<platform>.Platform}. */
	public static IPlatform PLATFORM;

	public interface IPlatform {
		boolean isModLoaded(String id);
		Path getConfigFolder();
	}
}
