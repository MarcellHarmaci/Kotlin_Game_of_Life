package main.kotlin.com.homework

import main.kotlin.com.homework.Game

fun getResource(filename: String): String {
    return Game::class.java.getResource(filename).toString()
}
