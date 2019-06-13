package com.goolge.kleinikov.stanislav.textilefactory.domain

sealed class Material(var amount: Double) {

    class RawMaterials(amount: Double) : Material(amount)

    class Threads(amount: Double) : Material(amount)

    class ColoredThreads(amount: Double, val color: Color) : Material(amount) {
        override fun toString(): String {
            return "$amount"
        }
    }
}

fun Material.reduce(amountToReduce: Double): Double {
    synchronized(this) {
        var returnValue = amountToReduce
        if (amount < amountToReduce) {
            returnValue = amount

        }
        amount -= returnValue
        return returnValue
    }
}

fun Material.increase(amount: Double) {
    synchronized(this) {
        this.amount += amount
    }
}