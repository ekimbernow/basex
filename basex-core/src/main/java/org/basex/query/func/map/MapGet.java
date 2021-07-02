package org.basex.query.func.map;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.map.*;
import org.basex.query.value.seq.*;
import org.basex.query.value.type.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-21, BSD License
 * @author Leo Woerteler
 */
public final class MapGet extends StandardFunc {
  @Override
  public Value value(final QueryContext qc) throws QueryException {
    final XQMap map = toMap(exprs[0], qc);
    final Item key = toAtomItem(exprs[1], qc);

    return map.get(key, info);
  }

  @Override
  protected Expr opt(final CompileContext cc) {
    final Expr expr1 = exprs[0];
    if(expr1 == XQMap.empty()) return Empty.VALUE;

    // lookup may result in empty sequence
    final Type type = expr1.seqType().type;
    if(type instanceof MapType) exprType.assign(((MapType) type).declType.occ.union(Occ.ZERO));

    return this;
  }
}
