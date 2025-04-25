<template>
    <GModal small title="Are you sure?" :show="show" @hide="$emit('hide')" :noHelp="noHelp" closeBtnTitle="Cancel, keep data" closeBtnColor="green">
        <template #help>
            {{ displayname }} will be deleted from the textlens server.<br />
            Make sure you have a copy of the original files somewhere.
        </template>
        <p>You will <slot name="action">delete</slot> <b>{{ displayname }}</b>. This can not be undone.</p>
        <template #buttons>
            <GButton red @click="$emit('delete', item); $emit('hide')">Confirm</GButton>
        </template>
    </GModal>
</template>

<script lang='ts'>
import { defineComponent } from 'vue'
import { GButton, GModal } from '@/components'

export default defineComponent({
    name: "DeleteModal",
    props: ["item", "displayname", "id", "show", "noHelp"],
});
</script>