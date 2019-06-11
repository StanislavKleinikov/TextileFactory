package com.goolge.kleinikov.stanislav.textilefactory.domain

interface Controller<T> {
    fun control(material: T)
}