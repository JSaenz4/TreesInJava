
/**
 * this class Cons implements a Lisp-like Cons cell
 *
 * @author  Gordon S. Novak Jr.
 * @version 29 Nov 01; 25 Aug 08; 05 Sep 08; 08 Sep 08; 12 Sep 08; 24 Sep 08
 *          06 Oct 08; 07 Oct 08; 09 Oct 08; 27 Mar 09; 18 Mar 11
 */

import java.util.StringTokenizer;

interface Functor { Object fn(Object x); }

interface Predicate { boolean pred(Object x); }

public class Cons
{
    // instance variables
    private Object car;
    private Cons cdr;
    private Cons(Object first, Cons rest)
       { car = first;
         cdr = rest; }
    public static Cons cons(Object first, Cons rest)
      { return new Cons(first, rest); }
    public static boolean consp (Object x)
       { return ( (x != null) && (x instanceof Cons) ); }
// safe car, returns null if lst is null
    public static Object first(Cons lst) {
        return ( (lst == null) ? null : lst.car  ); }
// safe cdr, returns null if lst is null
    public static Cons rest(Cons lst) {
      return ( (lst == null) ? null : lst.cdr  ); }
    public static Object second (Cons x) { return first(rest(x)); }
    public static Object third (Cons x) { return first(rest(rest(x))); }
    public static void setfirst (Cons x, Object i) { x.car = i; }
    public static void setrest  (Cons x, Cons y) { x.cdr = y; }
   public static Cons list(Object ... elements) {
       Cons list = null;
       for (int i = elements.length-1; i >= 0; i--) {
           list = cons(elements[i], list);
       }
       return list;
   }
    // access functions for expression representation
    public static Object op  (Cons x) { return first(x); }
    public static Object lhs (Cons x) { return first(rest(x)); }
    public static Object rhs (Cons x) { return first(rest(rest(x))); }
    public static boolean numberp (Object x)
       { return ( (x != null) &&
                  (x instanceof Integer || x instanceof Double) ); }
    public static boolean integerp (Object x)
       { return ( (x != null) && (x instanceof Integer ) ); }
    public static boolean floatp (Object x)
       { return ( (x != null) && (x instanceof Double ) ); }
    public static boolean stringp (Object x)
       { return ( (x != null) && (x instanceof String ) ); }

    // convert a list to a string for printing
    public String toString() {
       return ( "(" + toStringb(this) ); }
    public static String toString(Cons lst) {
       return ( "(" + toStringb(lst) ); }
    private static String toStringb(Cons lst) {
       return ( (lst == null) ?  ")"
                : ( first(lst) == null ? "()" : first(lst).toString() )
                  + ((rest(lst) == null) ? ")"
                     : " " + toStringb(rest(lst)) ) ); }

// member returns null if requested item not found
public static Cons member (Object item, Cons lst) {
  if ( lst == null )
     return null;
   else if ( item.equals(first(lst)) )
           return lst;
         else return member(item, rest(lst)); }

public static Cons union (Cons x, Cons y) {
  if ( x == null ) return y;
  if ( member(first(x), y) != null )
       return union(rest(x), y);
  else return cons(first(x), union(rest(x), y)); }

    // combine two lists: (append '(a b) '(c d e))  =  (a b c d e)
public static Cons append (Cons x, Cons y) {
  if (x == null)
     return y;
   else return cons(first(x),
                    append(rest(x), y)); }

    // look up key in an association list
    // (assoc 'two '((one 1) (two 2) (three 3)))  =  (two 2)
public static Cons assoc(Object key, Cons lst) {
  if ( lst == null )
     return null;
  else if ( key.equals(first((Cons) first(lst))) )
      return ((Cons) first(lst));
          else return assoc(key, rest(lst)); }

