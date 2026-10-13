// Libraries & stores
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import stores, { AppStore, CorporaStore, JobSelectionStore } from '@/stores'
// Types & API
import * as API from '@/api/indexing'
import { Format } from '@/types/documents'

/**
 * Sends an annotation layer of a corpus to the server to be indexed.
 */
const useIndexing = defineStore('indexing', () => {
    // Stores
    const corporaStore = stores.useCorpora() as CorporaStore
    const app = stores.useApp() as AppStore
    const jobSelection = stores.useJobSelection() as JobSelectionStore

    // Fields
    const loading = ref(false)
    // UUIDs of corpora that were indexed successfully (this session only).
    const indexedCorpora = ref([] as string[])
    // Result message of the last index request per corpus UUID, shown in the index modal until replaced.
    const messages = ref({} as { [corpus: string]: string })
    // Indexing always uses CoNLL-U.
    const format = Format.Conllu
    const linksAreValid = computed(() => {
        return corporaStore.activeUUID !== null && jobSelection.hypothesisJobId !== null
    })

    // Methods
    function isIndexed(corpus: string): boolean {
        return indexedCorpora.value.includes(corpus)
    }

    function messageFor(corpus: string): string | null {
        return messages.value[corpus] ?? null
    }

    function index() {
        loading.value = true
        const corpus = corporaStore.activeUUID
        API.postIndex(corpus, jobSelection.hypothesisJobId, format)
            .then(response => {
                messages.value[corpus] = response.data.message
                // An already existing corpus can be queried as well.
                if (!indexedCorpora.value.includes(corpus)) indexedCorpora.value.push(corpus)
            })
            .catch(error => app.handleServerError("index corpus", error))
            .finally(() => loading.value = false)
    }

    // Exports
    return {
        // Fields
        format, linksAreValid, loading, indexedCorpora, messages,
        // Methods
        index, isIndexed, messageFor,
    }
})

export default useIndexing
