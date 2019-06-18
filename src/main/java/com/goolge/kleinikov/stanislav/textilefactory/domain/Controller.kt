package com.goolge.kleinikov.stanislav.textilefactory.domain

interface Controller<in T : Material, out R> {
    fun control(material: T): R
}