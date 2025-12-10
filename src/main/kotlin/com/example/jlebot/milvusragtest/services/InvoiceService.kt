package com.example.jlebot.milvusragtest.services

import org.springframework.web.multipart.MultipartFile

interface InvoiceService {

    fun load(invoice: MultipartFile) : String

}