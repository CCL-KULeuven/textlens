package org.ivdnt.galahad.web.service

import jakarta.servlet.http.HttpServletRequest
import org.apache.logging.log4j.kotlin.Logging
import org.ivdnt.galahad.annotations.Layer
import org.ivdnt.galahad.app.User
import org.ivdnt.galahad.corpora.Corpus
import org.ivdnt.galahad.documents.DocumentFormat
import org.ivdnt.galahad.export.CorpusExport
import org.ivdnt.galahad.util.FileMapper
import org.ivdnt.galahad.util.createZipFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.File
import java.util.*

// TODO: use the corpus owner instead.
private const val BLACKLAB_USER = "mijail@ccl.kuleuven.be"

@Service
class IndexService(val corpora: CorporaService) : Logging {
    @Autowired
    private val request: HttpServletRequest? = null

    @Value("\${blacklab.url:http://localhost:8080/blacklab-server}")
    private lateinit var blacklabUrl: String

    private val user get() = User.fromRequest(request)

    /**
     * Convert the layer of [job] to [format] and index it in BlackLab.
     */
    fun index(corpusID: UUID, job: String, format: DocumentFormat) {
        val corpus = corpora.readAsWriterOrThrow(corpusID, user)
        val export = CorpusExport.create(corpus, job, format, user, shouldMerge = false, posHeadOnly = false)
        sendIndexRequest(corpus, export)
    }

    /**
     * Create a BlackLab corpus, then upload all documents of the layer to it as one zip, converted to the export format.
     */
    private fun sendIndexRequest(corpus: Corpus, export: CorpusExport) {
        val restTemplate = RestTemplate()
        val corpusName = "$BLACKLAB_USER:${corpus.mutableMetadata.name}"

        // Firstly, create corpus.
        val createParams = LinkedMultiValueMap<String, String>().apply {
            add("name", corpusName)
            add("format", "conll-u")
        }
        val createHeaders = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        restTemplate.postForEntity("$blacklabUrl/", HttpEntity(createParams, createHeaders), String::class.java)

        // Secondly, index all documents within the new corpus as a single zip.
        // No CMDI metadata, as BlackLab would try to index those files as well.
        val docs = corpus.documents.readAll().map { export.documentExport(it) to it }
            .filter { (docExport, _) -> docExport.layer != Layer.EMPTY }
        val files: Sequence<FileMapper> = docs.asSequence().map { (docExport, doc) ->
            "${File(doc.name).nameWithoutExtension}.${export.format.extension}" to { out -> docExport.convert(out) }
        }
        val zip = createZipFile(files)
        try {
            val formData = LinkedMultiValueMap<String, Any>().apply {
                add("data", object : FileSystemResource(zip) {
                    // BlackLab recognises archives by their .zip extension.
                    override fun getFilename() = "corpus.zip"
                })
            }
            val docsHeaders = HttpHeaders().apply { contentType = MediaType.MULTIPART_FORM_DATA }
            logger.info("Indexing ${docs.size} documents in BlackLab corpus $corpusName")
            restTemplate.postForEntity(
                "$blacklabUrl/{corpus}/docs/?api=4", HttpEntity(formData, docsHeaders), String::class.java, corpusName
            )
        } finally {
            zip.delete()
        }
    }
}
