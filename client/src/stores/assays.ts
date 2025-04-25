// Libraries & stores
import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import stores, { AppStore } from '@/stores'
// Types & API
import { AssaysType, AssayDescription } from '@/types/assays'
import * as API from '@/api/assays'

/**
 * Contains dataset assays.
 */
const useAssays = defineStore('assays', () => {
    // Stores
    const app = stores.useApp() as AppStore

    // Fields
    const loading = ref(false)
    const assays = ref({} as AssaysType)
    const datasets = computed(() => {
        const aggregate: Record<string, string> = {}
        Object.values(assays.value).forEach(entry => Object.keys(entry).forEach(key => aggregate[key] = "something"))
        return Object.keys(aggregate)
    })
    // Hardcoded for now, but I doubt we would ever want this differently.
    const aspects: AssayDescription[] = [
        { id: "bothAgree", description: "lemma & PoS correct" },
        { id: "lemmaAgree", description: "lemma correct" },
        { id: "posAgree", description: "PoS correct" }
    ]

    // Methods
    /**
     * Reload all assays.
     */
    function reload() {
        loading.value = true
        API.getAssays()
            .then(response => assays.value = response.data)
            .catch(error => app.handleServerError("fetch assays", error))
            .finally(() => loading.value = false)
    }

    // Exports
    return {
        // Fields
        aspects, assays, datasets, loading,
        // Methods
        reload,
    }
})

export default useAssays