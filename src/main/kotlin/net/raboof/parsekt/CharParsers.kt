package net.raboof.parsekt

import kotlin.text.Regex

abstract class CharParsers<TInput>() : Parsers<TInput>() {
    // implement anyChar to read a character from a sequence
    abstract val anyChar: Parser<TInput, Char>

    public fun char(ch: Char): Parser<TInput, Char> {
        return anyChar.filter({ c -> c == ch })
    }

    public fun char(predicate: (Char) -> Boolean): Parser<TInput, Char> {
        return anyChar.filter(predicate)
    }

    public fun char(regex: Regex): Parser<TInput, Char> {
        return anyChar.filter({ ch: Char -> regex.matches(ch.toString())})
    }

//    public val whitespace: Parser<TInput, List<Char>> = repeat(char(' ') or char('\t') or char('\n') or char('\r'));
    public val whitespace = repeat(char(Regex("""\s""")))
    public val wordChar = char(Regex("""\w"""))
    public fun wsChar(ch: Char) = whitespace then char(ch)
    public val token = repeat1(wordChar).between(whitespace)

    public fun concat(p1: Parser<TInput, Char>, p2: Parser<TInput, List<Char>>): Parser<TInput, List<Char>> {
        return p1.project({v: Char, l: List<Char> -> arrayListOf(v) + l })({p2})
    }
}

fun <TInput> Parser<TInput, List<Char>>.string(): Parser<TInput, String> {
    return this.withResult { Result(String(it.value.toCharArray()), it.rest) }
}