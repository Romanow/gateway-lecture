package ru.romanow.dictionary

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DictionaryApplication

fun main(args: Array<String>) {
    SpringApplication.run(DictionaryApplication::class.java, *args)
}
