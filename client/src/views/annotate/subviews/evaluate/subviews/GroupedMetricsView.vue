<template>
    <div>
        <MetricsTable title="Grouped Metrics" :loading :columns :items>
            <template #help>
                <p>In PoS Metrics an overview is given of the (dis)agreement for lemma and PoS per part-of-speech. Click
                    on
                    the (dis)agreement value to show a data sample.</p>
            </template>
            <template #prepend v-if="metrics.metrics != null">
                <p style="text-align: center;">
                    <b>
                        Only the 100 most frequent groups are shown.
                    </b>
                </p>
                <div class="table-controls">
                    <div class="table-control">
                        Annotation:
                        <GInput type="select" :options="metricOptions" v-model="selectedMetric" />
                    </div>

                    <div class="table-control">
                        Group by:
                        <GInput type="select" :options="groupOptions" v-model="selectedGroup" />
                    </div>

                    <div class="table-control" v-if="selectedMetric == selectedGroup">
                        Single/multiple analysis:
                        <GInput type="select" :options="singleOrMultipleOptions" v-model="selectedSingleOrMultiple" />
                    </div>
                </div>
            </template>
        </MetricsTable>

        <EvaluationInfoBox :eval="metrics" />
    </div>
</template>

<script setup lang='ts'>
// Libraries & stores
import { ref, computed } from 'vue'
import { storeToRefs } from 'pinia'
import stores from "@/stores"
// API & types
import { metricsPerPosColumns } from '@/stores/evaluation/metrics'
// Components
import { EvaluationInfoBox } from '@/components'
import MetricsTable from '@/components/tables/MetricsTable.vue'

// Fields
const { loading, metrics } = storeToRefs(stores.useMetrics())
const metricOptions = computed(() => {
    return [
        { value: "pos", text: "PoS" },
        { value: "lemma", text: "Lemma" },
        { value: "lemmaPos", text: "PoS + Lemma" }
    ]
    if (metrics.value?.metrics == null) return []
    return Object.keys(metrics.value.metrics).map((key) => ({ value: key, text: key.split(/(?=[A-Z])/).join(" ") }))
})
const groupOptions = [
    { value: "pos", text: "PoS" },
    { value: "lemma", text: "Lemma" },
]
const selectedMetric = ref(metricOptions.value[0]?.value)
const selectedGroup = ref(metricOptions.value[0]?.value)
const singleOrMultipleOptions = [
    { value: "both", text: "Both" },
    { value: "single", text: "Single" },
    { value: "multi", text: "Multiple" },
]
const selectedSingleOrMultiple = ref(singleOrMultipleOptions[0]?.value)
const columns = computed(() => metricsPerPosColumns)
const metricName = computed(() => {
    let annotation = null
    if (selectedSingleOrMultiple.value == "both" || selectedMetric.value != selectedGroup.value) {
        annotation = selectedMetric.value
    } else {
        annotation = selectedSingleOrMultiple.value + capitalize(selectedMetric.value)
    }
    const group = capitalize(selectedGroup.value)
    return annotation + "By" + group

})
const posMetrics = computed(() => {
    if (metrics.value?.metrics?.[metricName.value] == null) return []
    // Copy over the metrics (depending on selectedMetric.value) from: 
    // { ADJ: { ADJ: { pos : { f1, recall, ... }, lemma : { f1, recall, ... } } } } }
    // to:
    // { ADJ: { ADJ: { f1, recall, ..., } } }
    const ret = metrics.value.metrics[metricName.value].grouped.map((i) => ({
        name: i.name,
        count: i.classes.classCount,
        truePositive: i.classes.truePositive,
        falsePositive: i.classes.falsePositive,
        falseNegative: i.classes.falseNegative,
        noMatch: i.classes.noMatch,
        precision: i.metrics.precision,
        recall: i.metrics.recall,
        f1: i.metrics.f1,
    }))
    return ret
})
const singlePosMetrics = computed(() => {
    return Object.values(posMetrics.value).filter((pos) => !pos.name.includes("+"))
})
const multiPosMetrics = computed(() => {
    return Object.values(posMetrics.value).filter((pos) => pos.name.includes("+"))
})
const items = computed(() => {
    // if (selectedSingleOrMultiple.value == "single") return singlePosMetrics.value
    // if (selectedSingleOrMultiple.value == "multi") return multiPosMetrics.value
    return posMetrics.value
})

// Methods
function capitalize(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1)
}
</script>
