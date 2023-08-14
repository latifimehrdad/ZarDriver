package com.zarholding.zardriver.view.fragment.download

/**
 * Create by Mehrdad on 1/16/2023
 */

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zarholding.zardriver.ZarViewModel
import com.zarholding.zardriver.model.repository.DownloadFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadFileRepository: DownloadFileRepository
) : ZarViewModel() {

    private var destinationFile: File
    val downloadProgress: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val downloadSuccessLiveData: MutableLiveData<File> by lazy { MutableLiveData<File>() }


    //---------------------------------------------------------------------------------------------- init
    init {
        val downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val destinationDir = File(downloadFolder.absolutePath, "Zar")
        if (!destinationDir.exists())
            destinationDir.mkdir()
        destinationFile = File(
            destinationDir.absolutePath,
            "Zar_${getNewFileName()}.apk"
        )

    }
    //---------------------------------------------------------------------------------------------- init


    //______________________________________________________________________________________________ getNewFileName
    private fun getNewFileName(): String? {
        return SimpleDateFormat("yyyy_MM_dd__HH_mm_ss", Locale.getDefault()).format(Date())
    }
    //______________________________________________________________________________________________ getNewFileName


    //---------------------------------------------------------------------------------------------- downloadFile
    fun downloadFile(fileName: String, appName: String) {
        if (destinationFile.exists())
            destinationFile.delete()
        viewModelScope.launch(IO) {
            delay(500)
            downloadFileRepository.downloadApkFile(appName, fileName)
                .body()?.saveFile()?.collect { downloadState ->
                    when (downloadState) {
                        is DownloadState.Downloading -> {
                            downloadProgress.postValue(downloadState.progress)
                        }

                        is DownloadState.Failed -> {

                        }

                        DownloadState.Finished -> {
                            downloadSuccessLiveData.postValue(destinationFile)
                        }
                    }
                }
        }
    }
    //---------------------------------------------------------------------------------------------- downloadFile


    //---------------------------------------------------------------------------------------------- DownloadState
    private sealed class DownloadState {
        data class Downloading(val progress: Int) : DownloadState()
        object Finished : DownloadState()
        data class Failed(val error: Throwable? = null) : DownloadState()
    }
    //---------------------------------------------------------------------------------------------- DownloadState


    //---------------------------------------------------------------------------------------------- saveFile
    private fun ResponseBody.saveFile(): Flow<DownloadState> {
        return flow {
            emit(DownloadState.Downloading(0))
            try {
                byteStream().use { inputStream ->
                    destinationFile.outputStream().use { outputStream ->
                        val totalBytes = contentLength()
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var progressBytes = 0L

                        var bytes = inputStream.read(buffer)
                        while (bytes >= 0) {
                            outputStream.write(buffer, 0, bytes)
                            progressBytes += bytes
                            bytes = inputStream.read(buffer)
                            emit(DownloadState.Downloading(((progressBytes * 100) / totalBytes).toInt()))
                        }
                    }
                }
                emit(DownloadState.Finished)
            } catch (e: Exception) {
                emit(DownloadState.Failed(e))
            }
        }
            .flowOn(IO)
            .distinctUntilChanged()
    }
    //---------------------------------------------------------------------------------------------- saveFile


}