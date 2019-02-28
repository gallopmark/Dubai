package com.uroad.http.util

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MultipartUtils {
    companion object {
        fun filesToMultipartBody(fileKey: String?, files: MutableList<File>): MultipartBody {
            val builder = MultipartBody.Builder()
            for (file in files) {
                val requestBody = RequestBody.create(MediaType.parse(FileTypeUtils.getMimeType(file)), file)
                val name = fileKey ?: "file"
                builder.addFormDataPart(name, file.name, requestBody)
            }
            builder.setType(MultipartBody.FORM)
            return builder.build()
        }

        fun filesToMultipartBodyParts(fileKey: String?, files: MutableList<File>): MutableList<MultipartBody.Part> {
            val parts = ArrayList<MultipartBody.Part>(files.size)
            for (file in files) {
                val requestBody = RequestBody.create(MediaType.parse(FileTypeUtils.getMimeType(file)), file)
                val name = fileKey ?: "file"
                val part = MultipartBody.Part.createFormData(name, file.name, requestBody)
                parts.add(part)
            }
            return parts
        }
    }
}