package com.asfoundation.wallet.entity

data class RequestProperties(
    val clientID: String,
    val clientVersion: String,
    val clientVersionCode: Long,
    val language: Int,
    val androidClientInfo: DeviceInfo,
    val isKidsEnabled: Boolean,
    val lat: Double?,
    val lon: Double?,
    val appThemeState: Int
)

data class DeviceInfo(
    var sdkVersion: Int,
    var model: String,
    var product: String,
    var osBuild: String,
    var hardware: String,
    var device: String,
    var mnc: Int,
    var mcc: Int,
    var locale: String,
    var city: String,
    var province: String,
    var country: String,
    var cpu: String,
    var dpi: Int,
    val width: Int,
    val height: Int,
    var manufacturer: String,
    val adId: String,
    val adOptOut: Boolean,
    val androidId: String
)