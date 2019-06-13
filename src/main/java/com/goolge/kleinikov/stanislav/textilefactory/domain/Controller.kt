package com.goolge.kleinikov.stanislav.textilefactory.domain

interface Controller<in T, out R> {

    fun control(material: T): R
}