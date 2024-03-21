package org.example.parser.sintactic

import token.Token


interface SintacticChecker {

    fun checkSyntax(tokenList: List<Token>): Boolean
}