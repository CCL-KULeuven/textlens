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
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.io.File
import java.util.*

// TODO: use the corpus owner instead.
private const val BLACKLAB_USER = "mijail@ccl.kuleuven.be"

/**
 * Outcome of an index request. [indexed] is false when the corpus already existed in BlackLab and nothing was sent.
 */
data class IndexResult(val indexed: Boolean, val message: String)

@Service
class IndexService(val corpora: CorporaService) : Logging {
    @Autowired
    private val request: HttpServletRequest? = null

    @Value("\${blacklab.url:http://localhost:8080/blacklab-server}")
    private lateinit var blacklabUrl: String

    private val user get() = User.fromRequest(request)

    /**
     * Convert the layer of [job] to [format] and index it in BlackLab, unless the BlackLab corpus already exists.
     */
    fun index(corpusID: UUID, job: String, format: DocumentFormat): IndexResult {
        val corpus = corpora.readAsWriterOrThrow(corpusID, user)
        val restTemplate = RestTemplate()
        val corpusName = blacklabCorpusName(corpus)
        if (corpusExists(restTemplate, corpusName)) {
            logger.info("BlackLab corpus $corpusName already exists, skipping indexing")
            return IndexResult(
                indexed = false,
                message = "Corpus ${corpus.mutableMetadata.name} already exists in BlackLab, skipping indexing."
            )
        }
        val export = CorpusExport.create(corpus, job, format, user, shouldMerge = false, posHeadOnly = false)
        sendIndexRequest(restTemplate, corpusName, corpus, export)
        return IndexResult(indexed = true, message = "Corpus ${corpus.mutableMetadata.name} was indexed in BlackLab.")
    }

    private fun blacklabCorpusName(corpus: Corpus) = "$BLACKLAB_USER:${corpus.mutableMetadata.name}"

    /**
     * Create a BlackLab corpus, then upload all documents of the layer to it as one zip, converted to the export format.
     */
    private fun sendIndexRequest(restTemplate: RestTemplate, corpusName: String, corpus: Corpus, export: CorpusExport) {
        // Firstly, create corpus.
        createCorpus(restTemplate, corpusName)

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

    /**
     * Whether BlackLab already has a corpus named [corpusName]. BlackLab answers 404 for unknown corpora.
     */
    private fun corpusExists(restTemplate: RestTemplate, corpusName: String): Boolean = try {
        restTemplate.getForEntity("$blacklabUrl/{corpus}/?api=4", String::class.java, corpusName)
        true
    } catch (e: HttpClientErrorException.NotFound) {
        false
    }

    private fun createCorpus(restTemplate: RestTemplate, corpusName: String) {
        val createParams = LinkedMultiValueMap<String, String>().apply {
            add("name", corpusName)
            add("format", "conll-u")
        }
        val createHeaders = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        logger.info("Creating BlackLab corpus $corpusName")
        restTemplate.postForEntity("$blacklabUrl/", HttpEntity(createParams, createHeaders), String::class.java)
    }
}
