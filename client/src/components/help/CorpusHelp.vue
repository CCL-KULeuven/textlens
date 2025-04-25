<template>
    <div>
        <h2>Corpora</h2>
        <ol>
            <li>Press 'new corpus' to create one or more corpora.</li>
            <li>Press the 'upload documents' icon (<i class="fa fa-file-text"></i> ) to upload one or more files to your corpus.</li>
            <li>Press the 'annotate' icon (<i class="fa fa-code"></i>) to annotate the selected corpus.</li>
            <li>Press the 'export' icon (<i class="fa fa-download"></i>) to export the annotated corpus data.</li>
            <li>Press the 'edit' icon (<i class="fa fa-pencil"></i>) to modify the corpus metadata.</li>
            <li>Press the ‘delete’ icon (<i class="fa fa-trash"></i>) to remove the selected corpus and all annotations.</li>
        </ol>
        
    </div>
</template>

<script setup lang="ts">
// Components
import { GNav } from "@/components"

defineProps({
    showLink: Boolean,
})
</script>
