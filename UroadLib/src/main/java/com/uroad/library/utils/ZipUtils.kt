package com.uroad.library.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipUtils {
    companion object {
        /**
         * 解压zip到指定的路径
         *
         * @param zipFileString ZIP的名称
         * @param outPathString 要解压缩路径
         */
        fun UnZipFolder(zipFileString: String, outPathString: String) {
            var inZip: ZipInputStream? = null
            var szName: String
            var fos: FileOutputStream? = null
            try {
                inZip = ZipInputStream(FileInputStream(zipFileString))
                while (inZip.nextEntry != null) {
                    val zipEntry = inZip.nextEntry
                    szName = zipEntry.name
                    if (zipEntry.isDirectory) {
                        //获取部件的文件夹名
                        szName = szName.substring(0, szName.length - 1)
                        val folder = File(outPathString + File.separator + szName)
                        folder.mkdirs()
                    } else {
                        val file = File(outPathString + File.separator + szName)
                        if (!file.exists()) {
                            file.parentFile.mkdirs()
                            file.createNewFile()
                        }
                        // 获取文件的输出流
                        fos = FileOutputStream(file)
                        val buffer = ByteArray(1024)
                        // 读取（字节）字节到缓冲区
                        while (inZip.read(buffer) != -1) {
                            // 从缓冲区（0）位置写入（字节）字节
                            fos.write(buffer, 0, inZip.read(buffer))
                            fos.flush()
                        }
                        fos.close()
                    }
                }
            } catch (e: Exception) {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }
                }
                if (inZip != null) {
                    try {
                        inZip.close()
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }
                }
            }
        }

        fun UnZipFolder(zipFileString: String, outPathString: String, name: String) {
            var szName = name
            var inZip: ZipInputStream? = null
            var fos: FileOutputStream? = null
            try {
                inZip = ZipInputStream(FileInputStream(zipFileString))
                while (inZip.nextEntry != null) {
                    //szName = zipEntry.getName();
                    if (inZip.nextEntry.isDirectory) {
                        //获取部件的文件夹名
                        szName = szName.substring(0, szName.length - 1)
                        val folder = File(outPathString + File.separator + szName)
                        folder.mkdirs()
                    } else {
                        val file = File(outPathString + File.separator + szName)
                        if (!file.exists()) {
                            file.parentFile.mkdirs()
                            file.createNewFile()
                        }
                        // 获取文件的输出流
                        fos = FileOutputStream(file)
                        val buffer = ByteArray(1024)
                        // 读取（字节）字节到缓冲区
                        while (inZip.read(buffer) != -1) {
                            // 从缓冲区（0）位置写入（字节）字节
                            fos.write(buffer, 0, inZip.read(buffer))
                            fos.flush()
                        }
                        fos.close()
                    }
                }
                inZip.close()
            } catch (e: IOException) {
                if (fos != null) {
                    try {
                        fos.close()
                    } catch (e1: IOException) {
                        e1.printStackTrace()
                    }

                }
                if (inZip != null) {
                    try {
                        inZip.close()
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                    }

                }
            }

        }

        /**
         * 压缩文件和文件夹
         *
         * @param srcFileString 要压缩的文件或文件夹
         * @param zipFileString 解压完成的Zip路径
         */
        fun ZipFolder(srcFileString: String, zipFileString: String) {
            var outZip: ZipOutputStream? = null
            try {
                //创建ZIP
                outZip = ZipOutputStream(FileOutputStream(zipFileString))
                //创建文件
                val file = File(srcFileString)
                //压缩
                zipFiles(file.parent + File.separator, file.name, outZip)
                //完成和关闭
                outZip.finish()
                outZip.close()
            } catch (e: Exception) {
                if (outZip != null) {
                    try {
                        outZip.close()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }

                }
            }

        }

        /**
         * 压缩文件
         */
        private fun zipFiles(folderString: String, fileString: String, zipOutputSteam: ZipOutputStream?) {
            if (zipOutputSteam == null)
                return
            val file = File(folderString + fileString)
            var inputStream: FileInputStream? = null
            try {
                if (file.isFile) {
                    val zipEntry = ZipEntry(fileString)
                    inputStream = FileInputStream(file)
                    zipOutputSteam.putNextEntry(zipEntry)
                    val buffer = ByteArray(4096)
                    while (inputStream.read(buffer) != -1) {
                        zipOutputSteam.write(buffer, 0, inputStream.read(buffer))
                    }
                    zipOutputSteam.closeEntry()
                } else {
                    //文件夹
                    val fileList = file.list()
                    //没有子文件和压缩
                    if (fileList.size <= 0) {
                        val zipEntry = ZipEntry(fileString + File.separator)
                        zipOutputSteam.putNextEntry(zipEntry)
                        zipOutputSteam.closeEntry()
                    }
                    //子文件和递归
                    for (aFileList in fileList) {
                        zipFiles(folderString, fileString + File.separator + aFileList, zipOutputSteam)
                    }
                }
            } catch (e: Exception) {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e1: Exception) {
                        e1.printStackTrace()
                    }

                }
            }

        }

        /**
         * 返回zip的文件输入流
         *
         * @param zipFileString zip的名称
         * @param fileString    ZIP的文件名
         * @return InputStream
         */
        @Throws(Exception::class)
        fun UpZip(zipFileString: String, fileString: String): InputStream {
            val zipFile = ZipFile(zipFileString)
            val zipEntry = zipFile.getEntry(fileString)
            return zipFile.getInputStream(zipEntry)
        }

        /**
         * 返回ZIP中的文件列表（文件和文件夹）
         *
         * @param zipFileString  ZIP的名称
         * @param bContainFolder 是否包含文件夹
         * @param bContainFile   是否包含文件
         */
        @Throws(Exception::class)
        fun getFileList(zipFileString: String, bContainFolder: Boolean, bContainFile: Boolean): List<File> {
            val fileList = ArrayList<File>()
            val inZip = ZipInputStream(FileInputStream(zipFileString))
            var szName: String
            while (inZip.nextEntry != null) {
                szName = inZip.nextEntry.name
                if (inZip.nextEntry.isDirectory) {
                    // 获取部件的文件夹名
                    szName = szName.substring(0, szName.length - 1)
                    val folder = File(szName)
                    if (bContainFolder) {
                        fileList.add(folder)
                    }
                } else {
                    val file = File(szName)
                    if (bContainFile) {
                        fileList.add(file)
                    }
                }
            }
            inZip.close()
            return fileList
        }
    }
}