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
    // Indexing always uses CoNLL-U.
    const format = Format.Conllu
    const linksAreValid = computed(() => {
        return corporaStore.activeUUID !== null && jobSelection.hypothesisJobId !== null
    })

    // Methods
    function index() {
        loading.value = true
        API.postIndex(corporaStore.activeUUID, jobSelection.hypothesisJobId, format)
            .catch(error => app.handleServerError("index corpus", error))
            .finally(() => loading.value = false)
    }

    // Exports
    return {
        // Fields
        format, linksAreValid, loading,
        // Methods
        index,
    }
})

export default useIndexing
