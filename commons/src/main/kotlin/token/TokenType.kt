package token

enum class TokenType {
    // Single-character tokens.
    ASSIGNATION, // =
    LEFT_PAREN,
    RIGHT_PAREN,
    PLUS, // +
    MINUS, // -
    STAR, // *
    SLASH, // /
    LEFT_BRACE, // {
    RIGHT_BRACE, // }

    // Separators
    SEMICOLON, // ;
    COLON, // :

    // Keywords
    PRINT, // print
    IF, // if
    ELSE, // else
    NUMBER_TYPE, // Number
    STRING_TYPE, // String
    BOOLEAN_TYPE, // Boolean
    LET, // VARIABLE_KEYWORD name: String
    CONST, // const
    READ_INPUT, // readInput
    READ_ENV, // readEnv

    // Literals
    STRING_LITERAL, // "hola"
    NUMBER_LITERAL, // 123
    BOOLEAN_LITERAL, // true or false
    VALUE_IDENTIFIER_LITERAL, // let VALUE_IDENTIFIER: STRING
}
