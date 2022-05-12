package mil.nga.mgrs.utm;

import mil.nga.mgrs.MGRSConstants;

/**
 * Hemisphere enumeration
 * 
 * @author wnewman
 * @author osbornb
 */
public enum Hemisphere {

	/**
	 * Northern hemisphere
	 */
	NORTH,

	/**
	 * Southern hemisphere
	 */
	SOUTH;

	/**
	 * Get the hemisphere of the band letter
	 * 
	 * @param letter
	 *            band letter
	 * @return hemisphere
	 */
	public static Hemisphere fromBandLetter(char letter) {
		return letter < MGRSConstants.BAND_LETTER_NORTH ? Hemisphere.SOUTH
				: Hemisphere.NORTH;
	}

}