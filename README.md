# regexp-pattern-searcher
a regexp pattern searcher using the finite state machine, deque and compiler techniques

REconpiler accepts a regexp pattern as a command-line argument and produce as standard output a description of the corresponding FSM, such that each line of output includes four things: the state-number, a string containing the input-symbol(s) this state must match (or branch-state indicator BR), and two numbers indicating the two possible next states if a match is made.

* To use REcompiler

	java REcompiler "regexp"
	
REsearcher accepts, as standard input, the output of the first program, then it must execute a search for matching patterns within the text of a file whose name is given as a command-line argument. Each line of the text file that contains a match is output to standard out just once, regardless of the number of times the pattern might be satisfied in that line.

* To use REsearcher with a given regexp

	java REcompiler "regexp" | java REsearcher \<filename>
	
a wellformed regexp for REcompiler is specified as follows:

	1	any symbol that does not have a special meaning (as given below) is a literal that matches itself	
	2	. is a wildcard symbol that matches any literal	
	3	adjacent regexps are concatenated to form a single regexp	
	4	* indicates closure (zero or more occurrences) on the preceding regexp	
	5	? indicates that the preceding regexp can occur zero or one time
	6	| is an infix alternation operator such that if r and e are regexps, then r|e is a regexp that matches one of either r or e
	7	( and ) may enclose a regexp to raise its precedence in the usual manner; such that if e is a regexp, then (e) is a regexp and is equivalent to e. e cannot be empty.
	8	[ and ] may enclose a list of literals and matches one and only one of the enclosed literals. Any special symbols in the list lose their special meaning, except ] which must appear first in the list if it is a literal. The enclosed list cannot be empty.
	9	^[ and ] may enclose a list of literals and matches one and only one literal NOT included in the enclosed literals. Any special symbols in the list lose their special meaning, except ] which must appear first in the list if it is a literal. The enclosed list cannot be empty.
	10	\ is an escape character that matches nothing but indicates the symbol immediately following the backslash loses any special meaning and is to be interpretted as a literal symbol
	11	operator precedence is as follows (from high to low):
		◦	escaped characters (i.e. symbols preceded by \)
		◦	parentheses (i.e. the most deeply nested regexps have the highest precedence)
		◦	list of alternative literals (i.e. [ or ^[ with ])
		◦	repetition/option operators (i.e. * and ?)
		◦	concatenation
		◦	alternation (i.e. |)
