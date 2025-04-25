<template>
    <div>

        <!-- Styled label for input -->
        <label for="file-upload" class="custom-file-upload">
            <i class="fa fa-upload"></i> 
            Upload file(s)
        </label>
        <!-- Actual input -->
        <input type="file" ref="uploadInput" name="filefield" multiple id="file-upload" style="display: none;"
            accept=".xml, .tsv, .txt, .zip, .conllu, .naf"
            @change="e => filesToUpload = Object.values(e.target.files as FileList)" />


        <!-- List of currently selected files. -->
        <ul v-if="filesToUpload.length > 0">
            <!-- First 5 file names are shown-->
            <li v-for="file in filesToUpload.slice(0, 4)" :key="file.name">{{ file.name }}</li>
            <!-- Overflow -->
            <li v-if="filesToUpload.length > 4">
                + {{ filesToUpload.length - 4 }} more {{ filesToUpload.length == 5 ? "file" : "files" }}
            </li>
        </ul>

        <!-- Confirmation and clear buttons after a selection has been made -->
        <template v-if="filesToUpload.length != 0">
            <GButton green @click="documentsStore.uploadAll(); $refs.uploadInput.value = null">
                Upload
            </GButton>
            <GButton plain @click="filesToUpload = []; $refs.uploadInput.value = null">
                &#10006;&nbsp;clear
            </GButton>
        </template>

        <!-- Error for illegal selection -->
        <GInfo error v-if="illegalFiles.length > 0">
            You have selected some filetype(s) that are not supported in textlens:
            <ul>
                <li v-for="file in illegalFiles" :key="file.name">
                    {{ file.name }}
                </li>
            </ul>

            Do you want to upload the text? You can:
            <ol>
                <li>copy-paste it to NotePad</li>
                <li>save as .txt</li>
                <li>upload it to textlens</li>
            </ol>
        </GInfo>

        <!-- upload busy info -->
        <GInfo v-show="uploadBusyCount > 0" spinner>
            Upload will continue in the background, please don't close the browser. Currently uploading:
            {{ uploadBusyCount }}
        </GInfo>

        <!-- Errors for files that could not be parsed by the server (e.g. broken xml tags)-->
        <GInfo error v-show="uploadErrorCount > 0">
            <div v-for="(value, key) in uploading" :key="key">
                <span v-if="value.status == 'error'">{{ key }}: {{ value.message }}</span>
            </div>
        </GInfo>
    </div>
</template>

<script setup lang='ts'>
// Libraries & stores
import stores from '@/stores'
import { storeToRefs } from 'pinia'
// Components
import { GButton, GInfo } from '@/components'

// Stores
const documentsStore = stores.useDocuments()
const { filesToUpload, illegalFiles, uploadBusyCount, uploadErrorCount, uploading } = storeToRefs(documentsStore)
</script>

<style scoped>
.custom-file-upload {
    border: 0px solid #ccc;
    background-color: var(--int-theme);
    display: inline-block;
    padding: 10px 12px;
    cursor: pointer;
    margin-right: 10px;
    font-style: normal;

    &:hover {
        background-color: var(--int-theme-hover);
    }

    &:active {
        background-color: var(--int-theme-active);
    }
}

ul,
ol {
    width: fit-content;
    text-align: left;
    margin: 1em auto;
}
</style>