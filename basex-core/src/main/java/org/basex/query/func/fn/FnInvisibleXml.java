package org.basex.query.func.fn;

import static org.basex.query.QueryError.*;
import static org.basex.query.value.type.SeqType.*;

import java.io.*;
import java.util.*;

import org.basex.io.*;
import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.util.list.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.query.var.*;
import org.basex.util.*;
import org.basex.util.hash.*;
import org.nineml.coffeefilter.*;
import org.nineml.coffeegrinder.parser.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-23, BSD License
 * @author Gunther Rademacher
 */
public class FnInvisibleXml extends StandardFunc {
  /** The invisible XML parser generator. */
  private Generator generator;

  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    if (generator == null) {
      for (String className : Arrays.asList(
          "org.nineml.coffeefilter.InvisibleXml",
          "org.nineml.coffeefilter.InvisibleXmlParser",
          "org.nineml.coffeefilter.InvisibleXmlDocument",
          "org.nineml.coffeegrinder.parser.GearleyResult")) {
        if (Reflect.find(className) == null) {
          throw BASEX_CLASSPATH_X_X.get(ii, definition.local(), className);
        }
      }
      generator = new Generator();
    }
    return generator.generate(qc, ii, sc, toString(exprs[0], qc));
  }

  /**
   * Invisible XML parser generator.
   */
  private static class Generator {
    /** Invisible XML processor. */
    private static InvisibleXml ixml = new InvisibleXml();

    /**
     * Generate a parser from an invisible XML grammar.
     * @param qc query context
     * @param ii input info
     * @param sc static context
     * @param grammar the invisible XML grammar
     * @return the parsing function
     * @throws QueryException query exception
     */
    public Item generate(final QueryContext qc, final InputInfo ii, final StaticContext sc,
        final String grammar) throws QueryException {
      final InvisibleXmlParser parser = ixml.getParserFromIxml(grammar);
      if (!parser.constructed()) {
        final Exception ex = parser.getException();
        if (ex != null) throw IXML_UNEXPECTED_X.get(ii, ex);
        InvisibleXmlDocument doc = parser.getFailedParse();
        GearleyResult result = doc.getResult();
        throw IXML_GEN_X_X_X.get(ii, result.getLastToken(),
            doc.getLineNumber(), doc.getColumnNumber());
      }
      final Var[] params = {new VarScope(sc).addNew(new QNm("input"), STRING_O, true, qc, ii)};
      final Expr arg = new VarRef(ii, params[0]);
      final ParseInvisibleXml parseFunction = new ParseInvisibleXml(ii, arg, parser);
      final FuncType type = FuncType.get(parseFunction.seqType(), STRING_O);
      return new FuncItem(sc, new AnnList(), null, params, type, parseFunction, params.length, ii);
    }
  }

  /**
   * Result function of fn:invisible-xml: parse invisible XML input.
   */
  private static class ParseInvisibleXml extends Arr {
    /** Generated invisible XML parser. */
    private final InvisibleXmlParser parser;

    /**
     * Constructor.
     * @param info input info
     * @param arg function argument
     * @param parser generated invisible XML parser
     */
    protected ParseInvisibleXml(final InputInfo info, final Expr arg,
        final InvisibleXmlParser parser) {
      super(info, DOCUMENT_NODE_O, arg);
      this.parser = parser;
    }

    @Override
    public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
      final String input = toString(exprs[0].atomItem(qc, ii), qc);
      final InvisibleXmlDocument doc = parser.parse(input);
      if (!doc.succeeded()) {
        GearleyResult result = doc.getResult();
        throw IXML_INP_X_X_X.get(ii, result.getLastToken(),
            doc.getLineNumber(), doc.getColumnNumber());
      }
      try {
        return new DBNode(IO.get(doc.getTree()));
      } catch(final IOException ex) {
        throw IXML_RESULT_X.get(ii, ex);
      }
    }

    @Override
    public Expr copy(final CompileContext cc, final IntObjMap<Var> vm) {
      return copyType(new ParseInvisibleXml(info, exprs[0].copy(cc, vm), parser));
    }

    @Override
    public void toString(final QueryString qs) {
      qs.token("parse-invisible-xml").params(exprs);
    }
  }
}
