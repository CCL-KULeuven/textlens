<template>
    <div>
        <p style='color:red; text-align: center'>
            Due to a hack at the Dutch Language Institute (Instituut voor de Nederlandse Taal) <br>
            corpora uploaded to <i>Textlens</i> after April 4th may no longer be available. <br>
            We apologize for the inconvenience. <br>
        </p>
        <!-- Owner corpus table -->
        <CorpusTable :type="TableCorporaType.User" :corpora="corporaStore.allCorpora"
            @delete="corpus => {setActiveCorpus(corpus); deleteCorpusData = corpus}" 
            @update="corpus => {setActiveCorpus(corpus); editMode(corpus)}" false
            @create="showNewCorpusModal = true"
            @export="corpus => {setActiveCorpus(corpus); exportCorpusData = corpus} "
            @annotate="corpus => {setActiveCorpus(corpus); annotateCorpusData = corpus}"
            @upload="corpus => {setActiveCorpus(corpus); uploadCorpusData = corpus}">
<!--             <template #title>Your corpora</template>
 -->        </CorpusTable>

        <!-- Shared corpus table -->
        <CorpusTable :type="TableCorporaType.User" :corpora="corporaStore.sharedCorpora"
            @delete="corpus => {setActiveCorpus(corpus); deleteCorpusData = corpus}" 
            @update="corpus => {setActiveCorpus(corpus); editMode(corpus)}" false
            @create="showNewCorpusModal = true"
            @export="corpus => {setActiveCorpus(corpus); exportCorpusData = corpus} "
            @annotate="corpus => {setActiveCorpus(corpus); annotateCorpusData = corpus}"
            @upload="corpus => {setActiveCorpus(corpus); uploadCorpusData = corpus}" sharedWithYou>
            <template #title>Shared with you</template>
            <template #help>
                Here you can see the corpora that have been shared with you. <br>
                If a corpus has been shared with you as a
                collaborator, you can make modifications. <br>
                If it has been shared with you as a viewer, you can only
                inspect and evaluate it.
            </template>
        </CorpusTable>

        <!-- Public corpus table 
        <CorpusTable :type="TableCorporaType.Public" :corpora="corporaStore.publicCorpora"
            @delete="corpus => deleteCorpusData = corpus" @update="corpus => editMode(corpus)" selectable
            @create="showNewCorpusModal = true">
            <template #help>
                <BenchmarkSetsHelp /><br />
                You can inspect them in further detail on the
                <GNav :route="{ path: '/annotate/evaluate' }">Evaluate tab</GNav>.
            </template>
        </CorpusTable>
-->
        <!-- Create modal -->
        <CorpusForm title="Create new corpus" :show="showNewCorpusModal" @hide="showNewCorpusModal = false"
            :action="metadata => { corporaStore.createCorpus(metadata); showNewCorpusModal = false }"
            :cancel="() => showNewCorpusModal = false">
            <template #help>Fill in the metadata and create a corpus.
                <br>
                <CorpusFormHelp />
            </template>
        </CorpusForm>

        <!-- Update modal -->
        <CorpusForm title="Update corpus metadata" :show="updateCorpusData !== null" @hide="updateCorpusData = null"
            update :item="updateCorpusData"
            :action="metadata => { corporaStore.updateCorpus(updateCorpusData.uuid, metadata); updateCorpusData = null }"
            :cancel="() => updateCorpusData = null">
            <template #help>
                Change the metadata of an existing corpus.
                <br>
                <CorpusFormHelp />
            </template>
        </CorpusForm>

        <!-- Upload docs modal -->
        <UploadModal title="Upload documents" :corpus="uploadCorpusData" :type="TableCorporaType.User" :show="uploadCorpusData !== null" @hide="uploadCorpusData = null"
            :cancel="() => uploadCorpusData = null">
            <template #help>Upload documents to the corpus.
                <br>
                <CorpusFormHelp />
            </template>
        </UploadModal>

        <!-- Annotate modal -->
        <AnnotateModal title="Annotate" :corpus="annotateCorpusData" :show="annotateCorpusData !== null" @hide="annotateCorpusData = null"
            :cancel="() => annotateCorpusData = null">
            <template #help>Tag your data with the available taggers.
                <br>
                <CorpusFormHelp />
            </template>
        </AnnotateModal>


        <!-- Export modal -->
        <ExportModal :show="exportCorpusData !== null" :item="exportCorpusData" :corpus="exportCorpusData" 
            @hide="exportCorpusData = null" />

        <!-- Delete modal -->
        <DeleteModal :show="deleteCorpusData !== null" :item="deleteCorpusData"
            :displayname="'Corpus ' + (deleteCorpusData !== null ? deleteCorpusData.name : '[null]')"
            @delete="corporaStore.deleteCorpus" @hide="deleteCorpusData = null" />
    </div>
</template>

<script setup lang='ts'>
// Libraries & stores
import { ref, onMounted } from 'vue'
import stores, { CorporaStore, JobsStore } from '@/stores'
// Types & API
import { CorpusMetadata } from '@/types/corpora'
import { TableCorporaType } from '@/types/table'
// Components
import { CorpusTable, GNav } from '@/components'
import CorpusForm from '@/components/modals/corpus/CorpusForm.vue'
import DeleteModal from "@/components/modals/DeleteModal.vue"
import UploadModal from "@/components/modals/UploadModal.vue"
import AnnotateModal from "@/components/modals/AnnotateModal.vue"
import ExportModal from "@/components/modals/ExportModal.vue"
import CorpusFormHelp from "@/components/help/CorpusFormHelp.vue"
// import BenchmarkSetsHelp from "@/components/help/BenchmarkSetsHelp.vue"

// Stores
const corporaStore = stores.useCorpora() as CorporaStore
const jobsStore = stores.useJobs() as JobsStore


// Fields
const showNewCorpusModal = ref(false)
const selectedCorpus = ref(corporaStore.activeCorpus)
// Once not null, respective modal is shown.
const deleteCorpusData = ref(null as null | CorpusMetadata)
const uploadCorpusData = ref(null as null | CorpusMetadata)
const annotateCorpusData = ref(null as null | CorpusMetadata)
const updateCorpusData = ref(null as null | CorpusMetadata)
const exportCorpusData = ref(null as null | CorpusMetadata)


const editMode = (corpus: CorpusMetadata) => {
    // Deepcopy so we can modify the object freely.
    updateCorpusData.value = JSON.parse(JSON.stringify(corpus))
}

// Mounts & watches
/** 
 * Although CorporaView lives in AnnotateView, which reloads corpora, 
 * the number of jobs could change when navigating between jobs and corpora, requiring a reload.
 */
onMounted(() => {
    corporaStore.reload()
    var loadData = function(){
        corporaStore.reload()
        // Do stuff
        setTimeout(loadData, 10000);
    };
    setTimeout(loadData,10000);
})

function setActiveCorpus(corpus: CorpusMetadata){
    selectedCorpus.value = corpus;
    corporaStore.activeUUID = corpus.uuid; 
    jobsStore.reload();
}

</script>
