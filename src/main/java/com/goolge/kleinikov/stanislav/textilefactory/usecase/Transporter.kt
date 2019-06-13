package com.goolge.kleinikov.stanislav.textilefactory.usecase

import com.goolge.kleinikov.stanislav.textilefactory.domain.Consignment
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ColoredThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Material
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class Transporter(
    private val consignment: Consignment,
    private val scheduler: Scheduler
) {

    fun startManageRawMaterial(departments: List<RawQualityDepartment>): Disposable {
        return consignment.rawMaterialManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { consignment.rawMaterialRemaining() == 0.0 }
                    .map {
                        val toCheck = consignment.getRawMaterial(department.checkPerTime)
                        if (toCheck > 0) {
                            val checked = department.control(Material.RawMaterials(toCheck))
                            consignment.putCheckedRawMaterial(checked)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageCheckedRawMaterial(departments: List<ThreadProducer>): Disposable {
        return consignment.checkedRawMaterialManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { consignment.checkedRawMaterialRemaining() == 0.0 }
                    .map {
                        val toProduce = consignment.getCheckedRawMaterial(department.producePerTime)
                        if (toProduce > 0) {
                            val produced = department.produce(Material.RawMaterials(toProduce))
                            consignment.putThreads(produced)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageThreads(departments: List<ThreadQualityDepartment>): Disposable {
        return consignment.threadsManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { consignment.threadsRemaining() == 0.0 }
                    .map {
                        val toCheck = consignment.getThreads(department.checkPerTime)
                        if (toCheck > 0) {
                            val checked = department.control(Material.Threads(toCheck))
                            consignment.putCheckedThreads(checked)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageCheckedThreads(departments: List<ColoredThreadProducer>): Disposable {
        return consignment.checkedThreadsManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { consignment.checkedThreadsRemaining() == 0.0 }
                    .map {
                        val toProduce = consignment.getCheckedThreads(department.producePerTime)
                        if (toProduce > 0) {
                            val produced = department.produce(Material.Threads(toProduce))
                            consignment.putColoredThreads(produced)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageColoredThreads(departments: List<ColoredThreadsQualityDepartment>): Disposable {
        return consignment.coloredThreadManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { consignment.coloredThreadsRemaining() > 0 }
                    .map {
                        val toCheck = consignment.getColoredThreads(department.checkPerTime)
                        if (toCheck != null) {
                            val checked = department.control(toCheck)
                            consignment.putCheckedColoredThreads(checked)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }
}