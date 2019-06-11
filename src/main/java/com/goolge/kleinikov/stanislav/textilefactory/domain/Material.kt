package com.goolge.kleinikov.stanislav.textilefactory.domain

sealed class Material(var amount: Double) {

    class RawMaterials(amount: Double) : Material(amount)

    class Threads(amount: Double) : Material(amount)

    class ColoredThreads(amount: Double, val color: Color) : Material(amount)
}