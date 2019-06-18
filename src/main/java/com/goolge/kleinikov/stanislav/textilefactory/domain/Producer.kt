package com.goolge.kleinikov.stanislav.textilefactory.domain

interface Producer<in T : Material, out R> {
    fun produce(material: T): R
}