package com.goolge.kleinikov.stanislav.textilefactory.usecase

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ColoredThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.ExecutionDepartment.ThreadProducer
import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import com.goolge.kleinikov.stanislav.textilefactory.domain.Material
import com.goolge.kleinikov.stanislav.textilefactory.domain.Storage
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

class Transporter(
    private val storage: Storage,
    private val scheduler: Scheduler
) {

    fun startManageRawMaterial(departments: List<RawQualityDepartment>): Disposable {
        return storage.rawMaterialManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { storage.rawMaterialRemaining() == 0.0 }
                    .map {
                        val toCheck = storage.getRawMaterial(department.checkPerTime)
                        if (toCheck > 0) {
                            val checked = department.control(Material.RawMaterials(toCheck))
                            storage.putCheckedRawMaterial(checked)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageCheckedRawMaterial(departments: List<ThreadProducer>): Disposable {
        return storage.checkedRawMaterialManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { storage.checkedRawMaterialRemaining() == 0.0 }
                    .map {
                        val toProduce = storage.getCheckedRawMaterial(department.producePerTime)
                        if (toProduce > 0) {
                            val produced = department.produce(Material.RawMaterials(toProduce))
                            storage.putThreads(produced)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageThreads(departments: List<ThreadQualityDepartment>): Disposable {
        return storage.threadsManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { storage.threadsRemaining() == 0.0 }
                    .map {
                        val toCheck = storage.getThreads(department.checkPerTime)
                        if (toCheck > 0) {
                            val checked = department.control(Material.Threads(toCheck))
                            storage.putCheckedThreads(checked)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageCheckedThreads(departments: List<ColoredThreadProducer>): Disposable {
        return storage.checkedThreadsManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { storage.checkedThreadsRemaining() == 0.0 }
                    .map {
                        val toProduce = storage.getCheckedThreads(department.producePerTime)
                        if (toProduce > 0) {
                            val produced = department.produce(Material.Threads(toProduce))
                            storage.putColoredThreads(produced)
                        }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }

    fun startManageColoredThreads(departments: List<ColoredThreadsQualityDepartment>): Disposable {
        return storage.coloredThreadManager
            .flatMapIterable { departments }
            .map { department ->
                Observable
                    .just(department)
                    .repeatUntil { storage.coloredThreadsRemaining() > 0 }
                    .map {
                         val toCheck = storage.getColoredThreads(department.checkPerTime)
                         if (toCheck != null) {
                             val checked = department.control(toCheck)
                             storage.putCheckedColoredThreads(checked)
                         }
                    }
                    .subscribeOn(scheduler)
                    .subscribe()
            }
            .subscribeOn(scheduler)
            .subscribe()
    }
}