    public static int square(int x) { return x*x; }
    public static int pow (int x, int n) {        // x to the power n
        if ( n <= 0 ) return 1;
        if ( (n & 1) == 0 )
            return square( pow(x, n / 2) );
        else return x * pow(x, n - 1); }

public static Object reader(String str) {
    return readerb(new StringTokenizer(str, " \t\n\r\f()'", true)); }

public static Object readerb( StringTokenizer st ) {
    if ( st.hasMoreTokens() ) {
        String nexttok = st.nextToken();
        if ( nexttok.charAt(0) == ' ' ||
             nexttok.charAt(0) == '\t' ||
             nexttok.charAt(0) == '\n' ||
             nexttok.charAt(0) == '\r' ||
             nexttok.charAt(0) == '\f' )
            return readerb(st);
        if ( nexttok.charAt(0) == '(' )
            return readerlist(st);
        if ( nexttok.charAt(0) == '\'' )
            return list("QUOTE", readerb(st));
        return readtoken(nexttok); }
    return null; }

    public static Object readtoken( String tok ) {
        if ( (tok.charAt(0) >= '0' && tok.charAt(0) <= '9') ||
             ((tok.length() > 1) &&
              (tok.charAt(0) == '+' || tok.charAt(0) == '-' ||
               tok.charAt(0) == '.') &&
              (tok.charAt(1) >= '0' && tok.charAt(1) <= '9') ) ||
             ((tok.length() > 2) &&
              (tok.charAt(0) == '+' || tok.charAt(0) == '-') &&
              (tok.charAt(1) == '.') &&
              (tok.charAt(2) >= '0' && tok.charAt(2) <= '9') )  ) {
            boolean dot = false;
            for ( int i = 0; i < tok.length(); i++ )
                if ( tok.charAt(i) == '.' ) dot = true;
            if ( dot )
                return Double.parseDouble(tok);
            else return Integer.parseInt(tok); }
        return tok; }

public static Cons readerlist( StringTokenizer st ) {
    if ( st.hasMoreTokens() ) {
        String nexttok = st.nextToken();
        if ( nexttok.charAt(0) == ' ' ||
             nexttok.charAt(0) == '\t' ||
             nexttok.charAt(0) == '\n' ||
             nexttok.charAt(0) == '\r' ||
             nexttok.charAt(0) == '\f' )
            return readerlist(st);
        if ( nexttok.charAt(0) == ')' )
            return null;
        if ( nexttok.charAt(0) == '(' ) {
            Cons temp = readerlist(st);
            return cons(temp, readerlist(st)); }
        if ( nexttok.charAt(0) == '\'' ) {
            Cons temp = list("QUOTE", readerb(st));
            return cons(temp, readerlist(st)); }
        return cons( readtoken(nexttok),
                     readerlist(st) ); }
    return null; }

    // read a list of strings, producing a list of results.
public static Cons readlist( Cons lst ) {
    if ( lst == null )
        return null;
    return cons( reader( (String) first(lst) ),
                 readlist( rest(lst) ) ); }

    // You can use these association lists if you wish.
    public static Cons engwords = list(list("+", "sum"),
                                       list("-", "difference"),
                                       list("*", "product"),
                                       list("/", "quotient"),
                                       list("expt", "power"));

    public static Cons opprec = list(list("=", new Integer(1)),
                                     list("+", new Integer(5)),
                                     list("-", new Integer(5)),
                                     list("*", new Integer(6)),
                                     list("/", new Integer(6)) );


