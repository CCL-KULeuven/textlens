<template>
    <GCard title="Version">
        <template #help>Here you can find some information about the exact version you are currently
            running.</template>
        <KeyValues :data="versionYaml" />
    </GCard>
</template>

<script setup lang='ts'>
import versionYaml from '@/../version.yml'
import { GCard, KeyValues } from '@/components'
</script>