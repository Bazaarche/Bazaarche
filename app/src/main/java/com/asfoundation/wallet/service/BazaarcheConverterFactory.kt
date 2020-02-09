package com.asfoundation.wallet.service

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


class BazaarcheConverterFactory(private val gson: Gson) : Converter.Factory() {

  override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                     retrofit: Retrofit): Converter<ResponseBody, *>? {

    if (annotations.any { it is GutSingleReply }) {

      val adapter = gson.getAdapter(TypeToken.get(type))

      return BazaarcheResponseBodyConverter(adapter)
    }

    return null
  }

  private class BazaarcheResponseBodyConverter(private val adapter: TypeAdapter<out Any>) : Converter<ResponseBody, Any> {

    override fun convert(value: ResponseBody): Any {

      val jsonString = value.string()

      val jsonObject = JSONObject(jsonString)
      val singleReply = jsonObject.getJSONObject(SINGLE_REPLY)
      val response = singleReply.getJSONObject(singleReply.keys().next()).toString()

      return adapter.fromJson(response)
    }

  }
}

annotation class GutSingleReply

private const val SINGLE_REPLY = "singleReply"
