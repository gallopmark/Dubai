package com.uroad.http.upload

import com.uroad.http.util.FileTypeUtils
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*
import java.io.File


interface UploadApi {

    companion object {
        fun fileToMultipartBodyPart(fileKey: String?, file: File, listener: SingleFileUploadListener?): MultipartBody.Part {
            val requestBody = RequestBody.create(MediaType.parse(FileTypeUtils.getMimeType(file)), file)
            val wrapper = RequestBodyWrapper(requestBody, listener)
            val name = fileKey ?: "filename"
            return MultipartBody.Part.createFormData(name, file.name, wrapper)
        }
    }

    @Multipart
    @POST
    fun uploadFileWithPart(@Url url: String, @Part part: MultipartBody.Part): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFilesWithParts(@Url url: String, @Part parts: MutableList<MultipartBody.Part>): Observable<ResponseBody>

    @Multipart
    @POST
    fun uploadFileWithRequestBody(@Url url: String, @Body multipartBody: MultipartBody): Observable<ResponseBody>
}