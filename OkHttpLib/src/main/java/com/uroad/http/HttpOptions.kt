package com.uroad.http

import okhttp3.Cache
import okhttp3.CookieJar

class HttpOptions private constructor() {
    private var writeTimeout: Long = 30 * 1000L
    private var connectTimeout: Long = 10 * 1000L
    private var readTimeout: Long = 10 * 1000L
    private var callTimeout: Long = 0
    private var cookieJar = CookieJar.NO_COOKIES
    private var headers: HashMap<String, String>? = null
    private var cache: Cache? = null

    constructor(builder: Builder) : this() {
        this.writeTimeout = builder.writeTimeout
        this.connectTimeout = builder.connectTimeout
        this.readTimeout = builder.readTimeout
        this.cookieJar = builder.cookieJar
        this.cache = builder.cache
        this.headers = builder.headers
    }

    fun writeTimeout(): Long = writeTimeout
    fun connectTimeout(): Long = connectTimeout
    fun readTimeout(): Long = readTimeout
    fun callTimeout(): Long = callTimeout
    fun cookieJar(): CookieJar = cookieJar
    fun headers(): HashMap<String, String>? = headers
    fun cache(): Cache? = cache

    class Builder {
        internal var writeTimeout: Long = 30 * 1000L
        internal var connectTimeout: Long = 10 * 1000L
        internal var readTimeout: Long = 10 * 1000L
        private var callTimeout: Long = 0
        internal var cookieJar = CookieJar.NO_COOKIES
        internal var headers: HashMap<String, String>? = null
        internal var cache: Cache? = null

        fun writeTimeout(writeTimeout: Long): Builder {
            this.writeTimeout = writeTimeout
            return this
        }

        fun connectTimeout(connectTimeout: Long): Builder {
            this.connectTimeout = connectTimeout
            return this
        }

        fun readTimeout(readTimeout: Long): Builder {
            this.readTimeout = readTimeout
            return this
        }

        fun callTimeout(callTimeout: Long): Builder {
            this.callTimeout = callTimeout
            return this
        }

        fun cache(cache: Cache?): Builder {
            this.cache = cache
            return this
        }

        fun cookieJar(cookieJar: CookieJar): Builder {
            this.cookieJar = cookieJar
            return this
        }

        fun headers(headers: HashMap<String, String>?): Builder {
            this.headers = headers
            return this
        }

        fun build(): HttpOptions {
            return HttpOptions(this)
        }
    }
}