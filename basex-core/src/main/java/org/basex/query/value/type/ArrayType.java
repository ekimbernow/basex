package org.basex.query.value.type;

import static org.basex.query.QueryError.*;

import java.io.*;

import org.basex.core.*;
import org.basex.io.in.DataInput;
import org.basex.query.*;
import org.basex.query.value.array.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Type for arrays.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public final class ArrayType extends FuncType {
  /** Name. */
  public static final byte[] ARRAY = Token.token(QueryText.ARRAY);

  /**
   * Constructor.
   * @param declType declared return type
   */
  ArrayType(final SeqType declType) {
    super(declType, SeqType.INTEGER_O);
  }

  /**
   * Creates an array type.
   * @param declType declared return type
   * @return array type
   */
  public static ArrayType get(final SeqType declType) {
    return declType.arrayType();
  }

  @Override
  public XQArray cast(final Item item, final QueryContext qc, final StaticContext sc,
      final InputInfo info) throws QueryException {

    if(item instanceof XQArray) {
      final XQArray a = (XQArray) item;
      if(a.instanceOf(this)) return a;
    }
    throw typeError(item, this, info);
  }

  @Override
  public XQArray read(final DataInput in, final QueryContext qc)
      throws IOException, QueryException {
    final ArrayBuilder ab = new ArrayBuilder();
    for(int s = in.readNum() - 1; s >= 0; s--) ab.append(Store.read(in, qc));
    return ab.array();
  }

  @Override
  public boolean eq(final Type type) {
    return this == type || type instanceof ArrayType && declType.eq(((ArrayType) type).declType);
  }

  @Override
  public boolean instanceOf(final Type type) {
    if(this == type || type.oneOf(SeqType.ARRAY, SeqType.FUNCTION, AtomType.ITEM)) return true;
    if(!(type instanceof FuncType) || type instanceof MapType) return false;

    final FuncType ft = (FuncType) type;
    return declType.instanceOf(ft.declType) && (
      type instanceof ArrayType ||
      ft.argTypes.length == 1 && ft.argTypes[0].instanceOf(SeqType.INTEGER_O)
    );
  }

  @Override
  public Type union(final Type type) {
    if(instanceOf(type)) return type;
    if(type.instanceOf(this)) return this;

    if(type instanceof ArrayType) {
      final ArrayType at = (ArrayType) type;
      return get(declType.union(at.declType));
    }
    return type instanceof MapType  ? SeqType.FUNCTION :
           type instanceof FuncType ? type.union(this) : AtomType.ITEM;
  }

  @Override
  public ArrayType intersect(final Type type) {
    if(instanceOf(type)) return this;
    if(type.instanceOf(this)) return (ArrayType) type;

    if(!(type instanceof FuncType) || type instanceof MapType) return null;

    final FuncType ft = (FuncType) type;
    final SeqType dt = declType.intersect(ft.declType);
    if(dt == null) return null;

    if(type instanceof ArrayType) return get(dt);

    return null;
  }

  @Override
  public AtomType atomic() {
    return argTypes[0].type.atomic();
  }

  @Override
  public ID id() {
    return ID.ARRAY;
  }

  @Override
  public String toString() {
    final Object[] param = this == SeqType.ARRAY ? WILDCARD : new Object[] { declType };
    return new QueryString().token(ARRAY).params(param).toString();
  }
}
