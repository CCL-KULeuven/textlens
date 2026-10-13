package org.ivdnt.galahad.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.ivdnt.galahad.app.INDEX_URL
import org.ivdnt.galahad.documents.DocumentFormat
import org.ivdnt.galahad.exceptions.ErrorResponse
import org.ivdnt.galahad.web.service.IndexResult
import org.ivdnt.galahad.web.service.IndexService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class IndexController(
    val indexService: IndexService,
) {

    @Autowired
    private val response: HttpServletResponse? = null

    @Operation(
        summary = "Index job layer",
        description = "Convert the layer of a job to a format and send it to be indexed, unless the corpus is already indexed. Requires write access to the corpus.",
    )
    @ApiResponse(
        responseCode = "202", description = "The layer was sent to be indexed."
    )
    @ApiResponse(
        responseCode = "200", description = "The corpus already exists in the index, so nothing was indexed."
    )
    @ApiResponse(
        responseCode = "404",
        description = "Corpus or job was not found.",
        content = [Content(array = ArraySchema(schema = Schema(implementation = ErrorResponse::class)))]
    )
    @CrossOrigin
    @PostMapping(INDEX_URL)
    fun postIndex(
        @PathVariable @Parameter(description = "Corpus UUID") corpus: UUID,
        @PathVariable @Parameter(description = "Tagger name") job: String,
        @RequestParam(defaultValue = "conllu") @Parameter(description = "Format to index") format: DocumentFormat,
    ): IndexResult {
        val result = indexService.index(corpus, job, format)
        response?.status = if (result.indexed) HttpServletResponse.SC_ACCEPTED else HttpServletResponse.SC_OK
        return result
    }
}
