package com.example.jlebot.milvusragtest.api

import com.example.jlebot.milvusragtest.services.InvoiceService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
class InvoiceApi(
    private val service: InvoiceService
) {

    @PostMapping("/upload")
    fun upload(
        @RequestParam("invoice") invoice: MultipartFile,
    ): ResponseEntity<String> {
        return ok(service.load(invoice))
    }

}