package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.expr.CmpG.*;
import org.basex.query.expr.gflwor.*;
import org.basex.query.func.*;
import org.basex.query.iter.*;
import org.basex.query.value.item.*;
import org.basex.query.value.type.*;
import org.basex.query.var.*;
import org.basex.util.*;
import org.basex.util.hash.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public class FnEvery extends StandardFunc {
  @Override
  public final Bln item(final QueryContext qc, final InputInfo ii) throws QueryException {
    // implementation for dynamic function lookup
    final Iter input = arg(0).iter(qc);
    final FItem predicate = toFunctionOrNull(arg(1), 2, qc);

    int p = 0;
    final boolean some = some();
    for(Item item; (item = input.next()) != null;) {
      final boolean test = predicate == null ? toBoolean(item) :
        toBoolean(qc, predicate, item, Int.get(++p));
      if(test == some) return Bln.get(some);
    }
    return Bln.get(!some);
  }

  @Override
  protected final Expr opt(final CompileContext cc) throws QueryException {
    final Expr input = arg(0), predicate = arg(1);
    final SeqType st = input.seqType();
    final boolean some = some();
    if(st.zero()) return cc.voidAndReturn(input, Bln.get(!some), info);

    Expr result = null;
    if(defined(1)) {
      final int arity = arity(predicate);
      if(arity == 1 || arity == 2) {
        final IntObjMap<Var> vm = new IntObjMap<>();
        final Var i = cc.copy(new Var(new QNm("item"), null, cc.qc, info), vm);
        final Expr item = new VarRef(info, i).optimize(cc);
        final For fr;
        Expr pos = null;
        if(arity == 1) {
          // some : (for $i in INPUT return PREDICATE($i)) = true()
          // every:  not((for $i in INPUT return PREDICATE($i)) = false())
          fr = new For(i, input).optimize(cc);
        } else {
          // some : (for $i at $p in INPUT return PREDICATE($i, $p)) = true()
          // every:  not((for $i at $p in INPUT return PREDICATE($i, $p)) = false())
          final Var p = cc.copy(new Var(new QNm("pos"), null, cc.qc, info), vm);
          fr = new For(i, p, null, input, false).optimize(cc);
          pos = new VarRef(info, p).optimize(cc);
        }
        final Expr[] args = arity == 1 ? new Expr[] { item } : new Expr[] { item, pos };
        final Expr rtrn = new DynFuncCall(info, sc, coerce(1, cc, arity), args).optimize(cc);
        result = new GFLWOR(info, fr, rtrn).optimize(cc);
      }
    } else {
      // some : INPUT = true()
      // every: not(INPUT = false())
      result = input;
    }
    if(result != null) {
      final Expr cmp = new CmpG(info, result, Bln.get(some), OpG.EQ, null, sc).optimize(cc);
      return some ? cmp : cc.function(Function.NOT, info, cmp);
    }
    return this;
  }

  /**
   * Compare some/all results.
   * @return flag
   */
  boolean some() {
    return false;
  }

  @Override
  public int hofIndex() {
    return 1;
  }
}
