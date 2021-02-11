package org.basex.query.func.jobs;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-21, BSD License
 * @author Christian Gruen
 */
public final class JobsInvoke extends JobsEval {
  @Override
  public Str item(final QueryContext qc, final InputInfo ii) throws QueryException {
    return eval(toQuery(toToken(exprs[0], qc), qc), qc);
  }
}
