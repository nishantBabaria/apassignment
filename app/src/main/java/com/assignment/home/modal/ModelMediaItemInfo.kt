package com.assignment.home.modal

//we often create classes to hold some data in it. In such classes, some standard functions are often derivable from the data
data class ModelMediaItemInfo(
    var domain: String? = null,
    var basePath: String? = null,
    var key: String? = null,
):java.io.Serializable
