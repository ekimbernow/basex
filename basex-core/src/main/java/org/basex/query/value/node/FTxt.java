package org.basex.query.value.node;

import java.util.function.*;

import org.basex.query.*;
import org.basex.query.value.type.*;
import org.basex.util.*;
import org.w3c.dom.*;

/**
 * Text node fragment.
 *
 * @author BaseX Team 2005-22, BSD License
 * @author Christian Gruen
 */
public final class FTxt extends FNode {
  /**
   * Constructor.
   * @param t text value
   */
  public FTxt(final String t) {
    this(Token.token(t));
  }

  /**
   * Constructor.
   * @param value text value
   */
  public FTxt(final byte[] value) {
    super(NodeType.TEXT);
    this.value = value;
  }

  /**
   * Constructor for creating a text from a DOM node.
   * Originally provided by Erdal Karaca.
   * @param txt DOM node
   */
  public FTxt(final Text txt) {
    this(txt.getData());
  }

  @Override
  public FTxt materialize(final QueryContext qc, final Predicate<ANode> test,
      final InputInfo ii) {
    return materialized(test, ii) ? this : new FTxt(value);
  }

  @Override
  public void toXml(final QueryPlan plan) {
    plan.add(plan.create(this), value);
  }

  @Override
  public void toString(final QueryString qs) {
    qs.quoted(value);
  }
}
