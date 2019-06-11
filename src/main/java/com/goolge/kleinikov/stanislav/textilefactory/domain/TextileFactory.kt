package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Material.RawMaterials
import io.reactivex.Observable
import java.text.DateFormat
import java.util.*

import java.util.concurrent.TimeUnit

class TextileFactory {

    fun start() {
        val interval = Observable.interval(5, TimeUnit.SECONDS)
        interval
            .map { RawMaterials(Math.random() * 100 + 100) }
            .map { rawMaterials ->
                val report = Report(
                    date = DateFormat.getDateTimeInstance().format(Date()),
                    rawMaterial = rawMaterials.amount
                )
                Pair(report, rawMaterials)
            }.map { pair ->
                val rawMaterials = pair.second
                RawQualityDepartment.control(rawMaterials)
                val threads = ThreadProducer.produce(rawMaterials)
                val report = pair.first
                report.threads = threads.amount
                report.defectiveRawMaterial = report.rawMaterial - rawMaterials.amount
                report.threadsProduced = ThreadProducer.totalProduced
                report.percentThreadProducing = ThreadProducer.percentUsefulness
                Pair(report, threads)
            }
            .map { pair ->
                val threads = pair.second
                ThreadQualityDepartment.control(threads)
                val coloredThreads = ColoredThreadProducer.produce(threads)
                val report = pair.first
                report.defectiveThreads = report.threads - threads.amount
                report.coloredThread = coloredThreads.amount
                report.color = coloredThreads.color
                report.coloredThreadProduced = ColoredThreadProducer.totalProduced
                report.percentColoredThreadProducing = ColoredThreadProducer.percentUsefulness
                Pair(report, coloredThreads)
            }.map { pair ->
                val coloredThreads = pair.second
                ColoredThreadsQualityDepartment.control(coloredThreads)
                val report = pair.first
                report.defectiveColoredThread = report.coloredThread - coloredThreads.amount
                report
            }.blockingSubscribe { report: Report? -> println(report) }
    }
}
