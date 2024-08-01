package ru.romanow.dictionary.domain

interface Identity<ID> {
    fun getId(): ID?
}
