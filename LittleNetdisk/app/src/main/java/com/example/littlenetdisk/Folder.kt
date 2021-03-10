package com.example.littlenetdisk

import java.io.Serializable

class Folder:Serializable{
    var fid:Int=-1
    var folderName:String=""
    var path:String=""
    var father:Int=-1
    var username:String=""
    var size:Int=-1
}
