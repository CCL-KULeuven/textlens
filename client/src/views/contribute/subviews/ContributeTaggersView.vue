<template>
    <div><GCard>
        <template #title>Contribute a tagger</template>
        <template #help>Creating linguistic infrastructure is a lot of work. Why not do it together?</template>
        

        <p>
            Textlens and Galahad use embedded Docker containers running the taggers. You can share your tagger as a Docker image with us
            and
            other researchers. The easiest way to create a Docker image that implements the basic Galahad API is to extend
            from
            the galahad-tagger-base image. A base implementation is available on GitHub.
        </p>
        <p>
            
                See <ExternalLink
                    href="https://github.com/INL/galahad-taggers-dockerized">
                    galahad-taggers-dockerized </ExternalLink> 
             for more information.
        </p>

        </GCard>
        </div>
</template>

<script setup lang='ts'>
import { GCard, GInfo, ExternalLink } from '@/components'
</script>