    // ****** your code starts here ******

public static Integer maxbt (Object tree) {
	if(consp(tree))
		return Math.max(maxbt(first((Cons)tree)), maxbt(rest((Cons)tree)));
	else if(numberp(tree))
		return (int) (Integer) tree;
	else return Integer.MIN_VALUE;
}

public static Cons vars (Object expr) {
	return vars2(expr, null);
}

public static Cons vars2(Object expr, Cons result)
{
	if(consp(expr))
		return
			union(vars(first((Cons)expr)), vars(rest((Cons)expr)));
	else if(stringp(expr)&&assoc(expr, opprec)==null&&assoc(expr, engwords)==null)
		return cons((String) expr, result);
	else return null;
}

public static boolean occurs(Object value, Object tree) {
	if(consp(tree))
		return
			occurs(value, first((Cons)tree)) || occurs(value, rest((Cons)tree));
	else if(tree==value)
		return true;
	else return false;
}

public static Integer eval(Object tree)
{
    Integer var=0, val=0;

    if(consp(tree)==false)
        return (Integer)tree;
    else if(consp(lhs((Cons)tree)))
    	var=eval(lhs((Cons)tree));
    else
    	var=(Integer)lhs((Cons)tree);
    if(consp(rhs((Cons)tree)))
    	val=eval(rhs((Cons)tree));
    else if(rhs((Cons)tree) != null)
    	val=(Integer)rhs((Cons)tree);
    if(op((Cons)tree).equals("+"))
    	return var+val;
    if(op((Cons)tree).equals("-")&&val==null)
    	return var*-1;
    if(op((Cons)tree).equals("-")&&val!=null)
    	return var-val;
    if(op((Cons)tree).equals("*"))
    	return var*val;
    if(op((Cons)tree).equals("/"))
    	return var/val;
    if(op((Cons)tree).equals("expt"))
    	return pow(var,val);
    return null;
}

public static Integer eval(Object tree,Cons bindings)
{
	Integer var=0, val=0;

    if(consp(tree)==false)
    {
        if(integerp(tree))
            return (Integer)tree;
        else return evaluate(bindings,(String)tree);
    }
    if (consp(lhs((Cons)tree)))
        var=eval(lhs((Cons)tree), bindings);
    else if(integerp(lhs((Cons)tree)))
    	var=(Integer)lhs((Cons)tree);
    else
    	var=evaluate(bindings,(String)lhs((Cons)tree));
    if(consp(rhs((Cons)tree)))
    	val=eval(rhs((Cons)tree),bindings);
    else if(rhs((Cons)tree)!=null&&integerp(rhs((Cons)tree)))
    	val=(Integer)rhs((Cons)tree);
    else if(rhs((Cons)tree)!=null)
    	val=evaluate(bindings,(String)rhs((Cons)tree));
    if(op((Cons)tree).equals("+"))
    	return var+val;
    if(op((Cons)tree).equals("-")&&val==null)
    	return var*-1;
    if(op((Cons)tree).equals("-")&&val!=null)
    	return var-val;
    if(op((Cons)tree).equals("*"))
    	return var*val;
    if(op((Cons)tree).equals("/"))
    	return var/val;
    if(op((Cons)tree).equals("expt"))
    	return pow(var,val);
    return null;
}

public static Integer evaluate(Cons bindings, String tree)
{
    if (((String)first((Cons)first(bindings))).equals(tree))
        return (Integer)first((Cons)rest((Cons)first(bindings)));
    return evaluate(rest(bindings), tree);
}

public static Cons english(Object tree)
{
	Cons left=null,right=null;

    if(!consp(tree))
        return list(tree);
    else if(consp(lhs((Cons)tree)))
    	left=english(lhs((Cons)tree));
    else
    	left=list(lhs((Cons)tree));
    if(consp(rhs((Cons)tree)))
    	right=english(rhs((Cons)tree));
    else if(rhs((Cons)tree)!=null)
    	right=list(rhs((Cons)tree));
    if(op((Cons)tree).equals("+"))
    	return list("the sum of ", left, " and ", right);
    if(op((Cons)tree).equals("-") && right == null)
    	return list("negative ", left);
    if(op((Cons)tree).equals("-") && right != null)
    	return list("the difference of ", left, " and ", right);
    if(op((Cons)tree).equals("*"))
    	return list("the product of ",  left, " and ", right);
    if(op((Cons)tree).equals("/"))
    	return list("the quotient of ", left, " and ", right);
    if(op((Cons)tree).equals("expt"))
    	return list(left, " raised to the power of ", right);
    return null;
}



public static String tojava (Object tree) {
   return (tojavab(tree, 0) + ";"); }

public static String tojavab (Object tree, int prec)
{
	if(consp(tree))
	{
		if(op((Cons)tree).equals("="))
			return lhs((Cons)tree)+" "+op((Cons)tree)+" "+tojavab(rhs((Cons)tree), 1);
		else if(op((Cons)tree).equals("+"))
		{
			if(prec>5)
				return "("+(String)tojavab(lhs((Cons)tree), 6)+op((Cons)tree)+tojavab(rhs((Cons)tree), 6)+")";
			else
				return tojavab(lhs((Cons)tree), 6)+" "+op((Cons)tree)+tojavab(rhs((Cons)tree), 6);
		}
		else if((op((Cons)tree).equals("*")))
			return tojavab(lhs((Cons)tree), 6)+op((Cons)tree)+tojavab(rhs((Cons)tree), 6);
		else if(stringp(op((Cons)tree)))
			return "Math."+op((Cons)tree)+"(theta)";
	}

	else if(stringp(tree))
		return ""+tree;

	return null;
}

