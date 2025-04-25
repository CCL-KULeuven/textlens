<template>
    <div> <!--Extra div for padding on small screens.-->
        <GCard title="Glossary">
            <template #help>Find a description of key terms in the glossary.</template>

            
            <h3 id="clariah">CLARIAH</h3>
            Common Lab Research Infrastructure for the Arts and Humanities
            See <ExternalLink href="https://www.clariah.nl/">clariah.nl</ExternalLink> and
            <ExternalLink href="https://ivdnt.org/onderzoek-projecten/clariah/">CLARIAH at INT</ExternalLink>

            
            <h3 id="int">INT</h3>
            Instituut voor de Nederlandse taal, the Dutch Language Institute.
            See <ExternalLink href="https://www.ivdnt.org/">ivdnt.org</ExternalLink>

            
        </GCard>
    </div>
</template>

<script setup lang='ts'>
import { GCard, ExternalLink } from '@/components'
</script>

<style scoped="true">
:deep(.content-wrapper)>.content {
    flex: 0 1 800px !important;
}
</style>
