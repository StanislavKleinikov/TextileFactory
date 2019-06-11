package com.goolge.kleinikov.stanislav.textilefactory.domain

interface Producer<T, R> {
    fun produce(material: T): R
}