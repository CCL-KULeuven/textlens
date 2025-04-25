<template>
    <div>
        <MetricsTable title="Global metrics" :loading :columns :items>
            <template #help>
                <p>In Global Metrics an overview is given of the (dis)agreement between the two layers that have been
                    selected for lemma and PoS comparison. Click on the (dis)agreement value to show a data sample.</p>

            </template>
        </MetricsTable>

        <EvaluationInfoBox :eval="metrics" />
    </div>
</template>

<script setup lang='ts'>
// Libraries & stores
import { storeToRefs } from 'pinia'
import { computed } from 'vue'
import stores from "@/stores"
// API & types
import { metricsPerPosColumns } from '@/stores/evaluation/metrics'
// Components
import MetricsTable from '@/components/tables/MetricsTable.vue'
import { EvaluationInfoBox } from '@/components'

// Fields
const { loading, metrics } = storeToRefs(stores.useMetrics())
const columns = computed(() => {
    const withoutName = metricsPerPosColumns.filter((col) => !["precision", "recall", "f1", "falsePositive", "name"].includes(col.key))
    const addColumns = [
        { key: "annotation", label: "annotation", sortOn: x => x.annotation },
        { key: "group", label: "grouped by", sortOn: x => x.group },
        { key: "macroPrecision", label: "macro\nprecision", sortOn: x => x.macroPrecision },
        { key: "macroRecall", label: "macro\nrecall", sortOn: x => x.macroRecall },
        { key: "macroF1", label: "macro\nf1", sortOn: x => x.macroF1 },
        { key: "microAccuracy", label: "micro\naccuracy", sortOn: x => x.microAccuracy }
    ]
    return addColumns.concat(withoutName)
})
const items = computed(() => {
    if (metrics.value?.metrics == null)
        return []
    else {
        // metrics has the form
        // { pos: { f1, recall, ... }, lemma: { f1, recall, ... }, lemmaPos: { f1, recall, ... } }
        // We want to transform this to
        // [ { name: "PoS", f1, recall, ... }, { name: "Lemma", f1, recall, ... }, { name: "Lemma & PoS", f1, recall, ... } ]
        const ret = Object.keys(metrics.value.metrics).map((key) => ({ name: key, ...metrics.value.metrics[key] })).map((i) => {
            const annoAndGroup = annotationAndGroupFromName(i.name)
            return {
                annotation: i.setting.annotation,
                group: i.setting.group,
                count: i.classes.classCount,
                truePositive: i.classes.truePositive,
                falseNegative: i.classes.falseNegative,
                noMatch: i.classes.noMatch,
                macroPrecision: i.macro.precision,
                macroRecall: i.macro.recall,
                macroF1: i.macro.f1,
                microAccuracy: i.micro.accuracy,
            }
        })
        return ret
    }
})

// Methods
function annotationAndGroupFromName(name: string) {
    const names = name.split("By")
    const annotation = splitCamelCase(names[0]).toLowerCase().split(" ")
    const group = names[1].toLowerCase()
    return { annotation: annotation, group: group }
}

function splitCamelCase(s: string) {
    return s.split(/(?=[A-Z])/).join(" ")
}
</script>
