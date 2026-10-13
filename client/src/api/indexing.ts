/**
 * API calls for indexing the annotation layer of a corpus.
 */

// Libraries & stores
import axios, { AxiosResponse } from "axios"
// Types & API
import { Format } from "@/types/documents"
import { UUID } from "@/types/corpora"

// Custom types
/** Server response to an index request. `indexed` is false when the corpus already existed in the index. */
export type IndexResult = {
    indexed: boolean
    message: string
}

// Paths
const indexPath = (corpus: UUID, job: string) => `/corpora/${corpus}/jobs/${job}/index`

// Public methods
/**
 * Ask the server to index the layer of a job.
 * @param corpus UUID of the corpus.
 * @param job Tagger job name, i.e. the annotation layer.
 * @param format Format the layer is converted to before indexing.
 */
export function postIndex(corpus: UUID, job: string, format: Format): Promise<AxiosResponse<IndexResult>> {
    return axios.post(indexPath(corpus, job), null, { params: { format: format } })
}
