package mil.nga.mgrs.grid;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.utm.Hemisphere;
import mil.nga.mgrs.utm.UTM;

/**
 * MGRS Column and Row labeler for 100 kilometer square
 * 
 * @author osbornb
 */
public class ColumnRowLabeler extends Labeler {

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 */
	public ColumnRowLabeler(int minZoom) {
		super(minZoom);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public ColumnRowLabeler(int minZoom, Integer maxZoom) {
		super(minZoom, maxZoom);
	}

	/**
	 * Constructor
	 * 
	 * @param enabled
	 *            enabled labeler
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 */
	public ColumnRowLabeler(boolean enabled, int minZoom, Integer maxZoom) {
		super(enabled, minZoom, maxZoom);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Label> getLabels(Bounds tileBounds, int precision,
			GridZone zone) {

		List<Label> labels = new ArrayList<>();

		int zoneNumber = zone.getNumber();
		Hemisphere hemisphere = zone.getHemisphere();

		tileBounds = tileBounds.toDegrees();

		tileBounds = tileBounds.overlap(zone.getBounds());

		UTM lowerLeftUTM = UTM.from(tileBounds.getSouthwest(), zoneNumber,
				hemisphere);
		double lowerEasting = (Math.floor(lowerLeftUTM.getEasting() / precision)
				* precision) - precision;
		double lowerNorthing = (Math
				.ceil(lowerLeftUTM.getNorthing() / precision) * precision);

		UTM upperRightUTM = UTM.from(tileBounds.getNortheast(), zoneNumber,
				hemisphere);
		double upperEasting = (Math
				.floor(upperRightUTM.getEasting() / precision) * precision)
				+ precision;
		double upperNorthing = (Math
				.ceil(upperRightUTM.getNorthing() / precision) * precision)
				+ precision;

		double northing = lowerNorthing;
		while (northing < upperNorthing) {
			double easting = lowerEasting;
			double newNorthing = northing + precision;
			while (easting < upperEasting) {
				double newEasting = easting + precision;

				// Draw cell name
				labels.add(getLabel(precision, zone, easting, northing));

				easting = newEasting;
			}

			northing = newNorthing;
		}

		return labels;
	}

	/**
	 * Get the grid zone label
	 * 
	 * @param precision
	 *            precision in meters
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return labels
	 */
	private Label getLabel(int precision, GridZone zone, double easting,
			double northing) {

		Bounds bounds = zone.getBounds();
		int zoneNumber = zone.getNumber();
		char bandLetter = zone.getLetter();
		Hemisphere hemisphere = zone.getHemisphere();

		UTM lowerLeftUTM = UTM.from(bounds.getSouthwest(), zoneNumber,
				hemisphere);
		UTM upperRightUTM = UTM.from(bounds.getNortheast(), zoneNumber,
				hemisphere);

		double newNorthing = northing - precision;
		double centerNorthing = northing - (precision / 2);

		double newEasting = easting + precision;
		double centerEasting = easting + (precision / 2);

		if (newNorthing < lowerLeftUTM.getNorthing()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					centerEasting, lowerLeftUTM.getNorthing()));
			UTM utm = UTM.from(Point.degrees(currentLatLng.getLongitude(),
					bounds.getSouth()), zoneNumber, hemisphere);
			centerNorthing = ((northing - lowerLeftUTM.getNorthing()) / 2)
					+ lowerLeftUTM.getNorthing();
			newNorthing = utm.getNorthing();
		} else if (northing > upperRightUTM.getNorthing()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					centerEasting, upperRightUTM.getNorthing()));
			UTM utm = UTM.from(Point.degrees(currentLatLng.getLongitude(),
					bounds.getNorth()), zoneNumber, hemisphere);
			centerNorthing = ((upperRightUTM.getNorthing() - newNorthing) / 2)
					+ newNorthing;
			northing = utm.getNorthing();
		}

		if (easting < lowerLeftUTM.getEasting()) {
			Point currentLatLng = Point.from(new UTM(zoneNumber, hemisphere,
					newEasting, centerNorthing));
			UTM utm = UTM.from(
					Point.degrees(bounds.getWest(),
							currentLatLng.getLatitude()),
					zoneNumber, hemisphere);
			centerEasting = utm.getEasting()
					+ ((newEasting - utm.getEasting()) / 2);
			easting = utm.getEasting();
		} else if (newEasting > upperRightUTM.getEasting()) {
			Point currentLatLng = Point.from(
					new UTM(zoneNumber, hemisphere, easting, centerNorthing));
			UTM utm = UTM.from(
					Point.degrees(bounds.getEast(),
							currentLatLng.getLatitude()),
					zoneNumber, hemisphere);
			centerEasting = easting + ((utm.getEasting() - easting) / 2);
			newEasting = utm.getEasting();
		}

		String id = MGRS.get100KId(centerEasting, centerNorthing, zoneNumber);
		Point center = Point.from(
				new UTM(zoneNumber, hemisphere, centerEasting, centerNorthing));

		Point l1 = Point
				.from(new UTM(zoneNumber, hemisphere, easting, newNorthing));
		Point l2 = Point
				.from(new UTM(zoneNumber, hemisphere, easting, northing));
		Point l3 = Point
				.from(new UTM(zoneNumber, hemisphere, newEasting, northing));
		Point l4 = Point
				.from(new UTM(zoneNumber, hemisphere, newEasting, newNorthing));

		double minLatitude = Math.max(l1.getLatitude(), l4.getLatitude());
		double maxLatitude = Math.min(l2.getLatitude(), l3.getLatitude());

		double minLongitude = Math.max(l1.getLongitude(), l2.getLongitude());
		double maxLongitude = Math.min(l3.getLongitude(), l4.getLongitude());

		Bounds labelBounds = Bounds.degrees(minLongitude, minLatitude,
				maxLongitude, maxLatitude);

		return new Label(id, center, labelBounds, zoneNumber, bandLetter);
	}

}
