package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ColoredThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import io.reactivex.Observable
import java.lang.Thread.sleep
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TextileFactory private constructor(
        private val rawQualityDepartments: List<RawQualityDepartment> = listOf(RawQualityDepartment()),
        private val threadQualityDepartment: List<ThreadQualityDepartment> = listOf(ThreadQualityDepartment()),
        private val coloredThreadsQualityDepartment: List<ColoredThreadsQualityDepartment> = listOf(
                ColoredThreadsQualityDepartment()
        ),
        private val threadProducers: List<ThreadProducer> = listOf(ThreadProducer()),
        private val coloredThreadProducer: List<ColoredThreadProducer> = listOf(ColoredThreadProducer()),
        private val storage: Storage = Storage(),
        private val transport: Transport = Transport(
                storage,
                rawQualityDepartments,
                threadQualityDepartment,
                coloredThreadsQualityDepartment,
                threadProducers, coloredThreadProducer
        )
) {

    companion object {
        fun getDefaultFactory(): TextileFactory {
            return TextileFactory()
        }
    }

    fun start() {

        storage.getCompletedConsignmentManager()
                .debounce(150, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribe {
                    println(it)
                }
        transport.startManageStorage()

        val interval = Observable.interval(5000, TimeUnit.MILLISECONDS).startWith(-1)
        interval.map {
            val consignment = Consignment(id = it, date = DateFormat.getDateTimeInstance().format(Date())
                    , totalMaterial = Random.nextInt(200) + 100.toDouble())
            storage.processNewConsignment(consignment)
        }.subscribe()

        sleep(30000)
    }
}
