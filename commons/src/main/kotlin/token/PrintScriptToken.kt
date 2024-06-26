package token

import position.Coordinate

class PrintScriptToken(
    override val type: TokenType,
    override val value: String,
    override val start: Coordinate,
    override val end: Coordinate,
) : Token
