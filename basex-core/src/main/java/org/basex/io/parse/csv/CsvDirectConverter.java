package org.basex.io.parse.csv;

import org.basex.build.csv.*;
import org.basex.query.expr.constr.*;
import org.basex.query.value.node.*;
import org.basex.util.*;

/**
 * This class converts CSV data to XML, using direct or attributes conversion.
 *
 * @author BaseX Team 2005-23, BSD License
 * @author Christian Gruen
 */
final class CsvDirectConverter extends CsvConverter {
  /** Document root. */
  private FDoc doc;
  /** Root node. */
  private FBuilder root;
  /** Record builder. */
  private FBuilder record;

  /**
   * Constructor.
   * @param opts CSV options
   */
  CsvDirectConverter(final CsvParserOptions opts) {
    super(opts);
  }

  @Override
  protected void record() {
    if(record != null) root.add(record);
    record = new FBuilder(new FElem(RECORD));
    col = 0;
  }

  @Override
  protected void header(final byte[] value) {
    headers.add(ats ? value : XMLToken.encode(value, lax));
  }

  @Override
  protected void entry(final byte[] entry) {
    final byte[] name = headers.get(col++);
    final FBuilder elem;
    if(ats) {
      elem = new FBuilder(new FElem(ENTRY));
      if(name != null) elem.add(NAME, name);
    } else {
      elem = new FBuilder(new FElem(name != null ? name : ENTRY));
    }
    record.add(elem.add(entry));
  }

  @Override
  protected void init(final String uri) {
    doc = new FDoc(uri);
    root = new FBuilder(new FElem(CSV));
  }

  @Override
  protected FNode finish() {
    if(record != null) root.add(record);
    return new FBuilder(doc).add(root).finish();
  }
}
