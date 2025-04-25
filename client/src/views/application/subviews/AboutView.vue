<template>
    <GCard title="About">
        <template #help>Here you can find some information about the application and the exact version you are currently
            running.</template>
        <div class="center">
            <p>Textlens is a fork based on <ExternalLink href="https://portal.clarin.ivdnt.org/galahad">Galahad</ExternalLink>, which was created by the
                <ExternalLink href="https://www.ivdnt.org">
                    Dutch Language Institute
                </ExternalLink>. </p><p>This project was funded by FWO International Research Infrastructures with grant number I000921N as part of the CLARIAH-VL project.
            </p>
            <a style="margin-left: 20px;" href="https://www.fwo.be/en/">
                <img width="200px" src="@/assets/fwo.png" alt="Logo FWO"
                    title="FWO" />
            </a>
            <a style="margin-left: 20px;" href="https://clariahvl.hypotheses.org">
                <img width="200px" src="@/assets/clariahvl.png" alt="Logo CLARIAH-VL"
                    title="CLARIAH-VL" />
            </a>

            <a style="margin-left: 20px;" href="https://clarin-be.ivdnt.org">
                <img width="200px" src="@/assets/clarin-be.png" alt="Logo CLARIN-BE"
                    title="CLARIN-BE" />
            </a>

            <a style="margin-left: 20px;" href="https://clarin.eu">
                <img width="200px" src="@/assets/clarin.png" alt="Logo CLARIN"
                    title="CLARIN" />
            </a>
            
            <a style="margin-left: 20px;" href="https://www.arts.kuleuven.be/ling/ccl">
                <img width="200px" src="@/assets/kul.png" alt="Logo KU Leuven"
                    title="KU Leuven" />
            </a>
            
            <a style="margin-left: 20px;" href="https://www.ivdnt.org">
                <img width="200px" src="@/assets/ivdNt-logo-3regels.png" alt="Logo Instituut voor de Nederlandse taal"
                    title="Instituut voor de Nederlandse taal" />
            </a>
            <!-- <h2>Version</h2>
            <KeyValues :data="versionYaml" /> -->
        </div>
    </GCard>
</template>

<script setup lang='ts'>
import versionYaml from '@/../version.yml'
import { GCard, KeyValues } from '@/components'
</script>

<style scoped="true">
.center {
    text-align: center
}
</style>
