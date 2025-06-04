<template>
    <GTable title="Corpora" helpSubject="corpora" :columns :items="displayCorpora" :loading=false
        sortedByField="name" :sortDesc="false" :noHelp="displayCorpora && displayCorpora.length == 0"
        v-if="displayCorpora.length > 0 || (type == TableCorporaType.User && !sharedWithYou)">

        <template #title>
            <slot name="title">
                {{ displayCorpora.length }} {{ type }} {{ displayCorpora.length == 1 ? ' corpus' : ' corpora' }}
            </slot>
        </template>

        <template #help>
            <slot name="help">
                <component :is="help.corpora"></component>
            </slot>
        </template>

        <template #table-empty-instruction>
            <slot name="help">
                <component :is="help.start"></component>
            </slot>
        </template>

        <template #prepend>
            <div style="display: flex; align-items: center; justify-content: center; margin-bottom:1em"
                v-if="type == TableCorporaType.User || (userStore.user.admin && type == TableCorporaType.Public)">
                <GButton green @click="$emit('create')" v-if="type == TableCorporaType.User && !sharedWithYou">
                    New corpus
                </GButton>
            </div>

        </template>

        <!-- name cell -->
        <template #cell-name="data">
            <ExternalLink v-if="data.item.sourceURL" :href="data.item.sourceURL">
                {{ data.item.name}}
            </ExternalLink>
            <span v-else>{{ data.item.name }}</span>
        </template>

        <!-- size in bytes cell -->
        <template #cell-sizeInBytes="data">
            <span v-if="data.value > 1023">{{ Utils.formatBytes(data.value) }}</span>
            <span v-else>~ 0</span>
        </template>

        <!-- last modified cell -->
        <template #cell-lastModified="data">
            <span style="white-space:nowrap">{{ new Date(data.value).toLocaleString("nl", {
                year: "2-digit",
                month: "2-digit",
                day: "numeric",
                hour: "2-digit",
                minute: "2-digit",
            }) }}</span>
        </template>

        <!-- collaborators cell -->
        <template #cell-collaborators="data">
            {{ formatCollaborators(data.item) }}
        </template>



        <!-- actions cell -->
        <template #cell-actions="data">
            <div style="display: flex;">
                <GButton v-if="data.item.numDocs === 0" yellow title="Upload documents" @click="$emit('upload', data.item)">
                    <i class="fa fa-upload"></i> 
                </GButton>

                <GButton v-else yellow title="Upload/view documents" @click="$emit('upload', data.item)">
                    <i class="fa fa-file-text"></i>    
                </GButton>
                
                <GButton yellow title="Annotate data" :disabled="!corpusHasDocs(data.item)" @click="$emit('annotate', data.item)">
                    <i v-if="data.item.activeJobs > 0" class="fa fa-code fa-spin"></i>
                    <i v-else class="fa fa-code"></i>
                </GButton>

                <GButton yellow title="Export results" :disabled="!corpusHasResults(data.item)" @click="$emit('export', data.item)">
                    <i class="fa fa-download"></i>
                </GButton>

                <GButton orange title="Edit corpus metadata" @click="$emit('update', data.item)">
                    <i class="fa fa-pencil"></i>
                </GButton>

                <GButton v-if="!sharedWithYou || userStore.user.admin"
                    :disabled="!userStore.canDelete(data.item)" red title="Delete corpus"
                    @click="$emit('delete', data.item)">
                    <i class="fa fa-trash"></i>
                </GButton>
                
            </div>
        </template>
    </GTable>
</template>

<script setup lang='ts'>
// Libraries & stores
import { PropType, ref, computed } from 'vue'
import stores from '@/stores'
// API & types
import { CorpusMetadata } from '@/types/corpora'
import { TableCorporaType, Field } from '@/types/table'
import * as Utils from '@/api/utils'
// Components
import { ExternalLink, GButton, GTable } from '@/components'
import help from '@/components/help'

// Stores
const userStore = stores.useUser()
const corporaStore = stores.useCorpora()

// Props
const props = defineProps({
    corpora: Array<CorpusMetadata>, 
    type: String as PropType<TableCorporaType>,
    selectable: Boolean, sharedWithYou: Boolean
})

// Fields
const selectedCorpus = ref(corporaStore.activeCorpus)
const editable = props.type === TableCorporaType.User
const displayCorpora = computed(() => {
    if (props.type === TableCorporaType.User) {
        if (props.sharedWithYou) {
            return props.corpora.filter(i => i.collaborators.includes(userStore.user.id) || i.viewers.includes(userStore.user.id))
        } else {
            return props.corpora.filter(i => i.owner === userStore.user.id)
        }
    } else {
        return props.corpora
    }
})
// Enable edit & delete buttons only if activeCorpus is in this table.
// (Not that CorpusForm cares, but looks nicer)
const activeCorpusInTable = computed(() => {
    return displayCorpora.value.map(i => i.uuid).includes(selectedCorpus.value?.uuid)
})

const columns: Field[] = [
    { key: "uuid", isPrimaryField: true, hidden: true },
    { key: "name", sortOn: x => x.name, label: "corpus" },
    { key: "language", sortOn: x => x.language },
    { key: 'sizeInBytes', label: "size", sortOn: x => x.sizeInBytes },
    { key: "eraFrom", sortOn: x => x.eraFrom, label: "year from", hidden: true },
    { key: "eraTo", sortOn: x => x.eraTo, label: "year to", hidden: true },
  //  { key: "tagset", sortOn: x => x.tagset },
    { key: "source", label: "source", sortOn: x => x.source, hidden: true  },
    { key: "lastModified", sortOn: x => x.lastModified, label: "last modified", hidden: true  },
    { key: "collaborators", hidden: !editable, sortOn: x => customSharedSort(x), label: "shared with" },
    { key: "activeJobs", sortOn: x => x.activeJobs, label: "active jobs" },
    //{ key: "overallProgress", sortOn: x => x.overallProgress, label: "overall progress" },
    { key: "numResults", sortOn: x => x.numResults, label: "results" },
    { key: "numDocs", sortOn: x => x.numDocs, label: "docs" },
    { key: "actions"},

]

// Watches
// We can't use corporaStore.activeCorpus directly, because there will be multiple corpus tables on the page.
/* watch(() => corporaStore.activeCorpus, () => {
    return selectedCorpus.value = corporaStore.activeCorpus
}, { immediate: true })
watch(() => selectedCorpus.value, () => {
    if (selectedCorpus.value) {
        corporaStore.activeUUID = selectedCorpus.value?.uuid
    }
}, { immediate: true })
 */
// Methods
function corpusHasDocs(corpus: CorpusMetadata | null): boolean {
        if (!corpus) return false
        return (corpus.numDocs ?? 0) > 0
}

function corpusHasResults(corpus: CorpusMetadata | null): boolean {
        if (!corpus) return false
        return (corpus.numResults ?? 0) > 0
}

function formatCollaborators(i: CorpusMetadata): string {
    if (i.public) return "Public"
    const numPeople = i.collaborators.length + i.viewers.length
    if (numPeople == 0) return "No one"
    return numPeople == 1 ? `${numPeople} person` : `${numPeople} people`
}

function customSharedSort(i: CorpusMetadata) {
    if (i.public) return -1
    return i.collaborators.length + i.viewers.length
}

</script>
