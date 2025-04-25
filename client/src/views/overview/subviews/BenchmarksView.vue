<template>
    <div class="center">
        <GCard title="Benchmarks">
            <template #help>
                Benchmarks show the performance of taggers on the default datasets.
                The accuracy scores are given for lemma, PoS, and both.
                <br />
                For more details on the datasets, see the
                <GNav :route="{ path: '/overview/datasets' }">
                    datasets overview
                </GNav>
            </template>

            <GTable v-for="aspect in assays.aspects" :key="aspect.id"
                :title="`Benchmarks for &quot;${aspect.description}&quot;`" :columns :items="items"
                :loading="assays.loading" noHelp>

                <template #table-empty-instruction>
                    No benchmarks appeared? That is not right! Please contact the INT at
                    <MailAddress />
                </template>

                <!-- tagger name -->

                <template #cell-tagger="d">
                    <ExternalLink v-if="d.item.tagger !== SOURCE_LAYER"
                        :href="`/overview/taggers#${d.item.tagger}`">
                        {{ d.item.tagger }}
                    </ExternalLink>
                    <div v-else>
                        <span style="font-weight: bold">{{ d.item.tagger }}</span>
                    </div>
                </template>

                <template #cell="d">
                    {{ d.value.count ? score(d.value, aspect) : "" }}
                    <span v-if="showAsterisk(d.value, aspect)">*</span>
                </template>

            </GTable>

            <GInfo style="width:fit-content; min-width:0%">
                <b>*</b>: when taggers use a different tagset than the reference tagset, the score can be very low.
            </GInfo>
        </GCard>
    </div>
</template>

<script setup lang='ts'>
// Libraries & stores
import { computed, onMounted } from 'vue'
import stores, { AssaysStore } from '@/stores'
// API & Types
import { Assay, AssayDescription } from '@/types/assays'
import { SOURCE_LAYER } from '@/types/jobs'
// Components
import { MailAddress, GTable, GInfo, GCard, GNav } from '@/components'

// Stores
const assays = stores.useAssays() as AssaysStore

// Fields
const datasetFields = computed(() => assays.datasets.map(dataset => {
    return {
        key: dataset,
        label: dataset
    }
}))
const columns = computed(() => [{ key: "tagger", label: "Taggers" }].concat(datasetFields.value))
const items = computed(() => {
    return Object.keys(assays.assays).map(key => {
        // deepcopy, and we are mixing string with number at this point
        const assay = JSON.parse(JSON.stringify(assays.assays[key])) as any
        assay.tagger = key
        return assay
    })
})

// Watches & mounts
// Only needs to load once
onMounted(() => {
    assays.reload()
})

// Methods
/** Calculate the score to 2 decimals */
function score(assay: Assay, desc: AssayDescription): string {
    // We access the object value with a string,
    // so typescript needs some explicit typing.
    return (assay[desc.id as keyof Assay] as number / assay.count).toFixed(2)
}

/**
 * Show an asterisk for extremely low PoS scores
 */
function showAsterisk(assay: Assay, aspect: AssayDescription): boolean {
    return (parseFloat(score(assay, aspect)) <= 0.02)
        && !aspect.id.includes('lemma')
}
</script>

<style scoped lang="scss">
.center {
    display: flex;
    flex-direction: column;
    align-items: center;
}
</style>
