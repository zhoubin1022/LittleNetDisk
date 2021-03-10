package com.example.littlenetdisk

class ImageComparator:Comparator<Image> {
    override fun compare(o1: Image, o2: Image): Int {
        if(o1.name==""||o2.name=="") return 0
        else return o1.name.compareTo(o2.name)
    }
}