package by.nepravskysm.mailclientforeveonline.workers

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.impl.WorkerWrapper
import by.nepravskysm.domain.usecase.mails.GetNewMailCountUseCase
import by.nepravskysm.mailclientforeveonline.utils.makeNotification
import org.koin.core.KoinComponent
import org.koin.core.inject

class CheckNewMailWorker(context: Context, workerParams: WorkerParameters)
    : Worker(context, workerParams), KoinComponent{

    private val getNewMailCountUseCase: GetNewMailCountUseCase by inject()

    private val ctx:Context = context



    override fun doWork(): Result {

        Log.d("logdwork", " do WORK ======================>")
        var result = Result.success()

        getNewMailCountUseCase.execute {
            onComplite {
                if(it != 0){
                    makeNotification("You have $it new mail", ctx)
                }
            }

            onError { result = Result.retry() }
        }

        return Result.success()
    }
}