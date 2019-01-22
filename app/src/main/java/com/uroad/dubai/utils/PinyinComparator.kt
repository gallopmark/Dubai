package com.uroad.dubai.utils

import com.uroad.dubai.model.ContactMDL

import java.util.Comparator

class PinyinComparator : Comparator<ContactMDL> {

    override fun compare(o1: ContactMDL, o2: ContactMDL): Int {
        return if (o1.letters == "@" || o2.letters == "#") {
            1
        } else if (o1.letters == "#" || o2.letters == "@") {
            -1
        } else {
            o1.letters!!.compareTo(o2.letters!!)
        }
    }

}
