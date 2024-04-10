package parser.parse

import ast.Node
import token.Token

interface Parse {
    fun parse(tokenList: List<Token>): Node
}
