<template>
    <GModal :show="show" @hide="$emit('hide')" :noHelp=true :showHelp="false" :headless="true">
        <GCard :title="`Index corpus ${corporaStore.activeCorpus?.name}`" noHelp>
            <JobSelect customTitle="Annotation layer" />
            <GCard noHelp>
                <template #title>Index as format</template>
                <template #header> </template>
                <div id="center">
                    <p>Format: <b>CoNLL-U</b></p>

                    <GButton yellow title="Index data" showTitle @click="indexingStore.index()"
                        :disabled="indexingStore.loading || !indexingStore.linksAreValid">
                        <i class="fa fa-database"></i>
                    </GButton>

                    <GInfo spinner v-if="indexingStore.loading">
                        <span><i>Please wait while your corpus is being indexed.</i></span>
                    </GInfo>
                </div>
            </GCard>
        </GCard>
    </GModal>
</template>

<script setup lang="ts">
// Libraries & stores
import { onMounted } from "vue"
import stores, { CorporaStore, JobsStore, IndexingStore } from "@/stores"
// Components
import { GModal, GCard, GButton, GInfo, JobSelect } from "@/components"

// Stores
const corporaStore = stores.useCorpora() as CorporaStore
const jobsStore = stores.useJobs() as JobsStore
const indexingStore = stores.useIndexing() as IndexingStore

defineProps({
    show: { type: Boolean, default: true }
})

// Load jobs list once. jobSelectionStore takes care of the selected job.
onMounted(() => {
    jobsStore.reload()
})
</script>

<style scoped>
:deep(#center) {
    display: flex;
    flex-direction: column;
    gap: 10px;
    align-content: center;
    align-items: center;
}
</style>
