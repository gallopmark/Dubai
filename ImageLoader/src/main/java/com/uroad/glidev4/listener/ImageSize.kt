package com.uroad.glidev4.listener

class ImageSize(var width: Int, var height: Int) {

    override fun toString(): String {
        return width.toString() + SEPARATOR + height
    }

    companion object {
        private val SEPARATOR = "x"
    }
}
