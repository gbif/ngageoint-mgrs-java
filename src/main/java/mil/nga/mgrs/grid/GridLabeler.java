package mil.nga.mgrs.grid;

import java.util.ArrayList;
import java.util.List;

import mil.nga.mgrs.MGRS;
import mil.nga.mgrs.color.Color;
import mil.nga.mgrs.features.Bounds;
import mil.nga.mgrs.features.Point;
import mil.nga.mgrs.gzd.GridZone;
import mil.nga.mgrs.utm.Hemisphere;

/**
 * MGRS grid labeler
 * 
 * @author osbornb
 */
public class GridLabeler extends Labeler {

	/**
	 * Default Constructor
	 */
	public GridLabeler() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param color
	 *            label color
	 */
	public GridLabeler(int minZoom, Color color) {
		super(minZoom, color);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 */
	public GridLabeler(int minZoom, Color color, double textSize) {
		super(minZoom, color, textSize);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 * @param buffer
	 *            grid zone edge buffer (greater than or equal to 0.0 and less
	 *            than 0.5)
	 */
	public GridLabeler(int minZoom, Color color, double textSize,
			double buffer) {
		super(minZoom, color, textSize, buffer);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 * @param color
	 *            label color
	 */
	public GridLabeler(int minZoom, Integer maxZoom, Color color) {
		super(minZoom, maxZoom, color);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 */
	public GridLabeler(int minZoom, Integer maxZoom, Color color,
			double textSize) {
		super(minZoom, maxZoom, color, textSize);
	}

	/**
	 * Constructor
	 * 
	 * @param minZoom
	 *            minimum zoom
	 * @param maxZoom
	 *            maximum zoom
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 * @param buffer
	 *            grid zone edge buffer (greater than or equal to 0.0 and less
	 *            than 0.5)
	 */
	public GridLabeler(int minZoom, Integer maxZoom, Color color,
			double textSize, double buffer) {
		super(minZoom, maxZoom, color, textSize, buffer);
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
	 * @param color
	 *            label color
	 */
	public GridLabeler(boolean enabled, int minZoom, Integer maxZoom,
			Color color) {
		super(enabled, minZoom, maxZoom, color);
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
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 */
	public GridLabeler(boolean enabled, int minZoom, Integer maxZoom,
			Color color, double textSize) {
		super(enabled, minZoom, maxZoom, color, textSize);
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
	 * @param color
	 *            label color
	 * @param textSize
	 *            label text size
	 * @param buffer
	 *            grid zone edge buffer (greater than or equal to 0.0 and less
	 *            than 0.5)
	 */
	public GridLabeler(boolean enabled, int minZoom, Integer maxZoom,
			Color color, double textSize, double buffer) {
		super(enabled, minZoom, maxZoom, color, textSize, buffer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Label> getLabels(Bounds tileBounds, GridType gridType,
			GridZone zone) {

		List<Label> labels = null;

		Bounds drawBounds = zone.getDrawBounds(tileBounds, gridType);

		if (drawBounds != null) {

			labels = new ArrayList<>();

			int precision = gridType.getPrecision();

			for (double easting = drawBounds
					.getMinLongitude(); easting <= drawBounds
							.getMaxLongitude(); easting += precision) {
				for (double northing = drawBounds
						.getMinLatitude(); northing <= drawBounds
								.getMaxLatitude(); northing += precision) {

					Label label = getLabel(gridType, zone, easting, northing);
					if (label != null) {
						labels.add(label);
					}

				}
			}

		}

		return labels;
	}

	/**
	 * Get the grid zone label
	 * 
	 * @param gridType
	 *            grid type
	 * @param easting
	 *            easting
	 * @param northing
	 *            northing
	 * @return labels
	 */
	private Label getLabel(GridType gridType, GridZone zone, double easting,
			double northing) {

		Label label = null;

		int precision = gridType.getPrecision();
		Bounds bounds = zone.getBounds();
		int zoneNumber = zone.getNumber();
		Hemisphere hemisphere = zone.getHemisphere();

		Point northwest = Point.create(zoneNumber, hemisphere, easting,
				northing + precision);
		Point southwest = Point.create(zoneNumber, hemisphere, easting,
				northing);
		Point southeast = Point.create(zoneNumber, hemisphere,
				easting + precision, northing);
		Point northeast = Point.create(zoneNumber, hemisphere,
				easting + precision, northing + precision);

		double minLatitude = Math.max(southwest.getLatitude(),
				southeast.getLatitude());
		minLatitude = Math.max(minLatitude, bounds.getMinLatitude());
		double maxLatitude = Math.min(northwest.getLatitude(),
				northeast.getLatitude());
		maxLatitude = Math.min(maxLatitude, bounds.getMaxLatitude());

		double minLongitude = Math.max(southwest.getLongitude(),
				northwest.getLongitude());
		minLongitude = Math.max(minLongitude, bounds.getMinLongitude());
		double maxLongitude = Math.min(southeast.getLongitude(),
				northeast.getLongitude());
		maxLongitude = Math.min(maxLongitude, bounds.getMaxLongitude());

		if (minLongitude <= maxLongitude && minLatitude <= maxLatitude) {

			Bounds labelBounds = Bounds.degrees(minLongitude, minLatitude,
					maxLongitude, maxLatitude);
			Point center = labelBounds.getCenter();

			MGRS mgrs = center.toMGRS();
			String id = null;
			if (gridType == GridType.HUNDRED_KILOMETER) {
				id = mgrs.getColumnRowId();
			} else {
				id = mgrs.getEastingAndNorthing(gridType);
			}

			label = new Label(id, center, labelBounds, zoneNumber,
					zone.getLetter());
		}

		return label;
	}

}