package com.goolge.kleinikov.stanislav.textilefactory.entities

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class TextileFactory {

    fun start() {
        val interval = Observable.interval(5, TimeUnit.SECONDS)
        interval.map { RawMaterials(Math.random() * 100 + 100) }.blockingSubscribe { executeProcess(it) }
    }

    private fun executeProcess(rawMaterials: RawMaterials) {
        QualityDepartment.RawQualityDepartment.checkQuality(rawMaterials)
        val threads = ExecutionDepartment.ThreadProducer.produce(rawMaterials)
        QualityDepartment.ThreadQualityDepartment.checkQuality(threads)
        val coloredThreads = ExecutionDepartment.ColoredThreadProducer.produce(threads)
        QualityDepartment.ColoredThreadsQualityDepartment.checkQuality(coloredThreads)
    }
}
