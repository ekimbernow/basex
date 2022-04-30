package org.basex.query.func.geo;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

import com.vividsolutions.jts.geom.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-22, BSD License
 * @author Christian Gruen
 */
public final class GeoIsClosed extends GeoFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final Geometry geo = toGeometry(0, qc, LINE,
        Q_GML_LINEARRING, Q_GML_LINESTRING, Q_GML_MULTILINESTRING);
    return Bln.get(geo instanceof LineString ? ((LineString) geo).isClosed() :
      ((MultiLineString) geo).isClosed());
  }
}
