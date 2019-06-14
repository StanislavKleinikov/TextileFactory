package com.goolge.kleinikov.stanislav.textilefactory.domain

import com.goolge.kleinikov.stanislav.textilefactory.domain.Department.QualityDepartment.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class Transport(
    private val storage: Storage,
    private val rawQualityDepartments: List<RawQualityDepartment>,
    private val threadQualityDepartment: List<ThreadQualityDepartment>,
    private val coloredThreadsQualityDepartment: List<ColoredThreadsQualityDepartment>,
    private val threadProducers: List<Department.ExecutionDepartment.ThreadProducer>,
    private val coloredThreadProducer: List<Department.ExecutionDepartment.ColoredThreadProducer>
) {

    fun startManageStorage(): CompositeDisposable {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(startManageRawMaterial())
        return compositeDisposable
    }

    private fun startManageRawMaterial(): Disposable {
        return storage.getRawMaterialManager()
            .flatMapIterable { rawQualityDepartments }
            .map { department ->
                storage.getRawMaterial(department.checkPerTime)
                    .repeatUntil { storage.rawMaterialRemaining() > 0 }
                    .takeWhile { dto -> dto.amount > 0 }
                    .map { dto ->
                        val checked = department.control(Material.RawMaterials(dto.amount))
                        storage.putCheckedRawMaterial(dto.id, checked)
                    }
                    .subscribe()
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

   /* fun startManageCheckedRawMaterial(departments: List<Department.ExecutionDepartment.ThreadProducer>): Disposable {
        return storage.getCheckedRawMaterialManager()
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
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun startManageThreads(departments: List<ThreadQualityDepartment>): Disposable {
        return storage.getThreadManager()
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
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun startManageCheckedThreads(departments: List<Department.ExecutionDepartment.ColoredThreadProducer>): Disposable {
        return storage.getCheckedThreadManager()
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
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun startManageColoredThreads(departments: List<ColoredThreadsQualityDepartment>): Disposable {
        return storage.getColoredThreadManager()
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
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }*/
}