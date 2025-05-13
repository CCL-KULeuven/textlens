<template>
    <GButton yellow :loading="loading" :disabled="disabled" title="Download" :showTitle="showTitle">
        <!-- Note this object syntax to add css classes with booleans. -->
        <i class="fa fa-download" :class="{ 'wide': wide }"></i>
    </GButton>
</template>

<script setup lang="ts">
// Components
import { GButton } from "@/components"

defineProps({
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    wide: { type: Boolean, default: false },
    showTitle: { type: Boolean, default: false },
})
</script>

<style scoped>
.wide {
    padding: 0 1em;
}
</style>