    // ****** your code ends here ******

    public static void main( String[] args ) {
        Cons bt1 = (Cons) reader("(((23 77) -3 88) (99 7) 15 -1)");
        System.out.println("bt1 = " + bt1.toString());
        System.out.println("maxbt(bt1) = " + maxbt(bt1));

        Cons expr1 = list("=", "f", list("*", "m", "a"));
        System.out.println("expr1 = " + expr1.toString());
        System.out.println("vars(expr1) = " + vars(expr1).toString());

        Cons expr2 = list("=", "f", list("/", list("*", "m",
                                                   list("expt", "v",
                                                        new Integer(2))),
                                         "r"));
        System.out.println("expr2 = " + expr2.toString());
        System.out.println("vars(expr2) = " + vars(expr2).toString());
        System.out.println("occurs(m, expr2) = " + occurs("m", expr2));
        System.out.println("occurs(7, expr2) = " + occurs(new Integer(7), expr2));

        Cons expr3 = list("+", new Integer(3), list("*", new Integer(5),
                                                         new Integer(7)));
        System.out.println("expr3 = " + expr3.toString());
        System.out.println("eval(expr3) = " + eval(expr3));

        Cons expr4 = list("+", list("-", new Integer(3)),
                               list("expt", list("-", new Integer(7),
                                                      list("/", new Integer(4),
                                                                new Integer(2))),
                                            new Integer(3)));
        System.out.println("expr4 = " + expr4.toString());
        System.out.println("eval(expr4) = " + eval(expr4));

        System.out.println("eval(b) = " + eval("b", list(list("b", 7))));

        Cons expr5 = list("+", new Integer(3), list("*", new Integer(5), "b"));
        System.out.println("expr5 = " + expr5.toString());
        System.out.println("eval(expr5) = " + eval(expr5, list(list("b", 7))));

        Cons expr6 = list("+", list("-", "c"),
                          list("expt", list("-", "b", list("/", "z", "w")),
                                            new Integer(3)));
        Cons alist = list(list("c", 3), list("b", 7), list("z", 4),
                          list("w", 2), list("fred", 5));
        System.out.println("expr6 = " + expr6.toString());
        System.out.println("alist = " + alist.toString());
        System.out.println("eval(expr6) = " + eval(expr6, alist));
        System.out.println("english(expr5) = " + english(expr5).toString());
        System.out.println("english(expr6) = " + english(expr6).toString());
        System.out.println("tojava(expr1) = " + tojava(expr1).toString());
        Cons expr7 = list("=", "x", list("*", list("+", "a", "b"), "c"));
        System.out.println("expr7 = " + expr7.toString());
        System.out.println("tojava(expr7) = " + tojava(expr7).toString());
        Cons expr8 = list("=", "x", list("*", "r", list("sin", "theta")));
        System.out.println("expr8 = " + expr8.toString());
        System.out.println("tojava(expr8) = " + tojava(expr8).toString());


       Cons set3 = list("d", "b", "c", "a");

      }

}