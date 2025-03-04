package org.basex.query.func.unit;

import static org.basex.query.QueryError.*;

import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.util.*;
import org.basex.query.value.item.*;
import org.basex.query.value.seq.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public final class UnitAssertEquals extends UnitFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final Iter iter1 = arg(0).iter(qc), iter2 = arg(1).iter(qc);
    final DeepEqual deep = new DeepEqual(info, null, qc);
    Item item1, item2;
    int c = 1;
    while(true) {
      item1 = qc.next(iter1);
      item2 = iter2.next();
      final boolean empty1 = item1 == null, empty2 = item2 == null;
      if(empty1 && empty2) return Empty.VALUE;
      if(empty1 || empty2 || !deep.equal(item1, item2)) break;
      c++;
    }
    final Item item = defined(2) ? toNodeOrAtomItem(arg(2), qc) : null;
    throw new UnitException(info, UNIT_FAIL_X_X_X, item1, item2, c).value(item);
  }
}
