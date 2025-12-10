package com.example.jlebot.milvusragtest.services

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TextSplitter
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import kotlin.collections.forEach
import kotlin.collections.joinToString
import kotlin.collections.mapNotNull
import kotlin.text.orEmpty
import kotlin.to


@Service
class InvoiceServiceImpl(
    private val chatModel: ChatModel,
    private val vectorStore: VectorStore,
    private val jdbcChatMemoryRepository: JdbcChatMemoryRepository,
) : InvoiceService {

    private val chatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(jdbcChatMemoryRepository)
        .maxMessages(10) // Set the maximum number of messages to retain in memory
        .build()

    private val chatClient = ChatClient.builder(chatModel)
        .defaultAdvisors(
            MessageChatMemoryAdvisor.builder(chatMemory).build()
        ).build()

    override fun load(invoice: MultipartFile): String {
        val document = invoice.resource
        println("Loading document: ${document.filename}")

        val documentReader = TikaDocumentReader(document)
        val documents: List<Document> = documentReader.get()

        val textSplitter: TextSplitter = TokenTextSplitter()
        val splitDocuments: List<Document> = textSplitter.apply(documents)

        vectorStore.add(splitDocuments)

        return "Document ${document.filename} loaded with ${splitDocuments.size} chunks"
    }

}